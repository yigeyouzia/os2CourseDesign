package com.cyt.os;

import com.cyt.os.kernel.process.PCB;
import com.cyt.os.kernel.process.algorithm.FCFS;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        ObservableList<PCB> readyQueue = FXCollections.observableArrayList();
        readyQueue.addAll(
                new PCB("A", 1, 1, 10),
                new PCB("B", 2, 0, 20),
                new PCB("C", 3, 3, 30));
        FCFS fcfs = new FCFS(readyQueue);
        fcfs.run();
    }

    public static void main(String[] args) {
        launch();
    }
}