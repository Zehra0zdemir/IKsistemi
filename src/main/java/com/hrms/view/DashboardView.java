package com.hrms.view;

import com.hrms.controller.ReportController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardView extends BorderPane {

    private final Stage stage;
    private final String userEmail;
    private final VBox statsContainer = new VBox(20);

    public DashboardView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        // Sidebar
        VBox sidebar = createSidebar();
        setLeft(sidebar);

        // Main content
        VBox mainContent = createMainContent();
        setCenter(mainContent);

        loadStats();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-padding: 20;");

        // Logo/Title
        Label logo = new Label("İK Sistemi");
        logo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer1 = new Region();
        spacer1.setPrefHeight(30);

        // Navigation buttons
        Button dashboardBtn = createNavButton("📊 Dashboard", true);
        dashboardBtn.setOnAction(e -> {/* Already on dashboard */});

        Button employeesBtn = createNavButton("👥 Çalışanlar", false);
        employeesBtn.setOnAction(e -> openEmployeeManagement());

        Button attendanceBtn = createNavButton("📋 Yoklama", false);
        attendanceBtn.setOnAction(e -> openAttendance());

        Button reviewBtn = createNavButton("⭐ Değerlendirme", false);
        reviewBtn.setOnAction(e -> openReview());

        Button payrollBtn = createNavButton("💰 Bordro", false);
        payrollBtn.setOnAction(e -> openPayroll());

        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);

        // User info
        VBox userInfo = new VBox(5);
        userInfo.setStyle("-fx-background-color: #334155; -fx-padding: 15; -fx-background-radius: 8;");

        Label userLabel = new Label("Kullanıcı");
        userLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        Label emailLabel = new Label(userEmail);
        emailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600;");
        emailLabel.setWrapText(true);

        userInfo.getChildren().addAll(userLabel, emailLabel);

        Button logoutBtn = createNavButton("🚪 Çıkış", false);
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 12 20; " +
                          "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> logout());

        sidebar.getChildren().addAll(
                logo, spacer1,
                dashboardBtn, employeesBtn, attendanceBtn, reviewBtn, payrollBtn,
                spacer2, userInfo, logoutBtn
        );

        return sidebar;
    }

    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        
        if (active) {
            btn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 12 20; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-padding: 12 20; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; " +
                                                    "-fx-padding: 12 20; -fx-border-radius: 8; -fx-background-radius: 8;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; " +
                                                   "-fx-padding: 12 20; -fx-border-radius: 8; -fx-background-radius: 8;"));
        }
        return btn;
    }

    private VBox createMainContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8fafc;");

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerText = new VBox(5);
        Label title = new Label("Dashboard");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Sistem istatistiklerine hoş geldiniz");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        headerText.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Yenile");
        refreshBtn.setOnAction(e -> loadStats());

        header.getChildren().addAll(headerText, spacer, refreshBtn);

        // Stats container
        statsContainer.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(statsContainer, Priority.ALWAYS);

        content.getChildren().addAll(header, statsContainer);

        return content;
    }

    private void loadStats() {
        var stats = ReportController.getStats();

        // Clear and rebuild stats
        statsContainer.getChildren().clear();

        // Stats grid
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Stat cards
        grid.add(createStatCard("👥 Toplam Çalışan", String.valueOf(stats.totalEmployees()), "#3b82f6"), 0, 0);
        grid.add(createStatCard("⭐ Toplam Değerlendirme", String.valueOf(stats.totalReviews()), "#10b981"), 1, 0);
        grid.add(createStatCard("📊 Ortalama Puan", String.format("%.2f", stats.avgRating()), "#f59e0b"), 0, 1);
        grid.add(createStatCard("⚠️ Düşük Performans", String.valueOf(stats.lowPerformanceCount()), "#ef4444"), 1, 1);

        statsContainer.getChildren().add(grid);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(12);
        card.setPrefSize(250, 150);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + color + "; -fx-border-width: 0 0 0 4; " +
                     "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 24; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: 600;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 36px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);

        return card;
    }

    private void openEmployeeManagement() {
        stage.getScene().setRoot(new ModernEmployeeView(stage, userEmail));
        stage.setTitle("İK Sistemi - Çalışan Yönetimi");
    }

    private void openAttendance() {
        stage.getScene().setRoot(new AttendanceView(stage, userEmail));
        stage.setTitle("İK Sistemi - Yoklama");
    }

    private void openReview() {
        stage.getScene().setRoot(new ReviewView(stage, userEmail));
        stage.setTitle("İK Sistemi - Performans Değerlendirme");
    }

    private void openPayroll() {
        stage.getScene().setRoot(new PayrollView(stage, userEmail));
        stage.setTitle("İK Sistemi - Bordro");
    }

    private void logout() {
        stage.getScene().setRoot(new LoginView(stage));
        stage.setTitle("İK Sistemi - Giriş");
    }
}
