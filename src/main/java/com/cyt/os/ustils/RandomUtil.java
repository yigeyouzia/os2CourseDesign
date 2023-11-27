package com.cyt.os.ustils;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class RandomUtil {
    /* 进程标志符 */
    private static int count = 0;
    public static int getRandomPid() {
        return 10000 + (int)(89999 * Math.random());
    }
}
