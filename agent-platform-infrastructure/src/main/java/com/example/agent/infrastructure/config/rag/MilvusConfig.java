package com.example.agent.infrastructure.config.rag;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus 向量数据库配置属性.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "agent.rag.milvus")
public class MilvusConfig {

    /** Milvus 服务地址 */
    private String host = "localhost";

    /** Milvus 端口 */
    private int port = 19530;

    /** 连接超时（毫秒） */
    private int connectTimeoutMs = 10000;

    /** 请求超时（毫秒） */
    private int requestTimeoutMs = 30000;

    /** 索引默认类型 */
    private String defaultIndexType = "IVF_FLAT";

    /** 索引默认度量方式 */
    private String defaultMetricType = "COSINE";

    /** 最大连接数 */
    private int maxConnectionNum = 10;
}
