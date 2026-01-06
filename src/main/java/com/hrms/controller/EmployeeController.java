package com.hrms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hrms.dao.DepartmentDAO;
import com.hrms.dao.PositionDAO;
import com.hrms.model.Department;
import com.hrms.model.Employee;
import com.hrms.model.Position;
import com.hrms.services.EmployeeService;

/**
 * Çalışan yönetimi için Controller sınıfı
 */
public class EmployeeController {

    public record EmployeeResult(boolean success, String message, Employee employee) {}
    public record ListResult(boolean success, String message, List<Employee> employees) {}

    private final EmployeeService employeeService;
    private final DepartmentDAO departmentDAO;
    private final PositionDAO positionDAO;

    public EmployeeController() {
        this.employeeService = new EmployeeService();
        this.departmentDAO = new DepartmentDAO();
        this.positionDAO = new PositionDAO();
    }

    /**
     * Yeni çalışan ekler
     */
    public EmployeeResult addEmployee(
            String firstName,
            String lastName,
            String email,
            String phone,
            LocalDate hireDate,
            Integer departmentId,
            Integer positionId,
            BigDecimal baseSalary,
            LocalDate birthDate,
            String address,
            String password
    ) {
        try {
            // Validasyon
            if (firstName == null || firstName.trim().isEmpty()) {
                return new EmployeeResult(false, "Ad boş olamaz", null);
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                return new EmployeeResult(false, "Soyad boş olamaz", null);
            }
            if (email == null || email.trim().isEmpty()) {
                return new EmployeeResult(false, "Email boş olamaz", null);
            }
            if (password == null || password.length() < 4) {
                return new EmployeeResult(false, "Şifre en az 4 karakter olmalı", null);
            }
            if (baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) <= 0) {
                return new EmployeeResult(false, "Geçerli bir maaş giriniz", null);
            }

            // Employee oluştur
            Employee employee = new Employee(
                firstName.trim(),
                lastName.trim(),
                email.trim(),
                phone,
                hireDate != null ? hireDate : LocalDate.now(),
                departmentId,
                positionId,
                baseSalary,
                birthDate,
                address,
                password  // Service içinde hashlenecek
            );

            // Kaydet
            boolean success = employeeService.addEmployee(employee);

            if (success) {
                return new EmployeeResult(true, "Çalışan başarıyla eklendi", employee);
            } else {
                return new EmployeeResult(false, "Çalışan eklenemedi", null);
            }

        } catch (Exception e) {
            return new EmployeeResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Çalışan günceller
     */
    public EmployeeResult updateEmployee(
            int employeeId,
            String firstName,
            String lastName,
            String email,
            String phone,
            Integer departmentId,
            Integer positionId,
            BigDecimal baseSalary,
            String status
    ) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return new EmployeeResult(false, "Çalışan bulunamadı", null);
            }

            // Güncellemeleri yap
            employee.setFirstName(firstName.trim());
            employee.setLastName(lastName.trim());
            employee.setEmail(email.trim());
            employee.setPhone(phone);
            employee.setDepartmentId(departmentId);
            employee.setPositionId(positionId);
            employee.setBaseSalary(baseSalary);
            if (status != null) {
                employee.setStatus(status);
            }

            boolean success = employeeService.updateEmployee(employee);

            if (success) {
                return new EmployeeResult(true, "Çalışan güncellendi", employee);
            } else {
                return new EmployeeResult(false, "Güncelleme başarısız", null);
            }

        } catch (Exception e) {
            return new EmployeeResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Çalışan siler (soft delete)
     */
    public EmployeeResult deleteEmployee(int employeeId) {
        try {
            boolean success = employeeService.deleteEmployee(employeeId);
            if (success) {
                return new EmployeeResult(true, "Çalışan silindi", null);
            } else {
                return new EmployeeResult(false, "Silme başarısız", null);
            }
        } catch (Exception e) {
            return new EmployeeResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * ID'ye göre çalışan getirir
     */
    public EmployeeResult getEmployee(int employeeId) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                return new EmployeeResult(true, "Bulundu", employee);
            } else {
                return new EmployeeResult(false, "Çalışan bulunamadı", null);
            }
        } catch (Exception e) {
            return new EmployeeResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Tüm çalışanları listeler
     */
    public ListResult getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return new ListResult(true, "Listelendi", employees);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Aktif çalışanları listeler
     */
    public ListResult getActiveEmployees() {
        try {
            List<Employee> employees = employeeService.getActiveEmployees();
            return new ListResult(true, "Listelendi", employees);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * İsme göre arama yapar
     */
    public ListResult searchEmployees(String name) {
        try {
            List<Employee> employees = employeeService.searchEmployeesByName(name);
            return new ListResult(true, "Arama tamamlandı", employees);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Departmana göre çalışanları getirir
     */
    public ListResult getEmployeesByDepartment(int departmentId) {
        try {
            List<Employee> employees = employeeService.getEmployeesByDepartment(departmentId);
            return new ListResult(true, "Listelendi", employees);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Tüm departmanları getirir
     */
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    /**
     * Tüm pozisyonları getirir
     */
    public List<Position> getAllPositions() {
        return positionDAO.findAll();
    }

    /**
     * Çalışan istatistikleri
     */
    public record EmployeeStats(int total, int active, int inactive, int terminated) {}

    public EmployeeStats getEmployeeStats() {
        try {
            int total = employeeService.getTotalEmployeeCount();
            int active = employeeService.getActiveEmployeeCount();
            List<Employee> all = employeeService.getAllEmployees();
            
            int inactive = (int) all.stream()
                .filter(e -> "INACTIVE".equals(e.getStatus()))
                .count();
            
            int terminated = (int) all.stream()
                .filter(e -> "TERMINATED".equals(e.getStatus()))
                .count();

            return new EmployeeStats(total, active, inactive, terminated);
        } catch (Exception e) {
            return new EmployeeStats(0, 0, 0, 0);
        }
    }
}