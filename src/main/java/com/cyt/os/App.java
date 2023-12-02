package com.cyt.os;

import com.cyt.os.common.Context;
import com.cyt.os.common.Operation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/Log.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/ProcessTest.fxml"));

//        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/Process.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1400, 778);
//        primaryStage.setTitle("Hello!");
//        primaryStage.setScene(scene);
//        primaryStage.show();

        primaryStage = Operation.createStage("Main", "系统", false);
        //primaryStage.setMaximized(true);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

//        primaryStage = Operation.createStage("Process", "进程系统", false);
//        //primaryStage.setMaximized(true);
//        primaryStage.initStyle(StageStyle.TRANSPARENT);
//        primaryStage.setOnCloseRequest(event -> {
//            Platform.exit();
//            System.exit(0);
//        });
//        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}