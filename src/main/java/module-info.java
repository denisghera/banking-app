module ro.uvt.dp.gui.bankingapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens ro.uvt.dp.gui to javafx.fxml;
    exports ro.uvt.dp.gui;
}