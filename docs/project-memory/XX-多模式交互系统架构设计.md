# 多模式交互系统架构设计方案

> **日期**: 2026-07-03
> **分支**: master
> **触发**: 架构决策

## 做了什么
- 基于用户提供的"智能体多模式交互系统架构设计方案"（通用版），结合项目现有技术栈和 DDD 架构进行完整更新
- 创建 `docs/智能体多模式交互系统架构设计方案.md`（v2.0）

## 关键决策

1. **技术栈 100% 对齐项目现状**：将原方案中通用的 FastAPI/Python 建议全部替换为项目实际技术栈（Spring Boot 3.3.7 + Spring AI + MyBatis Plus + Sa-Token + Milvus + Nacos + Sentinel 等 20+ 项精确版本）

2. **分层架构映射到 DDD 六模块**：将原方案的通用四层模型精确映射为 interfaces → application → domain ← infrastructure + common + bootstrap

3. **引入 InteractionStrategy 抽象**：对应原方案"策略抽象基类"，设计 Java 接口 `InteractionStrategy<T, R>` + `InteractionStrategyFactory`，对齐项目现有 `TaskHandler<T>` 和 `ChunkStrategyFactory` 模式

4. **请求处理管道 7 步映射**：将原方案概念管道精确映射到现有实现（Sa-Token → Sentinel → 4层过滤链 → 3层识别链 → 策略工厂 → 策略执行 → Presidio 脱敏）

5. **保留全部设计原则**：开闭原则、单一职责、依赖倒置、接口隔离、故障隔离、防御式编程 6 大原则一字未改

6. **新增核心设计资产映射表**：标注哪些组件需新建（InteractionStrategy）、哪些需重构（intent→route dispatch）、哪些已就绪（SecurityFence/SSE/Nacos）

## 踩坑记录
- 项目当前 IntentRecognitionChain 识别意图后仅记录日志，未用于路由分发 — 这是多模式交互重构的核心改造点
- 项目已有 6 个策略注册表（ActionHandlerRegistry / MemoryExtractorRegistry / RerankerRegistry 等），均采用 Spring List<T> + InitializingBean 自动发现模式 — InteractionStrategyFactory 可直接复用此范式
- Langfuse 用 HTTP Ingestion API 直连，不依赖 langfuse-java SDK — 观测方案中的"Langfuse"指此自研集成方式

## 下一步
- 实现 `InteractionStrategy` 接口 + `InteractionStrategyFactory` 工厂
- 重构 `StreamOrchestrationService`，将硬编码管线改为策略调度
- 为每种 IntentCategory（FAQ/TASK/CHITCHAT/MULTI_STEP）实现对应策略
- 前端 P5 同步推进多模式 UI 容器
