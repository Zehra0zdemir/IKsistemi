package com.hrms.view;

import com.hrms.controller.EmployeeController;
import com.hrms.model.Department;
import com.hrms.model.Employee;
import com.hrms.model.Position;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EmployeeManagementView extends BorderPane {

    private final Stage stage;
    private final String userEmail;
    private final EmployeeController controller = new EmployeeController();

    // Form alanları
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
    
    private final Label infoLabel = new Label();
    private final TableView<Employee> table = new TableView<>();
    
    private Employee selectedEmployee = null;

    public EmployeeManagementView(Stage stage, String userEmail) {
        this.stage = stage;
        this.userEmail = userEmail;

        setPadding(new Insets(20));

        Label title = new Label("Çalışan Yönetimi");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(title);

        setTop(topBar);
        BorderPane.setMargin(getTop(), new Insets(0, 0, 10, 0));

        setLeft(buildForm());
        setCenter(buildTable());

        loadData();
        loadComboBoxes();
    }

    private VBox buildForm() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setPrefWidth(360);
        box.setStyle("""
            -fx-background-color: #f7f7f7;
            -fx-background-radius: 12;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 12;
        """);

        Label formTitle = new Label("Çalışan Bilgileri");
        formTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        int row = 0;
        grid.add(new Label("Ad:"), 0, row);
        grid.add(firstNameField, 1, row++);
        firstNameField.setPromptText("Ahmet");

        grid.add(new Label("Soyad:"), 0, row);
        grid.add(lastNameField, 1, row++);
        lastNameField.setPromptText("Yılmaz");

        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        emailField.setPromptText("ahmet@hrms.com");

        grid.add(new Label("Telefon:"), 0, row);
        grid.add(phoneField, 1, row++);
        phoneField.setPromptText("05551234567");

        grid.add(new Label("İşe Giriş:"), 0, row);
        grid.add(hireDatePicker, 1, row++);
        hireDatePicker.setValue(LocalDate.now());

        grid.add(new Label("Doğum Tarihi:"), 0, row);
        grid.add(birthDatePicker, 1, row++);

        grid.add(new Label("Departman:"), 0, row);
        grid.add(departmentBox, 1, row++);

        grid.add(new Label("Pozisyon:"), 0, row);
        grid.add(positionBox, 1, row++);

        grid.add(new Label("Maaş:"), 0, row);
        grid.add(salaryField, 1, row++);
        salaryField.setPromptText("20000");

        grid.add(new Label("Şifre:"), 0, row);
        grid.add(passwordField, 1, row++);
        passwordField.setPromptText("En az 4 karakter");

        Label adresLabel = new Label("Adres:");
        GridPane.setValignment(adresLabel, javafx.geometry.VPos.TOP);
        grid.add(adresLabel, 0, row);
        addressArea.setPrefRowCount(2);
        addressArea.setPromptText("Tam adres");
        grid.add(addressArea, 1, row++);

        Button saveBtn = new Button("Kaydet");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> saveEmployee());

        Button updateBtn = new Button("Güncelle");
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setOnAction(e -> updateEmployee());

        Button clearBtn = new Button("Temizle");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> clearForm());

        Button deleteBtn = new Button("Sil");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteEmployee());

        Button backBtn = new Button("Dashboard'a Dön");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> goBack());

        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 12px;");

        box.getChildren().addAll(
            formTitle, grid, saveBtn, updateBtn, clearBtn, deleteBtn, infoLabel, backBtn
        );
        return box;
    }

    private VBox buildTable() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(0, 0, 0, 15));

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchField.setPromptText("İsme göre ara...");
        searchField.setPrefWidth(250);
        Button searchBtn = new Button("Ara");
        searchBtn.setOnAction(e -> searchEmployees());
        Button refreshBtn = new Button("Yenile");
        refreshBtn.setOnAction(e -> loadData());
        searchBox.getChildren().addAll(searchField, searchBtn, refreshBtn);

        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        idCol.setPrefWidth(50);

        TableColumn<Employee, String> nameCol = new TableColumn<>("Ad Soyad");
        nameCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName())
        );
        nameCol.setPrefWidth(150);

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Employee, String> deptCol = new TableColumn<>("Departman");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        deptCol.setPrefWidth(130);

        TableColumn<Employee, String> posCol = new TableColumn<>("Pozisyon");
        posCol.setCellValueFactory(new PropertyValueFactory<>("positionTitle"));
        posCol.setPrefWidth(150);

        TableColumn<Employee, BigDecimal> salaryCol = new TableColumn<>("Maaş");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        salaryCol.setPrefWidth(100);

        TableColumn<Employee, String> statusCol = new TableColumn<>("Durum");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, nameCol, emailCol, deptCol, posCol, salaryCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Employee emp = table.getSelectionModel().getSelectedItem();
                if (emp != null) {
                    fillFormWithEmployee(emp);
                }
            }
        });

        box.getChildren().addAll(searchBox, table);
        return box;
    }

    private void loadComboBoxes() {
        List<Department> depts = controller.getAllDepartments();
        departmentBox.setItems(FXCollections.observableArrayList(depts));
        departmentBox.setConverter(new javafx.util.StringConverter<Department>() {
            @Override
            public String toString(Department dept) {
                return dept == null ? "" : dept.getDepartmentName();
            }
            @Override
            public Department fromString(String string) {
                return null;
            }
        });

        List<Position> positions = controller.getAllPositions();
        positionBox.setItems(FXCollections.observableArrayList(positions));
        positionBox.setConverter(new javafx.util.StringConverter<Position>() {
            @Override
            public String toString(Position pos) {
                return pos == null ? "" : pos.getPositionTitle();
            }
            @Override
            public Position fromString(String string) {
                return null;
            }
        });
    }

    private void loadData() {
        var result = controller.getActiveEmployees();
        if (result.success()) {
            table.setItems(FXCollections.observableArrayList(result.employees()));
        } else {
            showError(result.message());
        }
    }

    private void searchEmployees() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            loadData();
            return;
        }

        var result = controller.searchEmployees(query);
        if (result.success()) {
            table.setItems(FXCollections.observableArrayList(result.employees()));
            showInfo("Bulunan: " + result.employees().size());
        } else {
            showError(result.message());
        }
    }

    private void saveEmployee() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            LocalDate hireDate = hireDatePicker.getValue();
            LocalDate birthDate = birthDatePicker.getValue();
            Department dept = departmentBox.getValue();
            Position pos = positionBox.getValue();
            String salaryStr = salaryField.getText();
            String password = passwordField.getText();
            String address = addressArea.getText();

            if (salaryStr == null || salaryStr.trim().isEmpty()) {
                showError("Maaş giriniz");
                return;
            }

            BigDecimal salary = new BigDecimal(salaryStr.trim());

            var result = controller.addEmployee(
                firstName, lastName, email, phone, hireDate,
                dept != null ? dept.getDepartmentId() : null,
                pos != null ? pos.getPositionId() : null,
                salary, birthDate, address, password
            );

            if (result.success()) {
                showSuccess("Çalışan eklendi!");
                clearForm();
                loadData();
            } else {
                showError(result.message());
            }

        } catch (NumberFormatException e) {
            showError("Maaş sayı olmalı");
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
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            Department dept = departmentBox.getValue();
            Position pos = positionBox.getValue();
            BigDecimal salary = new BigDecimal(salaryField.getText().trim());

            var result = controller.updateEmployee(
                selectedEmployee.getEmployeeId(),
                firstName, lastName, email, phone,
                dept != null ? dept.getDepartmentId() : null,
                pos != null ? pos.getPositionId() : null,
                salary, null
            );

            if (result.success()) {
                showSuccess("Çalışan güncellendi!");
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

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Silme Onayı");
        confirm.setHeaderText("Çalışan silinecek");
        confirm.setContentText(selectedEmployee.getFullName() + " silinsin mi?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            var result = controller.deleteEmployee(selectedEmployee.getEmployeeId());
            if (result.success()) {
                showSuccess("Çalışan silindi!");
                clearForm();
                loadData();
            } else {
                showError(result.message());
            }
        }
    }

    private void fillFormWithEmployee(Employee emp) {
        selectedEmployee = emp;
        firstNameField.setText(emp.getFirstName());
        lastNameField.setText(emp.getLastName());
        emailField.setText(emp.getEmail());
        phoneField.setText(emp.getPhone());
        hireDatePicker.setValue(emp.getHireDate());
        birthDatePicker.setValue(emp.getBirthDate());
        salaryField.setText(emp.getBaseSalary().toString());
        addressArea.setText(emp.getAddress());
        passwordField.clear();

        // Departman seç
        if (emp.getDepartmentId() != null) {
            departmentBox.getItems().stream()
                .filter(d -> d.getDepartmentId() == emp.getDepartmentId())
                .findFirst()
                .ifPresent(departmentBox::setValue);
        }

        // Pozisyon seç
        if (emp.getPositionId() != null) {
            positionBox.getItems().stream()
                .filter(p -> p.getPositionId() == emp.getPositionId())
                .findFirst()
                .ifPresent(positionBox::setValue);
        }

        showInfo("Seçildi: " + emp.getFullName());
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
        infoLabel.setText("");
    }

    private void showSuccess(String msg) {
        infoLabel.setStyle("-fx-text-fill: green;");
        infoLabel.setText(msg);
    }

    private void showError(String msg) {
        infoLabel.setStyle("-fx-text-fill: red;");
        infoLabel.setText(msg);
    }

    private void showInfo(String msg) {
        infoLabel.setStyle("-fx-text-fill: blue;");
        infoLabel.setText(msg);
    }

    private void goBack() {
        stage.getScene().setRoot(new DashboardView(stage, userEmail));
        stage.setTitle("IK Sistemi - Dashboard");
    }
}