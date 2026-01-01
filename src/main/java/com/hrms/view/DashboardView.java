package com.hrms.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView extends VBox {

    public DashboardView(Stage stage, String userEmail) {
        setSpacing(12);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label welcome = new Label("Hoş geldin: " + userEmail);

        Button goReviewBtn = new Button("Performans Değerlendirme");
        goReviewBtn.setOnAction(e -> {
            // Bir sonraki adımda ReviewView'a bağlayacağız
        });

        Button logoutBtn = new Button("Çıkış");
        logoutBtn.setOnAction(e -> {
            stage.getScene().setRoot(new LoginView(stage));
            stage.setTitle("IK Sistemi - Giris");
        });

        getChildren().addAll(title, welcome, goReviewBtn, logoutBtn);
    }
}
