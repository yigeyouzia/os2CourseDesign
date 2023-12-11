package com.cyt.os.kernel.resourse;

import com.cyt.os.common.Config;
import com.cyt.os.common.Context;
import com.cyt.os.controller.ProcessController;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.resourse.data.ResourceRequest;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author cyt
 * @date 2023/11/29 21:51
 */
public class ResourceManager {

    /* 资源种类数目 */
    private final int RESOURCE_NUM = 3;

    private static final Logger log = Logger.getLogger(ResourceManager.class);

    private final IntegerProperty resourceA;
    private final IntegerProperty resourceB;
    private final IntegerProperty resourceC;

    public ResourceManager() {
        this.resourceA = new SimpleIntegerProperty(Config.RESOURCE_A);
        this.resourceB = new SimpleIntegerProperty(Config.RESOURCE_B);
        this.resourceC = new SimpleIntegerProperty(Config.RESOURCE_C);
    }

    public int getResourceA() {
        return resourceA.get();
    }


    public int getResourceB() {
        return resourceB.get();
    }


    public int getResourceC() {
        return resourceC.get();
    }

    public synchronized void allocate(ResourceRequest request) {
        int id = request.getId();
        log.info("进程 " + id + " 正式分配资源");

        List<Integer> requestSource = request.getSource();
        resourceA.set(resourceA.get() - requestSource.get(0));
        resourceB.set(resourceB.get() - requestSource.get(1));
        resourceC.set(resourceC.get() - requestSource.get(2));
        log.info("当前资源： " + resourceA.get() + "-" + resourceB.get() + "-" + resourceC.get());
        update();
    }

    /**
     * 释放所占有的资源
     *
     * @param pcb 进程pcb
     */
    public synchronized void release(PCB pcb) {
        List<Integer> alocR = pcb.getAlocR();
        log.info(pcb.getUid() + " 释放资源 --" + alocR);
        resourceA.set(resourceA.get() + alocR.get(0));
        resourceB.set(resourceB.get() + alocR.get(1));
        resourceC.set(resourceC.get() + alocR.get(2));
        log.info("当前资源： " + resourceA.get() + "-" + resourceB.get() + "-" + resourceC.get());
        update();
    }

    private void update() {
        ProcessController controller = (ProcessController) Context.controllerMap.get("Process");
        controller.updateResource(resourceA.get(), resourceB.get(), resourceC.get());
    }

}
