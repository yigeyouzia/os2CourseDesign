package com.cyt.os.kernel.process.algorithm;

import com.cyt.os.common.Config;
import com.cyt.os.kernel.process.PCB;
import com.cyt.os.kernel.process.PStatus;
import com.cyt.os.kernel.process.Process;
import javafx.application.Platform;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author cyt
 * @date 2023/11/27
 */
@SuppressWarnings("{all}")
public abstract class ProcessSchedulingAlgorithm implements Runnable {

    List<PCB> readyQueue;

    Logger log;

    public ProcessSchedulingAlgorithm(List<PCB> readyQueue, Logger log) {
        this.readyQueue = readyQueue;
        this.log = log;
    }

    @Override
    public abstract void run();

    public synchronized PCB removePCB(PCB pcb) {
        // TODO ui模式
        readyQueue.remove(pcb);
//        Platform.runLater(() -> readyQueue.remove(pcb));
        return pcb;
    }

    public synchronized void addPCB(PCB pcb) {
        Platform.runLater(() -> readyQueue.add(pcb));
    }

    public synchronized PCB removePCB(int index) {
        PCB pcb = readyQueue.get(index);
        readyQueue.remove(index);
        // TODO ui模式
//        Platform.runLater(() -> readyQueue.remove(index));
        log.info("移除pcb" + readyQueue.size());
        return pcb;
    }

    /**
     * 进程执行到结束
     * 适用于 出了RR的其他三个算法
     *
     * @param pcb
     */
    public void executeProcess(PCB pcb) {
        Process process = new Process(pcb);
        while (pcb.getStatus() != PStatus.DESTROY) {
            // 传给Process
            process.run(Math.toIntExact(Config.TIME_SLICE_5));
            try {
                TimeUnit.MILLISECONDS.sleep(Config.WAIT_INTERVAL_500);
            } catch (InterruptedException e) {
                log.info("进程 " + pcb.getPid() + " 被打断");
            }
        }
        log.info("进程 { " + pcb.getPid() + "} 结束");
    }
}
