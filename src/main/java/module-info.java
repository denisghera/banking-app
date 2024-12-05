module ro.uvt.dp.gui.bankingapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    opens ro.uvt.dp.gui to javafx.fxml;
    exports ro.uvt.dp.gui;
}