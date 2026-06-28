package com.example.agent.common.lock;

import lombok.Getter;

@Getter
public enum LockEnum {


    CONVERSATION_STATUS_TRANSITION("conversation", "status_transition_%s"),
    ;
    /**
     * 锁分类
     */
    private final String category;

    /**
     * 锁模板
     */
    private final String template;


    public LockKey getLockKey() {
        return new LockKey(this.category,this.template);
    }

    public static LockKey getLockKey(LockEnum lockEnum , Object ... params ) {
        return new LockKey(lockEnum.getCategory(), String.format(lockEnum.getTemplate(), params));
    }

    LockEnum (String category, String template) {
        this.category = category;
        this.template = template;
    }
}
