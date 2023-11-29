package com.cyt.os.kernel.resourse.data;

import com.cyt.os.enums.ResourceStatus;

/**
 * @author cyt
 * @date 2023/11/29 21:50
 */
public class Resource {
    /**
     * 资源状态
     */
    private ResourceStatus status = ResourceStatus.FREE;
    /**
     * 被占有的进程id
     */
    private int id = -1;
}
