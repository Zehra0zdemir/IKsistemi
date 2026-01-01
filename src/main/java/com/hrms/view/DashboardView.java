package com.hrms.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView extends VBox {

    private final Stage stage;
    private final String userEmail;

    public DashboardView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        setSpacing(12);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label welcome = new Label("Hoş geldin: " + userEmail);

        Button goReviewBtn = new Button("Performans Değerlendirme");
        goReviewBtn.setOnAction(e -> openReview());

        Button logoutBtn = new Button("Çıkış");
        logoutBtn.setOnAction(e -> logout());

        getChildren().addAll(title, welcome, goReviewBtn, logoutBtn);
    }

    private void openReview() {
        stage.getScene().setRoot(new ReviewView(stage, userEmail));
        stage.setTitle("IK Sistemi - Performans Degerlendirme");
    }

    private void logout() {
        stage.getScene().setRoot(new LoginView(stage));
        stage.setTitle("IK Sistemi - Giris");
    }
}
