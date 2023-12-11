package com.cyt.os.common;

import com.cyt.os.kernel.process.data.PCB;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class Config {

    /**
     * 进程调度算法等待时间
     */
    public static final Long WAIT_PROCESS_200 = 200L;

    /**
     * 等待时间间隔
     */
    public static final Long WAIT_INTERVAL_500 = 500L;

    /* 资源个数 */
    public static final int RESOURCE_A = 10;
    public static final int RESOURCE_B = 10;
    public static final int RESOURCE_C = 10;


    /**
     * 时间片大小
     */
    public static final int TIME_SLICE_5 = 5;

    /* 内存可视化基础宽度 */
    public static final int BASE_MEMORY_WIDTH = 100;
    /* 内存可视化比例 */
    public static final int BASE_MEMORY_SCALE = 20;

    /* 内存可视化比例 */
    public static final int BASE_STORAGE_SCALE = 10;

    /* LRU int capacity = 3; */
    public static final int LRU_CAPACITY = 3;

}
