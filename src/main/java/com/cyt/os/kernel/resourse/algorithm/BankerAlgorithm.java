package com.cyt.os.kernel.resourse.algorithm;

import com.cyt.os.kernel.resourse.ResourceManager;
import com.cyt.os.kernel.resourse.data.BankerData;
import com.cyt.os.kernel.resourse.data.ResourceRequest;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyt
 * @date 2023/11/29 22:07
 */
@Data
public class BankerAlgorithm {
    private static final Logger log = Logger.getLogger(BankerAlgorithm.class);


    /* 资源种类数目 */
    private final int RESOURCE_NUM = 3;
    /*  可用资源数 10 10 10 */
    private final List<Integer> available = new ArrayList<>();
    /* <进程，银行家算法数据结构 max all need> */
    private final Map<Integer, BankerData> data = new HashMap<>();
    private ResourceManager resourceManager;


    public BankerAlgorithm(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        available.add(resourceManager.getResourceA());
        available.add(resourceManager.getResourceB());
        available.add(resourceManager.getResourceC());
    }

    public void bankerAlgorithm(ResourceRequest request) {
        int id = request.getId();
        log.info("进程  " + id + "  进入银行家算法");
        System.out.println(available);

        // 1.需求
        for (int i = 0; i < RESOURCE_NUM; i++) {
            if (request.getSource().get(i) > data.get(id).getNeed().get(i)) {
                log.info("1 请求大于需要");
                return;
            }
        }

        // 2.可用 available
        for (int i = 0; i < RESOURCE_NUM; i++) {
            if (request.getSource().get(i) > available.get(i)) {
                log.info("2 请求大于剩余");
                return;
            }
        }

        // 3.试探性分配
        TryAllocation(request);
        System.out.println(available);
        // 4.安全性算法
    }

    /* 试探性分配 */
    private void TryAllocation(ResourceRequest request) {
        int id = request.getId();
        List<Integer> requestSource = request.getSource();
        List<Integer> allocation = data.get(id).getAllocation();
        List<Integer> need = data.get(id).getNeed();

        for (int i = 0; i < RESOURCE_NUM; i++) {
            available.set(i, available.get(i) - requestSource.get(i));
            allocation.set(i, allocation.get(i) + requestSource.get(i));
            need.set(i, need.get(i) - requestSource.get(i));
        }
        data.get(id).setAllocation(allocation);
        data.get(id).setNeed(need);
    }

    /* 安全性检查算法 */
    private Boolean SecurityDetection() {
        log.info("进入安全性检测算法");

        return true;
    }
}
