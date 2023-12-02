package com.cyt.os.kernel.resourse.data;

import com.cyt.os.kernel.process.data.PCB;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cyt
 * @date 2023/11/29 22:01
 */
@Data
public class ResourceRequest {
    //请求资源的进程编号
    private int id;

    //请求的资源列表
    private List<Integer> source;

    /**
     * 随机生成进程请求资源
     *
     * @param id 进程pid
     * @return
     */
    public static ResourceRequest generateRequest(int id) {
        ResourceRequest resourceRequest = new ResourceRequest();
        resourceRequest.setId(id);
        ArrayList<Integer> list = new ArrayList<>();
        Collections.addAll(list, 1, 1, 1);
        resourceRequest.setSource(list);
        return resourceRequest;
    }

    public static ResourceRequest generateRemainRequest(PCB pcb) {
        ResourceRequest r = new ResourceRequest();
        r.setId(pcb.getPid());
        List<Integer> list = pcb.getNeedR();
        r.setSource(list);
        return r;
    }
}
