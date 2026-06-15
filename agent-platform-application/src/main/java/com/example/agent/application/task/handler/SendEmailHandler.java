package com.example.agent.application.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发送邮件 Handler — Strategy 模式具体实现（高风险）.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class SendEmailHandler implements ActionHandler {

    @Override
    public String getActionType() {
        return "send_email";
    }

    @Override
    public String getDescription() {
        return "发送邮件给指定收件人";
    }

    @Override
    public String getParamsSchema() {
        return "{\"to\": \"收件人邮箱/姓名\", \"subject\": \"邮件主题\", \"body\": \"邮件正文内容\"}";
    }

    @Override
    public boolean isHighRisk() {
        return true;  // 发送邮件需审批确认
    }

    @Override
    public int maxRetries() {
        return 2;  // 邮件发送失败只重试 2 次
    }

    @Override
    public long timeoutMs() {
        return 15_000;  // 15s 超时
    }

    @Override
    public void validateParams(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (!params.containsKey("to") || params.get("to") == null || params.get("to").toString().isBlank()) {
            throw new IllegalArgumentException("缺少必填参数: to（收件人）");
        }
        if (!params.containsKey("subject") || params.get("subject") == null || params.get("subject").toString().isBlank()) {
            throw new IllegalArgumentException("缺少必填参数: subject（邮件主题）");
        }
        if (!params.containsKey("body")) {
            throw new IllegalArgumentException("缺少必填参数: body（邮件正文）");
        }
    }

    @Override
    public Object execute(Map<String, Object> params) throws Exception {
        String to = (String) params.get("to");
        String subject = (String) params.get("subject");
        String body = (String) params.get("body");
        log.info("[SendEmail] 发送邮件: to={}, subject={}", to, subject);

        // TODO: 实际调用邮件服务
        // emailService.send(to, subject, body);

        // 模拟发送成功
        return Map.of("sent", true, "to", to, "subject", subject, "timestamp", System.currentTimeMillis());
    }
}
