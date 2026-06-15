package com.example.agent.infrastructure.config.storage;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 对象存储配置 — 用于 RAG 文档上传/下载.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /** MinIO 服务地址 (e.g. http://localhost:9000) */
    private String endpoint = "http://localhost:9000";

    /** Access Key */
    private String accessKey = "minioadmin";

    /** Secret Key */
    private String secretKey = "minioadmin";

    /** 默认 Bucket */
    private String bucket = "knowledge-docs";

    /** 预签名 URL 有效期（分钟） */
    private int presignedExpiryMinutes = 10;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
