# IM 接入适配技术方案

> 版本: 1.0.0 | 生效日期: 2026-06-21 | 所属: P5-交互端  
> 关联文档: [[前端架构设计方案]] | [[Web聊天界面技术方案]]

---

## 一、概述

构建统一消息适配器层，将企业微信、钉钉等 IM 平台的消息格式转化为内部统一结构，实现跨渠道统一体验。用户可以通过企微/钉钉与 AI Agent 对话，审批操作直接在 IM 中完成。

**核心能力**:
- 多平台消息格式统一转换
- 企微/钉钉回调对接
- 渠道级鉴权与加密
- 统一消息格式 (T1 内部协议)
- 审批操作可在 IM 中完成

---

## 二、架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                     IM 客户端                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │ 企业微信  │  │   钉钉   │  │ 飞书(预留) │             │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘             │
│       │              │              │                    │
│       │  回调 URL    │  回调 URL    │  回调 URL          │
│       ▼              ▼              ▼                    │
│  ┌─────────────────────────────────────────────────┐    │
│  │          IM Gateway (统一入口)                    │    │
│  │  POST /api/v1/im/callback/{channel}              │    │
│  │  - 签名验证                                      │    │
│  │  - 消息解密                                      │    │
│  │  - 路由分发                                      │    │
│  └──────┬───────────────────────────────────────────┘    │
│         │                                                 │
│         ▼                                                 │
│  ┌─────────────────────────────────────────────────┐    │
│  │          IM Adapter Layer (适配器层)              │    │
│  │  ┌──────────────┐  ┌──────────────┐             │    │
│  │  │ WeComAdapter │  │ DingTalkAdapter│            │    │
│  │  │ 企微 → 内部   │  │ 钉钉 → 内部   │            │    │
│  │  └──────┬───────┘  └──────┬───────┘            │    │
│  │         └─────────┬────────┘                     │    │
│  │                   ▼                              │    │
│  │          UnifiedMessage (统一消息)               │    │
│  └───────────────────────┬───────────────────────────┘    │
│                          │                                 │
│                          ▼                                 │
│  ┌─────────────────────────────────────────────────┐    │
│  │          内部网关 (Agent Platform)                │    │
│  │  IntentRecognitionChain → Agent → Response       │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### 2.2 分层职责

| 层 | 职责 | 关键组件 |
|------|------|------|
| **IM Gateway** | 统一入口、签名验证、消息解密 | `ImCallbackController` |
| **Adapter Layer** | 消息格式转换、渠道适配 | `WeComAdapter` / `DingTalkAdapter` |
| **Agent Platform** | 意图识别、对话管理、工具调用 | 现有 P0-P4 全部能力 |

---

## 三、统一消息格式

### 3.1 UnifiedMessage 模型

```java
// domain/im/entity/UnifiedMessage.java
package com.example.agent.domain.im.entity;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class UnifiedMessage {
    /** 内部消息 ID */
    private String messageId;

    /** 来源渠道 */
    private ChannelType channel;

    /** 渠道侧用户 ID */
    private String channelUserId;

    /** 渠道侧会话 ID（群聊时为群 ID） */
    private String channelConversationId;

    /** 消息类型 */
    private MessageContentType contentType;

    /** 文本内容 */
    private String text;

    /** 图片/文件 URL */
    private String mediaUrl;

    /** 媒体类型 */
    private String mediaType;

    /** 扩展属性（渠道特有字段） */
    private Map<String, String> extras;

    /** 消息时间戳 */
    private Instant timestamp;
}
```

### 3.2 枚举定义

```java
// ChannelType.java
public enum ChannelType {
    WECOM("企业微信"),
    DINGTALK("钉钉"),
    FEISHU("飞书"),     // 预留
    WEB("Web聊天"),      // P5 Web 端
    ;

    private final String displayName;
}

// MessageContentType.java
public enum MessageContentType {
    TEXT,
    IMAGE,
    FILE,
    VOICE,
    CARD,        // 卡片消息（审批）
    EVENT,       // 事件（关注/取消关注/进入应用）
    ;
}
```

---

## 四、适配器接口

### 4.1 接口定义

