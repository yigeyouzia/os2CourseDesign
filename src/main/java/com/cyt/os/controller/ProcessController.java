package com.cyt.os.controller;

import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.SystemKernel;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.process.algorithm.ProcessSchedulingAlgorithm;
import com.cyt.os.kernel.process.data.PCB;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author JuLiy
 * @date 2022/12/8 21:43
 */
public class ProcessController extends RootController {

    private static final Logger log = Logger.getLogger(ProcessController.class);

    private ProcessManager pcsMgr;

    @FXML
    private JFXComboBox<String> cbbSa;
    @FXML
    private JFXListView<PCB> listBlock;
    @FXML
    private JFXListView<PCB> listReady;
    @FXML
    private TableView<PCB> tableProcess;
    @FXML
    private TableColumn<PCB, Integer> tcArrivalTime;
    @FXML
    private TableColumn<PCB, Integer> tcPId;
    @FXML
    private TableColumn<PCB, Integer> tcPriority;
    @FXML
    private TableColumn<PCB, Double> tcProgress;
    @FXML
    private TableColumn<PCB, Integer> tcMemory;
    @FXML
    private TableColumn<PCB, List<Integer>> tcMaxR;
    @FXML
    private TableColumn<PCB, Integer> tcServiceTime;
    @FXML
    private TableColumn<PCB, ProcessStatus> tcStatus;
    @FXML
    private TableColumn<PCB, String> tcUId;
    @FXML
    private TableColumn<PCB, Integer> tcUsedTime;
    @FXML
    private Text txtCpuTime;

    @FXML
    void initialize() {
        pcsMgr = MainController.systemKernel.getProcessManager();
        // CPU时间
        txtCpuTime.textProperty().bind(pcsMgr.getCpuTimeProperty());


        // 进程表格
        initTable();
        // 调度算法选项
        initCbb();
        // 开启调度算法线程
        MainController.systemKernel.start();
    }

    public void createNewProcess(ActionEvent actionEvent) {
        PCB pcb = pcsMgr.create();
        tableProcess.getItems().add(pcb);
    }

    public void destroyProcess(ActionEvent actionEvent) {

    }

    public void suspendProcess(ActionEvent actionEvent) {

    }

    public void activeProcess(ActionEvent actionEvent) {

    }

    public void stopPSA(ActionEvent actionEvent) {
    }

    public void continuePSA(ActionEvent actionEvent) {

    }

    private void initTable() {
        //设置数据
        tcPId.setCellValueFactory(new PropertyValueFactory<>("pid"));
        tcUId.setCellValueFactory(new PropertyValueFactory<>("uid"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tcPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        tcProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        tcMemory.setCellValueFactory(new PropertyValueFactory<>("memorySize"));
        tcMaxR.setCellValueFactory(new PropertyValueFactory<>("maxR"));
        tcArrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        tcServiceTime.setCellValueFactory(new PropertyValueFactory<>("serviceTime"));
        tcUsedTime.setCellValueFactory(new PropertyValueFactory<>("usedTime"));
        //设置进度条
        tcProgress.setCellFactory(ProgressBarTableCell.forTableColumn());

        tcMaxR.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(List<Integer> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    this.setText(null);
                } else {
                    //  this.setText(item.get(0) + "-" + item.get(1) + "-" + item.get(2));
                    // TODO 资源分配
                    this.setText(1 + "-" + 1 + "-" + 1);
                }
            }
        });
    }

    private void initCbb() {
        cbbSa.getItems().addAll("FCFS", "SJF", "PJF", "RR");
        cbbSa.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            try {
                ProcessSchedulingAlgorithm psa =
                        (ProcessSchedulingAlgorithm) Class.forName("com.cyt.os.kernel.process.algorithm." + nv)
                                .getConstructor(List.class)
                                .newInstance(pcsMgr.getReadyQueue());
                pcsMgr.setPsa(psa);
            } catch (ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        cbbSa.getSelectionModel().select(0);
    }
}
