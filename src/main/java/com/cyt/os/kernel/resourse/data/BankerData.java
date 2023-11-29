package com.cyt.os.kernel.resourse.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 每个进程对应银行家算法数据结构
 *
 * @author cyt
 * @date 2023/11/29 22:07
 */
@Data
public class BankerData {
    /* 最大需要 */
    private List<Integer> max = new ArrayList<>();
    /* 还需要资源 */
    private List<Integer> need = new ArrayList<>();
    /* 已分配资源 */
    private List<Integer> allocation = new ArrayList<>();
}
