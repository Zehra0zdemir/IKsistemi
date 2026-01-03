package com.hrms.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.hrms.controller.ReviewController;

public class ReviewView extends ScrollPane {

    private final Stage stage;
    private final String userEmail;

    public ReviewView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setFillWidth(true);

        Label title = new Label("Performans Değerlendirme");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER_LEFT);

        Spinner<Integer> tech = createSpinner();
        Spinner<Integer> comm = createSpinner();
        Spinner<Integer> team = createSpinner();
        Spinner<Integer> lead = createSpinner();

        grid.add(new Label("Teknik Yeterlilik"), 0, 0);
        grid.add(tech, 1, 0);

        grid.add(new Label("İletişim"), 0, 1);
        grid.add(comm, 1, 1);

        grid.add(new Label("Takım Çalışması"), 0, 2);
        grid.add(team, 1, 2);

        grid.add(new Label("Liderlik"), 0, 3);
        grid.add(lead, 1, 3);

        TextArea strengths = new TextArea();
        strengths.setPromptText("Güçlü yönler");
        strengths.setPrefRowCount(3);

        TextArea improvements = new TextArea();
        improvements.setPromptText("Gelişim alanları");
        improvements.setPrefRowCount(3);

        TextArea goalsArea = new TextArea();
        goalsArea.setPromptText("Gelecek dönem hedefleri");
        goalsArea.setPrefRowCount(3);

        Button submitBtn = new Button("Kaydet");
        Label info = new Label();

        submitBtn.setOnAction(e -> {
            int techVal = tech.getValue();
            int commVal = comm.getValue();
            int teamVal = team.getValue();
            int leadVal = lead.getValue();

            String normalizedGoals = goalsArea.getText();
            if (normalizedGoals == null || normalizedGoals.isBlank()) {
                normalizedGoals = null;
            }

            int employeeId = 1; // şimdilik sabit

            var result = ReviewController.submit(
                    userEmail,
                    employeeId,
                    techVal, commVal, teamVal, leadVal,
                    strengths.getText(),
                    improvements.getText(),
                    normalizedGoals
            );

            if (result.success()) {
                info.setStyle("-fx-text-fill: green;");
                info.setText("Kaydedildi. Ortalama: " + result.review().getOverallRating());
            } else {
                info.setStyle("-fx-text-fill: red;");
                info.setText(result.message());
            }
        });

        Button backBtn = new Button("Geri Dön");
        backBtn.setOnAction(e -> backToDashboard());

        content.getChildren().addAll(
                title,
                grid,
                new Label("Güçlü Yönler"), strengths,
                new Label("Gelişim Alanları"), improvements,
                new Label("Hedefler"), goalsArea,
                submitBtn, info, backBtn
        );

        setContent(content);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    private Spinner<Integer> createSpinner() {
        Spinner<Integer> spinner = new Spinner<>(1, 10, 5);
        spinner.setEditable(false);
        return spinner;
    }

    private void backToDashboard() {
        stage.getScene().setRoot(new DashboardView(stage, userEmail));
        stage.setTitle("IK Sistemi - Dashboard");
    }
}
