package com.example.agent.common.lock;

public interface TaskCallable<R> {

    /**
     * 运行任务
     * @return
     * @throws Throwable
     */
    R call() throws Throwable;
}
