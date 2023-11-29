package com.cyt.os.kernel.process.algorithm;

import com.cyt.os.common.Config;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.process.data.PCB;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 先来先执行
 *
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
//        log.info("readyQueue  " + this.readyQueue.size());
        while (!readyQueue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(Config.WAIT_PROCESS_200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 取出到达时间最小
            // TODO ACTIVE_READY
            Optional<PCB> minArrival = readyQueue.stream().
                    filter(pcb -> pcb.getStatus() == ProcessStatus.ACTIVE_READY).
                    min(Comparator.comparingInt(PCB::getArrivalTime));

            if (minArrival.isPresent()) {
                PCB min = minArrival.get();
                PCB pcb = removePCB(min);
                // 若挂起 加入队尾
                if (pcb.getStatus() == ProcessStatus.STATIC_READY) {
                    addPCB(pcb);
                    continue;
                }
                executeProcess(pcb);
            }
        }
    }
}
