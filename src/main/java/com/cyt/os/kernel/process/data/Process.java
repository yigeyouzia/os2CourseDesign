package com.cyt.os.kernel.process.data;

import com.cyt.os.enums.PStatus;

/**
 * @author cyt
 * @date 2023/11/24
 */
@SuppressWarnings({"all"})
public class Process {

    private final PCB pcb;


    public Process(PCB pcb) {
        this.pcb = pcb;
    }

    /* 进程运行 time：时间片*/
    public void run(int time) {
        // 1.还能运行
        if ((pcb.getUsedTime() + time) <= pcb.getServiceTime()) {
            pcb.setUsedTime(pcb.getUsedTime() + time);
            System.out.println(pcb.getUid() + " 运行了：" + time);
        } else {
            pcb.setUsedTime(pcb.getServiceTime());
            // 销毁
            pcb.setStatus(PStatus.DESTROY);
        }
    }
}