```java
// domain/im/service/ImAdapter.java
package com.example.agent.domain.im.service;

import com.example.agent.domain.im.entity.UnifiedMessage;

/**
 * IM 适配器接口 — 各渠道实现此接口完成消息转换
 * 
 * 设计模式: Strategy + Adapter
 */
public interface ImAdapter {

    /**
     * 支持的渠道类型
     */
    ChannelType supportedChannel();

    /**
     * 将 IM 平台原始消息转为内部统一格式
     *
     * @param raw 平台原始消息（JSON String / XML）
     * @return 统一消息对象
     */
    UnifiedMessage convert(String raw);

    /**
     * 将内部回复转为 IM 平台格式并发送
     *
     * @param reply 内部统一回复
     * @param channelUserId 渠道侧用户 ID
     */
    void sendReply(UnifiedMessage reply, String channelUserId);

    /**
     * 验证回调签名
     *
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param body 消息体
     * @return true=验证通过
     */
    boolean verifySignature(String signature, String timestamp, String nonce, String body);

    /**
     * 解密消息（企微 AES 解密 / 钉钉无需解密）
     *
     * @param encrypted 加密消息
     * @return 解密后的明文
     */
    String decrypt(String encrypted);
}
```

### 4.2 适配器注册中心

```java
// application/im/ImAdapterRegistry.java
package com.example.agent.application.im;

import com.example.agent.domain.im.service.ImAdapter;
import com.example.agent.domain.im.entity.ChannelType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 适配器注册中心 — 自动发现并注册所有 ImAdapter 实现
 * 
 * 设计模式: Mediator + Observer (InitializingBean 自动注册)
 */
@Component
public class ImAdapterRegistry implements InitializingBean {

    private final Map<ChannelType, ImAdapter> adapters = new ConcurrentHashMap<>();

    private final List<ImAdapter> adapterList;

    public ImAdapterRegistry(List<ImAdapter> adapterList) {
        this.adapterList = adapterList;
    }

    @Override
    public void afterPropertiesSet() {
        for (ImAdapter adapter : adapterList) {
            adapters.put(adapter.supportedChannel(), adapter);
            log.info("[IM] 注册适配器: {} → {}", 
                adapter.supportedChannel(), adapter.getClass().getSimpleName());
        }
    }

    /**
     * 根据渠道获取适配器
     */
    public ImAdapter getAdapter(ChannelType channel) {
        ImAdapter adapter = adapters.get(channel);
        if (adapter == null) {
            throw new IllegalArgumentException("不支持的渠道: " + channel);
        }
        return adapter;
    }

    /**
     * 获取所有已注册的渠道
     */
    public Set<ChannelType> getSupportedChannels() {
        return Collections.unmodifiableSet(adapters.keySet());
    }
}
```

---

## 五、渠道实现

### 5.1 企业微信适配器

