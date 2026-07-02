package com.example.agent.domain.task.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务执行结果 — TaskHandler 返回值.
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResult {

    /** 是否执行成功 */
    private boolean success;

    /** 结果 JSON（写入 t_async_task.result_json） */
    private String resultJson;

    /** 错误信息（success=false 时填写） */
    private String errorMessage;

    public static TaskResult ok() {
        return TaskResult.builder().success(true).build();
    }

    public static TaskResult ok(String resultJson) {
        return TaskResult.builder().success(true).resultJson(resultJson).build();
    }

    public static TaskResult fail(String errorMessage) {
        return TaskResult.builder().success(false).errorMessage(errorMessage).build();
    }
}
