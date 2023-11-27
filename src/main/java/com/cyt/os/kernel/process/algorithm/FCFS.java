package com.cyt.os.kernel.process.algorithm;

import com.cyt.os.common.Config;
import com.cyt.os.kernel.process.PCB;
import com.cyt.os.kernel.process.PStatus;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class FCFS extends ProcessSchedulingAlgorithm {

    private static final Logger log = Logger.getLogger(FCFS.class);

    public FCFS(List<PCB> readyQueue) {
        super(readyQueue, log);
    }

    @Override
    public void run() {
        log.warn("FCFS先来先服务开始调度");
        while (!readyQueue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(Config.WAIT_PROCESS_200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 取出到达时间最小
            // TODO ACTIVE_READY
            Optional<PCB> minArrival = readyQueue.stream().
                    filter(pcb -> pcb.getStatus() == PStatus.CREATE).
                    min(Comparator.comparingInt(PCB::getArrivalTime));
            if (minArrival.isPresent()) {
                PCB min = minArrival.get();
                PCB pcb = removePCB(min);
                // 若挂起 加入队尾
                if (pcb.getStatus() == PStatus.STATIC_READY) {
                    addPCB(pcb);
                    continue;
                }
                executeProcess(pcb);
            }
        }
    }
}
