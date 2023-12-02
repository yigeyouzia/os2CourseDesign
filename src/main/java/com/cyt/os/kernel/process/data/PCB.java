package com.cyt.os.kernel.process.data;

import com.cyt.os.controller.MainController;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.memory.data.MemoryBlock;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.resourse.algorithm.BankerAlgorithm;
import com.cyt.os.ustils.RandomUtil;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//进程控制块
@SuppressWarnings("{all}")
public class PCB {
    /**
     * 进程内部标识符
     */
    private final IntegerProperty pid;
    /**
     * 用户标识符
     */
    private final StringProperty uid;
    /**
     * 进程优先级，数字越大优先级越高
     */
    private final IntegerProperty priority;
    /**
     * 进程状态
     */
    private final ObjectProperty<ProcessStatus> status;

    /**
     * 到达时间
     */
    private final IntegerProperty arrivalTime;
    /**
     * 进程总计所需时间
     */
    private final IntegerProperty serviceTime;
    /**
     * 进程已运行时间
     */
    private final IntegerProperty usedTime;
    /**
     * 进程剩余时间
     */
    private final IntegerProperty remainingTime;
    /**
     * 进程已等待时间
     */
    private final IntegerProperty waitedTime;
    /**
     * 进程运行进度
     */
    private final DoubleProperty progress;

    /**
     * 所需资源总数
     */
    private final List<Integer> maxR;
    /**
     * 仍需资源数
     */
    private final List<Integer> needR;
    /**
     * 已分配资源数
     */
    private final List<Integer> alocR;
    /**
     * 所需内存
     */
    private int memorySize;

    /**
     * 进程所对应的小内存块
     */
    private MemoryBlock memoryBlock;

    public PCB() {
        this.pid = new SimpleIntegerProperty(RandomUtil.getRandomPid());
        this.uid = new SimpleStringProperty();
        this.priority = new SimpleIntegerProperty(0);
        this.status = new SimpleObjectProperty<>(ProcessStatus.CREATE);

        this.arrivalTime = new SimpleIntegerProperty(0);
        this.serviceTime = new SimpleIntegerProperty(1);
        this.usedTime = new SimpleIntegerProperty(0);
        this.remainingTime = new SimpleIntegerProperty(0);
        this.waitedTime = new SimpleIntegerProperty(0);

        this.progress = new SimpleDoubleProperty(0.0);

        this.maxR = new ArrayList<>();
        this.needR = new ArrayList<>();
        this.alocR = new ArrayList<>();
//        this.memorySize = new Random().nextInt(450) + 50;
        this.memorySize = 50;

        //数据绑定，设置剩余时间、进程进度为动态更新
        this.remainingTime.bind(this.serviceTime.subtract(this.usedTime));
        this.progress.bind(this.usedTime.multiply(1.0).divide(this.serviceTime));
    }

    /**
     * @param name     进程名称
     * @param priority 优先级
     * @param arrival  到达时间
     * @param runtime  运行时间
     */
    public PCB(String name, int priority, int arrival, int runtime) {
        this.pid = new SimpleIntegerProperty(RandomUtil.getRandomPid());
        this.uid = new SimpleStringProperty(name);
        this.priority = new SimpleIntegerProperty(priority);
        this.status = new SimpleObjectProperty<>(ProcessStatus.CREATE);

        this.arrivalTime = new SimpleIntegerProperty(arrival);
        this.serviceTime = new SimpleIntegerProperty(runtime);
        this.usedTime = new SimpleIntegerProperty(0);
        this.remainingTime = new SimpleIntegerProperty(0);
        this.waitedTime = new SimpleIntegerProperty(0);

        this.progress = new SimpleDoubleProperty(0.0);

        this.maxR = new ArrayList<>();
        this.needR = new ArrayList<>();
        this.alocR = new ArrayList<>();
        this.memorySize = new Random().nextInt(450) + 4450;

        //数据绑定，设置剩余时间、进程进度为动态更新
        this.remainingTime.bind(this.serviceTime.subtract(this.usedTime));
        this.progress.bind(this.usedTime.multiply(1.0).divide(this.serviceTime));
    }

    public void initResources(List<Integer> list) {
        maxR.addAll(list);
        needR.addAll(list);
        Collections.addAll(alocR, 0, 0, 0);
    }

    public void updateResources(List<Integer> list) {
        needR.set(0, needR.get(0) - list.get(0));
        needR.set(1, needR.get(1) - list.get(1));
        needR.set(2, needR.get(2) - list.get(2));

        alocR.set(0, alocR.get(0) + list.get(0));
        alocR.set(1, alocR.get(1) + list.get(1));
        alocR.set(2, alocR.get(2) + list.get(2));
    }

    public void IncreaseRunTime(int num) {
        this.usedTime.add(1);
    }

    public void DecreaseRunTime(int num) {
        this.usedTime.add(-1);
    }

    public int getPid() {
        return pid.get();
    }

    public void setPid(int pid) {
        this.pid.set(pid);
    }

    public IntegerProperty pidProperty() {
        return pid;
    }

    public String getUid() {
        return uid.get();
    }

    public void setUid(String uid) {
        this.uid.set(uid);
    }

    public StringProperty uidProperty() {
        return uid;
    }

    public int getPriority() {
        return priority.get();
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public ProcessStatus getStatus() {
        return status.get();
    }

    public void setStatus(ProcessStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<ProcessStatus> statusProperty() {
        return status;
    }

    public int getArrivalTime() {
        return arrivalTime.get();
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime.set(arrivalTime);
    }

    public IntegerProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime.get();
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime.set(serviceTime);
    }

    public IntegerProperty serviceTimeProperty() {
        return serviceTime;
    }

    public int getUsedTime() {
        return usedTime.get();
    }

    public void setUsedTime(int usedTime) {
        this.usedTime.set(usedTime);
    }

    public IntegerProperty usedTimeProperty() {
        return usedTime;
    }

    public int getRemainingTime() {
        return remainingTime.get();
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime.set(remainingTime);
    }

    public IntegerProperty remainingTimeProperty() {
        return remainingTime;
    }

    public int getWaitedTime() {
        return waitedTime.get();
    }

    public void setWaitedTime(int waitedTime) {
        this.waitedTime.set(waitedTime);
    }

    public IntegerProperty waitedTimeProperty() {
        return waitedTime;
    }

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public List<Integer> getMaxR() {
        return maxR;
    }

    public List<Integer> getNeedR() {
        return needR;
    }

    public List<Integer> getAlocR() {
        return alocR;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public MemoryBlock getHole() {
        return memoryBlock;
    }

    public void setHole(MemoryBlock memoryBlock) {
        this.memoryBlock = memoryBlock;
    }

    @Override
    public String toString() {
        return "Pcb{" +
                "pid=" + pid +
                ", uid=" + uid +
                ", priority=" + priority +
                ", status=" + status +
                ", arriveTime=" + arrivalTime +
                ", serviceTime=" + serviceTime +
                ", usedTime=" + usedTime +
                ", remainingTime=" + remainingTime +
                ", waitedTime=" + waitedTime +
                ", progress=" + progress +
                '}';
    }

    public void releaseAllResources() {
        //释放资源
        MainController.systemKernel.getResourceManager().release(this);
        //更新银行家算法中的数据
        BankerAlgorithm ba = ProcessManager.getBA();
        ba.getData().remove(pid.get());
        ba.release(maxR);
        //释放资源后检查是否有进程可以申请资源
        Platform.runLater(() -> {
            MainController.systemKernel.getProcessManager().checkBlock();
            MainController.systemKernel.getProcessManager().checkUnReady();
        });
    }
}

