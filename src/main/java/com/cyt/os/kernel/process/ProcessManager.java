package com.cyt.os.kernel.process;

import com.cyt.os.common.Operation;
import com.cyt.os.controller.MainController;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.exception.BAException;
import com.cyt.os.exception.PCBNotFoundException;
import com.cyt.os.kernel.process.algorithm.ProcessSchedulingAlgorithm;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.resourse.algorithm.BankerAlgorithm;
import com.cyt.os.kernel.resourse.data.BankerData;
import com.cyt.os.kernel.resourse.data.ResourceRequest;
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
    private static BankerAlgorithm ba;


    /**
     * 进程调度算法
     */
    private ProcessSchedulingAlgorithm psa;
    private ProcessSchedulingAlgorithm tempPsa;
    /* CPU 时间*/
    private final StringProperty cpuTime = new SimpleStringProperty("0");


    @Override
    public void run() {
        ba = new BankerAlgorithm(MainController.systemKernel.getResourceManager());
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

        PCB pcb = new PCB();
        // TODO 初始化pcb
        pcb.setMemorySize(random.nextInt(200) + 100);
        int pid = RandomUtil.getRandomPid();
        pcb.setPid(pid);
        pcb.setServiceTime(random.nextInt(30) + 20);
        pcb.setPriority(random.nextInt(10));
        pcb.setUid(RandomUtil.getRandomUid());
        pcb.setArrivalTime(Integer.parseInt(cpuTime.get()));
        PCBList.add(pcb);


        // 1.资源分配
        initResource(pcb);
        // 初始化请求
        ResourceRequest req = ResourceRequest.generateRequest(pcb.getPid());

        // 2.申请分配内存
        try {
            log.info("fenpei: " + pcb.getMemorySize());
            // 分配内存
            MainController.systemKernel.
                    getMemoryManager().
                    getMAA().
                    allocateMemory(pcb.getMemorySize(), pcb.getPid());
            // 分配资源
            log.info("req" + req.getSource());
            ba.bankerAlgorithm(req);
            pcb.updateResources(req.getSource());
            pcb.setStatus(ProcessStatus.ACTIVE_READY);
            log.info("设置");
        } catch (RuntimeException e) {
            pcb.setStatus(ProcessStatus.ACTIVE_BLOCK);
            blockQueue.add(pcb);
            log.error(e.getMessage());
        }


        // 3.设置活动就绪
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

    /**
     * 根据pid获取对应的PCB
     */
    public PCB getPCB(int pid) {
        for (PCB pcb : PCBList) {
            if (pcb.getPid() == pid) {
                return pcb;
            }
        }
        log.error("未找到pid为:" + pid + " 的进程");
        throw new PCBNotFoundException();
    }

    /**
     * 挂起进程
     *
     * @param pid 进程pid
     */
    public void suspend(int pid) {
        PCB pcb = getPCB(pid);
        if (pcb == null) {
            return;
        }

        //活动阻塞->静止阻塞
        if (pcb.getStatus().equals(ProcessStatus.ACTIVE_BLOCK)) {
            pcb.setStatus(ProcessStatus.STATIC_BLOCK);
        }
        //活动就绪->静止就绪
        else if (pcb.getStatus().equals(ProcessStatus.ACTIVE_READY)) {
            pcb.setStatus(ProcessStatus.STATIC_READY);
        }
        //执行->静止就绪
        else if (pcb.getStatus().equals(ProcessStatus.RUNNING)) {
            pcb.setStatus(ProcessStatus.STATIC_READY);
        } else {
            Operation.showErrorAlert("进程状态有误，无法挂起");
        }
    }

    /**
     * 激活原语
     *
     * @param id 进程id
     */
    public void active(int id) {
        PCB pcb = getPCB(id);

        if (pcb == null) {
            return;
        }

        //静止阻塞->活动阻塞
        if (pcb.getStatus().equals(ProcessStatus.STATIC_BLOCK)) {
            pcb.setStatus(ProcessStatus.ACTIVE_BLOCK);
        }
        //静止就绪->活动就绪
        if (pcb.getStatus().equals(ProcessStatus.STATIC_READY)) {
            pcb.setStatus(ProcessStatus.ACTIVE_READY);
        } else {
            Operation.showErrorAlert("进程状态有误，无法激活");
        }
    }


    /**
     * 终止原语
     *
     * @param id 进程id
     */
    public void destroy(int id) {
        PCB pcb = getPCB(id);

        if (pcb == null) {
            return;
        }

        /* 终止原语 释放所有资源 TODO */
        pcb.releaseAllResources();
        MainController.systemKernel
                .getMemoryManager()
                .getMAA()
                .release(pcb.getPid());
        pcb.setStatus(ProcessStatus.DESTROY);
        readyQueue.remove(pcb);
    }

    public ObservableList<PCB> getReadyQueue() {
        return this.readyQueue;
    }

    public ObservableList<PCB> getBlockQueue() {
        return this.blockQueue;
    }

    public StringProperty getCpuTimeProperty() {
        return cpuTime;
    }

    public static BankerAlgorithm getBA() {
        return ba;
    }

    /**
     * 检查是否有创建时请求资源分配失败的进程，有则重新申请资源
     */
    public void checkUnReady() {
        for (PCB pcb : PCBList) {
            if (pcb.getStatus() == ProcessStatus.CREATE) {
                ResourceRequest req = ResourceRequest.generateRequest(pcb.getPid());
                try {
                    ba.bankerAlgorithm(req);
                    pcb.updateResources(req.getSource());
                    pcb.setStatus(ProcessStatus.ACTIVE_READY);
                    readyQueue.add(pcb);
                } catch (BAException e) {
                    log.error(e.getMessage());
                }
                break;
            }
        }
    }

    public void stopPSA() {
        synchronized (saThread) {
            saThread.suspend();
        }
    }

    public void continuePSA() {
        synchronized (saThread) {
            saThread.resume();
        }
    }

    /**
     * 检查是否有陷入阻塞的进程，有则重新申请资源
     */
    public void checkBlock() {
        for (PCB pcb : PCBList) {
            if (pcb.getStatus() == ProcessStatus.ACTIVE_BLOCK) {
                // TODO bug resource
//                ResourceRequest req = ResourceRequest.generateRemainRequest(pcb);
                ResourceRequest req = ResourceRequest.generateRequest(pcb.getPid());
                try {
                    ba.bankerAlgorithm(req);
                    pcb.updateResources(req.getSource());
                    pcb.setStatus(ProcessStatus.ACTIVE_READY);
                    blockQueue.remove(pcb);
                    readyQueue.add(pcb);
                } catch (BAException e) {
                    log.error(e.getMessage());
                }
                break;
            }
        }
    }
}
