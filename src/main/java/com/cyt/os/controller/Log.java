package com.cyt.os.controller;

/**
 * @author cyt
 * @date 2023/11/30 12:37
 */

import java.io.*;
import java.nio.charset.Charset;

import com.cyt.os.ustils.ConsoleOutputRedirector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;

public class Log {

    private static final Logger log = Logger.getLogger(Log.class);


    @FXML
//    TextArea console = new TextArea();
    TextArea console = ConsoleOutputRedirector.wrap(new TextArea(), 100L);


    public void button(ActionEvent actionEvent) {
        console.appendText("11");
        log.info("info");
        log.warn("warn");
        log.error("error");
        log.debug("debug");
        log.fatal("fatal");
        System.out.println(111);
    }
}
