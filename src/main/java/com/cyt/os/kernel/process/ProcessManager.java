package com.cyt.os.kernel.process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cyt
 * @date 2023/11/24
 */
public class ProcessManager {
    /** 就绪队列 */
    private final ObservableList<PCB> readyQueue = FXCollections.observableArrayList();
    /** 阻塞队列 */
    private final ObservableList<PCB> blockQueue = FXCollections.observableArrayList();
    /** PCB表，便于检索PCB */
    private final List<PCB> PCBList = new ArrayList<>();

}
