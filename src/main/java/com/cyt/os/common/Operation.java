package com.cyt.os.common;

import com.cyt.os.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * @author cyt
 * @date 2023/12/1
 */
public class Operation {

    /**
     * 创建新窗口
     *
     * @param fxmlName     要加载的fxml文件名(无需后缀)
     * @param title        窗口标题
     * @param isResizeable 窗口是否可缩放
     * @return 创建好的窗口对象
     */
    public static Stage createStage(String fxmlName, String title, boolean isResizeable) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("view/" + fxmlName + ".fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        Scene scene = new Scene(root, 1400, 778);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(isResizeable);
//        if (!"Main".equals(fxmlName)) {
//            stage.setAlwaysOnTop(true);
//        }
        Context.stageMap.put(fxmlName, stage);
        return stage;
    }

    public static void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
