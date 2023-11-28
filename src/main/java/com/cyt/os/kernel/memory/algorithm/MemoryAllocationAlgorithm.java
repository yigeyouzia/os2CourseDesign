package com.cyt.os.kernel.memory.algorithm;

import com.cyt.os.enums.MemoryStatus;
import com.cyt.os.kernel.memory.data.MemoryBlock;
import com.cyt.os.kernel.process.data.PCB;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author cyt
 * @date 2023/11/28
 */
public abstract class MemoryAllocationAlgorithm {

    /**
     * 不再分割的剩余分区的大小
     */
    private static final int MIN_SIZE = 50;
    Logger log;
    List<MemoryBlock> memoryList;

    boolean isAllocated = false;

    public MemoryAllocationAlgorithm(List<MemoryBlock> memoryList, Logger log) {
        this.memoryList = memoryList;
        this.log = log;
    }

    /**
     * 分配内存
     *
     * @param size 所需内存大小
     * @param id   进程pid
     */
    public abstract void allocateMemory(int size, int id);

    /**
     * 分配内存的底层实现
     *
     * @param size  所需内存大小
     * @param id    进程pid
     * @param block 要分配的内存块
     */
    void allocate(int size, int id, MemoryBlock block) {
        //若剩余分区大小大于不再分割下限，则进行分割
        if (block.getSize() - size >= MIN_SIZE) {
            // 0 + 20 ~~ 100
            MemoryBlock newBlock = new MemoryBlock(block.getStartAddress() + size, block.getSize() - size);
            // 成为新的块
            block.setSize(size);
            memoryList.add(memoryList.indexOf(block) + 1, newBlock);
        }
        block.setStatus(MemoryStatus.USED);
        block.setId(id);
        update();
    }

    public void release(int id) {
        for (int i = 0; i < memoryList.size(); i++) {
            MemoryBlock block = memoryList.get(i);
            if (block.getId() == id) {
                block.setStatus(MemoryStatus.FREE);
                block.setId(-1);

                //与前后两个空闲分区相邻
                if (i != 0 && i != memoryList.size() - 1 &&
                        memoryList.get(i - 1).getStatus() == MemoryStatus.FREE &&
                        memoryList.get(i + 1).getStatus() == MemoryStatus.FREE) {
                    memoryList.get(i - 1).setSize(memoryList.get(i - 1).getSize() + block.getSize() + memoryList.get(i + 1).getSize());
                    memoryList.remove(i);
                    memoryList.remove(i);
                    update();
                    log.info("回收的内存分区与前后分区合并，新空闲分区起址为：" + memoryList.get(i - 1).getStartAddress() +
                            "，大小为：" + memoryList.get(i - 1).getSize());
                    break;
                }
                //与前一个空闲分区相邻
                else if (i != 0 && memoryList.get(i - 1).getStatus() == MemoryStatus.FREE) {
                    memoryList.get(i - 1).setSize(memoryList.get(i - 1).getSize() + block.getSize());
                    memoryList.remove(i);
                    update();
                    log.info("回收的内存分区与前分区合并，新空闲分区起址为：" + memoryList.get(i - 1).getStartAddress() +
                            "，大小为：" + memoryList.get(i - 1).getSize());
                    break;
                }
                //与后一个空闲分区相邻
                else if (i != memoryList.size() - 1 && memoryList.get(i + 1).getStatus() == MemoryStatus.FREE) {
                    block.setSize(block.getSize() + memoryList.get(i + 1).getSize());
                    log.info(block);
                    memoryList.remove(i + 1);
                    update();
                    log.info("回收的内存分区与后分区合并，新空闲分区起址为：" + block.getStartAddress() +
                            "，大小为：" + block.getSize());
                    break;
                }
                //不与空闲分区相邻就不用再处理了
                else {
                    log.info("回收的内存分区无法合并");
                }

            }
        }
    }

    public void showMemory() {
        System.out.println("------------------------------------");
        System.out.println("分区编号\t分区始址\t分区尾址\t分区大小\t空闲状态\t");
        System.out.println("------------------------------------");
        for (int i = 0; i < memoryList.size(); i++) {
            MemoryBlock memoryBlock = memoryList.get(i);
            System.out.println(i + "\t\t" + memoryBlock.getStartAddress() + "\t\t" +
                    memoryBlock.getLast() + "\t\t"
                    + memoryBlock.getSize() + "  \t" + memoryBlock.getStatus());
        }
        System.out.println("------------------------------------");
    }

    public void showPcbs(List<PCB> pcbs) {
        System.out.println("------------------------------------");
        System.out.println("进程编号\t进程状态\t进程起始地址\t进程尾址\t进程大小\t");
        System.out.println("------------------------------------");
        if (pcbs.size() > 0) {
            for (int i = 0; i < pcbs.size(); i++) {
                PCB pcb = pcbs.get(i);
                System.out.println(pcb.getPid() + "  \t" + pcb.getStatus() + "\t\t" +
                        pcb.getHole().getStartAddress() + "\t\t\t" +
                        pcb.getHole().getLast() + "\t\t" +
                        pcb.getHole().getSize());
            }
        } else {
            System.err.println("\t\t\t暂无进程！");
        }
        System.out.println("------------------------------------");
    }

    private void update() {
//        MemoryController controller = (MemoryController) Context.controllerMap.get("Memory");
//        controller.update();
    }
}
