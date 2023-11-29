package com.cyt.os.kernel.process.data;

import com.cyt.os.enums.ProcessStatus;
import org.apache.log4j.Logger;

/**
 * @author cyt
 * @date 2023/11/24
 */
@SuppressWarnings({"all"})
public class Process {

    public static final Logger log = Logger.getLogger(Process.class.getName());

    private final PCB pcb;


    public Process(PCB pcb) {
        this.pcb = pcb;
    }

    /* 进程运行 time：时间片*/
    public void run(int time) {
        // 1.还能运行
        if ((pcb.getUsedTime() + time) <= pcb.getServiceTime()) {
            pcb.setUsedTime(pcb.getUsedTime() + time);
            log.info(pcb.getUid() + " 运行了：" + time);
        } else {
            pcb.setUsedTime(pcb.getServiceTime());
            // 销毁
            pcb.setStatus(ProcessStatus.DESTROY);
        }
    }
}
