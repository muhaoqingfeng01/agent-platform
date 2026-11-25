# D11 Presidio Docker 交付实现方案

> **类别**: D | **优先级**: P3  
> **依赖**: C3；镜像 `mcr.microsoft.com/presidio-analyzer`

## 1. 场景

NER PII 服务与 App 同网。

## 2. 设计要点

compose 只起 analyzer（一期 Java 自己脱敏，可不启 anonymizer）。中文模型按 Presidio 文档安装。资源限制 CPU。App `security.pii.presidio.base-url=http://presidio-analyzer:3000`。

## 3. 步骤

1. compose 服务 + healthcheck。  
2. 网络与 app 同一 network。  
3. 压测超时。  
4. 默认 prod 也可关，合规租户再开。

## 4. 验收

curl `/health`；C3 样例人名被识别。

## 5. 风险

镜像体积与内存，笔记本可关闭。
