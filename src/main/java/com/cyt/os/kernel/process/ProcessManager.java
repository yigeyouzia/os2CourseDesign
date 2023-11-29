package com.cyt.os.kernel.process;

import com.cyt.os.controller.MainController;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.process.algorithm.ProcessSchedulingAlgorithm;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.resourse.algorithm.BankerAlgorithm;
import com.cyt.os.kernel.resourse.data.BankerData;
import com.cyt.os.ustils.RandomUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class ProcessManager extends Thread {
    public static final Logger log = Logger.getLogger(ProcessManager.class.getName());
    /* random */
    private final Random random = new Random();

    /* 线程 */
    private Thread saThread;

    /* 就绪队列 */
    private final ObservableList<PCB> readyQueue = FXCollections.observableArrayList();
    /* 阻塞队列 */
    private final ObservableList<PCB> blockQueue = FXCollections.observableArrayList();
    /* PCB表，便于检索PCB */
    private final List<PCB> PCBList = new ArrayList<>();

    /* 银行家算法 */
    private BankerAlgorithm ba = new BankerAlgorithm(MainController.systemKernel.getResourceManager());


    /**
     * 进程调度算法
     */
    private ProcessSchedulingAlgorithm psa;
    private ProcessSchedulingAlgorithm tempPsa;
    /* CPU 时间*/
    private final StringProperty cpuTime = new SimpleStringProperty("0");


    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error("进程管理器时间流逝时被打断");
            }
            cpuTime.set(String.valueOf(Integer.parseInt(cpuTime.get()) + 1));
            updatePSA();
        }
    }

    /**
     * 创建原语
     *
     * @return
     */
    public PCB create() {
        System.out.println(RandomUtil.UidNameLength);
        PCB pcb = new PCB();
        // TODO 初始化pcb
        pcb.setMemorySize(50);
        int pid = RandomUtil.getRandomPid();
        pcb.setPid(pid);
        pcb.setServiceTime(random.nextInt(30) + 20);
        pcb.setPriority(random.nextInt(10));
        pcb.setUid(RandomUtil.getRandomUid());
        pcb.setArrivalTime(random.nextInt(20) + 20);
        pcb.setArrivalTime(Integer.parseInt(cpuTime.get()));
        PCBList.add(pcb);

        initResource(pcb);


        // 1.申请分配内存
        MainController.systemKernel.getMemoryManager().getMAA().allocateMemory(pcb.getMemorySize(), pcb.getPid());
//        System.out.println("================分配之前：");
//        MainController.systemKernel.getMemoryManager().getMAA().showMemory();
//        System.out.println("================分配之后：");
//        MainController.systemKernel.getMemoryManager().getMAA().allocateMemory(50, pcb.getPid());
//        MainController.systemKernel.getMemoryManager().getMAA().showMemory();

        // 2.资源分配
        // 3.设置活动就绪
        pcb.setStatus(ProcessStatus.ACTIVE_READY);

        if (pcb.getStatus() == ProcessStatus.ACTIVE_READY) {
            readyQueue.add(pcb);
        }
        return pcb;
    }

    /**
     * 初始化资源信息
     */
    private void initResource(PCB pcb) {
        //随机生成进程所需资源总数 资源a b c
        int ra = random.nextInt(3) + 3;
        int rb = random.nextInt(2) + 3;
        int rc = random.nextInt(1) + 3;

        //更新银行家算法中的数据
        List<Integer> maxList = new ArrayList<>();
        Collections.addAll(maxList, ra, rb, rc);

        /* 更新pcb对应数据 */
        pcb.initResources(maxList);

        List<Integer> needList = new ArrayList<>();
        Collections.addAll(needList, ra, rb, rc);

        List<Integer> alocList = new ArrayList<>();
        Collections.addAll(alocList, 0, 0, 0);


        BankerData data = new BankerData();
        data.setMax(maxList);
        data.setNeed(needList);
        data.setAllocation(alocList);
        ba.getData().put(pcb.getPid(), data);
    }

    // 设置算法
    public void setPsa(ProcessSchedulingAlgorithm psa) {
        this.tempPsa = psa;
    }

    /* 算法切换 */
    private void updatePSA() {
        //初次启动时，默认调度算法为复选框的默认第一个选项
        if (psa == null) {
            psa = tempPsa;
        }

        //第一次启动或上次的调度算法执行完毕后，再次启动
        if (saThread == null ||
                saThread.getState() == State.TERMINATED) {

            if (psa != tempPsa) {
                psa = tempPsa;
                log.info("切换算法:" + psa.getClass().getSimpleName());
            }
            saThread = new Thread(psa);
            saThread.start();
        }
    }

    public ObservableList<PCB> getReadyQueue() {
        return this.readyQueue;
    }

    public StringProperty getCpuTimeProperty() {
        return cpuTime;
    }
}