```java
// infrastructure/im/WeComAdapter.java
package com.example.agent.infrastructure.im;

import com.example.agent.domain.im.entity.*;
import com.example.agent.domain.im.service.ImAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Component
public class WeComAdapter implements ImAdapter {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${im.wecom.corp-id}")
    private String corpId;

    @Value("${im.wecom.token}")
    private String token;

    @Value("${im.wecom.encoding-aes-key}")
    private String encodingAesKey;

    @Value("${im.wecom.agent-id}")
    private String agentId;

    @Value("${im.wecom.secret}")
    private String secret;

    @Override
    public ChannelType supportedChannel() {
        return ChannelType.WECOM;
    }

    @Override
    public UnifiedMessage convert(String raw) {
        try {
            JsonNode root = objectMapper.readTree(raw);
            JsonNode msg = root.path("Msg");

            String msgType = msg.path("MsgType").asText();
            MessageContentType contentType = mapWeComType(msgType);

            UnifiedMessage.UnifiedMessageBuilder builder = UnifiedMessage.builder()
                .channel(ChannelType.WECOM)
                .channelUserId(msg.path("FromUserName").asText())
                .timestamp(Instant.ofEpochSecond(msg.path("CreateTime").asLong()));

            switch (contentType) {
                case TEXT:
                    builder.contentType(MessageContentType.TEXT)
                           .text(msg.path("Content").asText());
                    break;
                case IMAGE:
                    builder.contentType(MessageContentType.IMAGE)
                           .mediaUrl(msg.path("PicUrl").asText());
                    break;
                case EVENT:
                    builder.contentType(MessageContentType.EVENT)
                           .text(msg.path("Event").asText());
                    break;
            }

            return builder.build();
        } catch (Exception e) {
            log.error("[WeCom] 消息转换失败: {}", e.getMessage(), e);
            throw new RuntimeException("企微消息转换失败", e);
        }
    }

    @Override
    public void sendReply(UnifiedMessage reply, String channelUserId) {
        // 先获取 access_token
        String accessToken = getAccessToken();

        Map<String, Object> body = new HashMap<>();
        body.put("touser", channelUserId);
        body.put("msgtype", "text");
        body.put("agentid", agentId);

        Map<String, String> text = new HashMap<>();
        text.put("content", reply.getText());
        body.put("text", text);

        restClient.post()
            .uri("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={token}", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .toBodilessEntity();

        log.info("[WeCom] 消息已发送: userId={}, content={}", channelUserId, reply.getText());
    }

    @Override
    public boolean verifySignature(String signature, String timestamp, String nonce, String body) {
        try {
            String[] arr = {token, timestamp, nonce, body};
            Arrays.sort(arr);
            String str = String.join("", arr);
            String sha1 = sha1(str);
            return sha1.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("[WeCom] 签名验证失败", e);
            return false;
        }
    }

    @Override
    public String decrypt(String encrypted) {
        try {
            byte[] aesKey = Base64.getDecoder().decode(encodingAesKey + "=");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            // 去除 PKCS#7 填充
            int pad = decrypted[decrypted.length - 1] & 0xFF;
            String plain = new String(decrypted, 0, decrypted.length - pad, StandardCharsets.UTF_8);

            // 解析: random(16) + msgLen(4) + msg + corpId
            return plain.substring(20, 20 + ByteBuffer.wrap(
                plain.substring(16, 20).getBytes(StandardCharsets.UTF_8)).getInt());
        } catch (Exception e) {
            log.error("[WeCom] 解密失败", e);
            throw new RuntimeException("企微消息解密失败", e);
        }
    }

    /**
     * 获取企微 access_token（生产环境应缓存 + 定时刷新）
     */
    private String getAccessToken() {
        // TODO: 实现 token 缓存（Redis TTL 7200s）
        var response = restClient.get()
            .uri("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpId}&corpsecret={secret}",
                corpId, secret)
            .retrieve()
            .body(Map.class);
        return (String) response.get("access_token");
    }

    private MessageContentType mapWeComType(String wecomType) {
        return switch (wecomType) {
            case "text" -> MessageContentType.TEXT;
            case "image" -> MessageContentType.IMAGE;
            case "voice" -> MessageContentType.VOICE;
            case "file" -> MessageContentType.FILE;
            case "event" -> MessageContentType.EVENT;
            default -> MessageContentType.TEXT;
        };
    }

    private String sha1(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
```

### 5.2 钉钉适配器

