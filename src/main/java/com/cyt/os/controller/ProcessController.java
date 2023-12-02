package com.cyt.os.controller;

import com.cyt.os.common.Operation;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.process.algorithm.ProcessSchedulingAlgorithm;
import com.cyt.os.kernel.process.data.PCB;
import com.cyt.os.kernel.resourse.ResourceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author JuLiy
 * @date 2022/12/8 21:43
 */
public class ProcessController extends RootController {

    private static final Logger log = Logger.getLogger(ProcessController.class);

    /* 调度算法 */
    @FXML
    private Menu processAlg;
    /* 阻塞队列 */
    @FXML
    private TableView<PCB> tableSuspend;
    @FXML
    private TableColumn<PCB, Integer> tcUId1;
    @FXML
    private TableColumn<PCB, Integer> tcStatus1;

    /* 就绪队列 */
    @FXML
    private TableView<PCB> tableReady;

    @FXML
    private TableColumn<PCB, Integer> tcUId2;

    @FXML
    private TableColumn<PCB, Integer> tcStatus2;

    /* 资源 */
    @FXML
    private PieChart ResourcePieA;
    /* 进程管理器 */
    private ProcessManager pcsMgr;

    /* 进程表格列 */
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
    public TableColumn<PCB, Integer> tcNeedR;

    /* CPU时间 */
//    @FXML
//    private Text txtCpuTime;

    @FXML
    void initialize() {
        pcsMgr = MainController.systemKernel.getProcessManager();
        // CPU时间
//        txtCpuTime.textProperty().bind(pcsMgr.getCpuTimeProperty());

        // 进程表格
        initTable();
        // 调度算法选项
        initCbb();
        initReadyTable();
        initBlockTable();
        /* 资源饼图 */
        initPie();
        /* 内存条 */
        // 开启调度算法线程
        MainController.systemKernel.start();
    }

    public void initPie() {
        ObservableList<PieChart.Data> answer = FXCollections.observableArrayList();
        ResourceManager rm = MainController.systemKernel.getResourceManager();
        answer.addAll(new PieChart.Data("可用资源A", 1.0),
                new PieChart.Data("占用资源A", 0.0));
        ResourcePieA.setData(answer);
    }

    private void initReadyTable() {
        tcUId1.setCellValueFactory(new PropertyValueFactory<>("uid"));
        tcStatus1.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableReady.setItems(pcsMgr.getReadyQueue());
    }

    private void initBlockTable() {
        tcUId2.setCellValueFactory(new PropertyValueFactory<>("uid"));
        tcStatus2.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableSuspend.setItems(pcsMgr.getBlockQueue());
    }

    public void createNewProcess(ActionEvent actionEvent) {
        PCB pcb = pcsMgr.create();
        tableProcess.getItems().add(pcb);
//        tableReady.setItems(pcsMgr.getReadyQueue());
    }

    public void destroyProcess(ActionEvent actionEvent) {
        log.error("销毁进程");
        PCB pcb = getPCB();
        if (pcb != null) {
            pcsMgr.destroy(pcb.getPid());
        }
    }

    public void suspendProcess(ActionEvent actionEvent) {
        log.warn("挂起进程");
        PCB pcb = getPCB();
        if (pcb != null) {
            pcsMgr.suspend(pcb.getPid());
            tableSuspend.setItems(pcsMgr.getBlockQueue());
        }
    }

    public void activeProcess(ActionEvent actionEvent) {
        log.warn("激活进程");
        PCB pcb = getPCB();
        if (pcb != null) {
            pcsMgr.active(pcb.getPid());
        }
    }

    public void stopPSA(ActionEvent actionEvent) {
        log.warn("禁止进程");
        PCB pcb = getPCB();
        ObservableList<PCB> blockQueue = MainController.systemKernel.getProcessManager().getBlockQueue();
        ResourceManager resourceManager = MainController.systemKernel.getResourceManager();
        System.out.println(blockQueue);
    }

    public void continuePSA(ActionEvent actionEvent) {
        log.warn("继续进程");
        PCB pcb = getPCB();
        MainController.systemKernel.getMemoryManager().getMAA().showMemory();
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
        tcNeedR.setCellValueFactory(new PropertyValueFactory<>("needR"));
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
                    this.setText(item.get(0) + "-" + item.get(1) + "-" + item.get(2));
                }
            }
        });
    }

    private void initCbb() {
        ObservableList<MenuItem> items = processAlg.getItems();
        items.forEach(item -> {
            //item1的单击事件
            item.setOnAction(event -> {
                MenuItem source = (MenuItem) event.getSource();
                String text = source.getText();
                try {
                    ProcessSchedulingAlgorithm psa = (ProcessSchedulingAlgorithm) Class.forName("com.cyt.os.kernel.process.algorithm." + text)
                            .getConstructor(List.class)
                            .newInstance(pcsMgr.getReadyQueue());
                    pcsMgr.setPsa(psa);
                } catch (ClassNotFoundException | NoSuchMethodException |
                         InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    /**
     * 获取选中进程的PCB
     */
    private PCB getPCB() {
        PCB pcb = null;
        if (tableProcess.getSelectionModel().isEmpty()) {
            Operation.showErrorAlert("请先选中一个进程！");
        } else {
            pcb = tableProcess.getSelectionModel().getSelectedItem();
        }
        log.info(pcb);
        return pcb;
    }

    /* 更新饼图 */
    public void updateResource(int ra, int rb, int rc) {
        log.info("更新饼图： " + ra + rb + rc);
        ObservableList<PieChart.Data> a = FXCollections.observableArrayList();
        a.addAll(new PieChart.Data("可用资源A", (double) ra / 10),
                new PieChart.Data("占用资源A", (double) (10 - ra) / 10));
        ResourcePieA.setData(a);
    }
}