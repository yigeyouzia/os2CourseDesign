package com.cyt.os.common;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JuLiy
 * @date 2022/12/8 21:43
 */
public class Context {
    public static final Map<String, Stage> stageMap = new HashMap<>();
    public static final Map<String, Object> controllerMap = new HashMap<>();
}
