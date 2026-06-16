package com.example.agent.infrastructure.mcp;

import com.example.agent.common.exception.BusinessException;
import com.example.agent.domain.tool.entity.ToolRegistry;
import com.example.agent.domain.tool.valueobject.AuthConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * HTTP 工具适配器 — 将 HTTP REST API 包装为统一的工具调用格式.
 *
 * <p>对于无法提供标准 MCP 协议的遗留系统，通过此适配器将 HTTP API
 * 注册为平台工具，使 Agent 可以像调用 MCP 工具一样调用 HTTP 接口.
 *
 * <p>请求流程：
 * <ol>
 *   <li>从工具的 ToolSchema 中解析 HTTP 方法和请求体模板</li>
 *   <li>将传入参数填充到 URL 路径或请求体中</li>
 *   <li>自动注入认证头（API Key / Bearer Token / Basic Auth）</li>
 *   <li>发送 HTTP 请求并解析响应</li>
 * </ol>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class HttpToolAdapter {

    /** JSON 序列化工具 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** Spring Boot 3.x RestClient — 同步 HTTP 客户端 */
    private final RestClient restClient;

    /**
     * 构造函数注入 RestClient.Builder（Spring Boot 自动配置）.
     *
     * @param restClientBuilder Spring Boot 自动配置的 RestClient.Builder
     */
    public HttpToolAdapter(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * 调用 HTTP 工具 — 统一的 HTTP 工具调用入口.
     *
     * <p>根据工具注册的 endpoint 和 schema，构建 HTTP 请求，
     * 注入认证信息，执行请求并返回解析后的结果.
     *
     * @param tool   工具注册实体（包含 endpoint、authConfig、schema）
     * @param params 调用参数（由 LLM 根据 ToolSchema 生成）
     * @return HTTP 响应结果（已解析为 Java 对象）
     * @throws BusinessException 如果 HTTP 调用失败
     */
    public Object invokeHttpTool(ToolRegistry tool, Map<String, Object> params) {
        String url = tool.getEndpoint();
        if (url == null || url.isBlank()) {
            throw new BusinessException(400, "HTTP 工具未配置 endpoint: " + tool.getToolId());
        }

        // 从 Schema 中解析 HTTP 方法（默认 POST）
        HttpMethod method = resolveHttpMethod(tool);

        log.info("[HTTP Tool] 调用开始: toolId={}, name={}, method={}, url={}",
                tool.getToolId(), tool.getName(), method, url);

        try {
            // 构建请求
            RestClient.RequestBodySpec request = restClient
                    .method(method)
                    .uri(url)
                    .headers(headers -> {
                        // 注入认证头
                        applyAuth(headers, tool.getAuthConfig());
                        // 设置通用头
                        headers.set("Content-Type", "application/json");
                        headers.set("Accept", "application/json");
                    });

            // POST/PUT/PATCH 请求需要设置请求体
            if (requiresBody(method)) {
                String requestBody = objectMapper.writeValueAsString(params);
                request.body(requestBody);
            }

            // 执行请求并获取响应
            var response = request.retrieve()
                    .toEntity(String.class);

            String responseBody = response.getBody();
            log.info("[HTTP Tool] 调用成功: toolId={}, status={}, duration=unknown",
                    tool.getToolId(), response.getStatusCode());

            // 尝试解析为 JSON 对象，失败则返回原始字符串
            return parseResponse(responseBody);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[HTTP Tool] 调用失败: toolId={}, url={}, error={}",
                    tool.getToolId(), url, e.getMessage());
            throw new BusinessException(500, "HTTP 工具调用失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从工具 Schema 中解析 HTTP 方法.
     * <p>如果 Schema 中未指定方法，默认使用 POST.
     *
     * @param tool 工具注册实体
     * @return HTTP 方法枚举
     */
    private HttpMethod resolveHttpMethod(ToolRegistry tool) {
        if (tool.getSchema() != null && tool.getSchema().getInputSchema() != null) {
            try {
                Map<String, Object> schemaMap = objectMapper.readValue(
                        tool.getSchema().getInputSchema(),
                        new TypeReference<Map<String, Object>>() {});
                if (schemaMap.containsKey("method")) {
                    String methodStr = schemaMap.get("method").toString().toUpperCase();
                    return HttpMethod.valueOf(methodStr);
                }
            } catch (Exception e) {
                log.debug("[HTTP Tool] 无法从 Schema 解析 HTTP 方法，使用默认 POST: toolId={}",
                        tool.getToolId());
            }
        }
        return HttpMethod.POST;  // 默认 POST
    }

    /**
     * 将认证配置注入到 HTTP 请求头中.
     *
     * @param headers    目标请求头（可变）
     * @param authConfig 认证配置
     */
    private void applyAuth(org.springframework.http.HttpHeaders headers, AuthConfig authConfig) {
        if (authConfig == null) {
            return;
        }

        String authType = authConfig.getAuthType();
        if (authType == null) {
            return;
        }

        switch (authType.toUpperCase()) {
            case "API_KEY":
                if (authConfig.getApiKey() != null) {
                    headers.set("Authorization", "Bearer " + authConfig.getApiKey());
                }
                break;
            case "BEARER":
                if (authConfig.getToken() != null) {
                    headers.set("Authorization", "Bearer " + authConfig.getToken());
                }
                break;
            case "BASIC":
                if (authConfig.getUsername() != null && authConfig.getPassword() != null) {
                    String basicAuth = java.util.Base64.getEncoder().encodeToString(
                            (authConfig.getUsername() + ":" + authConfig.getPassword()).getBytes());
                    headers.set("Authorization", "Basic " + basicAuth);
                }
                break;
            case "NONE":
            default:
                // 无需认证
                break;
        }

        // 注入自定义请求头
        if (authConfig.getHeaders() != null) {
            authConfig.getHeaders().forEach(headers::set);
        }
    }

    /**
     * 判断 HTTP 方法是否需要请求体.
     *
     * @param method HTTP 方法
     * @return true 表示需要请求体（POST/PUT/PATCH）
     */
    private boolean requiresBody(HttpMethod method) {
        return method == HttpMethod.POST
                || method == HttpMethod.PUT
                || method == HttpMethod.PATCH;
    }

    /**
     * 解析 HTTP 响应体 — 尝试解析为 JSON，失败返回原始字符串.
     *
     * @param responseBody HTTP 响应字符串
     * @return 解析后的对象（Map/List）或原始字符串
     */
    private Object parseResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            // 尝试解析为 JSON 对象
            return objectMapper.readValue(responseBody, Object.class);
        } catch (Exception e) {
            log.debug("[HTTP Tool] 响应非 JSON 格式，返回原始字符串");
            return responseBody;
        }
    }
}
