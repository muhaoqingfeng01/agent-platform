# D10 MinIO 环境交付实现方案

> **类别**: D | **优先级**: P2  
> **现有**: MinIO Java Client；无标准环境

## 1. 场景

知识库上传/下载真实对象；无 MinIO 则上传失败。

## 2. 设计要点

Compose 服务 minio + `mc` 初始化 bucket（与 `application-dev` bucket 名一致）。AK/SK 环境变量。生产用独立磁盘与生命周期策略。

## 3. 步骤

1. D7 加入 minio:9000/console 9001。  
2. 启动脚本 `mc mb`。  
3. 健康检查：app 依赖可选（RAG 非必须启动，但 upload API 需）。  
4. 备份策略文档。

## 4. 验收

上传 PDF 后控制台可见 `{knowledgeId}/{documentId}/...`。

## 5. 风险

开发误连生产桶，用独立 bucket 名。
