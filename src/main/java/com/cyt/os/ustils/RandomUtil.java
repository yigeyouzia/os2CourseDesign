package com.cyt.os.ustils;

import java.util.Random;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class RandomUtil {

    private static final Random random = new Random();


    public static final String[] UidName = new String[]{
            "QQ音乐", "CF", "LOL", "金铲铲", "云顶之弈", "任务管理器", "IDEA", "Eclipse",
            "Clion", "Pycharm", "GoLand", "VsCode", "PS", "Visual Paradigm", "Chrome", "Edge",
            "nginx", "WeGame", "steam", "PUBG", "CS2", "三国杀", "原神", "迅雷",
    };

    public static Integer UidNameLength = 0;

    static {
        UidNameLength = UidName.length;
    }

    /* 进程标志符 */
    private static int count = 0;

    public static int getRandomPid() {
        return 10000 + (int) (89999 * Math.random());
    }

    public static String getRandomUid() {
        int index = RandomUtil.random.nextInt(UidNameLength);
        return RandomUtil.UidName[index];
    }
}
