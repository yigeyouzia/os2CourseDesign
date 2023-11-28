package process;

import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.process.algorithm.RR;

import java.util.ArrayList;

/**
 * @author cyt
 * @date 2023/11/28
 */
public class RRTest {
    public static void main(String[] args) {
        ProcessManager processManager = new ProcessManager();
        ArrayList<PCB> readyQueue = new ArrayList<>();
        // 4.时间片轮转
        readyQueue.add(new PCB("A", 1, 1, 10));
        readyQueue.add(new PCB("B", 2, 0, 20));
        readyQueue.add(new PCB("C", 3, 3, 30));
        RR rr = new RR(readyQueue);
        rr.run();
    }
}
