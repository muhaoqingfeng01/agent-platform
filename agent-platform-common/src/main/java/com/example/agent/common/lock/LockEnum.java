package com.example.agent.common.lock;

import lombok.Getter;

@Getter
public enum LockEnum {


    CONVERSATION_STATUS_TRANSITION("conversation", "status_transition_%s"),

    // ========== 文档操作锁 ==========
    /** 文档互斥锁 — 解析/弃用/删除同一文档时互斥，同一时间仅允许一个操作执行 */
    DOCUMENT_MUTEX("document", "doc_mutex_%s"),
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
