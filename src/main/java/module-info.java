module com.cyt.ctqos {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires log4j;
    requires com.jfoenix;


    opens com.cyt.os to javafx.fxml;
    exports com.cyt.os;
    exports com.cyt.os.controller;
    opens com.cyt.os.controller to javafx.fxml;
}