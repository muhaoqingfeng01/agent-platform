package com.example.agent.infrastructure.rag;

import com.example.agent.domain.knowledge.entity.Document;
import com.example.agent.domain.knowledge.service.TextExtractor;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Apache Tika 文本提取器 — 从 MinIO 读取文件并解析纯文本.
 * <p>
 * 支持格式: PDF, DOCX, TXT, MD, HTML, CSV 等.
 *
 * @author Agent Platform Team
 * @since 1.3.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TikaTextExtractor implements TextExtractor {

    private final MinioClient minioClient;
    private final Tika tika = new Tika();

    // TODO: 若 Tika 解析大文件 OOM，设置 tika.setMaxStringLength(10_000_000) 限制 10MB

    @Override
    public String extractText(Document doc) throws Exception {
        log.info("[Tika] 开始解析: docId={}, fileType={}, filename={}",
                doc.getDocumentId(), doc.getFileType(), doc.getFilename());

        // TODO: 确认 MinIO bucket 名与配置一致
        String bucket = "knowledge-docs";
        String objectPath = doc.getMinioPath();

        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectPath).build())) {

            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_TYPE, resolveMimeType(doc.getFileType()));
            // TODO: Tika 2.x 中 RESOURCE_NAME_KEY 常量路径可能变化，此处注入文件名供解析器参考
            metadata.set("resourceName", doc.getFilename());

            String text = tika.parseToString(is, metadata);

            log.info("[Tika] 解析完成: docId={}, textLen={}, fileSize={}",
                    doc.getDocumentId(), text != null ? text.length() : 0, doc.getFileSize());
            return text != null ? text : "";
        }
    }

    private String resolveMimeType(String fileType) {
        if (fileType == null) return "application/octet-stream";
        return switch (fileType.toUpperCase()) {
            case "PDF" -> "application/pdf";
            case "DOCX" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "TXT" -> "text/plain";
            case "MD" -> "text/markdown";
            case "HTML" -> "text/html";
            case "CSV" -> "text/csv";
            default -> "application/octet-stream";
        };
    }
}
