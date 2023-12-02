package com.cyt.os.common;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cyt
 * @date 2023/12/1
 */
public class Context {
    public static final Map<String, Stage> stageMap = new HashMap<>();
    public static final Map<String, Object> controllerMap = new HashMap<>();
}
