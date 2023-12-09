package com.cyt.os.controller;

import com.cyt.os.common.Config;
import com.cyt.os.common.Operation;
import com.cyt.os.enums.MemoryStatus;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.kernel.memory.algorithm.MemoryAllocationAlgorithm;
import com.cyt.os.kernel.memory.data.MemoryBlock;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.process.algorithm.ProcessSchedulingAlgorithm;
import com.cyt.os.kernel.process.data.PCB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    /* 使用工具 */
    @FXML
    private Menu toolBar;
    /* 内存条可视化 */
    @FXML
    private VBox memoryBank;
    /* 内存分配算法 */
    @FXML
    private Menu memoryAlg;

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
    @FXML
    private PieChart ResourcePieB;
    @FXML
    private PieChart ResourcePieC;
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
        initReadyTable();
        initBlockTable();
        /* 内存调度算法  */
        initMemoryAlg();
        /* 内存条可视化 */
        updateMemoryBank();
        /* 资源饼图 */
        updateResource(0, 0, 0);
        /* 使用工具 */
        initToolBar();
        // 开启调度算法线程
        MainController.systemKernel.start();
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
        tableReady.setItems(pcsMgr.getReadyQueue());
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
        log.warn("暂停进程");
        pcsMgr.stopPSA();
    }

    public void continuePSA(ActionEvent actionEvent) {
        log.warn("继续进程");
        pcsMgr.continuePSA();
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
                // 得到文本信息
                MenuItem source = (MenuItem) event.getSource();
                String text = source.getText();
                try {
                    ProcessSchedulingAlgorithm psa = (ProcessSchedulingAlgorithm) Class
                            .forName("com.cyt.os.kernel.process.algorithm." + text)
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

    private void initMemoryAlg() {
        ObservableList<MenuItem> items = memoryAlg.getItems();
        items.forEach(item -> {
            //item1的单击事件
            item.setOnAction(event -> {
                MenuItem source = (MenuItem) event.getSource();
                String text = source.getText();
                try {
                    ObservableList<MemoryBlock> memoryList = MainController.systemKernel
                            .getMemoryManager()
                            .getMemoryList();
                    MemoryAllocationAlgorithm maa = (MemoryAllocationAlgorithm) Class
                            .forName("com.cyt.os.kernel.memory.algorithm." + text)
                            .getConstructor(List.class)
                            .newInstance(memoryList);
                    MainController.systemKernel
                            .getMemoryManager()
                            .setMAA(maa);
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
        ObservableList<PieChart.Data> b = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> c = FXCollections.observableArrayList();


        a.addAll(new PieChart.Data("可用资源A", (double) ra / 10),
                new PieChart.Data("占用资源A", (double) (10 - ra) / 10));
        b.addAll(new PieChart.Data("可用资源B", (double) rb / 10),
                new PieChart.Data("占用资源B", (double) (10 - rb) / 10));
        c.addAll(new PieChart.Data("可用资源C", (double) rc / 10),
                new PieChart.Data("占用资源C", (double) (10 - rc) / 10));
        ResourcePieA.setData(a);
        ResourcePieB.setData(b);
        ResourcePieC.setData(c);
    }


    /* 更新内存 */
    public void updateMemoryBank() {
        memoryBank.setPadding(new Insets(30, 10, 10, 10));
        // memoryBank.setSpacing(20);
        // 清除原来的矩形
        memoryBank.getChildren().clear();
        ObservableList<MemoryBlock> memoryList = MainController.systemKernel
                .getMemoryManager()
                .getMemoryList();
        // 遍历添加矩形
        memoryList.forEach(item -> {
            Rectangle r = new Rectangle(Config.BASE_MEMORY_HEIGHT,
                    (double) item.getSize() / Config.BASE_MEMORY_SCALE);
            if (item.getStatus() == MemoryStatus.FREE) {
                r.setFill(Color.GREEN);
            } else {
                r.setFill(Color.RED);
            }
            memoryBank.getChildren().add(r);
        });
    }

    /* 使用工具功能 */
    private void initToolBar() {
        ObservableList<MenuItem> items = toolBar.getItems();
        // 1.一键清空所有实例
        items.get(1).setOnAction(event -> {
            log.warn("清空实例");
            tableProcess.getItems().clear();
        });
        // 2.查看内存分布
        items.get(2).setOnAction(event -> MainController.systemKernel
                .getMemoryManager()
                .getMAA()
                .showMemory());
        // 3.重置cpu时间为0
        items.get(3).setOnAction(event -> MainController.systemKernel
                .getProcessManager()
                .reSetCpuTimeProperty());
    }
}