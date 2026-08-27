# D7 Docker 单机编排实现方案

> **类别**: D | **优先级**: P2  
> **现状**: 无 docker-compose

## 1. 场景

一台机器起 MySQL、Redis、Milvus、MinIO、Nacos、App，开发/演示可跑通 RAG 上传。

## 2. 设计要点

`deploy/docker-compose.yml`：依赖健康检查后再起 app。密钥 `.env` 不入库。App 镜像多阶段构建 JDK17。

服务：mysql:8、redis、milvus 独立 etcd/minio 按其官方 compose 精简、minio 业务桶、nacos standalone 可选、app。

## 3. 实现步骤

1. `Dockerfile` 复制 bootstrap jar。  
2. compose + `docs/database` 初始化脚本挂载。  
3. README 一键：`docker compose up -d`。  
4. 端口：8080/3306/6379/19530/9000。

## 4. 验收

空机器 compose 后 Swagger 可登录种子 admin。

## 5. 风险

Milvus 内存；开发机 16GB 建议关掉 Langfuse/Presidio。
