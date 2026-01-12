package com.hrms.view;

import com.hrms.controller.EmployeeController;
import com.hrms.model.Department;
import com.hrms.model.Employee;
import com.hrms.model.Position;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ModernEmployeeView extends BorderPane {

    private final Stage stage;
    private final String userEmail;
    private final EmployeeController controller = new EmployeeController();

    // Form fields
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField phoneField = new TextField();
    private final DatePicker hireDatePicker = new DatePicker();
    private final DatePicker birthDatePicker = new DatePicker();
    private final ComboBox<Department> departmentBox = new ComboBox<>();
    private final ComboBox<Position> positionBox = new ComboBox<>();
    private final TextField salaryField = new TextField();
    private final TextArea addressArea = new TextArea();
    private final PasswordField passwordField = new PasswordField();
    private final TextField searchField = new TextField();
    private final ComboBox<String> statusBox = new ComboBox<>();
    
    private final Label messageLabel = new Label();
    private final TableView<Employee> table = new TableView<>();
    private FilteredList<Employee> filteredData;
    
    private Employee selectedEmployee = null;

    public ModernEmployeeView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        // Sidebar navigation
        VBox sidebar = createSidebar();
        setLeft(sidebar);

        // Main content area
        VBox mainContent = createMainContent();
        setCenter(mainContent);

        loadData();
        loadComboBoxes();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-padding: 20;");

        Label logo = new Label("İK Sistemi");
        logo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer1 = new Region();
        spacer1.setPrefHeight(30);

        Button dashboardBtn = createNavButton("📊 Dashboard", false);
        dashboardBtn.setOnAction(e -> goToDashboard());

        Button employeesBtn = createNavButton("👥 Çalışanlar", true);

        Button attendanceBtn = createNavButton("📋 Yoklama", false);
        attendanceBtn.setOnAction(e -> goToAttendance());

        Button reviewBtn = createNavButton("⭐ Değerlendirme", false);
        reviewBtn.setOnAction(e -> goToReview());

        Button payrollBtn = createNavButton("💰 Bordro", false);
        payrollBtn.setOnAction(e -> goToPayroll());

        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);

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
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8fafc;");

        // Header
        HBox header = createHeader();
        
        // Content area with form and table
        HBox mainArea = new HBox(20);
        VBox.setVgrow(mainArea, Priority.ALWAYS);
        
        // Form panel
        VBox formPanel = createFormPanel();
        
        // Table panel
        VBox tablePanel = createTablePanel();
        HBox.setHgrow(tablePanel, Priority.ALWAYS);
        
        mainArea.getChildren().addAll(formPanel, tablePanel);
        
        content.getChildren().addAll(header, mainArea);

        return content;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerText = new VBox(5);
        Label title = new Label("Çalışan Yönetimi");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label subtitle = new Label("Çalışan kayıtlarını yönetin");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        headerText.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Yenile");
        refreshBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 10 20; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        refreshBtn.setOnMouseEntered(e -> refreshBtn.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; " +
                                                              "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        refreshBtn.setOnMouseExited(e -> refreshBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
                                                             "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        refreshBtn.setOnAction(e -> {
            loadData();
            showSuccess("Liste yenilendi");
        });

        header.getChildren().addAll(headerText, spacer, refreshBtn);

        return header;
    }

    private VBox createFormPanel() {
        VBox panel = new VBox(20);
        panel.setPrefWidth(400);
        panel.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                      "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 20; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        Label formTitle = new Label("Çalışan Bilgileri");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1e293b;");

        // Form grid
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(16);
        grid.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;
        
        // Ad
        grid.add(createLabel("Ad"), 0, row);
        firstNameField.setPromptText("Ahmet");
        firstNameField.setPrefHeight(40);
        firstNameField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(firstNameField, 1, row++);

        // Soyad
        grid.add(createLabel("Soyad"), 0, row);
        lastNameField.setPromptText("Yılmaz");
        lastNameField.setPrefHeight(40);
        lastNameField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                              "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(lastNameField, 1, row++);

        // Email
        grid.add(createLabel("Email"), 0, row);
        emailField.setPromptText("ahmet@sirket.com");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(emailField, 1, row++);

        // Telefon
        grid.add(createLabel("Telefon"), 0, row);
        phoneField.setPromptText("05551234567");
        phoneField.setPrefHeight(40);
        phoneField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(phoneField, 1, row++);

        // İşe Giriş Tarihi
        grid.add(createLabel("İşe Giriş"), 0, row);
        hireDatePicker.setValue(LocalDate.now());
        hireDatePicker.setPrefHeight(40);
        hireDatePicker.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6;");
        grid.add(hireDatePicker, 1, row++);

        // Doğum Tarihi
        grid.add(createLabel("Doğum Tarihi"), 0, row);
        birthDatePicker.setPrefHeight(40);
        birthDatePicker.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                                "-fx-border-radius: 6; -fx-background-radius: 6;");
        grid.add(birthDatePicker, 1, row++);

        // Departman
        grid.add(createLabel("Departman"), 0, row);
        departmentBox.setPromptText("Seçiniz");
        departmentBox.setPrefHeight(40);
        departmentBox.setMaxWidth(Double.MAX_VALUE);
        departmentBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                              "-fx-border-radius: 6; -fx-background-radius: 6;");
        grid.add(departmentBox, 1, row++);

        // Pozisyon
        grid.add(createLabel("Pozisyon"), 0, row);
        positionBox.setPromptText("Seçiniz");
        positionBox.setPrefHeight(40);
        positionBox.setMaxWidth(Double.MAX_VALUE);
        positionBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                            "-fx-border-radius: 6; -fx-background-radius: 6;");
        grid.add(positionBox, 1, row++);

        // Maaş
        grid.add(createLabel("Maaş (₺)"), 0, row);
        salaryField.setPromptText("20000");
        salaryField.setPrefHeight(40);
        salaryField.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        grid.add(salaryField, 1, row++);

        // Durum
        grid.add(createLabel("Durum"), 0, row);
        statusBox.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE", "TERMINATED"));
        statusBox.setValue("ACTIVE");
        statusBox.setPrefHeight(40);
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6;");
        grid.add(statusBox, 1, row++);

        // Şifre
        grid.add(createLabel("Şifre"), 0, row);
        passwordField.setPromptText("En az 4 karakter");
        passwordField.setPrefHeight(40);
        grid.add(passwordField, 1, row++);

        // Adres
        Label addrLabel = createLabel("Adres");
        GridPane.setValignment(addrLabel, javafx.geometry.VPos.TOP);
        grid.add(addrLabel, 0, row);
        addressArea.setPromptText("Tam adres");
        addressArea.setPrefRowCount(3);
        grid.add(addressArea, 1, row++);

        // Configure column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Buttons
        HBox buttonBox = new HBox(10);
        
        Button saveBtn = new Button("💾 Kaydet");
        saveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 10 20; " +
                        "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; " +
                                                        "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                                                       "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> saveEmployee());
        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        Button updateBtn = new Button("✏️ Güncelle");
        updateBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 10 20; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        updateBtn.setOnMouseEntered(e -> updateBtn.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; " +
                                                            "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        updateBtn.setOnMouseExited(e -> updateBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
                                                           "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setOnAction(e -> updateEmployee());
        HBox.setHgrow(updateBtn, Priority.ALWAYS);

        buttonBox.getChildren().addAll(saveBtn, updateBtn);

        HBox buttonBox2 = new HBox(10);
        
        Button clearBtn = new Button("🔄 Temizle");
        clearBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 10 20; " +
                         "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; " +
                                                          "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; " +
                                                         "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> clearForm());
        HBox.setHgrow(clearBtn, Priority.ALWAYS);

        Button deleteBtn = new Button("🗑️ Sil");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 10 20; " +
                          "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; " +
                                                            "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                                           "-fx-padding: 10 20; -fx-border-radius: 6; -fx-background-radius: 6;"));
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> deleteEmployee());
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);

        buttonBox2.getChildren().addAll(clearBtn, deleteBtn);

        // Message label
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(formTitle, grid, buttonBox, buttonBox2, messageLabel);

        return panel;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: 600; -fx-text-fill: #334155;");
        return label;
    }

    private VBox createTablePanel() {
        VBox panel = new VBox(15);
        VBox.setVgrow(panel, Priority.ALWAYS);

        // Search bar
        HBox searchBar = new HBox(15);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 12; " +
                          "-fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        Label searchLabel = new Label("🔍 Ara:");
        searchLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #1e293b;");

        searchField.setPromptText("Ad, soyad veya email ile arama...");
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, old, newVal) -> filterEmployees(newVal));

        searchBar.getChildren().addAll(searchLabel, searchField);

        // Table
        setupTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        panel.getChildren().addAll(searchBar, table);

        return panel;
    }

    private void setupTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; " +
                      "-fx-border-radius: 12; -fx-background-radius: 12;");

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        idCol.setPrefWidth(60);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Ad Soyad");
        nameCol.setCellValueFactory(data -> {
            Employee e = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(e.getFirstName() + " " + e.getLastName());
        });
        nameCol.setPrefWidth(150);

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);

        TableColumn<Employee, String> phoneCol = new TableColumn<>("Telefon");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);

        TableColumn<Employee, String> deptCol = new TableColumn<>("Departman");
        deptCol.setCellValueFactory(data -> {
            String deptName = data.getValue().getDepartmentName();
            return new javafx.beans.property.SimpleStringProperty(deptName != null ? deptName : "-");
        });
        deptCol.setPrefWidth(120);

        TableColumn<Employee, String> posCol = new TableColumn<>("Pozisyon");
        posCol.setCellValueFactory(data -> {
            String posTitle = data.getValue().getPositionTitle();
            return new javafx.beans.property.SimpleStringProperty(posTitle != null ? posTitle : "-");
        });
        posCol.setPrefWidth(120);

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("Maaş");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        salaryCol.setPrefWidth(100);

        TableColumn<Employee, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new javafx.geometry.Insets(4, 12, 4, 12));
                    badge.setStyle("-fx-border-radius: 12; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: 600;");
                    
                    if ("ACTIVE".equals(status)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #d1fae5; -fx-text-fill: #065f46;");
                    } else if ("INACTIVE".equals(status)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, deptCol, posCol, salaryCol, statusCol);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Employee selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    fillForm(selected);
                }
            }
        });
    }

    private void filterEmployees(String searchText) {
        if (filteredData == null) return;
        
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredData.setPredicate(null);
        } else {
            String lower = searchText.toLowerCase();
            filteredData.setPredicate(emp -> 
                emp.getFirstName().toLowerCase().contains(lower) ||
                emp.getLastName().toLowerCase().contains(lower) ||
                emp.getEmail().toLowerCase().contains(lower) ||
                (emp.getPhone() != null && emp.getPhone().contains(searchText))
            );
        }
    }

    private void loadData() {
        var result = controller.getAllEmployees();
        if (result.success()) {
            filteredData = new FilteredList<>(FXCollections.observableArrayList(result.employees()));
            table.setItems(filteredData);
        }
    }

    private void loadComboBoxes() {
        // Departmanları yükle
        List<Department> depts = controller.getAllDepartments();
        departmentBox.setItems(FXCollections.observableArrayList(depts));
        
        // Department ComboBox görünümünü ayarla
        departmentBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Department>() {
            @Override
            protected void updateItem(Department dept, boolean empty) {
                super.updateItem(dept, empty);
                if (empty || dept == null) {
                    setText(null);
                } else {
                    setText(dept.getDepartmentName());
                }
            }
        });
        departmentBox.setButtonCell(new javafx.scene.control.ListCell<Department>() {
            @Override
            protected void updateItem(Department dept, boolean empty) {
                super.updateItem(dept, empty);
                if (empty || dept == null) {
                    setText(null);
                } else {
                    setText(dept.getDepartmentName());
                }
            }
        });

        // Pozisyonları yükle
        List<Position> positions = controller.getAllPositions();
        positionBox.setItems(FXCollections.observableArrayList(positions));
        
        // Position ComboBox görünümünü ayarla
        positionBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Position>() {
            @Override
            protected void updateItem(Position pos, boolean empty) {
                super.updateItem(pos, empty);
                if (empty || pos == null) {
                    setText(null);
                } else {
                    setText(pos.getPositionTitle() + " (" + pos.getLevel() + ")");
                }
            }
        });
        positionBox.setButtonCell(new javafx.scene.control.ListCell<Position>() {
            @Override
            protected void updateItem(Position pos, boolean empty) {
                super.updateItem(pos, empty);
                if (empty || pos == null) {
                    setText(null);
                } else {
                    setText(pos.getPositionTitle() + " (" + pos.getLevel() + ")");
                }
            }
        });
        
        System.out.println("Departman sayısı: " + (depts != null ? depts.size() : 0));
        System.out.println("Pozisyon sayısı: " + (positions != null ? positions.size() : 0));
    }

    private void saveEmployee() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            LocalDate hireDate = hireDatePicker.getValue();
            LocalDate birthDate = birthDatePicker.getValue();
            Department dept = departmentBox.getValue();
            Position pos = positionBox.getValue();
            String salaryStr = salaryField.getText().trim();
            String address = addressArea.getText().trim();
            String password = passwordField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Ad, soyad, email ve şifre zorunludur");
                return;
            }

            if (password.length() < 4) {
                showError("Şifre en az 4 karakter olmalıdır");
                return;
            }

            BigDecimal salary;
            try {
                salary = new BigDecimal(salaryStr);
            } catch (NumberFormatException e) {
                showError("Geçerli bir maaş giriniz");
                return;
            }

            var result = controller.addEmployee(
                    firstName, lastName, email, phone, hireDate,
                    dept != null ? dept.getDepartmentId() : null,
                    pos != null ? pos.getPositionId() : null,
                    salary, birthDate, address, password
            );

            if (result.success()) {
                showSuccess(result.message());
                clearForm();
                loadData();
            } else {
                showError(result.message());
            }

        } catch (Exception e) {
            showError("Hata: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        if (selectedEmployee == null) {
            showError("Lütfen güncellenecek çalışanı seçin");
            return;
        }

        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Department dept = departmentBox.getValue();
            Position pos = positionBox.getValue();
            String salaryStr = salaryField.getText().trim();
            String status = statusBox.getValue();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                showError("Ad ve soyad zorunludur");
                return;
            }

            BigDecimal salary;
            try {
                salary = new BigDecimal(salaryStr);
            } catch (NumberFormatException e) {
                showError("Geçerli bir maaş giriniz");
                return;
            }

            var result = controller.updateEmployee(
                    selectedEmployee.getEmployeeId(),
                    firstName, lastName, email, phone,
                    dept != null ? dept.getDepartmentId() : null,
                    pos != null ? pos.getPositionId() : null,
                    salary, status
            );

            if (result.success()) {
                showSuccess(result.message());
                clearForm();
                loadData();
            } else {
                showError(result.message());
            }

        } catch (Exception e) {
            showError("Hata: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        if (selectedEmployee == null) {
            showError("Lütfen silinecek çalışanı seçin");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Onay");
        alert.setHeaderText("Çalışan Silme");
        alert.setContentText(selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + 
                             " isimli çalışanı silmek istediğinize emin misiniz?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                var result = controller.deleteEmployee(selectedEmployee.getEmployeeId());
                if (result.success()) {
                    showSuccess(result.message());
                    clearForm();
                    loadData();
                } else {
                    showError(result.message());
                }
            }
        });
    }

    private void fillForm(Employee emp) {
        selectedEmployee = emp;
        firstNameField.setText(emp.getFirstName());
        lastNameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());
        phoneField.setText(emp.getPhone());
        hireDatePicker.setValue(emp.getHireDate());
        birthDatePicker.setValue(emp.getBirthDate());
        // Departman ve pozisyon ID'lerine göre combo box değerlerini ayarla
        if (emp.getDepartmentId() != null) {
            departmentBox.getItems().stream()
                .filter(d -> d.getDepartmentId() == emp.getDepartmentId())
                .findFirst().ifPresent(departmentBox::setValue);
        }
        if (emp.getPositionId() != null) {
            positionBox.getItems().stream()
                .filter(p -> p.getPositionId() == emp.getPositionId())
                .findFirst().ifPresent(positionBox::setValue);
        }
        salaryField.setText(emp.getBaseSalary().toString());
        addressArea.setText(emp.getAddress());
        statusBox.setValue(emp.getStatus());
        passwordField.clear();
    }

    private void clearForm() {
        selectedEmployee = null;
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        hireDatePicker.setValue(LocalDate.now());
        birthDatePicker.setValue(null);
        departmentBox.setValue(null);
        positionBox.setValue(null);
        salaryField.clear();
        addressArea.clear();
        passwordField.clear();
        statusBox.setValue("ACTIVE");
        messageLabel.setText("");
        table.getSelectionModel().clearSelection();
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: 600;");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 600;");
    }

    private void goToDashboard() {
        stage.getScene().setRoot(new DashboardView(stage, userEmail));
        stage.setTitle("İK Sistemi - Dashboard");
    }

    private void goToAttendance() {
        stage.getScene().setRoot(new AttendanceView(stage, userEmail));
        stage.setTitle("İK Sistemi - Yoklama");
    }

    private void goToReview() {
        stage.getScene().setRoot(new ReviewView(stage, userEmail));
        stage.setTitle("İK Sistemi - Performans Değerlendirme");
    }

    private void goToPayroll() {
        stage.getScene().setRoot(new PayrollView(stage, userEmail));
        stage.setTitle("İK Sistemi - Bordro");
    }

    private void logout() {
        stage.getScene().setRoot(new LoginView(stage));
        stage.setTitle("İK Sistemi - Giriş");
    }
}
