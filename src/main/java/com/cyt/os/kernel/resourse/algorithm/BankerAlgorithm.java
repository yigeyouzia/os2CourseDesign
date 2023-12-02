package com.cyt.os.kernel.resourse.algorithm;

import com.cyt.os.exception.BAException;
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
        log.info("**********进程P" + id + "  进入银行家算法 *************");
        System.out.println(available);

        // 1.需求
        for (int i = 0; i < RESOURCE_NUM; i++) {
            if (request.getSource().get(i) > data.get(id).getNeed().get(i)) {
                log.error("1 请求大于需要");
                throw new BAException("进程P" + id + "请求的资源超过自己需要资源--" +
                        request.getSource().get(i) + ">" + data.get(id).getNeed().get(i));
            }
        }

        // 2.可用 available
        for (int i = 0; i < RESOURCE_NUM; i++) {
            if (request.getSource().get(i) > available.get(i)) {
                log.warn("2 请求大于剩余");
                throw new BAException("进程P" + id + "请求的资源超过可用资源---" +
                        request.getSource() + ">" + available.get(i));
            }
        }

        // 3.试探性分配
        TryAllocation(request);
        // 4.安全性算法
        if (SecurityDetection()) {
            log.info("进程P" + id + "  的资源请求：" + request.getSource() + " 通过");
            // 调用manager分配
            resourceManager.allocate(request);
        } else {
            // 回滚
            rollbackAllocation(request);
            log.error("进程P" + id + "的资源请求：" + request.getSource() + " 不通过");
            log.error("当前available:" + available);
            throw new BAException("进程P" + id + "的资源请求：" + request.getSource() + " 不通过");
        }
    }


    /**
     * 试探性分配
     *
     * @param request 请求资源
     */
    private void TryAllocation(ResourceRequest request) {
        int id = request.getId();
        log.info("进程P" + id + "  尝试试探性分配");

        List<Integer> requestSource = request.getSource();
        List<Integer> allocation = data.get(id).getAllocation();
        List<Integer> need = data.get(id).getNeed();

        for (int i = 0; i < RESOURCE_NUM; i++) {
            // available
            available.set(i, available.get(i) - requestSource.get(i));
            // allocation
            allocation.set(i, allocation.get(i) + requestSource.get(i));
            // need
            need.set(i, need.get(i) - requestSource.get(i));
        }
        data.get(id).setAllocation(allocation);
        data.get(id).setNeed(need);
        log.info("试探完毕： ");
        log.info("available:" + available);
    }

    /**
     * 回滚资源 （+-和上面相反）
     *
     * @param request 请求资源
     */
    private void rollbackAllocation(ResourceRequest request) {
        int id = request.getId();
        List<Integer> requestSource = request.getSource();
        List<Integer> allocation = data.get(id).getAllocation();
        List<Integer> need = data.get(id).getNeed();

        for (int i = 0; i < RESOURCE_NUM; i++) {
            // available
            available.set(i, available.get(i) + requestSource.get(i));
            // allocation
            allocation.set(i, allocation.get(i) - requestSource.get(i));
            // need
            need.set(i, need.get(i) + requestSource.get(i));
        }
        data.get(id).setAllocation(allocation);
        data.get(id).setNeed(need);
    }

    /* 安全性检查算法 */
    private Boolean SecurityDetection() {
        log.info("进入安全性检测算法");
        ArrayList<Integer> work = new ArrayList<>(available);
        // key:进程id value：是都被分配
        HashMap<Integer, Boolean> finish = new HashMap<>();
        // 初始化为false
        data.forEach((id, bankerData) -> finish.put(id, false));

        int processNum = data.size();
        for (int i = 0; i < processNum; i++) {
            // 是否存在进程可以回收 need < work
            boolean flag = false;
            for (Map.Entry<Integer, BankerData> entry : data.entrySet()) {
                List<Integer> need = entry.getValue().getNeed();
                List<Integer> allocation = entry.getValue().getAllocation();
                Integer id = entry.getKey();

                // 已经分配
                if (finish.get(id)) {
                    continue;
                }

                // 遍历每个资源  need < work ?
                int j = 0;
                for (; j < RESOURCE_NUM; j++) {
                    if (need.get(j) > work.get(j)) {
                        break;
                    }
                }

                // need 都< work 可以回收
                if (j == RESOURCE_NUM) {
                    for (j = 0; j < RESOURCE_NUM; j++) {
                        // 释放资源
                        work.set(j, work.get(j) + allocation.get(j));
                    }
                    finish.put(id, true);
                    // 存在可回收资源
                    flag = true;
                    log.info("资源  " + id + " 回收完毕");
                }
                // 不存在可回收  跳出循环
                if (!flag) {
                    break;
                }
            }
            // 判断所有finish是否为true： 是则安全
            for (Map.Entry<Integer, Boolean> entry : finish.entrySet()) {
                if (!entry.getValue()) {
                    log.error("此时资源不安全");
                    return false;
                }
            }
            log.info("此时资源安全");
            return true;
        }
        return true;
    }

    public void release(List<Integer> list) {
        for (int i = 0; i < RESOURCE_NUM; i++) {
            available.set(i, available.get(i) + list.get(i));
        }
    }
}
