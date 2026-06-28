package com.example.agent.common.helper;


import com.example.agent.common.exception.BusinessException;
import com.example.agent.common.exception.SecurityBlockedException;
import com.example.agent.common.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
public class ResultRespHelper {

    /**
     * 统一处理响应结果
     * @param logPrefix 日志前缀
     * @param req 请求参数
     * @param function 业务逻辑
     * @return
     * @param <Req> 请求参数类型
     * @param <Resp> 响应参数类型
     */
    public static <Req, Resp> Result<Resp> responseInvoke(String logPrefix, Req req, Function<Req, Resp> function) {
        Result<Resp> response = new Result<>();
        long startTimes = System.currentTimeMillis();
        try {
            response.setCode(Result.SUCCESS_CODE);
            response.setData(function.apply(req));
        } catch (SecurityBlockedException e) {
            log.warn("{}权限校验异常，request:{},msg:{}",logPrefix,req,e.getMessage());
            response.setCode(e.getCode());
            response.setMessage(e.getMessage());
            return response;

        } catch (BusinessException e) {
            log.warn("{}业务异常，request:{},msg:{}", logPrefix, req, e.getMessage());
            response.setCode(e.getCode());
            response.setMessage(e.getMessage());
            return response;

        } catch (IllegalArgumentException e) {
            log.warn("{}非法参数，request:{},msg:{}", logPrefix, req, e.getMessage());
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;

        } catch (Exception e) {
            log.error("{}未预期的未知异常，request:{},msg:{}", logPrefix, req, e.getMessage(), e);
            response.setCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTimes;
        response.setTimestamp(executionTime);
        log.info("{}响应成功，耗时:{}ms,request:{},response:{}", logPrefix, executionTime, req, response);
        return response;
    }


    public static <Req, Resp> Result<Resp> responseInvoke(Function<Req, Resp> function) {
        return responseInvoke("", null, function);
    }
}
