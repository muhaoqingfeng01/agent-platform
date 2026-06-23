package com.example.agent.common.helper;

import cn.hutool.json.JSONUtil;
import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.SecurityBlockedException;
import com.example.agent.common.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 最外层异常拦截处理器
 * <p>
 * 通过多层 catch 精准捕获系统内主动抛出的异常，打印日志，
 * 并将异常信息（状态码 + 消息）透传给前端。
 * 无异常时正常返回数据。
 *
 * <h3>catch 层级与映射规则</h3>
 * <table>
 *   <tr><th>catch 顺序</th><th>日志级别</th><th>前端返回</th></tr>
 *   <tr><td>{@link SecurityBlockedException}</td><td>WARN</td><td>e.getCode() + e.getMessage()</td></tr>
 *   <tr><td>{@link BusinessException}</td><td>WARN</td><td>e.getCode() + e.getMessage()</td></tr>
 *   <tr><td>{@link IllegalArgumentException}</td><td>WARN</td><td>400 + e.getMessage()</td></tr>
 *   <tr><td>{@link Exception}</td><td>ERROR（含完整堆栈）</td><td>500 + "服务器内部错误"</td></tr>
 * </table>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 无上下文
 * ResultCheckHelper.wrap(() -> { ... });
 *
 * // 带上下文字符串 + 对象（日志打印）
 * ResultCheckHelper.wrap(() -> { ... }, "文档上传", file.getOriginalFilename());
 * ResultCheckHelper.wrap(() -> { ... }, "批量解析", Map.of("knowledgeId", kid, "count", ids.size()));
 * }</pre>
 *
 * @author agent-platform
 * @since 2026-06-24
 */
@Slf4j
public final class ResultCheckHelper {

    private ResultCheckHelper() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }


    // ==================== wrap：带上下文（字符串 + 对象） ====================

    /**
     * 包装执行，异常时日志附带上下文字符串和详情对象
     *
     * @param supplier 业务逻辑
     * @param method  上下文描述（如 "文档上传"、"批量解析"）
     * @param request   详情对象（如文件名、请求参数 Map），toString 后打印
     * @param <T>      响应数据类型
     */
    public static <T> Result<T> wrap(Supplier<Result<T>> supplier, String method, Object request) {
        try {
            log.info("[ResultCheck] 执行业务逻辑: method={}, request={}", method, JSONUtil.toJsonStr( request));
            Result<T> result = supplier.get();
            if (result == null) {
                log.warn("[ResultCheck] 业务逻辑返回了 null Result，降级为默认失败响应");
                return Result.fail("操作返回空结果");
            }
            if (!result.isSuccess()) {
                log.warn("[ResultCheck] 业务返回非成功状态: method={}, request={}, code={}, message={}",
                        method, request, result.getCode(), result.getMessage());
            }
            return result;

        } catch (SecurityBlockedException e) {
            log.warn("[ResultCheck] 安全阻断: method={}, request={}, eventType={}, matchedPattern={}, code={}, message={}",
                    method, request, e.getEventType(), e.getMatchedPattern(), e.getCode(), e.getMessage());
            return Result.fail(e.getCode(), e.getMessage());

        } catch (BusinessException e) {
            log.warn("[ResultCheck] 业务异常: method={}, request={}, type={}, code={}, message={}",
                    method, request, e.getClass().getSimpleName(), e.getCode(), e.getMessage());
            return Result.fail(e.getCode(), e.getMessage());

        } catch (IllegalArgumentException e) {
            log.warn("[ResultCheck] 非法参数: method={}, request={}, message={}",
                    method, request, e.getMessage());
            return Result.fail(400, e.getMessage());

        } catch (Exception e) {
            log.error("[ResultCheck] 未预期的未知异常: method={}, request={}, type={}, message={}",
                    method, request, e.getClass().getName(), e.getMessage(), e);
            return Result.fail(500, "服务器内部错误");
        }
    }
}
