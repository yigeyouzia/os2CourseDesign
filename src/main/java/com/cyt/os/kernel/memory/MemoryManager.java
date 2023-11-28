package com.cyt.os.kernel.memory;

import com.cyt.os.enums.MemoryStatus;
import com.cyt.os.kernel.memory.algorithm.MemoryAllocationAlgorithm;
import com.cyt.os.kernel.memory.data.MemoryBlock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cyt
 * @date 2023/11/28
 */
public class MemoryManager {

    private final ObservableList<MemoryBlock> memoryList = FXCollections.observableArrayList();

    /** 内存分配算法 */
    private MemoryAllocationAlgorithm maa;

    public MemoryManager() {
        //200*2 300*2 500*4 1000*2 总计5000
        memoryList.add(new MemoryBlock(0, 200));
        memoryList.add(new MemoryBlock(200, 200));
        memoryList.add(new MemoryBlock(400, 300));
        memoryList.add(new MemoryBlock(700, 300));
        memoryList.add(new MemoryBlock(1000, 500));
        memoryList.add(new MemoryBlock(1500, 500));
        memoryList.add(new MemoryBlock(2000, 500));
        memoryList.add(new MemoryBlock(2500, 500));
        memoryList.add(new MemoryBlock(3000, 1000));
        memoryList.add(new MemoryBlock(4000, 1000));
    }

    public int getMaxSize() {
        AtomicInteger maxSize = new AtomicInteger();
        memoryList.forEach(block -> maxSize.addAndGet(block.getSize()));
        return maxSize.get();
    }

    public int getUsedSize() {
        AtomicInteger usedSize = new AtomicInteger();
        memoryList.stream()
                .filter(block -> block.getStatus() == MemoryStatus.USED)
                .forEach(block -> usedSize.addAndGet(block.getSize()));
        return usedSize.get();
    }

    public MemoryAllocationAlgorithm getMAA() {
        return this.maa;
    }

    public void setMAA(MemoryAllocationAlgorithm maa) {
        this.maa = maa;
    }

    public ObservableList<MemoryBlock> getMemoryList() {
        return this.memoryList;
    }

}