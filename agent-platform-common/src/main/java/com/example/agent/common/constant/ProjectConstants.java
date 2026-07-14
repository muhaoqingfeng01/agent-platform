package com.example.agent.common.constant;

/**
 * 项目全局静态常量 — 不适合动态配置的编译期常数.
 *
 * <p>分类原则:
 * <ul>
 *   <li>放这里: 全环境统一 / 架构级固定约束 / 变更即需重启</li>
 *   <li>放 Nacos: 运行时需调整 / A/B 测试参数</li>
 *   <li>放 YAML: 启动必需 / 环境差异大 / 敏感凭据</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.6.0
 */
public final class ProjectConstants {

    private ProjectConstants() { /* 工具类禁止实例化 */ }

    // ========== 索引构建参数（Collection 创建后不可变） ==========

    public static final class IndexBuild {
        private IndexBuild() {}

        /** 默认索引类型 */
        public static final String DEFAULT_TYPE = "IVF_FLAT";
        /** 默认相似度度量 */
        public static final String DEFAULT_METRIC = "COSINE";
        /** IVF Flat nlist 参数 */
        public static final int NLIST = 128;
        /** HNSW M 参数 */
        public static final int HNSW_M = 16;
        /** HNSW efConstruction 参数 */
        public static final int HNSW_EF_CONSTRUCTION = 200;
        /** DiskANN max_degree 参数 */
        public static final int DISKANN_MAX_DEGREE = 56;
        /** PQ 量化位数 */
        public static final int PQ_NBITS = 8;
        /** PQ 子向量维度 */
        public static final int PQ_M = 768;
        /** DiskANN search_list_size */
        public static final int SEARCH_LIST_SIZE = 100;
    }

    // ========== 安全加密常数 ==========

    public static final class Security {
        private Security() {}

        /** BCrypt 工作因子 — 10≈100ms, 12≈400ms, 14≈1.6s */
        public static final int BCRYPT_STRENGTH = 12;
    }

    // ========== 分页默认值 ==========

    public static final class Page {
        private Page() {}

        /** 默认页码（0-based） */
        public static final int DEFAULT_PAGE_NUM = 0;
        /** 默认每页大小 */
        public static final int DEFAULT_PAGE_SIZE = 20;
        /** 最大每页大小 */
        public static final int MAX_PAGE_SIZE = 100;
    }

    // ========== 线程池拒绝策略 ==========

    public static final class ThreadPool {
        private ThreadPool() {}

        /** 异步任务线程池拒绝策略 — CallerRuns 保证任务不丢失 */
        public static final String ASYNC_TASK_REJECTION = "CallerRunsPolicy";
        /** 审计日志线程池拒绝策略 — DiscardOldest 丢弃最旧日志 */
        public static final String AUDIT_LOG_REJECTION = "DiscardOldestPolicy";
    }

    // ========== 业务规模限制 ==========

    public static final class BusinessLimit {
        private BusinessLimit() {}

        /** 单知识库最大文档数 */
        public static final int MAX_DOCUMENT_COUNT = 1000;
        /** 文件上传大小上限 (bytes) — 50MB */
        public static final long MAX_FILE_SIZE = 50L * 1024 * 1024;
        /** 文档最小分片大小 (bytes) — 5MB */
        public static final long MIN_PART_SIZE = 5L * 1024 * 1024;
    }

    // ========== API 文档元信息 ==========

    public static final class ApiDoc {
        private ApiDoc() {}

        /** 本地开发环境地址 */
        public static final String DEV_SERVER_URL = "http://localhost:8080";
        /** 开发环境地址 */
        public static final String DEV_ENV_URL = "http://dev.agent-platform.local";
        /** 生产环境地址 */
        public static final String PROD_ENV_URL = "https://api.agent-platform.local";
        /** License URL */
        public static final String LICENSE_URL = "https://agent-platform.local/license";
        /** 联系邮箱 */
        public static final String CONTACT_EMAIL = "dev@agent-platform.local";
    }

    // ========== WebSocket ==========

    public static final class WebSocket {
        private WebSocket() {}

        /** 对话 WebSocket 端点 */
        public static final String CONVERSATION_ENDPOINT = "/ws/conversation";
        /** 最大会话数 */
        public static final int MAX_SESSION_COUNT = 10000;
    }
}
