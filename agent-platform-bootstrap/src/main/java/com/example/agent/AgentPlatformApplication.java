package com.example.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Agent Platform - Enterprise AI Agent Platform
 * <p>
 * DDD Architecture:
 * <ul>
 *   <li><b>domain</b> — Aggregates, Entities, Value Objects, Domain Services, Repository Interfaces</li>
 *   <li><b>application</b> — Application Services, Use Cases, DTOs, Event Handlers</li>
 *   <li><b>infrastructure</b> — Repository Impls, Persistence, MCP, Security, Config</li>
 *   <li><b>interfaces</b> — REST Controllers, WebSocket, Facades</li>
 *   <li><b>common</b> — Shared Kernel: Base Classes, Utils, Exceptions, Aspects</li>
 * </ul>
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = "com.example.agent")
@ConfigurationPropertiesScan(basePackages = "com.example.agent")
public class AgentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentPlatformApplication.class, args);
    }
}
