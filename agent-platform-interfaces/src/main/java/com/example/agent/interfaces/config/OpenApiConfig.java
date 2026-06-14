package com.example.agent.interfaces.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SpringDoc OpenAPI (Swagger) 配置
 * <p>
 * 访问地址:
 * <ul>
 *   <li>Swagger UI: <a href="http://localhost:8080/swagger-ui.html">/swagger-ui.html</a></li>
 *   <li>OpenAPI JSON: <a href="http://localhost:8080/v3/api-docs">/v3/api-docs</a></li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI agentPlatformOpenAPI() {
        return new OpenAPI()
                // --- 基本信息 ---
                .info(new Info()
                        .title("Agent Platform API")
                        .description("""
                                ## 企业级 Agent 平台 REST API

                                ### 模块说明
                                - **Agent 管理** — Agent 配置、启停、版本管理
                                - **对话管理** — 会话创建、消息收发、流式响应
                                - **知识库** — 文档上传、知识检索、命中反馈
                                - **工具平台** — MCP 工具注册、调用、审批
                                - **提示词管理** — 模板编辑、版本发布、回滚
                                - **任务引擎** — DAG 任务规划、执行追踪
                                - **租户管理** — 租户、用户、角色、权限
                                - **安全审计** — 敏感词、安全事件、审计日志
                                - **效果评估** — 评测数据集、评估执行、优化工单

                                ### 认证方式
                                所有 API 通过 `Authorization: Bearer {token}` 鉴权（Sa-Token）。
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Agent Platform Team")
                                .email("dev@agent-platform.local"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://agent-platform.local/license")))
                // --- 服务器列表 ---
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("本地开发"),
                        new Server().url("http://dev.agent-platform.local").description("开发环境"),
                        new Server().url("https://api.agent-platform.local").description("生产环境")
                ))
                // --- Sa-Token Bearer 鉴权 ---
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .name("Bearer")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Sa-Token")
                                .description("输入 Sa-Token（不含 'Bearer ' 前缀）")))
                // --- API 分组标签 ---
                .tags(List.of(
                        new Tag().name("Agent 管理").description("Agent 配置、版本、启停"),
                        new Tag().name("对话管理").description("会话、消息、流式响应"),
                        new Tag().name("知识库").description("文档上传、检索、命中反馈"),
                        new Tag().name("工具平台").description("MCP 工具注册、调用、适配"),
                        new Tag().name("提示词管理").description("模板 CRUD、版本发布"),
                        new Tag().name("任务引擎").description("DAG 任务规划与执行"),
                        new Tag().name("租户管理").description("租户、用户、角色、权限（RBAC）"),
                        new Tag().name("安全审计").description("敏感词、安全事件、审计日志"),
                        new Tag().name("效果评估").description("评测数据集、评估执行、优化工单"),
                        new Tag().name("审批管理").description("高风险工具审批工单"),
                        new Tag().name("健康检查").description("Actuator 健康探针"),
                        new Tag().name("意图管理").description("意图定义、分类规则")
                ));
    }
}
