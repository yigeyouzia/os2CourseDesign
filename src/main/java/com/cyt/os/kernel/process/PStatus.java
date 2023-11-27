package com.cyt.os.kernel.process;

public enum PStatus {
    /** 执行状态 */
    RUNNING,
    /** 活动就绪状态 */
    ACTIVE_READY,
    /** 静止就绪状态 */
    STATIC_READY,
    /** 活动阻塞状态 */
    ACTIVE_BLOCK,
    /** 静止阻塞状态 */
    STATIC_BLOCK,
    /** 创建状态 */
    CREATE,
    /** 终止状态 */
    DESTROY
}
