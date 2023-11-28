package com.cyt.os.kernel.process.algorithm;

import com.cyt.os.common.Config;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.enums.PStatus;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cyt
 * @date 2023/11/28
 */
public class RR extends ProcessSchedulingAlgorithm {

    private static final Logger log = Logger.getLogger(RR.class);


    public RR(List<PCB> readyQueue) {
        super(readyQueue, log);
    }

    @Override
    public void run() {
        while (!readyQueue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //取出就绪队列的第一个进程
            PCB pcb = removePCB(0);
            //若该进程被挂起，则移到就绪队列队尾
            if (pcb.getStatus() == PStatus.STATIC_READY) {
                addPCB(pcb);
                continue;
            }
            executeProcess(pcb, Config.TIME_SLICE_5);
        }
    }
}

