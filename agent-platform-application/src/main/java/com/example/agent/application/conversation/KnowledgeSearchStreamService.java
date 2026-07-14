package com.example.agent.application.conversation;

import com.example.agent.application.knowledge.HybridSearchApplicationService;
import com.example.agent.application.knowledge.dto.SearchResultDTO;
import com.example.agent.application.memory.SessionMemoryService;
import com.example.agent.domain.conversation.entity.Message;
import com.example.agent.infrastructure.annotation.Auditable;
import com.example.agent.infrastructure.config.sse.SseEventFactory;
import com.example.agent.infrastructure.config.nacos.SessionConfig;
import com.example.agent.infrastructure.context.TenantContext;
import com.example.agent.infrastructure.metrics.AgentMetrics;
import com.example.agent.infrastructure.observability.LangfuseTraceService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 知识库检索流式服务 — RAG（检索增强生成）模式的 SSE 流式管线，含完整对话生命周期.
 * <p>
 * 与 {@link StreamOrchestrationService} 对等的流式编排服务，专用于
 * {@code InteractionMode#KNOWLEDGE_SEARCH} 模式。
 *
 * <h3>执行流程（7 步完整管线）</h3>
 * <ol>
 *   <li><b>保存用户消息</b> — 持久化到会话，自动同步 Redis 短期记忆</li>
 *   <li><b>加载会话上下文</b> — 从 Redis 读取最近 N 轮对话历史</li>
 *   <li><b>上下文增强检索</b> — 将历史主题融入检索 Query 提升召回率</li>
 *   <li><b>无命中</b> → SSE 流式推送友好提示"未找到相关信息"</li>
 *   <li><b>有命中</b> → 构建 RAG Prompt（检索上下文 + 对话历史 + 用户问题）→ LLM 流式输出</li>
 *   <li><b>保存助手消息</b> + 记录指标 + Langfuse 追踪</li>
 *   <li><b>长期记忆提取</b>（异步）</li>
 * </ol>
 *
 * <h3>对话生命周期对比</h3>
 * <pre>
 * 智能对话 (StreamOrchestrationService):   知识库检索 (本服务):
 *   ✅ 保存用户消息                          ✅ 保存用户消息
 *   ✅ 加载会话历史                          ✅ 加载会话历史（用于检索增强 + Prompt 上下文）
 *   ✅ 意图识别                             ➖ 不需要（mode 已确定路由）
 *   ✅ 构建 Prompt（含历史）                  ✅ 构建 RAG Prompt（检索内容 + 历史 + 问题）
 *   ✅ LLM 流式                             ✅ LLM 流式
 *   ✅ 保存助手消息                          ✅ 保存助手消息
 *   ✅ 长期记忆提取                          ✅ 长期记忆提取
 * </pre>
 *
 * <h3>RAG Prompt 设计（三层上下文）</h3>
 * <ul>
 *   <li><b>对话历史</b> — 帮助 LLM 理解指代/省略/追问意图</li>
 *   <li><b>检索内容</b> — 从知识库检索到的权威参考信息</li>
 *   <li><b>用户问题</b> — 当前待回答的问题</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.7.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeSearchStreamService {

    private final HybridSearchApplicationService hybridSearchService;
    private final MessageApplicationService messageService;
    private final SessionMemoryService sessionMemoryService;
    private final ChatClient chatClient;
    private final AgentMetrics metrics;
    private final LangfuseTraceService langfuseTrace;
    /** 🆕 P6 配置治理子方案03: SSE 心跳+上下文轮数从 Nacos 动态读取（消除与 StreamOrchestrationService 的重复定义） */
    private final SessionConfig sessionConfig;

    /** 知识库检索无结果时的提示模板 */
    private static final String NO_RESULT_TEMPLATE = "您检索的「%s」内容本知识库暂时未涵盖，请联系相关人员及时更新内容。";

    // ==================== RAG Prompt 模板 ====================

    /**
     * RAG 系统提示词 — 三层上下文结构（对话历史 + 检索内容 + 用户问题）.
     * <p>
     * 对话历史帮助 LLM 理解指代和追问，检索内容提供权威参考，
     * 5 条硬约束确保不臆造。
     */
    private static final String RAG_SYSTEM_PROMPT = """
            你是一个专业的知识库助手。请严格根据以下从知识库中检索到的内容回答用户的问题。

            ## 对话历史（帮助你理解用户的追问和指代）
            %s

            ## 检索到的知识库内容
            %s

            ## 回答规则（必须严格遵守）
            1. **仅根据上述检索内容回答** — 不要使用你自己的先验知识或训练数据中的信息
            2. **未找到时明确告知** — 如果检索内容与用户问题不相关或不足以回答问题，请明确说"根据当前知识库的内容，我无法回答这个问题"
            3. **引用来源** — 在回答中引用具体内容时，请标注来源文档名称
            4. **结合对话历史理解意图** — 如果用户使用了"它"、"这个"、"那个"等指代词，请结合对话历史理解
            5. **简洁准确** — 回答应简洁、准确、有条理，避免冗长
            6. **禁止臆造** — 绝对禁止编造知识库中不存在的信息

            ## 用户问题
            %s
            """;

    // ==================== 公开 API ====================

    /**
     * 执行知识库检索 + LLM 流式生成管线（含完整对话生命周期）.
     *
     * @param conversationId 会话 ID
     * @param tenantId       租户 ID
     * @param userId         用户 ID
     * @param userContent    用户输入内容
     * @param knowledgeId    知识库 ID（null 表示检索所有已启用知识库）
     * @param emitter        SSE 发射器
     */
    @Auditable(action = "RAG_LLM_CALL", resourceType = "CONVERSATION", recordResponse = false)
    public void executeStreamPipeline(String conversationId, Long tenantId, String userId,
                                       String userContent, String knowledgeId, SseEmitter emitter) {
        // 将 HTTP 线程捕获的上下文注入当前执行线程
        TenantContext.setTenantId(tenantId);
        TenantContext.setUserId(userId);

        // 启动消息处理耗时采样
        Timer.Sample messageSample = Timer.start();

        ScheduledExecutorService heartbeatExecutor = null;
        try {
            // ========== Step 1: 保存用户消息（自动同步 Redis 短期记忆） ==========
            Message userMsg = messageService.saveUserMessage(conversationId, userContent);
            log.info("[RAG-Stream] 用户消息已保存: convId={}, msgId={}", conversationId, userMsg.getMessageId());

            // ========== Step 2: 加载会话上下文 ==========
            List<Message> history = sessionMemoryService.getRecentMessages(conversationId, sessionConfig.getContextRounds());
            log.info("[RAG-Stream] 加载会话上下文: convId={}, historySize={}", conversationId, history.size());

            // ========== Step 3: 启动心跳 ==========
            heartbeatExecutor = startHeartbeat(emitter);

            // ========== Step 4: 上下文增强检索 ==========
            sendEvent(emitter, SseEventFactory.thinking("正在检索知识库..."));

            // 将对话历史中的关键信息融入检索 Query，提升多轮追问场景的召回率
            String enrichedQuery = enrichQueryWithHistory(userContent, history);
            log.info("[RAG-Stream] 检索Query: original='{}' → enriched='{}'", userContent, enrichedQuery);

            Timer.Sample ragSample = Timer.start();
            SearchResultDTO searchResult = hybridSearchService.search(
                    enrichedQuery, knowledgeId, null);
            long ragDurationNs = ragSample.stop(metrics.getRagRetrievalTimer());

            List<SearchResultDTO.HitItem> hits = searchResult.getHits();
            int hitCount = hits != null ? hits.size() : 0;
            metrics.recordRagHits(tenantId, knowledgeId != null ? knowledgeId : "all", hitCount);

            log.info("[RAG-Stream] 检索完成: convId={}, kbId={}, hitCount={}, ragDurationMs={}",
                    conversationId, knowledgeId, hitCount, ragDurationNs / 1_000_000);

            // ========== Step 5: 无命中 → 友好提示 ==========
            if (hitCount == 0) {
                String noResultMsg = String.format(NO_RESULT_TEMPLATE, truncate(userContent, 50));
                log.info("[RAG-Stream] 未检索到相关内容: convId={}, query={}", conversationId, userContent);

                // 模拟流式输出友好提示（逐字推送以保持前端体验一致）
                streamTextWithHeartbeat(emitter, noResultMsg, heartbeatExecutor);

                // 保存助手消息
                Message assistantMsg = messageService.saveAssistantMessage(
                        conversationId, noResultMsg, estimateTokenCount(noResultMsg));
                sendEvent(emitter, SseEventFactory.done(estimateTokenCount(noResultMsg), assistantMsg.getMessageId()));
                emitter.complete();

                messageSample.stop(metrics.getMessageProcessingTimer());
                return;
            }

            // ========== Step 6: 有命中 → 推送文件引用 + 构建 RAG Prompt + LLM 流式输出 ==========
            // 推送检索命中的文件引用信息（前端可用此渲染"参考文档"侧栏，提供预览/下载入口）
            List<SearchResultDTO.DocumentRef> docRefs = searchResult.getDocuments();
            if (docRefs != null && !docRefs.isEmpty()) {
                sendEvent(emitter, SseEventFactory.references(buildReferencesPayload(docRefs)));
                log.info("[RAG-Stream] 推送文件引用: convId={}, docCount={}", conversationId, docRefs.size());
            }

            sendEvent(emitter, SseEventFactory.thinking(
                    String.format("已检索到 %d 条相关内容，正在生成回答...", hitCount)));

            // 构建三层上下文 Prompt
            String historyContext = buildHistoryContext(history);
            String retrievedContext = buildRetrievedContext(hits);
            String fullPrompt = String.format(RAG_SYSTEM_PROMPT, historyContext, retrievedContext, userContent);

            // LLM 流式输出
            StringBuilder fullResponse = new StringBuilder();
            AtomicInteger tokenCount = new AtomicInteger(0);

            // 捕获 MDC traceId（Lambda 回调在不同线程执行）
            String traceId = org.slf4j.MDC.get("traceId");

            // 启动 LLM 调用耗时采样
            Timer.Sample llmSample = Timer.start();

            chatClient.prompt()
                    .user(fullPrompt)
                    .stream()
                    .chatResponse()
                    .doOnNext(resp -> {
                        String token = resp.getResult().getOutput().getText();
                        if (token != null) {
                            fullResponse.append(token);
                            tokenCount.incrementAndGet();
                            sendEvent(emitter, SseEventFactory.token(token));
                        }
                    })
                    .doOnComplete(() -> {
                        long llmDurationMs = llmSample.stop(metrics.getLlmCallTimer());
                        metrics.recordTokenConsumption(tenantId, "deepseek", tokenCount.get());
                        messageSample.stop(metrics.getMessageProcessingTimer());

                        // Langfuse RAG 调用追踪（异步发送）
                        langfuseTrace.logLLMCallAsync(traceId, conversationId, "deepseek",
                                fullPrompt, fullResponse.toString(), llmDurationMs / 1_000_000, tokenCount.get());

                        // 保存助手消息
                        Message assistantMsg = messageService.saveAssistantMessage(
                                conversationId, fullResponse.toString(), tokenCount.get());
                        sendEvent(emitter, SseEventFactory.done(tokenCount.get(), assistantMsg.getMessageId()));
                        emitter.complete();

                        log.info("[RAG-Stream] 流式完成: convId={}, tokens={}, responseLength={}",
                                conversationId, tokenCount.get(), fullResponse.length());

                        // ========== Step 7: 长期记忆提取（异步） ==========
                        messageService.extractLongTermMemoryAsync(conversationId, userId, tenantId);
                    })
                    .doOnError(error -> {
                        llmSample.stop(metrics.getLlmCallTimer());
                        metrics.recordLlmError(tenantId, "deepseek", error.getClass().getSimpleName());
                        messageSample.stop(metrics.getMessageProcessingTimer());

                        log.error("[RAG-Stream] LLM 调用失败: convId={}", conversationId, error);
                        sendEvent(emitter, SseEventFactory.error("LLM 调用失败，请稍后重试", 500));
                        emitter.completeWithError(error);
                    })
                    .subscribe();

        } catch (Exception e) {
            messageSample.stop(metrics.getMessageProcessingTimer());
            log.error("[RAG-Stream] 管线异常: convId={}", conversationId, e);
            sendEvent(emitter, SseEventFactory.error(e.getMessage(), 500));
            emitter.completeWithError(e);
        } finally {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdown();
            }
        }
    }

    // ==================== 内部方法 ====================

    /**
     * 将对话历史融入检索 Query — 解决多轮追问场景的语义断裂问题.
     * <p>
     * <b>为什么需要：</b>用户追问时经常使用省略和指代：
     * <pre>
     *   第1轮: "公司的年假政策是什么？"
     *   第2轮: "那需要提前多久申请？"
     *   → 仅用"那需要提前多久申请"去检索 → 向量匹配无结果
     *   → 融入历史后: "年假政策 申请 提前多久" → 精准命中
     * </pre>
     * <p>
     * <b>策略：</b>从最近几轮用户消息中提取内容，与当前 query 拼接。
     * 不是简单的字符串拼接，而是只提取用户的历史提问（不含助手回答），
     * 因为用户的提问才是检索的真正意图。
     *
     * @param userContent 当前用户输入
     * @param history     最近 N 轮对话历史
     * @return 增强后的检索 Query
     */
    private String enrichQueryWithHistory(String userContent, List<Message> history) {
        if (history == null || history.isEmpty()) {
            return userContent;
        }

        // 只提取最近几轮用户消息的内容作为上下文增强
        List<String> recentUserQueries = history.stream()
                .filter(m -> m.getRole() != null && "USER".equals(m.getRole().name()))
                .map(Message::getContent)
                .filter(c -> c != null && !c.isBlank())
                .toList();

        if (recentUserQueries.isEmpty()) {
            return userContent;
        }

        // 取最近 3 条用户提问（不含当前这条，因为当前消息刚保存，已在 history 末尾）
        // 如果当前消息也在列表中，排除它
        List<String> previousQueries = recentUserQueries.stream()
                .filter(q -> !q.equals(userContent))
                .toList();

        if (previousQueries.isEmpty()) {
            return userContent;
        }

        // 取最近最多 3 条，每条截断到 30 字作为关键信号
        String historySignals = previousQueries.stream()
                .skip(Math.max(0, previousQueries.size() - 3))
                .map(q -> truncate(q, 30))
                .collect(Collectors.joining(" "));

        // 合并: 历史关键信号 + 当前问题
        return historySignals + " " + userContent;
    }

    /**
     * 构建对话历史上下文文本 — 帮助 LLM 理解追问和指代.
     *
     * @param history 最近 N 轮对话历史
     * @return 格式化的对话历史文本，无历史时返回"（无历史对话）"
     */
    private String buildHistoryContext(List<Message> history) {
        if (history == null || history.isEmpty()) {
            return "（无历史对话）";
        }

        StringBuilder ctx = new StringBuilder();
        for (Message msg : history) {
            String roleLabel = msg.getRole() != null && "USER".equals(msg.getRole().name()) ? "用户" : "助手";
            ctx.append(roleLabel).append(": ").append(msg.getContent()).append("\n");
        }
        return ctx.toString();
    }

    /**
     * 构建检索上下文文本 — 将命中 chunk 拼接为结构化上下文.
     * <p>
     * 每个 chunk 包含来源文档名和内容，按 rankPosition 排序。
     *
     * @param hits 检索命中列表（已按相关性排序）
     * @return 格式化的检索上下文文本
     */
    private String buildRetrievedContext(List<SearchResultDTO.HitItem> hits) {
        StringBuilder ctx = new StringBuilder();
        for (int i = 0; i < hits.size(); i++) {
            SearchResultDTO.HitItem hit = hits.get(i);
            ctx.append("---\n");
            ctx.append("【来源文档】: ");

            if (hit.getDocumentFilename() != null && !hit.getDocumentFilename().isBlank()) {
                ctx.append(hit.getDocumentFilename());
            } else {
                ctx.append(hit.getDocumentId());
            }

            ctx.append("\n");
            ctx.append("【相关内容】: ").append(hit.getContent()).append("\n");
        }
        return ctx.toString();
    }

    /**
     * 构建文件引用负载 — 将去重后的文档引用列表转换为前端可用的结构化数据.
     * <p>
     * 包含下载链接和预览链接，前端可根据 fileType 判断：
     * <ul>
     *   <li>图片/PDF/文本 → 内嵌预览（iframe / 图片查看器）</li>
     *   <li>Office 文档 → Office Web Viewer 或提示下载</li>
     *   <li>其他 → 仅提供下载</li>
     * </ul>
     *
     * @param docRefs 去重后的文档引用列表
     * @return 前端可直接使用的文件引用数据
     */
    private List<Map<String, Object>> buildReferencesPayload(List<SearchResultDTO.DocumentRef> docRefs) {
        return docRefs.stream()
                .map(doc -> {
                    Map<String, Object> ref = new java.util.LinkedHashMap<>();
                    ref.put("documentId", doc.getDocumentId());
                    ref.put("filename", doc.getFilename());
                    ref.put("fileType", doc.getFileType());
                    ref.put("fileSize", doc.getFileSize());
                    ref.put("downloadUrl", doc.getAccessUrl());
                    ref.put("previewUrl", doc.getPreviewUrl());
                    ref.put("uploadedAt", doc.getUploadedAt());
                    return ref;
                })
                .toList();
    }

    /**
     * 启动 SSE 心跳定时器 — 防止长连接被代理/负载均衡器断开.
     */
    private ScheduledExecutorService startHeartbeat(SseEmitter emitter) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "sse-rag-heartbeat"));
        executor.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data(""));
            } catch (Exception ignored) {
                // 连接已关闭，忽略
            }
        }, sessionConfig.getSseHeartbeatIntervalMs(), sessionConfig.getSseHeartbeatIntervalMs(), TimeUnit.MILLISECONDS);
        return executor;
    }

    /**
     * 安全发送 SSE 事件.
     */
    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            log.error("[RAG-Stream] SSE 事件发送失败", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 将文本逐字符流式推送 — 用于无命中时保持前端 SSE 体验一致.
     */
    private void streamTextWithHeartbeat(SseEmitter emitter, String text,
                                          ScheduledExecutorService heartbeatExecutor) {
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            sendEvent(emitter, SseEventFactory.token(ch));
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 估算文本 token 数量.
     */
    private int estimateTokenCount(String content) {
        if (content == null) return 0;
        return (int) Math.ceil(content.length() * 0.5);
    }

    /**
     * 截断文本到指定长度，超出部分用 "..." 替代.
     */
    private String truncate(String text, int maxChars) {
        if (text == null) return "";
        if (text.length() <= maxChars) return text;
        return text.substring(0, maxChars) + "...";
    }
}
