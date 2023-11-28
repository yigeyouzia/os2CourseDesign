package memory;

import com.cyt.os.kernel.memory.MemoryManager;
import com.cyt.os.kernel.memory.algorithm.FF;
import com.cyt.os.kernel.process.data.PCB;

/**
 * @author cyt
 * @date 2023/11/28
 */
public class TestAlgorithm {
    public static void main(String[] args) {
        MemoryManager memoryManager = new MemoryManager();
        PCB pcb = new PCB();
        memoryManager.setMAA(new FF(memoryManager.getMemoryList()));
        // 1.
        System.out.println("================分配之前：");
        memoryManager.getMAA().showMemory();
        System.out.println("================分配之后：");
        memoryManager.getMAA().allocateMemory(50, pcb.getPid());
        memoryManager.getMAA().showMemory();
    }
}
