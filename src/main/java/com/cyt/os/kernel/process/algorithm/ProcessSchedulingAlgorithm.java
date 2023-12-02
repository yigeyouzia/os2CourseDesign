package com.cyt.os.kernel.process.algorithm;

import com.cyt.os.common.Config;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.process.data.Process;
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
        //  Platform.runLater(() -> readyQueue.remove(pcb));
        return pcb;
    }

    public synchronized void addPCB(PCB pcb) {
        // TODO
//        readyQueue.add(pcb);
        log.info("添加进程  " + pcb.getUid() + " 到末尾");
        Platform.runLater(() -> readyQueue.add(pcb));
    }

    public synchronized PCB removePCB(int index) {
        // TODO ui模式
        PCB pcb = readyQueue.get(index);
        readyQueue.remove(index);
        //  Platform.runLater(() -> readyQueue.remove(index));
        log.info("移除pcb  " + pcb.getUid() + "剩余大小： " + readyQueue.size());
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
        // 设置进程正在运行
        pcb.setStatus(ProcessStatus.RUNNING);
        // 执行进程
        while (pcb.getStatus() != ProcessStatus.DESTROY) {
            // 传给Process
            process.run(Math.toIntExact(Config.TIME_SLICE_5));
            // 休息等待
            try {
                TimeUnit.MILLISECONDS.sleep(Config.WAIT_INTERVAL_500);
            } catch (InterruptedException e) {
                log.info("进程 " + pcb.getPid() + " 被打断");
            }
        }
        log.info("进程  " + pcb.getUid() + " 结束");
    }

    /**
     * 时间片轮转执行函数
     *
     * @param pcb
     * @param timeSlice
     */
    public void executeProcess(PCB pcb, int timeSlice) {
        Process process = new Process(pcb);
        // 设置进程正在运行
        pcb.setStatus(ProcessStatus.RUNNING);
        // 执行进程
        process.run(timeSlice);
        try {
            TimeUnit.MILLISECONDS.sleep(Config.WAIT_INTERVAL_500);
        } catch (InterruptedException e) {
            log.info("进程 " + pcb.getPid() + " 被打断");
        }
        if (pcb.getUsedTime() == pcb.getServiceTime()) {
            log.info("进程 { " + pcb.getPid() + "} 结束");
        } else if (pcb.getStatus() != ProcessStatus.ACTIVE_READY) {
            addPCB(pcb);
        }
    }
}
