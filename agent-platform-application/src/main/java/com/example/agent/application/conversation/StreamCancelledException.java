package com.example.agent.application.conversation;

/**
 * 用户主动取消 SSE 流式生成时抛出，用于中断 Reactor 订阅链.
 *
 * @author Agent Platform Team
 * @since 1.8.0
 */
public class StreamCancelledException extends RuntimeException {

    private final String streamId;

    public StreamCancelledException(String streamId) {
        super("stream cancelled: " + streamId);
        this.streamId = streamId;
    }

    public String getStreamId() {
        return streamId;
    }
}
