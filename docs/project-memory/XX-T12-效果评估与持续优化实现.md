# T12 效果评估与持续优化实现

> **日期**: 2026-06-18 | **分支**: master | **触发**: 代码生成

## 做了什么

### 模块 1: 离线评估与 LLM-as-Judge
- EvaluationDataset CRUD（含软删除、样本管理、批量导入）
- EvaluationRun 评测执行：遍历样本→获取Agent回答→LLM-as-Judge 4维度评分
- 3 个 Controller + 8 个端点

### 模块 2: BadCase 闭环与优化工单
- OptimizationTicket 状态机（OPEN→ANALYZING→IN_PROGRESS→RESOLVED→CLOSED）
- MessageFeedbackEvent → BadCaseAutoTicketService LLM 自动分析问题类型
- 反馈统计 API

## 关键决策

1. **LLM-as-Judge 4 维度**: 准确性/完整性/相关性/幻觉检测，加权总分 0.4a+0.3c+0.2r+0.1h
2. **Spring Event 解耦**: 点踩→发布 MessageFeedbackEvent→BadCaseAutoTicketService 异步监听
3. **工单状态机枚举**: `TicketStatus.canTransitionTo()` 封装合法流转规则

## 文件清单

### 新建 (~30 files)
- Domain: EvaluationDataset, EvaluationDatasetItem, EvaluationRun, OptimizationTicket, TicketStatus + 3 Repository 接口
- Infrastructure: 4 PO + 3 Mapper + 3 XML + 3 RepositoryImpl
- Application: EvaluationDatasetService, EvaluationRunService, OptimizationTicketService, BadCaseAutoTicketService, MessageFeedbackEvent + 10 DTO
- Interfaces: EvaluationDatasetController, EvaluationRunController, OptimizationTicketController

### 修改 (1 file)
- MessageController: 新增 ApplicationEventPublisher, feedback 端点发布 MessageFeedbackEvent
