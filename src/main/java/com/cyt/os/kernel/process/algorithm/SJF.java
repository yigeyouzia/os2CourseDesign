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
 * 服务时间最短优先
 *
 * @author cyt
 * @date 2023/11/27
 */
public class SJF extends ProcessSchedulingAlgorithm {

    private static final Logger log = Logger.getLogger(FCFS.class);

    public SJF(List<PCB> readyQueue) {
        super(readyQueue, log);
    }

    @Override
    public void run() {
        log.warn("SJF短作业优先开始调度");
        while (!readyQueue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(Config.WAIT_PROCESS_200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 取出到达时间最小
            // TODO ACTIVE_READY
            Optional<PCB> minService = readyQueue.stream().
                    filter(pcb -> pcb.getStatus() == PStatus.CREATE).
                    min(Comparator.comparingInt(PCB::getServiceTime));
            if (minService.isPresent()) {
                PCB min = minService.get();
                PCB pcb = removePCB(min);
                executeProcess(pcb);
            }
        }
    }
}
