package com.cyt.os.kernel;

import com.cyt.os.kernel.memory.MemoryManager;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.resourse.ResourceManager;
import lombok.Data;

/**
 * @author cyt
 * @date 2023/11/28
 */
@Data
public class SystemKernel {
    /**
     * 进程管理器
     */
    private final ProcessManager processManager = new ProcessManager();
    /**
     * 内存管理器
     */
    private final MemoryManager memoryManager = new MemoryManager();

    /* 资源管理 */
    private final ResourceManager resourceManager = new ResourceManager();


    /**
     * 启动各管理器
     */
    public void start() {
        this.processManager.start();
    }
}
