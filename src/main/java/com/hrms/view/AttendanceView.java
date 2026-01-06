package com.hrms.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.hrms.controller.AttendanceController;
import com.hrms.controller.EmployeeController;
import com.hrms.model.Attendance;
import com.hrms.model.Employee;
import com.hrms.model.LeaveRequest;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AttendanceView extends BorderPane {

    private final Stage stage;
    private final String userEmail;
    private final AttendanceController attendanceController = new AttendanceController();
    private final EmployeeController employeeController = new EmployeeController();

    // Form alanları
    private final ComboBox<Employee> employeeBox = new ComboBox<>();
    private final DatePicker datePicker = new DatePicker();
    private final TextField checkInField = new TextField();
    private final TextField checkOutField = new TextField();
    private final TextArea notesArea = new TextArea();
    private final Label infoLabel = new Label();
    private final Label statusLabel = new Label();

    // İzin formu
    private final DatePicker leaveStartPicker = new DatePicker();
    private final DatePicker leaveEndPicker = new DatePicker();
    private final ComboBox<String> leaveTypeBox = new ComboBox<>();
    private final TextArea leaveReasonArea = new TextArea();

    // Tablolar
    private final TableView<Attendance> attendanceTable = new TableView<>();
    private final TableView<LeaveRequest> leaveTable = new TableView<>();

    private final TabPane tabPane = new TabPane();

    public AttendanceView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        setPadding(new Insets(20));

        Label title = new Label("Devam Takip Sistemi");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button backBtn = new Button("Geri Dön");
        backBtn.setOnAction(e -> goBack());

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(title, new Region(), backBtn);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);

        setTop(topBar);
        BorderPane.setMargin(getTop(), new Insets(0, 0, 10, 0));

        buildTabs();
        setCenter(tabPane);

        loadEmployees();
        loadLeaveTypes();
    }

    private void buildTabs() {
        Tab checkInOutTab = new Tab("Giriş/Çıkış");
        checkInOutTab.setClosable(false);
        checkInOutTab.setContent(buildCheckInOutPane());

        Tab attendanceTab = new Tab("Devam Kayıtları");
        attendanceTab.setClosable(false);
        attendanceTab.setContent(buildAttendancePane());

        Tab leaveTab = new Tab("İzin Talepleri");
        leaveTab.setClosable(false);
        leaveTab.setContent(buildLeavePane());

        tabPane.getTabs().addAll(checkInOutTab, attendanceTab, leaveTab);
    }

    // ============================================
    // GİRİŞ/ÇIKIŞ SEKMESİ
    // ============================================
    private Pane buildCheckInOutPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        VBox leftBox = new VBox(15);
        leftBox.setPrefWidth(350);
        leftBox.setPadding(new Insets(15));
        leftBox.setStyle("""
            -fx-background-color: #f7f7f7;
            -fx-background-radius: 12;
        """);

        Label formTitle = new Label("Hızlı İşlemler");
        formTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        employeeBox.setPrefWidth(300);
        employeeBox.setPromptText("Çalışan seçin...");

        Button checkInBtn = new Button("Giriş Yap (Check-In)");
        checkInBtn.setMaxWidth(Double.MAX_VALUE);
        checkInBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        checkInBtn.setOnAction(e -> checkIn());

        Button checkOutBtn = new Button("Çıkış Yap (Check-Out)");
        checkOutBtn.setMaxWidth(Double.MAX_VALUE);
        checkOutBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        checkOutBtn.setOnAction(e -> checkOut());

        Button statusBtn = new Button("Bugünkü Durumu Kontrol Et");
        statusBtn.setMaxWidth(Double.MAX_VALUE);
        statusBtn.setOnAction(e -> checkTodayStatus());

        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-padding: 10; -fx-background-color: white; -fx-border-color: #ddd;");

        Separator sep = new Separator();

        Label manualTitle = new Label("Manuel Kayıt Ekle");
        manualTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        datePicker.setValue(LocalDate.now());
        checkInField.setPromptText("09:00");
        checkOutField.setPromptText("18:00");
        notesArea.setPrefRowCount(2);
        notesArea.setPromptText("Not (opsiyonel)");

        GridPane manualGrid = new GridPane();
        manualGrid.setHgap(10);
        manualGrid.setVgap(8);
        int row = 0;
        manualGrid.add(new Label("Tarih:"), 0, row);
        manualGrid.add(datePicker, 1, row++);
        manualGrid.add(new Label("Giriş:"), 0, row);
        manualGrid.add(checkInField, 1, row++);
        manualGrid.add(new Label("Çıkış:"), 0, row);
        manualGrid.add(checkOutField, 1, row++);
        manualGrid.add(new Label("Not:"), 0, row);
        manualGrid.add(notesArea, 1, row++);

        Button addManualBtn = new Button("Manuel Kayıt Ekle");
        addManualBtn.setMaxWidth(Double.MAX_VALUE);
        addManualBtn.setOnAction(e -> addManualAttendance());

        infoLabel.setWrapText(true);

        leftBox.getChildren().addAll(
            formTitle, new Label("Çalışan:"), employeeBox,
            checkInBtn, checkOutBtn, statusBtn, statusLabel,
            sep, manualTitle, manualGrid, addManualBtn, infoLabel
        );

        pane.setLeft(leftBox);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        Label todayLabel = new Label("Bugünkü Devam Kayıtları");
        todayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Button refreshBtn = new Button("Yenile");
        refreshBtn.setOnAction(e -> loadTodayAttendances());
        HBox headerBox = new HBox(10, todayLabel, refreshBtn);
        rightBox.getChildren().addAll(headerBox, buildTodayAttendanceTable());
        pane.setCenter(rightBox);

        loadTodayAttendances();

        return pane;
    }

    private TableView<Attendance> buildTodayAttendanceTable() {
        TableView<Attendance> table = new TableView<>();

        TableColumn<Attendance, String> nameCol = new TableColumn<>("Çalışan");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        nameCol.setPrefWidth(150);

        TableColumn<Attendance, LocalTime> inCol = new TableColumn<>("Giriş");
        inCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        inCol.setPrefWidth(80);

        TableColumn<Attendance, LocalTime> outCol = new TableColumn<>("Çıkış");
        outCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        outCol.setPrefWidth(80);

        TableColumn<Attendance, BigDecimal> hoursCol = new TableColumn<>("Saat");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));
        hoursCol.setPrefWidth(70);

        TableColumn<Attendance, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(nameCol, inCol, outCol, hoursCol, statusCol);
        return table;
    }

    // ============================================
    // DEVAM KAYITLARI SEKMESİ
    // ============================================
    private Pane buildAttendancePane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        VBox leftBox = new VBox(15);
        leftBox.setPrefWidth(300);

        ComboBox<Employee> empBox = new ComboBox<>();
        empBox.setItems(employeeBox.getItems());
        empBox.setConverter(employeeBox.getConverter());

        DatePicker startPicker = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker endPicker = new DatePicker(LocalDate.now());

        Button loadBtn = new Button("Kayıtları Getir");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setOnAction(e -> {
            Employee emp = empBox.getValue();
            if (emp == null) {
                showError("Çalışan seçin");
                return;
            }
            loadAttendanceHistory(emp.getEmployeeId(), startPicker.getValue(), endPicker.getValue());
        });

        Label statsLabel = new Label();
        statsLabel.setWrapText(true);
        statsLabel.setStyle("-fx-padding: 10; -fx-background-color: #e3f2fd; -fx-border-color: #2196F3;");

        Button calcBtn = new Button("İstatistik Hesapla");
        calcBtn.setMaxWidth(Double.MAX_VALUE);
        calcBtn.setOnAction(e -> {
            Employee emp = empBox.getValue();
            if (emp == null) return;
            calculateStats(emp.getEmployeeId(), startPicker.getValue(), endPicker.getValue(), statsLabel);
        });

        leftBox.getChildren().addAll(
            new Label("Çalışan:"), empBox,
            new Label("Başlangıç:"), startPicker,
            new Label("Bitiş:"), endPicker,
            loadBtn, calcBtn, statsLabel
        );

        pane.setLeft(leftBox);
        pane.setCenter(buildAttendanceTable());
        BorderPane.setMargin(pane.getCenter(), new Insets(0, 0, 0, 20));

        return pane;
    }

    private TableView<Attendance> buildAttendanceTable() {
        TableColumn<Attendance, LocalDate> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("attendanceDate"));
        dateCol.setPrefWidth(100);

        TableColumn<Attendance, LocalTime> inCol = new TableColumn<>("Giriş");
        inCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));

        TableColumn<Attendance, LocalTime> outCol = new TableColumn<>("Çıkış");
        outCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));

        TableColumn<Attendance, BigDecimal> hoursCol = new TableColumn<>("Saat");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));

        TableColumn<Attendance, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Attendance, String> notesCol = new TableColumn<>("Not");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setPrefWidth(150);

        attendanceTable.getColumns().addAll(dateCol, inCol, outCol, hoursCol, statusCol, notesCol);
        return attendanceTable;
    }

    // ============================================
    // İZİN TALEPLERİ SEKMESİ
    // ============================================
    private Pane buildLeavePane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        VBox leftBox = new VBox(15);
        leftBox.setPrefWidth(350);
        leftBox.setPadding(new Insets(15));
        leftBox.setStyle("-fx-background-color: #f7f7f7; -fx-background-radius: 12;");

        Label formTitle = new Label("Yeni İzin Talebi");
        formTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<Employee> empBox = new ComboBox<>();
        empBox.setItems(employeeBox.getItems());
        empBox.setConverter(employeeBox.getConverter());

        leaveStartPicker.setValue(LocalDate.now());
        leaveEndPicker.setValue(LocalDate.now().plusDays(1));
        leaveReasonArea.setPrefRowCount(3);
        leaveReasonArea.setPromptText("İzin sebebi...");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        int row = 0;
        grid.add(new Label("Çalışan:"), 0, row);
        grid.add(empBox, 1, row++);
        grid.add(new Label("İzin Tipi:"), 0, row);
        grid.add(leaveTypeBox, 1, row++);
        grid.add(new Label("Başlangıç:"), 0, row);
        grid.add(leaveStartPicker, 1, row++);
        grid.add(new Label("Bitiş:"), 0, row);
        grid.add(leaveEndPicker, 1, row++);
        grid.add(new Label("Sebep:"), 0, row);
        grid.add(leaveReasonArea, 1, row++);

        Button createBtn = new Button("İzin Talebi Oluştur");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createBtn.setOnAction(e -> createLeaveRequest(empBox));

        Button pendingBtn = new Button("Bekleyen Talepler");
        pendingBtn.setMaxWidth(Double.MAX_VALUE);
        pendingBtn.setOnAction(e -> loadPendingLeaves());

        Button allLeavesBtn = new Button("Tüm Talepler");
        allLeavesBtn.setMaxWidth(Double.MAX_VALUE);
        allLeavesBtn.setOnAction(e -> loadAllLeaves(empBox));

        Label leaveInfo = new Label();
        leaveInfo.setWrapText(true);

        leftBox.getChildren().addAll(
            formTitle, grid, createBtn, pendingBtn, allLeavesBtn, leaveInfo
        );

        pane.setLeft(leftBox);
        pane.setCenter(buildLeaveTable());
        BorderPane.setMargin(pane.getCenter(), new Insets(0, 0, 0, 20));

        return pane;
    }

    private TableView<LeaveRequest> buildLeaveTable() {
        TableColumn<LeaveRequest, String> empCol = new TableColumn<>("Çalışan");
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        empCol.setPrefWidth(120);

        TableColumn<LeaveRequest, String> typeCol = new TableColumn<>("Tip");
        typeCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getLeaveTypeDisplay())
        );
        typeCol.setPrefWidth(100);

        TableColumn<LeaveRequest, LocalDate> startCol = new TableColumn<>("Başlangıç");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveRequest, LocalDate> endCol = new TableColumn<>("Bitiş");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveRequest, Integer> daysCol = new TableColumn<>("Gün");
        daysCol.setCellValueFactory(new PropertyValueFactory<>("totalDays"));
        daysCol.setPrefWidth(50);

        TableColumn<LeaveRequest, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<LeaveRequest, String> reasonCol = new TableColumn<>("Sebep");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setPrefWidth(150);

        leaveTable.getColumns().addAll(empCol, typeCol, startCol, endCol, daysCol, statusCol, reasonCol);
        return leaveTable;
    }

    // ============================================
    // İŞLEM METODLARItable
    // ============================================
    private void checkIn() {
        Employee emp = employeeBox.getValue();
        if (emp == null) {
            showError("Lütfen çalışan seçin");
            return;
        }

        var result = attendanceController.checkIn(emp.getEmployeeId());
        if (result.success()) {
            showSuccess(result.message());
            loadTodayAttendances();
        } else {
            showError(result.message());
        }
    }

    private void checkOut() {
        Employee emp = employeeBox.getValue();
        if (emp == null) {
            showError("Lütfen çalışan seçin");
            return;
        }

        var result = attendanceController.checkOut(emp.getEmployeeId());
        if (result.success()) {
            showSuccess(result.message());
            loadTodayAttendances();
        } else {
            showError(result.message());
        }
    }

    private void checkTodayStatus() {
        Employee emp = employeeBox.getValue();
        if (emp == null) {
            showError("Lütfen çalışan seçin");
            return;
        }

        var result = attendanceController.getTodayStatus(emp.getEmployeeId());
        if (result.success() && result.attendance() != null) {
            Attendance att = result.attendance();
            String status = String.format(
                "Durum: %s\nGiriş: %s\nÇıkış: %s\nSaat: %.2f",
                att.getStatus(),
                att.getCheckIn(),
                att.getCheckOut() != null ? att.getCheckOut() : "-",
                att.getHoursWorked() != null ? att.getHoursWorked() : 0
            );
            statusLabel.setText(status);
        } else {
            statusLabel.setText("Bugün için kayıt yok");
        }
    }

    private void addManualAttendance() {
        Employee emp = employeeBox.getValue();
        if (emp == null) {
            showError("Çalışan seçin");
            return;
        }

        try {
            LocalDate date = datePicker.getValue();
            LocalTime checkIn = LocalTime.parse(checkInField.getText());
            LocalTime checkOut = LocalTime.parse(checkOutField.getText());
            String notes = notesArea.getText();

            var result = attendanceController.addManualAttendance(
                emp.getEmployeeId(), date, checkIn, checkOut, notes
            );

            if (result.success()) {
                showSuccess("Manuel kayıt eklendi");
                checkInField.clear();
                checkOutField.clear();
                notesArea.clear();
            } else {
                showError(result.message());
            }

        } catch (Exception e) {
            showError("Saat formatı hatalı (örn: 09:00)");
        }
    }

    private void loadTodayAttendances() {
        var result = attendanceController.getTodayAttendances();
        if (result.success()) {
            TableView<Attendance> todayTable = (TableView<Attendance>)
                ((VBox) ((BorderPane) tabPane.getTabs().get(0).getContent()).getCenter()).getChildren().get(1);
            todayTable.setItems(FXCollections.observableArrayList(result.attendances()));
        }
    }

    private void loadAttendanceHistory(int empId, LocalDate start, LocalDate end) {
        var result = attendanceController.getAttendancesByDateRange(empId, start, end);
        if (result.success()) {
            attendanceTable.setItems(FXCollections.observableArrayList(result.attendances()));
        } else {
            showError(result.message());
        }
    }

    private void calculateStats(int empId, LocalDate start, LocalDate end, Label label) {
        var hoursResult = attendanceController.calculateTotalHours(empId, start, end);
        var overtimeResult = attendanceController.calculateOvertimeHours(empId, start, end);
        var rateResult = attendanceController.calculateAttendanceRate(empId, start, end);

        String stats = String.format(
            "Toplam Saat: %.2f\nMesai: %.2f\nDevam Oranı: %%%.1f",
            hoursResult.hours(), overtimeResult.hours(), rateResult.rate()
        );
        label.setText(stats);
    }

    private void createLeaveRequest(ComboBox<Employee> empBox) {
        Employee emp = empBox.getValue();
        if (emp == null) {
            showError("Çalışan seçin");
            return;
        }

        String type = leaveTypeBox.getValue();
        if (type == null) {
            showError("İzin tipi seçin");
            return;
        }

        var result = attendanceController.createLeaveRequest(
            emp.getEmployeeId(), type,
            leaveStartPicker.getValue(),
            leaveEndPicker.getValue(),
            leaveReasonArea.getText()
        );

        if (result.success()) {
            showSuccess("İzin talebi oluşturuldu");
            leaveReasonArea.clear();
        } else {
            showError(result.message());
        }
    }

    private void loadPendingLeaves() {
        var result = attendanceController.getPendingLeaveRequests();
        if (result.success()) {
            leaveTable.setItems(FXCollections.observableArrayList(result.leaves()));
        }
    }

    private void loadAllLeaves(ComboBox<Employee> empBox) {
        Employee emp = empBox.getValue();
        if (emp == null) {
            var result = attendanceController.getPendingLeaveRequests();
            if (result.success()) {
                leaveTable.setItems(FXCollections.observableArrayList(result.leaves()));
            }
        } else {
            var result = attendanceController.getEmployeeLeaveRequests(emp.getEmployeeId());
            if (result.success()) {
                leaveTable.setItems(FXCollections.observableArrayList(result.leaves()));
            }
        }
    }

    private void loadEmployees() {
        var result = employeeController.getActiveEmployees();
        if (result.success()) {
            employeeBox.setItems(FXCollections.observableArrayList(result.employees()));
            employeeBox.setConverter(new javafx.util.StringConverter<Employee>() {
                @Override
                public String toString(Employee emp) {
                    return emp == null ? "" : emp.getFullName();
                }
                @Override
                public Employee fromString(String string) {
                    return null;
                }
            });
        }
    }

    private void loadLeaveTypes() {
        leaveTypeBox.setItems(FXCollections.observableArrayList(
            "ANNUAL", "SICK", "UNPAID", "MATERNITY", "PATERNITY"
        ));
    }

    private void showSuccess(String msg) {
        infoLabel.setStyle("-fx-text-fill: green;");
        infoLabel.setText(msg);
    }

    private void showError(String msg) {
        infoLabel.setStyle("-fx-text-fill: red;");
        infoLabel.setText(msg);
    }

    private void goBack() {
        stage.getScene().setRoot(new DashboardView(stage, userEmail));
        stage.setTitle("IK Sistemi - Dashboard");
    }
}