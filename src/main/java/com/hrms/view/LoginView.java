package com.hrms.view;

import com.hrms.controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.hrms.view.DashboardView;


public class LoginView extends VBox {
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label messageLabel = new Label();

    public LoginView(Stage stage) {
        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        var title = new Label("Giriş Yap");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        emailField.setPromptText("Email");
        passwordField.setPromptText("Şifre");

        var loginBtn = new Button("Giriş");
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String pass = passwordField.getText();

            var result = AuthController.login(email, pass);

            if (result.success()) {
                stage.getScene().setRoot(new DashboardView(stage, email));
                stage.setTitle("IK Sistemi - Dashboard");
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText(result.message());
            }

        });

        getChildren().addAll(title, emailField, passwordField, loginBtn, messageLabel);
    }
}
