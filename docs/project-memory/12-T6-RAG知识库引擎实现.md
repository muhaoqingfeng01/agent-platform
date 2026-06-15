# T6 RAG 知识库引擎 — 完整实现

> **日期**: 2026-06-15
> **分支**: master
> **触发**: 代码生成（基于 5 篇技术方案文档实现）

## 做了什么
- **~72 个新文件**，覆盖 DDD 四层全栈，编译通过（7/7 模块 BUILD SUCCESS）

### 1. Flyway 迁移（V1.3.0）
- `t_knowledge_base` 新增 8 字段：default_chunk_strategy, chunk_config_json, index_type, index_params_json, search_strategy, search_params_json, multi_stage_params_json, monitoring_params_json
- `t_document` 新增 6 字段：chunk_strategy, chunk_config_json, search_strategy_override, search_params_override_json, multi_stage_override_json

### 2. Domain 层（23 文件）
- **4 实体**: KnowledgeBase, Document, DocumentChunk, KnowledgeHitRecord
- **4 仓储接口**: KnowledgeBaseRepository, DocumentRepository, DocumentChunkRepository, KnowledgeHitRecordRepository
- **7 值对象**: DocumentStatus, ChunkStrategy, IndexType, MetricType, ConsistencyLevel, SearchStrategy, RerankerType
- **6 领域服务接口/实现**: KnowledgeBaseDomainService, ChunkStrategyService, PrecisionConfigDomainService, TextExtractor, EmbeddingServiceClient, MilvusStoreClient, VectorSearchProvider, FulltextSearchProvider

### 3. Application 层（18 文件）
- **6 DTO**: KnowledgeBaseDTO, DocumentDTO, SearchResultDTO, HitRecordDTO, PrecisionConfigDTO, ChunkConfigDTO
- **4 ApplicationService**: KnowledgeBaseApplicationService, DocumentApplicationService, HybridSearchApplicationService, PrecisionConfigApplicationService
- **7 策略文件**: ChunkStrategyFactory + 6 种切片策略（ParagraphSlidingWindow, FixedSize, MarkdownHeaderAware, SentenceLevel, RecursiveCharSplit, Semantic）
- **1 管线编排器**: DocumentPipelineOrchestrator

### 4. Infrastructure 层（18 文件）
- **4 PO**: KnowledgeBasePO, DocumentPO, DocumentChunkPO, KnowledgeHitRecordPO
- **4 Mapper 接口** + **4 Mapper XML**
- **4 RepositoryImpl**
- **2 配置类**: MilvusConfig, RagPrecisionProperties
- **4 Infra 实现**: MilvusCollectionManager, TikaTextExtractor, EmbeddingServiceClientImpl, HybridSearchProvidersImpl

### 5. Interfaces 层（8 文件）
- **3 Controller**: KnowledgeBaseController, DocumentController, KnowledgeSearchController
- **5 Request DTO**: CreateKnowledgeBaseRequest, UpdateKnowledgeBaseRequest, UpdateChunkConfigRequest, SetPrecisionConfigRequest, SetPrecisionOverrideRequest

## 关键设计决策
1. **DDD 端口适配器**: 将 TextExtractor/EmbeddingServiceClient/MilvusStoreClient 等定义为 Domain 层端口接口，Infrastructure 层实现，消除 Infrastructure→Application 的非法依赖
2. **Strategy 模式的切片策略**: 6 种策略各自独立实现 ChunkStrategyService，通过 ChunkStrategyFactory 统一获取
3. **四级配置覆盖**: 文档级 > 知识库级 > 策略预设 > 系统默认，PrecisionConfigDomainService 负责深度合并
4. **RRF 融合算法**: HybridSearchApplicationService 实现加权 RRF，支持向量+关键词独立权重配置
5. **Infrastructure 层使用 Stub**: Milvus/Tika/Embedding 目前为 Stub 实现，生产环境替换为真实 SDK

## API 端点（总计 ~20+）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/knowledge-bases` | 创建知识库 |
| GET | `/api/v1/knowledge-bases` | 知识库列表 |
| GET | `/api/v1/knowledge-bases/{id}` | 知识库详情 |
| PUT | `/api/v1/knowledge-bases/{id}` | 更新知识库 |
| PUT | `/api/v1/knowledge-bases/{id}/chunk-config` | 设置切片策略 |
| PUT | `/api/v1/knowledge-bases/{id}/precision-config` | 设置精度参数 |
| GET | `/api/v1/knowledge-bases/{id}/precision-config/resolved` | 查看合并配置 |
| DELETE | `/api/v1/knowledge-bases/{id}` | 删除知识库 |
| POST | `/api/v1/knowledge-bases/{kbId}/documents` | 上传文档 |
| GET | `/api/v1/documents` | 文档列表 |
| GET | `/api/v1/documents/{id}` | 文档详情 |
| GET | `/api/v1/documents/{id}/status` | 文档处理状态 |
| GET | `/api/v1/documents/{id}/chunks` | 文档切片列表 |
| PUT | `/api/v1/documents/{id}/precision-override` | 文档级精度覆盖 |
| DELETE | `/api/v1/documents/{id}` | 删除文档 |
| POST | `/api/v1/knowledge/search` | 混合检索 |
| GET | `/api/v1/knowledge/hits` | 命中记录 |
| POST | `/api/v1/knowledge/hits/{id}/feedback` | 人工标注 |
| GET | `/api/v1/knowledge/precision-strategies` | 策略预设列表 |

## 下一步
- P2-T7: MCP 平台实现
- 或 P3: 安全围栏
