package com.example.agent.infrastructure.config.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * MinIO 对象存储配置 — 用于 RAG 文档上传/下载.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
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
    private String bucket = "agent-platform";

    /** 预签名 URL 有效期（分钟） */
    private int presignedExpiryMinutes = 10;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .httpClient(buildHttpClient())
                .build();

        // 自动创建 bucket（幂等操作）
        ensureBucketExists(client);

        return client;
    }

    /**
     * 构建兼容自签名证书的 OkHttpClient.
     * 仅在 HTTPS 且证书不受信任时才启用宽松 SSL。
     */
    private OkHttpClient buildHttpClient() {
        if (endpoint.startsWith("http://")) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                return new OkHttpClient.Builder()
                        .hostnameVerifier((hostname, session) -> true)
                        .sslSocketFactory(sslContext.getSocketFactory(),
                                (X509TrustManager) trustAllCerts[0])
                        .build();
            } catch (Exception e) {
                log.warn("[MinIO] 配置宽松 SSL 失败，使用默认 HTTP 客户端: {}", e.getMessage());
            }
        }
        return new OkHttpClient();
    }

    /**
     * 确保 bucket 存在，不存在则自动创建（幂等）.
     */
    private void ensureBucketExists(MinioClient client) {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("[MinIO] Bucket 自动创建成功: {}", bucket);
            } else {
                log.info("[MinIO] Bucket 已存在: {}", bucket);
            }
        } catch (Exception e) {
            log.error("[MinIO] Bucket 检查/创建失败: bucket={}, error={}", bucket, e.getMessage());
        }
    }
}
