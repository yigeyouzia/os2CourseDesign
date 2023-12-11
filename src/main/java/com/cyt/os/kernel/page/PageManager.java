package com.cyt.os.kernel.page;

import com.cyt.os.controller.MainController;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.resourse.data.BankerData;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author cyt
 * @date 2023/12/11 18:26
 */
public class PageManager {

    public static final Logger log = Logger.getLogger(PageManager.class.getName());

    int[] pageReferences = {5, 0, 4, 0, 2, 5, 4, 2, 0};
    private Random random = new Random();

    int index = 0;

    public PCB createPage() {
        PCB pcb = new PCB();
        // TODO 初始化pcb
        pcb.setMemorySize(random.nextInt(20) + 10);
        int id = pageReferences[index % 9];
        index++;
        pcb.setPid(id);
        pcb.setServiceTime(random.nextInt(30) + 20);
        pcb.setUid("页面" + id);

        // 1.资源分配
        initResource0(pcb);
        // 2.申请分配内存
        try {
            log.info("page: " + pcb.getMemorySize());
            // 分配内存
            MainController.systemKernel.
                    getMemoryManager().
                    getMAA().
                    allocateMemory(pcb.getMemorySize(), pcb.getPid());
            // 分配资源
            pcb.setStatus(ProcessStatus.PAGE);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return pcb;
    }

    private void initResource0(PCB pcb) {
        //随机生成进程所需资源总数 资源a b c
        int ra = 0;
        int rb = 0;
        int rc = 0;

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
//        ba.getData().put(pcb.getPid(), data);
    }
}
