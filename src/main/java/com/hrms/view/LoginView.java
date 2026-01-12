package com.hrms.view;

import com.hrms.controller.AuthController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView extends StackPane {
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label messageLabel = new Label();

    public LoginView(Stage stage) {
        // Main container
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setMaxWidth(450);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                               "-fx-border-radius: 12; -fx-background-radius: 12; " +
                               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);");
        
        // Title section
        VBox titleSection = new VBox(8);
        titleSection.setAlignment(Pos.CENTER);
        
        Label title = new Label("İnsan Kaynakları Sistemi");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Hesabınıza giriş yapın");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        
        titleSection.getChildren().addAll(title, subtitle);
        
        // Form section
        VBox formSection = new VBox(16);
        formSection.setAlignment(Pos.CENTER);
        
        // Email field
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("E-posta");
        emailLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155;");
        emailField.setPromptText("ornek@sirket.com");
        emailField.setPrefHeight(45);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        emailBox.getChildren().addAll(emailLabel, emailField);
        
        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Şifre");
        passwordLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155;");
        passwordField.setPromptText("••••••••");
        passwordField.setPrefHeight(45);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                              "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Login button
        Button loginBtn = new Button("Giriş Yap");
        loginBtn.setDefaultButton(true);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(45);
        loginBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 10 20; " +
                         "-fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 16px; -fx-font-weight: 600;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; " +
                                                          "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6; " +
                                                          "-fx-font-size: 16px; -fx-font-weight: 600;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
                                                         "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6; " +
                                                         "-fx-font-size: 16px; -fx-font-weight: 600;"));
        
        // Message label
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px;");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);
        
        formSection.getChildren().addAll(emailBox, passwordBox, loginBtn, messageLabel);
        
        // Demo credentials info
        VBox demoInfo = new VBox(5);
        demoInfo.setAlignment(Pos.CENTER);
        demoInfo.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 15; -fx-background-radius: 8;");
        
        Label demoTitle = new Label("Demo Giriş Bilgileri");
        demoTitle.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: #475569;");
        
        Label demoEmail = new Label("Email: admin@example.com");
        Label demoPass = new Label("Şifre: admin123");
        demoEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        demoPass.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        
        demoInfo.getChildren().addAll(demoTitle, demoEmail, demoPass);
        
        mainContainer.getChildren().addAll(titleSection, formSection, demoInfo);
        
        // Login action
        loginBtn.setOnAction(e -> handleLogin(stage));
        emailField.setOnAction(e -> handleLogin(stage));
        passwordField.setOnAction(e -> handleLogin(stage));
        
        // Center the container
        setAlignment(Pos.CENTER);
        getChildren().add(mainContainer);
        
        // Background
        setStyle("-fx-background-color: #f8fafc;");
    }
    
    private void handleLogin(Stage stage) {
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        
        if (email.isEmpty() || pass.isEmpty()) {
            showError("Lütfen tüm alanları doldurun");
            return;
        }
        
        var result = AuthController.login(email, pass);
        
        if (result.success()) {
            stage.getScene().setRoot(new DashboardView(stage, email));
            stage.setTitle("İK Sistemi - Dashboard");
        } else {
            showError(result.message());
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: 600;");
    }
}
