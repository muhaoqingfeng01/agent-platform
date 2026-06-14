package com.example.agent.infrastructure.config.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * 对话状态机配置 — State 模式 + Spring StateMachine.
 *
 * @author Agent Platform Team
 * @since 1.0.0
 */
@Configuration
@EnableStateMachineFactory
public class ConversationStateMachineConfig
        extends EnumStateMachineConfigurerAdapter<ConversationState, ConversationEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<ConversationState, ConversationEvent> states)
            throws Exception {
        states.withStates()
                .initial(ConversationState.INIT)
                .states(EnumSet.allOf(ConversationState.class))
                .end(ConversationState.DONE)
                .end(ConversationState.ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ConversationState, ConversationEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                    .source(ConversationState.INIT)
                    .target(ConversationState.AWAIT_PARAM)
                    .event(ConversationEvent.MESSAGE_RECEIVED)
                .and()
                .withExternal()
                    .source(ConversationState.AWAIT_PARAM)
                    .target(ConversationState.EXECUTING)
                    .event(ConversationEvent.PARAMS_COMPLETE)
                .and()
                .withExternal()
                    .source(ConversationState.EXECUTING)
                    .target(ConversationState.DONE)
                    .event(ConversationEvent.COMPLETED)
                .and()
                .withExternal()
                    .source(ConversationState.EXECUTING)
                    .target(ConversationState.CONFIRMATION)
                    .event(ConversationEvent.CONFIRMATION_NEEDED)
                .and()
                .withExternal()
                    .source(ConversationState.CONFIRMATION)
                    .target(ConversationState.EXECUTING)
                    .event(ConversationEvent.USER_CONFIRMED)
                .and()
                .withExternal()
                    .source(ConversationState.CONFIRMATION)
                    .target(ConversationState.DONE)
                    .event(ConversationEvent.USER_REJECTED)
                .and()
                .withExternal()
                    .source(ConversationState.INIT)
                    .target(ConversationState.ERROR)
                    .event(ConversationEvent.ERROR_OCCURRED)
                .and()
                .withExternal()
                    .source(ConversationState.AWAIT_PARAM)
                    .target(ConversationState.ERROR)
                    .event(ConversationEvent.ERROR_OCCURRED)
                .and()
                .withExternal()
                    .source(ConversationState.EXECUTING)
                    .target(ConversationState.ERROR)
                    .event(ConversationEvent.ERROR_OCCURRED);
    }
}
