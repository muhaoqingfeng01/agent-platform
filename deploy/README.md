# 运维部署清单

本目录给出单机 Docker 与 K8s 的最小可运行编排，对应架构缺口 D 类。

## Docker 单机

```bash
docker compose -f deploy/docker-compose.yml up -d
```

包含：MySQL、Redis、Milvus、MinIO、Prometheus、Grafana、Jaeger、OTel Collector。

应用本身仍在宿主机以 `mvn spring-boot:run -pl agent-platform-bootstrap` 启动，Prometheus 通过 `host.docker.internal:8080` 抓取 `/actuator/prometheus`。

Grafana 导入 [grafana/agent-platform-dashboard.json](grafana/agent-platform-dashboard.json)。

## Kubernetes

```bash
kubectl apply -f deploy/k8s/service.yaml
kubectl apply -f deploy/k8s/deployment.yaml
```

部署前需创建 Secret `agent-platform-secret`，包含 `mysql-password` 与 `deepseek-api-key`。

## MinIO

Compose 中的 `milvus-minio` 同时可作为知识库对象存储。将应用的 `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` 指向 `localhost:9000`。
