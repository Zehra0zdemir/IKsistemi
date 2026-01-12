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
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
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

    // İzin formu
    private final TextArea leavePersonNameLabel = new TextArea();
    private final DatePicker leaveStartPicker = new DatePicker();
    private final DatePicker leaveEndPicker = new DatePicker();
    private final ComboBox<String> leaveTypeBox = new ComboBox<>();
    private final TextArea leaveReasonArea = new TextArea();
    private final Label leaveInfoLabel = new Label();

    // Tablolar
    private final TableView<Attendance> attendanceTable = new TableView<>();
    private final TableView<LeaveRequest> leaveTable = new TableView<>();

    private StackPane contentArea;

    public AttendanceView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        this.setStyle("-fx-background-color: #f8fafc;");

        // Sidebar
        VBox sidebar = createSidebar();

        // Content area
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Default: Show check-in/out
        showCheckInOut();

        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(sidebar, contentArea);

        setCenter(mainLayout);

        loadEmployees();
        loadLeaveTypes();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #1e293b;");
        sidebar.setPadding(new Insets(20));
        sidebar.setSpacing(10);

        // Logo/Title
        Label title = new Label("📋 Devam Takip");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        title.setPadding(new Insets(0, 0, 20, 0));

        // Navigation buttons
        Button checkInBtn = createNavButton("🕐 Giriş/Çıkış", true);
        checkInBtn.setOnAction(e -> {
            showCheckInOut();
            updateActiveButton(checkInBtn);
        });

        Button recordsBtn = createNavButton("📊 Devam Kayıtları", false);
        recordsBtn.setOnAction(e -> {
            showAttendanceRecords();
            updateActiveButton(recordsBtn);
        });

        Button leaveBtn = createNavButton("🏖 İzin Talebi", false);
        leaveBtn.setOnAction(e -> {
            showLeaveRequest();
            updateActiveButton(leaveBtn);
        });

        Button leaveListBtn = createNavButton("📝 İzin Listesi", false);
        leaveListBtn.setOnAction(e -> {
            showLeaveList();
            updateActiveButton(leaveListBtn);
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪 Geri Dön");
        logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 12 20; " +
                          "-fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-cursor: hand;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; " +
                                                            "-fx-padding: 12 20; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                                           "-fx-padding: 12 20; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px;"));
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> goBack());

        sidebar.getChildren().addAll(title, checkInBtn, recordsBtn, leaveBtn, leaveListBtn, spacer, logoutBtn);

        return sidebar;
    }

    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 16, 12, 16));
        
        if (active) {
            btn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 14px; " +
                                                    "-fx-border-radius: 8; -fx-background-radius: 8;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; " +
                                                   "-fx-border-radius: 8; -fx-background-radius: 8;"));
        }
        
        return btn;
    }

    private void updateActiveButton(Button activeBtn) {
        // Reset all buttons in sidebar
        VBox sidebar = (VBox) ((HBox) getCenter()).getChildren().get(0);
        for (javafx.scene.Node node : sidebar.getChildren()) {
            if (node instanceof Button && node != activeBtn) {
                Button btn = (Button) node;
                String text = btn.getText();
                if (!text.contains("Geri")) {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; " +
                                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
                    btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 14px; " +
                                                            "-fx-border-radius: 8; -fx-background-radius: 8;"));
                    btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; " +
                                                           "-fx-border-radius: 8; -fx-background-radius: 8;"));
                }
            }
        }
        
        activeBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; " +
                          "-fx-border-radius: 8; -fx-background-radius: 8;");
        activeBtn.setOnMouseEntered(null);
        activeBtn.setOnMouseExited(null);
    }

    private void showCheckInOut() {
        VBox content = new VBox(20);
        content.setMaxWidth(800);

        // Header
        VBox header = new VBox(5);
        Label title = new Label("🕐 Giriş/Çıkış Kaydı");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Günlük giriş ve çıkış saatlerini kaydedin");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        
        header.getChildren().addAll(title, subtitle);

        // Form Card
        VBox formCard = new VBox(20);
        formCard.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                         "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 24; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        int row = 0;

        // Çalışan
        grid.add(createLabel("Çalışan"), 0, row);
        employeeBox.setPromptText("Seçiniz");
        employeeBox.setPrefHeight(40);
        employeeBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                            "-fx-border-radius: 6; -fx-background-radius: 6;");
        employeeBox.setMaxWidth(Double.MAX_VALUE);
        grid.add(employeeBox, 1, row++);

        // Tarih
        grid.add(createLabel("Tarih"), 0, row);
        datePicker.setValue(LocalDate.now());
        datePicker.setPrefHeight(40);
        datePicker.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6;");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        grid.add(datePicker, 1, row++);

        // Giriş Saati
        grid.add(createLabel("Giriş Saati"), 0, row);
        checkInField.setPromptText("HH:MM (örn: 09:00)");
        checkInField.setPrefHeight(40);
        checkInField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(checkInField, 1, row++);

        // Çıkış Saati
        grid.add(createLabel("Çıkış Saati"), 0, row);
        checkOutField.setPromptText("HH:MM (örn: 18:00)");
        checkOutField.setPrefHeight(40);
        checkOutField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                              "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(checkOutField, 1, row++);

        // Notlar
        grid.add(createLabel("Notlar"), 0, row);
        notesArea.setPromptText("İsteğe bağlı notlar...");
        notesArea.setPrefRowCount(3);
        notesArea.setPrefHeight(80);
        notesArea.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(notesArea, 1, row++);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Buttons
        HBox buttonBox = new HBox(10);
        
        Button saveBtn = new Button("💾 Kaydet");
        saveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 12 24; " +
                        "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; " +
                                                        "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                                                       "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        saveBtn.setOnAction(e -> saveAttendance());

        Button clearBtn = new Button("🔄 Temizle");
        clearBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 12 24; " +
                         "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; " +
                                                          "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; " +
                                                         "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        clearBtn.setOnAction(e -> clearCheckInForm());

        buttonBox.getChildren().addAll(saveBtn, clearBtn);

        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(Double.MAX_VALUE);

        formCard.getChildren().addAll(grid, buttonBox, infoLabel);

        content.getChildren().addAll(header, formCard);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    private void showAttendanceRecords() {
        VBox content = new VBox(20);

        // Header
        VBox header = new VBox(5);
        Label title = new Label("📊 Devam Kayıtları");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Çalışan giriş-çıkış kayıtlarını görüntüleyin");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        
        header.getChildren().addAll(title, subtitle);

        // Table
        setupAttendanceTable();
        VBox.setVgrow(attendanceTable, Priority.ALWAYS);
        
        VBox tableCard = new VBox(attendanceTable);
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                          "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 20; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        content.getChildren().addAll(header, tableCard);
        VBox.setVgrow(content, Priority.ALWAYS);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
        
        loadAttendanceRecords();
    }

    private void showLeaveRequest() {
        VBox content = new VBox(20);
        content.setMaxWidth(800);

        // Header
        VBox header = new VBox(5);
        Label title = new Label("🏖 İzin Talebi");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Yeni izin talebi oluşturun");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        
        header.getChildren().addAll(title, subtitle);

        // Form Card
        VBox formCard = new VBox(20);
        formCard.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                         "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 24; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        int row = 0;
        
        // Çalışan seçimi
        grid.add(createLabel("Çalışan"), 0, row);
        ComboBox<Employee> leaveEmployeeBox = new ComboBox<>();
        leaveEmployeeBox.setPromptText("Seçiniz");
        leaveEmployeeBox.setPrefHeight(40);
        leaveEmployeeBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                                 "-fx-border-radius: 6; -fx-background-radius: 6;");
        leaveEmployeeBox.setMaxWidth(Double.MAX_VALUE);
        
        // Çalışanları yükle
        var result = employeeController.getAllEmployees();
        if (result.success() && result.employees() != null) {
            leaveEmployeeBox.setItems(FXCollections.observableArrayList(result.employees()));
            leaveEmployeeBox.setButtonCell(new javafx.scene.control.ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                    } else {
                        setText(emp.getFirstName() + " " + emp.getLastName());
                    }
                }
            });
            leaveEmployeeBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                    } else {
                        setText(emp.getFirstName() + " " + emp.getLastName());
                    }
                }
            });
        }
        grid.add(leaveEmployeeBox, 1, row++);

        // Başlangıç Tarihi
        grid.add(createLabel("Başlangıç"), 0, row);
        leaveStartPicker.setValue(LocalDate.now());
        leaveStartPicker.setPrefHeight(40);
        leaveStartPicker.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                                 "-fx-border-radius: 6; -fx-background-radius: 6;");
        leaveStartPicker.setMaxWidth(Double.MAX_VALUE);
        grid.add(leaveStartPicker, 1, row++);
        
        // Bitiş Tarihi
        grid.add(createLabel("Bitiş"), 0, row);
        leaveEndPicker.setValue(LocalDate.now().plusDays(1));
        leaveEndPicker.setPrefHeight(40);
        leaveEndPicker.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6;");
        leaveEndPicker.setMaxWidth(Double.MAX_VALUE);
        grid.add(leaveEndPicker, 1, row++);

        // İzin Türü
        grid.add(createLabel("İzin Türü"), 0, row);
        leaveTypeBox.setPromptText("Seçiniz");
        leaveTypeBox.setPrefHeight(40);
        leaveTypeBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6;");
        leaveTypeBox.setMaxWidth(Double.MAX_VALUE);
        grid.add(leaveTypeBox, 1, row++);

        // Sebep
        grid.add(createLabel("Sebep"), 0, row);
        leaveReasonArea.setPromptText("İzin sebebini yazınız...");
        leaveReasonArea.setPrefRowCount(4);
        leaveReasonArea.setPrefHeight(100);
        leaveReasonArea.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(leaveReasonArea, 1, row++);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Buttons
        HBox buttonBox = new HBox(10);
        
        Button submitBtn = new Button("📤 Gönder");
        submitBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 12 24; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; " +
                                                            "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
                                                           "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        submitBtn.setOnAction(e -> submitLeaveRequest(leaveEmployeeBox));

        Button clearBtn = new Button("🔄 Temizle");
        clearBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 12 24; " +
                         "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; " +
                                                          "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; " +
                                                         "-fx-padding: 12 24; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px;"));
        clearBtn.setOnAction(e -> clearLeaveForm(leaveEmployeeBox));

        buttonBox.getChildren().addAll(submitBtn, clearBtn);

        leaveInfoLabel.setWrapText(true);
        leaveInfoLabel.setMaxWidth(Double.MAX_VALUE);

        formCard.getChildren().addAll(grid, buttonBox, leaveInfoLabel);

        content.getChildren().addAll(header, formCard);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    private void showLeaveList() {
        VBox content = new VBox(20);

        // Header
        VBox header = new VBox(5);
        Label title = new Label("📝 İzin Listesi");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label subtitle = new Label("Tüm izin taleplerini görüntüleyin");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        
        header.getChildren().addAll(title, subtitle);

        // Table
        setupLeaveTable();
        VBox.setVgrow(leaveTable, Priority.ALWAYS);
        
        VBox tableCard = new VBox(leaveTable);
        tableCard.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                          "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 20; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        content.getChildren().addAll(header, tableCard);
        VBox.setVgrow(content, Priority.ALWAYS);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
        
        loadLeaveRequests();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155;");
        return label;
    }

    private void setupAttendanceTable() {
        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        attendanceTable.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                                "-fx-border-radius: 12; -fx-background-radius: 12;");

        // Mevcut sütunları temizle
        attendanceTable.getColumns().clear();

        TableColumn<Attendance, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("attendanceId"));
        idCol.setPrefWidth(60);

        TableColumn<Attendance, Integer> empIdCol = new TableColumn<>("Çalışan ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Attendance, LocalDate> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Attendance, LocalTime> checkInCol = new TableColumn<>("Giriş");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInTime"));

        TableColumn<Attendance, LocalTime> checkOutCol = new TableColumn<>("Çıkış");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutTime"));

        TableColumn<Attendance, BigDecimal> hoursCol = new TableColumn<>("Saat");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("totalHours"));

        attendanceTable.getColumns().addAll(idCol, empIdCol, dateCol, checkInCol, checkOutCol, hoursCol);
    }

    private void setupLeaveTable() {
        leaveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        leaveTable.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                           "-fx-border-radius: 12; -fx-background-radius: 12;");

        // Mevcut sütunları temizle
        leaveTable.getColumns().clear();

        TableColumn<LeaveRequest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        idCol.setPrefWidth(60);

        TableColumn<LeaveRequest, Integer> empCol = new TableColumn<>("Çalışan ID");
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<LeaveRequest, String> typeCol = new TableColumn<>("Tür");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("leaveType"));

        TableColumn<LeaveRequest, LocalDate> startCol = new TableColumn<>("Başlangıç");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveRequest, LocalDate> endCol = new TableColumn<>("Bitiş");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveRequest, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Aksiyon sütunu - Onay/Reddet butonları
        TableColumn<LeaveRequest, Void> actionCol = new TableColumn<>("İşlemler");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✅ Onayla");
            private final Button rejectBtn = new Button("❌ Reddet");
            private final HBox buttons = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                                   "-fx-padding: 5 10; -fx-border-radius: 4; -fx-background-radius: 4; " +
                                   "-fx-font-size: 12px; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                  "-fx-padding: 5 10; -fx-border-radius: 4; -fx-background-radius: 4; " +
                                  "-fx-font-size: 12px; -fx-cursor: hand;");

                approveBtn.setOnAction(e -> {
                    LeaveRequest leave = getTableView().getItems().get(getIndex());
                    approveLeave(leave);
                });

                rejectBtn.setOnAction(e -> {
                    LeaveRequest leave = getTableView().getItems().get(getIndex());
                    rejectLeave(leave);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    LeaveRequest leave = getTableView().getItems().get(getIndex());
                    // Sadece PENDING durumundaki izinler için butonları göster
                    if ("PENDING".equals(leave.getStatus())) {
                        setGraphic(buttons);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        leaveTable.getColumns().addAll(idCol, empCol, typeCol, startCol, endCol, statusCol, actionCol);
    }

    private void loadEmployees() {
        var result = employeeController.getAllEmployees();
        if (result.success() && result.employees() != null) {
            employeeBox.setItems(FXCollections.observableArrayList(result.employees()));
            employeeBox.setButtonCell(new javafx.scene.control.ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                    } else {
                        setText(emp.getFirstName() + " " + emp.getLastName());
                    }
                }
            });
            employeeBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Employee>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                    } else {
                        setText(emp.getFirstName() + " " + emp.getLastName());
                    }
                }
            });
        }
    }

    private void loadLeaveTypes() {
        leaveTypeBox.setItems(FXCollections.observableArrayList("ANNUAL", "SICK", "PERSONAL", "MATERNITY", "PATERNITY"));
    }

    private void loadAttendanceRecords() {
        var result = attendanceController.getTodayAttendances();
        if (result.success() && result.attendances() != null) {
            attendanceTable.setItems(FXCollections.observableArrayList(result.attendances()));
        }
    }

    private void loadLeaveRequests() {
        var result = attendanceController.getPendingLeaveRequests();
        if (result.success() && result.leaves() != null) {
            leaveTable.setItems(FXCollections.observableArrayList(result.leaves()));
        }
    }

    private void saveAttendance() {
        try {
            Employee emp = employeeBox.getValue();
            if (emp == null) {
                showError("Lütfen çalışan seçiniz");
                return;
            }

            LocalDate date = datePicker.getValue();
            if (date == null) {
                showError("Lütfen tarih seçiniz");
                return;
            }

            String checkInStr = checkInField.getText().trim();
            String checkOutStr = checkOutField.getText().trim();

            if (checkInStr.isEmpty()) {
                showError("Giriş saati gerekli");
                return;
            }

            LocalTime checkIn = LocalTime.parse(checkInStr);
            LocalTime checkOut = checkOutStr.isEmpty() ? null : LocalTime.parse(checkOutStr);

            var result = attendanceController.addManualAttendance(
                emp.getEmployeeId(),
                date,
                checkIn,
                checkOut,
                notesArea.getText()
            );
            if (result.success()) {
                showSuccess("Devam kaydı başarıyla oluşturuldu");
                clearCheckInForm();
                loadAttendanceRecords();
            } else {
                showError("Kayıt başarısız: " + result.message());
            }

        } catch (Exception e) {
            showError("Hata: " + e.getMessage());
        }
    }

    private void submitLeaveRequest(ComboBox<Employee> leaveEmployeeBox) {
        try {
            Employee emp = leaveEmployeeBox.getValue();
            if (emp == null) {
                showLeaveError("Lütfen çalışan seçiniz");
                return;
            }

            LocalDate start = leaveStartPicker.getValue();
            LocalDate end = leaveEndPicker.getValue();
            String type = leaveTypeBox.getValue();
            String reason = leaveReasonArea.getText();

            if (start == null || end == null) {
                showLeaveError("Tarih alanları gerekli");
                return;
            }

            if (type == null || type.isEmpty()) {
                showLeaveError("İzin türü seçiniz");
                return;
            }

            if (reason == null || reason.trim().isEmpty()) {
                showLeaveError("Sebep gerekli");
                return;
            }

            var result = attendanceController.createLeaveRequest(
                emp.getEmployeeId(),
                type,
                start,
                end,
                reason
            );
            if (result.success()) {
                showLeaveSuccess("İzin talebi başarıyla gönderildi");
                clearLeaveForm(leaveEmployeeBox);
                loadLeaveRequests();
            } else {
                showLeaveError("Talep başarısız: " + result.message());
            }

        } catch (Exception e) {
            showLeaveError("Hata: " + e.getMessage());
        }
    }

    private void clearCheckInForm() {
        employeeBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        checkInField.clear();
        checkOutField.clear();
        notesArea.clear();
        infoLabel.setText("");
    }

    private void clearLeaveForm(ComboBox<Employee> leaveEmployeeBox) {
        leaveEmployeeBox.setValue(null);
        leaveStartPicker.setValue(LocalDate.now());
        leaveEndPicker.setValue(LocalDate.now().plusDays(1));
        leaveTypeBox.setValue(null);
        leaveReasonArea.clear();
        leaveInfoLabel.setText("");
    }

    private void showSuccess(String msg) {
        infoLabel.setText("✅ " + msg);
        infoLabel.setStyle("-fx-text-fill: #10b981; -fx-padding: 10; -fx-background-color: #d1fae5; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6;");
    }

    private void showError(String msg) {
        infoLabel.setText("❌ " + msg);
        infoLabel.setStyle("-fx-text-fill: #ef4444; -fx-padding: 10; -fx-background-color: #fee2e2; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6;");
    }

    private void showLeaveSuccess(String msg) {
        leaveInfoLabel.setText("✅ " + msg);
        leaveInfoLabel.setStyle("-fx-text-fill: #10b981; -fx-padding: 10; -fx-background-color: #d1fae5; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6;");
    }

    private void showLeaveError(String msg) {
        leaveInfoLabel.setText("❌ " + msg);
        leaveInfoLabel.setStyle("-fx-text-fill: #ef4444; -fx-padding: 10; -fx-background-color: #fee2e2; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6;");
    }

    private void approveLeave(LeaveRequest leave) {
        try {
            // Onaylayan kişi ID'si için giriş yapan kullanıcının ID'sini bulmamız gerekir
            // Şimdilik sabit bir değer kullanıyoruz, gerçek uygulamada session'dan alınmalı
            Employee currentUser = getCurrentUser();
            if (currentUser == null) {
                showAlert("Hata", "Kullanıcı bilgisi bulunamadı", Alert.AlertType.ERROR);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("İzin Onaylama");
            confirm.setHeaderText("İzin talebini onaylamak istediğinizden emin misiniz?");
            confirm.setContentText("Çalışan ID: " + leave.getEmployeeId() + "\n" +
                                   "Tarih: " + leave.getStartDate() + " - " + leave.getEndDate() + "\n" +
                                   "Tür: " + leave.getLeaveType());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    var result = attendanceController.approveLeaveRequest(
                        leave.getLeaveId(), 
                        currentUser.getEmployeeId()
                    );
                    if (result.success()) {
                        showAlert("Başarılı", "İzin talebi onaylandı", Alert.AlertType.INFORMATION);
                        loadLeaveRequests(); // Tabloyu yenile
                    } else {
                        showAlert("Hata", "Onaylama başarısız: " + result.message(), Alert.AlertType.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Hata", "İşlem sırasında hata: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void rejectLeave(LeaveRequest leave) {
        try {
            Employee currentUser = getCurrentUser();
            if (currentUser == null) {
                showAlert("Hata", "Kullanıcı bilgisi bulunamadı", Alert.AlertType.ERROR);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("İzin Reddetme");
            confirm.setHeaderText("İzin talebini reddetmek istediğinizden emin misiniz?");
            confirm.setContentText("Çalışan ID: " + leave.getEmployeeId() + "\n" +
                                   "Tarih: " + leave.getStartDate() + " - " + leave.getEndDate() + "\n" +
                                   "Tür: " + leave.getLeaveType());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    var result = attendanceController.rejectLeaveRequest(
                        leave.getLeaveId(), 
                        currentUser.getEmployeeId()
                    );
                    if (result.success()) {
                        showAlert("Başarılı", "İzin talebi reddedildi", Alert.AlertType.INFORMATION);
                        loadLeaveRequests(); // Tabloyu yenile
                    } else {
                        showAlert("Hata", "Reddetme başarısız: " + result.message(), Alert.AlertType.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Hata", "İşlem sırasında hata: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Employee getCurrentUser() {
        // Email'e göre kullanıcıyı bul
        var result = employeeController.getAllEmployees();
        if (result.success() && result.employees() != null) {
            // Önce email ile eşleşmeyi dene
            for (Employee emp : result.employees()) {
                if (emp.getEmail() != null && emp.getEmail().trim().equalsIgnoreCase(userEmail.trim())) {
                    return emp;
                }
            }
            
            // Email bulunamazsa, ilk aktif çalışanı kullan (fallback)
            for (Employee emp : result.employees()) {
                if (emp.isActive()) {
                    System.out.println("UYARI: Email eşleşmesi bulunamadı, fallback kullanıcı: " + emp.getFullName());
                    return emp;
                }
            }
            
            // Hiçbir aktif kullanıcı yoksa ilk kullanıcıyı döndür
            if (!result.employees().isEmpty()) {
                Employee emp = result.employees().get(0);
                System.out.println("UYARI: Aktif kullanıcı bulunamadı, ilk kullanıcı kullanılıyor: " + emp.getFullName());
                return emp;
            }
        }
        return null;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void goBack() {
        DashboardView dashboard = new DashboardView(stage, userEmail);
        stage.getScene().setRoot(dashboard);
    }
}
