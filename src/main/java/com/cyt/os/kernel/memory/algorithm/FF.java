package com.cyt.os.kernel.memory.algorithm;

import com.cyt.os.kernel.memory.data.MemoryBlock;
import com.cyt.os.exception.MemoryException;
import com.cyt.os.enums.MemoryStatus;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 首次适应分配算法
 *
 * @author cyt
 * @date 2023/11/28
 */

public class FF extends MemoryAllocationAlgorithm {

    private static final Logger log = Logger.getLogger(FF.class);

    public FF(List<MemoryBlock> memoryList) {
        super(memoryList, log);
    }

    @Override
    public void allocateMemory(int size, int id) {
        int num = 0;
        for (MemoryBlock block : memoryList) {
            num++;
            if (block.getStatus() == MemoryStatus.FREE && block.getSize() >= size) {
                log.info("FF算法查询次数： " + num);
                allocate(size, id, block);
                isAllocated = true;
                log.info("成功向进程P" + id + "分配大小为" + size + "KB的内存块");
                break;
            }
        }
        if (!isAllocated) {
            throw new MemoryException("内存不足，无法向进程P" + id + "分配大小为" + size + "KB的内存块");
        }
    }
}
