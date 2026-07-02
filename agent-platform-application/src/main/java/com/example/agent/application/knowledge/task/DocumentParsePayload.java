package com.example.agent.application.knowledge.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档解析任务参数 — 透传给 DocumentParseTaskHandler.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentParsePayload {
    /** 文档 ID */
    private String documentId;
    /** 知识库 ID */
    private String knowledgeId;
}
