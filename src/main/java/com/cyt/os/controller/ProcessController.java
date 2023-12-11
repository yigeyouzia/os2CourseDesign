package com.cyt.os.controller;

import com.cyt.os.common.Config;
import com.cyt.os.common.Operation;
import com.cyt.os.enums.MemoryStatus;
import com.cyt.os.enums.ProcessStatus;
import com.cyt.os.exception.PageNotFoundException;
import com.cyt.os.kernel.memory.algorithm.MemoryAllocationAlgorithm;
import com.cyt.os.kernel.memory.data.MemoryBlock;
import com.cyt.os.kernel.page.PageManager;
import com.cyt.os.kernel.process.ProcessManager;
import com.cyt.os.kernel.process.algorithm.ProcessSchedulingAlgorithm;
import com.cyt.os.kernel.process.data.PCB;
import javafx.application.Platform;
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

    /* 外存 */
    @FXML
    private VBox externalStorage;

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
    /* 页面管理器 */
    private PageManager pageMgr;

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
        pageMgr = MainController.systemKernel.getPageManager();
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
        /* 磁盘条可视化 */
        initExternalStorage();
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

    /* 添加进程 */
    public void createNewProcess() {
        PCB pcb = pcsMgr.create();
        tableProcess.getItems().add(pcb);
        tableReady.setItems(pcsMgr.getReadyQueue());
    }

    public void addNewProcess(PCB pcb) {
        tableProcess.getItems().add(pcb);
    }

    public void destroyProcess(ActionEvent actionEvent) {
        log.error("销毁进程");
        try {
            PCB pcb = getPCB();
            if (pcb != null) {
                pcsMgr.destroy(pcb.getPid());
            }
        } catch (PageNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public void suspendProcess(ActionEvent actionEvent) {
        log.warn("挂起进程");
        try {
            PCB pcb = getPCB();
            if (pcb != null) {
                pcsMgr.suspend(pcb.getPid());
                tableSuspend.setItems(pcsMgr.getBlockQueue());
            }
        } catch (PageNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public void activeProcess(ActionEvent actionEvent) {
        log.warn("激活进程");
        try {
            PCB pcb = getPCB();
            if (pcb != null) {
                pcsMgr.active(pcb.getPid());
            }
        } catch (PageNotFoundException e) {
            log.error(e.getMessage());
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
            if (pcb.getPid() <= Config.PAGE_MAX_NUM) {
                Operation.showErrorAlert("不能选择页面！");
                throw new PageNotFoundException("不能选择页面");
            }
        }
        log.info(pcb);
        return pcb;
    }

    /* 选择页面 */
    private PCB getPage() {
        PCB pcb = null;
        if (tableProcess.getSelectionModel().isEmpty()) {
            Operation.showErrorAlert("请先选中一个页面！");
        } else {
            pcb = tableProcess.getSelectionModel().getSelectedItem();
            if (pcb.getPid() >= Config.PAGE_MAX_NUM) {
                Operation.showErrorAlert("不能选择进程！");
                throw new PageNotFoundException("不能选择进程");
            }
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


        a.addAll(new PieChart.Data("可用资源A", (double) ra / Config.RESOURCE_A),
                new PieChart.Data("占用资源A", (double) (Config.RESOURCE_A - ra) / 10));
        b.addAll(new PieChart.Data("可用资源B", (double) rb / Config.RESOURCE_B),
                new PieChart.Data("占用资源B", (double) (Config.RESOURCE_B - rb) / Config.RESOURCE_B));
        c.addAll(new PieChart.Data("可用资源C", (double) rc / Config.RESOURCE_C),
                new PieChart.Data("占用资源C", (double) (Config.RESOURCE_C - rc) / Config.RESOURCE_C));
        ResourcePieA.setData(a);
        ResourcePieB.setData(b);
        ResourcePieC.setData(c);
    }


    /* 更新内存 */
    public void updateMemoryBank() {
        // 清除原来的矩形
        memoryBank.getChildren().clear();
        ObservableList<MemoryBlock> memoryList = MainController.systemKernel
                .getMemoryManager()
                .getMemoryList();
        // 遍历添加矩形
        memoryList.forEach(item -> {
            Rectangle r = new Rectangle(Config.BASE_MEMORY_WIDTH,
                    (double) item.getSize() / Config.BASE_MEMORY_SCALE);
            if (item.getStatus() == MemoryStatus.FREE) {
                r.setFill(Color.GREEN);
            } else {
                r.setFill(Color.RED);
            }
            memoryBank.getChildren().add(r);

        });
    }

    /* 初始化外存 */
    private void initExternalStorage() {
        ObservableList<MemoryBlock> memoryList = MainController.systemKernel
                .getMemoryManager()
                .getMemoryList();
        // 遍历添加矩形
        for (int i = 0; i < memoryList.size(); i++) {
            Rectangle r = new Rectangle(Config.BASE_MEMORY_WIDTH,
                    (double) memoryList.get(i).getSize() / Config.BASE_STORAGE_SCALE);
            if (i % 2 == 0) {
                r.setFill(Color.GREEN);
            } else {
                r.setFill(Color.RED);
            }
            externalStorage.getChildren().add(r);
        }
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
        // 4.换入换出
        items.get(4).setOnAction(event -> SwapInAndOut());
    }

    /* 换入换出算法 */
    private void SwapInAndOut() {
        PCB currentPage = pageMgr.createPage();

        ObservableList<PCB> items = tableProcess.getItems();

        int index = 0;
        boolean isExist = false;
        for (PCB page : items) {
            if (page.getPid() == currentPage.getPid()) {
                isExist = true;
                break;
            }
            index++;
        }

        if (isExist) {
            tableProcess.getItems().remove(index);
        } else if (items.size() >= Config.LRU_CAPACITY) {
            tableProcess.getItems().remove(0);
        }
        tableProcess.getItems().add(currentPage);
    }

    /* 回收页面 */
    public void recyclePage() {
        try {
            PCB pcb = getPage();
            if (pcb != null) {
                Platform.runLater(() -> MainController.systemKernel
                        .getMemoryManager()
                        .getMAA()
                        .release(pcb.getPid()));
                tableProcess.getItems().remove(pcb);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}