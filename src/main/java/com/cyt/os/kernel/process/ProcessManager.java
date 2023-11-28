package com.cyt.os.kernel.process;

import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.SystemKernel;
import com.cyt.os.kernel.memory.MemoryManager;
import com.cyt.os.kernel.memory.algorithm.FF;
import com.cyt.os.kernel.process.data.PCB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class ProcessManager {
    /* 就绪队列 */
    private final ObservableList<PCB> readyQueue = FXCollections.observableArrayList();
    /* 阻塞队列 */
    private final ObservableList<PCB> blockQueue = FXCollections.observableArrayList();
    /* PCB表，便于检索PCB */
    private final List<PCB> PCBList = new ArrayList<>();

    public static SystemKernel systemKernel = new SystemKernel();



    /**
     * 创建原语
     *
     * @return
     */
    public PCB create() {
        PCB pcb = new PCB();
        // TODO 初始化pcb
        pcb.setMemorySize(50);
        PCBList.add(pcb);

        // 1.申请分配内存
        MemoryManager memoryManager = systemKernel.getMemoryManager();
        memoryManager.setMAA(new FF(memoryManager.getMemoryList()));
        System.out.println("================分配之前：");
        memoryManager.getMAA().showMemory();
        System.out.println("================分配之后：");
        memoryManager.getMAA().allocateMemory(50, pcb.getPid());
        memoryManager.getMAA().showMemory();

        if (pcb.getStatus() == ProcessStatus.ACTIVE_READY) {
            readyQueue.add(pcb);
        }
        return pcb;
    }
}
