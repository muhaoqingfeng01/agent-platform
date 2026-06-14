/**
 * Application Layer — Use Case Orchestration
 * <p>
 * Coordinates domain objects to fulfill use cases. Contains:
 * <ul>
 *   <li><b>Application Services</b> — Stateless services orchestrating domain logic per use case</li>
 *   <li><b>DTOs</b> — Data Transfer Objects for input/output at the application boundary</li>
 *   <li><b>Event Handlers</b> — Handlers reacting to domain events for cross-context operations</li>
 *   <li>{@code orchestration} — Cross-context saga orchestration for long-running agent tasks</li>
 * </ul>
 *
 * <p>
 * <b>Rule:</b> Application services NEVER contain business logic —
 * they delegate to domain services and aggregates.
 * </p>
 *
 * @since 1.0.0
 */
package com.example.agent.application;
