package Resource;

import com.cyt.os.kernel.resourse.ResourceManager;
import com.cyt.os.kernel.resourse.algorithm.BankerAlgorithm;
import com.cyt.os.kernel.resourse.data.BankerData;
import com.cyt.os.kernel.resourse.data.ResourceRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author cyt
 * @date 2023/11/29 21:58
 */
public class Test {

    public static void main(String[] args) {
        // 1.资源管理
        ResourceManager resourceManager = new ResourceManager();
        System.out.println(resourceManager.getResourceA());
        // 2.银行家算法
        BankerAlgorithm ba = new BankerAlgorithm(resourceManager);


        // 3.生成资源请求
        ResourceRequest request = ResourceRequest.generateRequest(520);
        BankerData bankerData = init();
        ba.getData().put(520, bankerData);
        // 4.算法
        System.out.println("初始数据： " + bankerData);
        System.out.println("请求数据： " + request);
        ba.bankerAlgorithm(request);
        System.out.println(bankerData);
    }

    private static BankerData init() {
        Random random = new Random();
        int ra = random.nextInt(3) + 3;
        int rb = random.nextInt(2) + 3;
        int rc = random.nextInt(1) + 3;

        //更新银行家算法中的数据
        List<Integer> maxList = new ArrayList<>();
        Collections.addAll(maxList, ra, rb, rc);


        List<Integer> needList = new ArrayList<>();
        Collections.addAll(needList, ra, rb, rc);

        List<Integer> alocList = new ArrayList<>();
        Collections.addAll(alocList, 0, 0, 0);


        BankerData data = new BankerData();
        data.setMax(maxList);
        data.setNeed(needList);
        data.setAllocation(alocList);
        return data;
    }
}