```java
// infrastructure/im/DingTalkAdapter.java
package com.example.agent.infrastructure.im;

import com.example.agent.domain.im.entity.*;
import com.example.agent.domain.im.service.ImAdapter;
// ... similar imports

@Slf4j
@Component
public class DingTalkAdapter implements ImAdapter {

    @Value("${im.dingtalk.app-key}")
    private String appKey;

    @Value("${im.dingtalk.app-secret}")
    private String appSecret;

    @Override
    public ChannelType supportedChannel() {
        return ChannelType.DINGTALK;
    }

    @Override
    public UnifiedMessage convert(String raw) {
        JsonNode root = objectMapper.readTree(raw);

        return UnifiedMessage.builder()
            .channel(ChannelType.DINGTALK)
            .channelUserId(root.path("senderId").asText())
            .channelConversationId(root.path("conversationId").asText())
            .contentType(MessageContentType.TEXT)
            .text(root.path("text").path("content").asText())
            .timestamp(Instant.ofEpochMilli(root.path("createAt").asLong()))
            .build();
    }

    @Override
    public void sendReply(UnifiedMessage reply, String channelUserId) {
        String accessToken = getAccessToken();

        Map<String, Object> body = Map.of(
            "robotCode", appKey,
            "userIds", List.of(channelUserId),
            "msgKey", "sampleText",
            "msgParam", Map.of("content", reply.getText())
        );

        restClient.post()
            .uri("https://api.dingtalk.com/v1.0/robot/oToMessages/batchSend")
            .header("x-acs-dingtalk-access-token", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .toBodilessEntity();

        log.info("[DingTalk] 消息已发送: userId={}", channelUserId);
    }

    @Override
    public boolean verifySignature(String signature, String timestamp, String nonce, String body) {
        // 钉钉使用 HmacSHA256 签名验证
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal((timestamp + "\n" + appSecret).getBytes());
            String expected = Base64.getEncoder().encodeToString(signData);
            return expected.equals(signature);
        } catch (Exception e) {
            log.error("[DingTalk] 签名验证失败", e);
            return false;
        }
    }

    @Override
    public String decrypt(String encrypted) {
        // 钉钉消息不加密
        return encrypted;
    }

    private String getAccessToken() {
        // TODO: 实现 token 缓存
        // POST https://api.dingtalk.com/v1.0/oauth2/accessToken
        // body: { "appKey": "...", "appSecret": "..." }
        return "...";
    }
}
```

---

## 六、统一回调入口

### 6.1 IM 回调 Controller

```java
// interfaces/rest/ImCallbackController.java
package com.example.agent.interfaces.rest;

import com.example.agent.application.im.ImAdapterRegistry;
import com.example.agent.application.im.ImMessageApplicationService;
import com.example.agent.domain.im.entity.ChannelType;
import com.example.agent.domain.im.entity.UnifiedMessage;
import com.example.agent.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/im")
@RequiredArgsConstructor
@Tag(name = "IM 接入", description = "企业微信/钉钉回调接口")
public class ImCallbackController {

    private final ImAdapterRegistry adapterRegistry;
    private final ImMessageApplicationService imMessageService;

    /**
     * 统一回调入口
     * POST /api/v1/im/callback/{channel}
     *
     * channel: wecom | dingtalk
     */
    @PostMapping("/callback/{channel}")
    @Operation(summary = "IM 消息回调")
    public String handleCallback(
            @PathVariable String channel,
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String body) {

        ChannelType channelType = ChannelType.valueOf(channel.toUpperCase());
        var adapter = adapterRegistry.getAdapter(channelType);

        // 1. 签名验证
        if (!adapter.verifySignature(signature, timestamp, nonce, body)) {
            log.warn("[IM] 签名验证失败: channel={}", channel);
            return "fail";
        }

        // 2. 消息解密（企微需要 AES 解密）
        String plainText = adapter.decrypt(body);

        // 3. 转换为统一消息
        UnifiedMessage message = adapter.convert(plainText);

        // 4. 异步处理（快速响应 IM 服务器）
        imMessageService.handleMessageAsync(message);

        // 5. 返回成功（IM 平台要求在 5s 内响应）
        return "success";
    }

    /**
     * URL 验证（企微首次配置回调 URL 时使用）
     */
    @GetMapping("/callback/{channel}")
    @Operation(summary = "IM 回调 URL 验证")
    public String verifyUrl(
            @PathVariable String channel,
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        ChannelType channelType = ChannelType.valueOf(channel.toUpperCase());
        var adapter = adapterRegistry.getAdapter(channelType);

        if (adapter.verifySignature(signature, timestamp, nonce, echostr)) {
            return adapter.decrypt(echostr);
        }
        return "fail";
    }
}
```

### 6.2 IM 消息应用服务

