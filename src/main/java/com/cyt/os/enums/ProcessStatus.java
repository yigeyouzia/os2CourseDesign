package com.cyt.os.enums;

public enum ProcessStatus {
    /** 执行状态 */
    RUNNING,

    /** 活动就绪状态  就绪队列*/
    ACTIVE_READY,
    /** 静止就绪状态 */
    STATIC_READY,

    /** 活动阻塞状态 阻塞队列*/
    ACTIVE_BLOCK,
    /** 静止阻塞状态 */
    STATIC_BLOCK,

    /** 创建状态 */
    CREATE,
    /** 终止状态 */
    DESTROY
}
