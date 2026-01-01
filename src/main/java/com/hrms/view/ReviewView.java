package com.hrms.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        Button submitBtn = new Button("Kaydet");
        Label info = new Label();

        submitBtn.setOnAction(e -> {
            double avg = (tech.getValue() + comm.getValue() + team.getValue() + lead.getValue()) / 4.0;
            info.setStyle("-fx-text-fill: green;");
            info.setText("Kaydedildi. Ortalama puan: " + avg);
        });

        Button backBtn = new Button("Geri Dön");
        backBtn.setOnAction(e -> backToDashboard());

        content.getChildren().addAll(
                title,
                grid,
                new Label("Güçlü Yönler"), strengths,
                new Label("Gelişim Alanları"), improvements,
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
