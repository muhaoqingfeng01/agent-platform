-- =========================================================
-- V1.6.0: 通用异步任务中心
-- 所有异步耗时操作（文档解析、报表导出、数据导入等）统一管理
-- =========================================================

CREATE TABLE IF NOT EXISTS t_async_task (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id       VARCHAR(64) NOT NULL UNIQUE        COMMENT '业务主键',
    task_type     VARCHAR(64) NOT NULL               COMMENT '任务类型: DOCUMENT_PARSE / REPORT_EXPORT / ...',
    biz_id        VARCHAR(128) NOT NULL              COMMENT '业务主体ID（如documentId），防重+查询',
    tenant_id     BIGINT NOT NULL                    COMMENT '租户ID',
    status        VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED | RUNNING | COMPLETED | FAILED | TIMEOUT',
    payload_json  JSON                               COMMENT '业务参数JSON，透传给TaskHandler',
    result_json   JSON                               COMMENT '执行结果JSON',
    timeout_at    DATETIME NOT NULL                  COMMENT '超时截止时间',
    retry_count   INT DEFAULT 0                      COMMENT '已重试次数',
    max_retries   INT DEFAULT 3                      COMMENT '最大重试次数',
    error_message TEXT                               COMMENT '错误信息',
    started_at    DATETIME                           COMMENT '开始执行时间',
    finished_at   DATETIME                           COMMENT '完成时间',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT DEFAULT 0                  COMMENT '逻辑删除',
    INDEX idx_type_status_timeout (task_type, status, timeout_at),
    INDEX idx_biz_id (biz_id),
    INDEX idx_task_type_biz (task_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用异步任务表';