```java
// application/im/ImMessageApplicationService.java
package com.example.agent.application.im;

import com.example.agent.domain.im.entity.UnifiedMessage;
import com.example.agent.application.conversation.ConversationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImMessageApplicationService {

    private final ConversationApplicationService conversationService;

    /**
     * 异步处理 IM 消息 — 避免阻塞 IM 服务器回调
     */
    @Async("imMessageExecutor")
    public void handleMessageAsync(UnifiedMessage message) {
        log.info("[IM] 收到消息: channel={}, userId={}, content={}",
            message.getChannel(), message.getChannelUserId(), message.getText());

        try {
            // 1. 根据 channelUserId 查找或创建内部用户
            String internalUserId = resolveInternalUser(message);

            // 2. 查找或创建会话
            String conversationId = conversationService.findOrCreateConversation(
                internalUserId, message.getChannelConversationId());

            // 3. 走正常对话流程（意图识别 → Agent 处理 → 回复）
            String reply = conversationService.processMessage(
                conversationId, message.getText());

            // 4. 通过适配器发回 IM 平台
            var adapter = adapterRegistry.getAdapter(message.getChannel());
            UnifiedMessage replyMsg = UnifiedMessage.builder()
                .channel(message.getChannel())
                .text(reply)
                .build();
            adapter.sendReply(replyMsg, message.getChannelUserId());

        } catch (Exception e) {
            log.error("[IM] 消息处理失败: {}", e.getMessage(), e);
        }
    }

    private String resolveInternalUser(UnifiedMessage message) {
        // 根据 channel + channelUserId 查找内部用户
        // 若不存在则自动创建（绑定 IM 账号）
        return "user_internal_id";
    }
}
```

---

## 七、配置项

### 7.1 application.yml

```yaml
# IM 接入配置
im:
  # 企业微信
  wecom:
    corp-id: ${WECOM_CORP_ID:}
    agent-id: ${WECOM_AGENT_ID:}
    secret: ${WECOM_SECRET:}
    token: ${WECOM_TOKEN:}
    encoding-aes-key: ${WECOM_ENCODING_AES_KEY:}
    enabled: false  # 默认关闭，部署时按需开启

  # 钉钉
  dingtalk:
    app-key: ${DINGTALK_APP_KEY:}
    app-secret: ${DINGTALK_APP_SECRET:}
    enabled: false

  # 飞书（预留）
  feishu:
    app-id: ${FEISHU_APP_ID:}
    app-secret: ${FEISHU_APP_SECRET:}
    enabled: false
```

### 7.2 前端配置

```json
{
  "im": {
    "enabledChannels": ["web"],
    "wecom": {
      "enabled": false,
      "qrCodeUrl": "https://work.weixin.qq.com/...",
      "appName": "Agent Platform 助手"
    },
    "dingtalk": {
      "enabled": false,
      "appName": "Agent Platform 助手"
    }
  }
}
```

---

## 八、支持的消息类型

| 消息类型 | 企微 | 钉钉 | Web |
|:--|:--:|:--:|:--:|
| **文本消息** | ✅ | ✅ | ✅ |
| **图片消息** | ✅ | ✅ | ✅ |
| **文件消息** | ✅ | ✅ | 🔜 |
| **语音消息** | ✅ | ❌ | ❌ |
| **审批卡片** | 🔜 | 🔜 | ✅ |
| **Markdown** | ✅ | ✅ | ✅ |
| **事件通知** | ✅ | ✅ | N/A |

---

## 九、安全考虑

1. **签名验证**: 每次回调必须验证签名，防止伪造请求
2. **消息加密**: 企微 AES-256-CBC 加密传输，钉钉 HTTPS 传输
3. **Token 安全**: access_token 通过 Redis 缓存 + 定时刷新，不落盘
4. **限流保护**: 回调接口使用 Sentinel 限流，防止恶意刷量
5. **日志脱敏**: IM 消息内容可能含敏感信息，日志输出时脱敏

---

## 十、开发路线

| 阶段 | 内容 | 优先级 | 工作量 |
|:--:|------|:--:|:--:|
| Phase 1 | 企微适配器 + 回调对接 | P2 | 3 天 |
| Phase 2 | 钉钉适配器 + 回调对接 | P2 | 2 天 |
| Phase 3 | 审批卡片通过 IM 推送 | P3 | 2 天 |
| Phase 4 | 飞书适配器（预留） | P4 | 2 天 |

---

> 📋 关联文档: [[前端架构设计方案]] | [[Web聊天界面技术方案]]  
> 📐 P5 子方案: `docs/P5-交互端/04-IM接入适配.md`
