package com.hrms.view;

import com.hrms.controller.ReportController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView extends VBox {

    private final Stage stage;
    private final String userEmail;

    private final Label statsLabel = new Label();

    public DashboardView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        setSpacing(12);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label welcome = new Label("Hoş geldin: " + userEmail);

        Button refreshBtn = new Button("İstatistikleri Yenile");
        refreshBtn.setOnAction(e -> loadStats());

        Button goReviewBtn = new Button("Performans Değerlendirme");
        goReviewBtn.setOnAction(e -> openReview());

        Button goPayrollBtn = new Button("Bordro / Maaş");
        goPayrollBtn.setOnAction(e -> openPayroll());


        Button logoutBtn = new Button("Çıkış");
        logoutBtn.setOnAction(e -> logout());

        statsLabel.setStyle("-fx-font-size: 13px;");
        loadStats();

        getChildren().addAll(title, welcome, statsLabel, refreshBtn, goReviewBtn, goPayrollBtn, logoutBtn);
    }

    private void loadStats() {
        var s = ReportController.getStats();
        statsLabel.setText(
                "Toplam Çalışan: " + s.totalEmployees() + "\n" +
                        "Toplam Değerlendirme: " + s.totalReviews() + "\n" +
                        "Ortalama Puan: " + String.format("%.2f", s.avgRating()) + "\n" +
                        "Düşük Performans (<3.0): " + s.lowPerformanceCount()
        );
    }

    private void openReview() {
        stage.getScene().setRoot(new ReviewView(stage, userEmail));
        stage.setTitle("IK Sistemi - Performans Degerlendirme");
    }

    private void openPayroll() {
    stage.getScene().setRoot(new PayrollView(stage, userEmail));
    stage.setTitle("IK Sistemi - Bordro");
    }


    private void logout() {
        stage.getScene().setRoot(new LoginView(stage));
        stage.setTitle("IK Sistemi - Giris");
    }
}
