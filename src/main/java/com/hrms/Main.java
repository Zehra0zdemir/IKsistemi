package com.hrms;

import com.hrms.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        var root = new LoginView(stage);
        var scene = new Scene(root, 420, 320);

        stage.setTitle("IK Sistemi - Giris");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
