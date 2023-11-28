package process;

import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.process.algorithm.FCFS;
import com.cyt.os.kernel.process.algorithm.PJF;
import com.cyt.os.kernel.process.algorithm.RR;
import com.cyt.os.kernel.process.algorithm.SJF;

import java.util.ArrayList;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class Algorithm {
    public static void main(String[] args) {
        ArrayList<PCB> readyQueue = new ArrayList<>();
        readyQueue.add(new PCB("A", 1, 1, 10));
        readyQueue.add(new PCB("B", 2, 0, 20));
        readyQueue.add(new PCB("C", 3, 3, 30));
        // 1.先来先服务
        FCFS fcfs = new FCFS(readyQueue);
        fcfs.run();
        // 2.优先级
        readyQueue.add(new PCB("A", 1, 1, 10));
        readyQueue.add(new PCB("B", 2, 0, 20));
        readyQueue.add(new PCB("C", 3, 3, 30));
        PJF pjf = new PJF(readyQueue);
        pjf.run();
        // 3.短作业优先
        readyQueue.add(new PCB("A", 1, 1, 10));
        readyQueue.add(new PCB("B", 2, 0, 20));
        readyQueue.add(new PCB("C", 3, 3, 30));
        SJF sjf = new SJF(readyQueue);
        sjf.run();
        // 4.时间片轮转
        readyQueue.add(new PCB("A", 1, 1, 10));
        readyQueue.add(new PCB("B", 2, 0, 20));
        readyQueue.add(new PCB("C", 3, 3, 30));
        RR rr = new RR(readyQueue);
        rr.run();
    }
}
