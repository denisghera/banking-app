package ro.uvt.dp.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.uvt.dp.entities.Bank;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Bank bank = Bank.getInstance("UVT_DP");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Banking Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
