package com.cyt.os.controller;

import com.cyt.os.common.Context;
import com.cyt.os.common.Operation;
import com.cyt.os.kernel.SystemKernel;
import javafx.fxml.FXML;

/**
 * @author cyt
 * @date 2023/11/29
 */
public class MainController {
    public static SystemKernel systemKernel = new SystemKernel();

    @FXML
    void initialize() {
        Operation.createStage   ("Process", "进程管理器", false)
                .initOwner(Context.stageMap.get("Main"));
        Context.stageMap.get("Process").show();
    }
}
