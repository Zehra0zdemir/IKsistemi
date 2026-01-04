package com.hrms.view;

import com.hrms.controller.PayrollController;
import com.hrms.model.Payroll;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class PayrollView extends BorderPane {

    private final Stage stage;
    private final String userEmail;

    private final PayrollController controller = new PayrollController();

    // Form alanları
    private final TextField employeeIdField = new TextField();
    private final TextField periodIdField = new TextField();
    private final TextField baseSalaryField = new TextField();
    private final TextField overtimeHoursField = new TextField();
    private final TextField overtimeRateField = new TextField();
    private final TextField deductionField = new TextField();
    private final ComboBox<String> taxRateBox = new ComboBox<>();

    private final Label info = new Label();

    // Tablo
    private final TableView<Payroll> table = new TableView<>();

    public PayrollView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        setPadding(new Insets(20));

        Label title = new Label("Bordro / Maaş Modülü");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        setTop(new VBox(10, title));
        BorderPane.setMargin(getTop(), new Insets(0, 0, 10, 0));

        setLeft(buildForm());
        setCenter(buildTable());

        // Default değerler
        taxRateBox.getItems().addAll("0.10", "0.15", "0.20", "0.25");
        taxRateBox.setValue("0.15");

        employeeIdField.setPromptText("Örn: 1");
        periodIdField.setPromptText("Örn: 1");
        baseSalaryField.setPromptText("Örn: 30000");
        overtimeHoursField.setPromptText("Örn: 10");
        overtimeRateField.setPromptText("Örn: 200");
        deductionField.setPromptText("Örn: 500");

        // İlk açılışta istersen boş bırak, kullanıcı employeeId girince history çekeriz
        info.setStyle("-fx-font-size: 12px;");
    }

    private VBox buildForm() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.TOP_LEFT);
        box.setPrefWidth(360);
        box.setStyle("""
            -fx-background-color: #f7f7f7;
            -fx-background-radius: 12;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 12;
        """);

        Label formTitle = new Label("Bordro Oluştur");
        formTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int r = 0;
        grid.add(new Label("Employee ID"), 0, r);
        grid.add(employeeIdField, 1, r++);

        grid.add(new Label("Period ID"), 0, r);
        grid.add(periodIdField, 1, r++);

        grid.add(new Label("Base Salary"), 0, r);
        grid.add(baseSalaryField, 1, r++);

        grid.add(new Label("Overtime Hours"), 0, r);
        grid.add(overtimeHoursField, 1, r++);

        grid.add(new Label("Overtime Rate"), 0, r);
        grid.add(overtimeRateField, 1, r++);

        grid.add(new Label("Deductions"), 0, r);
        grid.add(deductionField, 1, r++);

        grid.add(new Label("Tax Rate"), 0, r);
        grid.add(taxRateBox, 1, r++);

        Button saveBtn = new Button("Hesapla & Kaydet");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> onGenerate());

        Button historyBtn = new Button("Geçmişi Getir");
        historyBtn.setMaxWidth(Double.MAX_VALUE);
        historyBtn.setOnAction(e -> loadHistorySafe());

        Button backBtn = new Button("Dashboard'a Dön");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> back());

        info.setWrapText(true);

        box.getChildren().addAll(formTitle, grid, saveBtn, historyBtn, info, backBtn);
        return box;
    }

    private VBox buildTable() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(0, 0, 0, 15));

        Label t = new Label("Bordro Geçmişi");
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Payroll, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPayrollId()));

        TableColumn<Payroll, Number> periodCol = new TableColumn<>("Period");
        periodCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPeriodId()));

        TableColumn<Payroll, String> grossCol = new TableColumn<>("Gross");
        grossCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getGrossSalary())));

        TableColumn<Payroll, String> taxCol = new TableColumn<>("Tax");
        taxCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getTaxAmount())));

        TableColumn<Payroll, String> netCol = new TableColumn<>("Net");
        netCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getNetSalary())));

        table.getColumns().addAll(idCol, periodCol, grossCol, taxCol, netCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        box.getChildren().addAll(t, table);
        return box;
    }

    private void onGenerate() {
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            int periodId = Integer.parseInt(periodIdField.getText().trim());

            BigDecimal base = new BigDecimal(baseSalaryField.getText().trim());
            BigDecimal otH = new BigDecimal(zeroIfBlank(overtimeHoursField.getText()));
            BigDecimal otR = new BigDecimal(zeroIfBlank(overtimeRateField.getText()));
            BigDecimal ded = new BigDecimal(zeroIfBlank(deductionField.getText()));

            BigDecimal taxRate = new BigDecimal(taxRateBox.getValue());

            var res = controller.generatePayroll(
                    employeeId, periodId,
                    base, otH, otR,
                    ded, taxRate,
                    userEmail
            );

            if (res.success()) {
                info.setStyle("-fx-text-fill: green;");
                info.setText("Kaydedildi. Net Maaş: " + res.payroll().getNetSalary());

                // otomatik geçmişi yenile
                loadHistory(employeeId);
            } else {
                info.setStyle("-fx-text-fill: red;");
                info.setText(res.message());
            }

        } catch (NumberFormatException ex) {
            info.setStyle("-fx-text-fill: red;");
            info.setText("Lütfen sayısal alanları doğru gir.");
        } catch (Exception ex) {
            info.setStyle("-fx-text-fill: red;");
            info.setText("Hata: " + ex.getMessage());
        }
    }

    private void loadHistorySafe() {
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            loadHistory(employeeId);
        } catch (Exception ex) {
            info.setStyle("-fx-text-fill: red;");
            info.setText("Geçmiş için önce Employee ID gir.");
        }
    }

    private void loadHistory(int employeeId) {
        try {
            List<Payroll> list = controller.getPayrollHistory(employeeId);
            table.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ex) {
            info.setStyle("-fx-text-fill: red;");
            info.setText("Geçmiş yüklenemedi: " + ex.getMessage());
        }
    }

    private void back() {
        stage.getScene().setRoot(new DashboardView(stage, userEmail));
        stage.setTitle("IK Sistemi - Dashboard");
    }

    private String zeroIfBlank(String s) {
        if (s == null || s.trim().isEmpty()) return "0";
        return s.trim();
    }
}
