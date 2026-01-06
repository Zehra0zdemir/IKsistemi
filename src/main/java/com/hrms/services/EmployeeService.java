package com.hrms.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hrms.dao.EmployeeDAO;
import com.hrms.model.Employee;
import com.hrms.util.PasswordUtil;
import com.hrms.util.ValidationUtil;

/**
 * Çalışan iş mantığı için Service sınıfı
 */
public class EmployeeService {
    
    private EmployeeDAO employeeDAO;
    
    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    /**
     * Yeni çalışan ekler (Validasyon ile)
     */
    public boolean addEmployee(Employee employee) {
        // Validasyonlar
        if (!validateEmployee(employee)) {
            return false;
        }
        
        // Email benzersizliği kontrolü
        if (emailExists(employee.getEmail())) {
            System.err.println("✗ Bu email adresi zaten kullanımda!");
            return false;
        }
        
        // Şifreyi hashle
        if (employee.getPasswordHash() != null && !employee.getPasswordHash().isEmpty()) {
            String hashedPassword = PasswordUtil.hash(employee.getPasswordHash());
            employee.setPasswordHash(hashedPassword);
        }
        
        return employeeDAO.insert(employee);
    }
    
    /**
     * Çalışan günceller
     */
    public boolean updateEmployee(Employee employee) {
        // Validasyonlar
        if (!validateEmployee(employee)) {
            return false;
        }
        
        // Email değişmişse benzersizlik kontrolü
        Employee existing = employeeDAO.findById(employee.getEmployeeId());
        if (existing != null && !existing.getEmail().equals(employee.getEmail())) {
            if (emailExists(employee.getEmail())) {
                System.err.println("✗ Bu email adresi zaten kullanımda!");
                return false;
            }
        }
        
        return employeeDAO.update(employee);
    }
    
    /**
     * Çalışan siler (Soft delete)
     */
    public boolean deleteEmployee(int employeeId) {
        Employee employee = employeeDAO.findById(employeeId);
        if (employee == null) {
            System.err.println("✗ Çalışan bulunamadı!");
            return false;
        }
        
        return employeeDAO.delete(employeeId);
    }
    
    /**
     * Çalışan durumunu değiştirir
     */
    public boolean changeEmployeeStatus(int employeeId, String status) {
        Employee employee = employeeDAO.findById(employeeId);
        if (employee == null) {
            System.err.println("✗ Çalışan bulunamadı!");
            return false;
        }
        
        employee.setStatus(status);
        return employeeDAO.update(employee);
    }
    
    /**
     * ID'ye göre çalışan getirir
     */
    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.findById(employeeId);
    }
    
    /**
     * Tüm çalışanları getirir
     */
    public List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }
    
    /**
     * Aktif çalışanları getirir
     */
    public List<Employee> getActiveEmployees() {
        return employeeDAO.findAllActive();
    }
    
    /**
     * Departmana göre çalışanları getirir
     */
    public List<Employee> getEmployeesByDepartment(int departmentId) {
        return employeeDAO.findByDepartment(departmentId);
    }
    
    /**
     * Yöneticiye göre çalışanları getirir
     */
    public List<Employee> getEmployeesByManager(int managerId) {
        return employeeDAO.findByManager(managerId);
    }
    
    /**
     * İsme göre çalışan arar
     */
    public List<Employee> searchEmployeesByName(String name) {
        if (ValidationUtil.isEmpty(name)) {
            return employeeDAO.findAll();
        }
        return employeeDAO.searchByName(name);
    }
    
    /**
     * Email ve şifre ile giriş yapar
     */
    public Employee login(String email, String password) {
        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            System.err.println("✗ Email ve şifre boş olamaz!");
            return null;
        }
        
        Employee employee = employeeDAO.findByEmail(email);
        if (employee == null) {
            System.err.println("✗ Kullanıcı bulunamadı!");
            return null;
        }
        
        // Şifre kontrolü
        if (!PasswordUtil.verify(password, employee.getPasswordHash())) {
            System.err.println("✗ Şifre hatalı!");
            return null;
        }
        
        // Aktif mi kontrolü
        if (!employee.isActive()) {
            System.err.println("✗ Hesap aktif değil!");
            return null;
        }
        
        System.out.println("✓ Giriş başarılı: " + employee.getFullName());
        return employee;
    }
    
    /**
     * Şifre değiştirir
     */
    public boolean changePassword(int employeeId, String oldPassword, String newPassword) {
        Employee employee = employeeDAO.findById(employeeId);
        if (employee == null) {
            System.err.println("✗ Çalışan bulunamadı!");
            return false;
        }
        
        // Eski şifre kontrolü
        if (!PasswordUtil.verify(oldPassword, employee.getPasswordHash())) {
            System.err.println("✗ Eski şifre hatalı!");
            return false;
        }
        
        // Yeni şifre validasyonu
        if (newPassword.length() < 6) {
            System.err.println("✗ Şifre en az 6 karakter olmalı!");
            return false;
        }
        
        // Yeni şifreyi hashle ve güncelle
        employee.setPasswordHash(PasswordUtil.hash(newPassword));
        return employeeDAO.update(employee);
    }
    
    /**
     * Çalışan sayısını getirir
     */
    public int getTotalEmployeeCount() {
        return employeeDAO.getTotalCount();
    }
    
    /**
     * Aktif çalışan sayısını getirir
     */
    public int getActiveEmployeeCount() {
        return employeeDAO.getActiveCount();
    }
    
    /**
     * Email'in sistemde olup olmadığını kontrol eder
     */
    private boolean emailExists(String email) {
        return employeeDAO.findByEmail(email) != null;
    }
    
    /**
     * Çalışan validasyonu yapar
     */
    private boolean validateEmployee(Employee employee) {
        // Ad validasyonu
        if (ValidationUtil.isEmpty(employee.getFirstName())) {
            System.err.println("✗ Ad boş olamaz!");
            return false;
        }
        
        // Soyad validasyonu
        if (ValidationUtil.isEmpty(employee.getLastName())) {
            System.err.println("✗ Soyad boş olamaz!");
            return false;
        }
        
        // Email validasyonu
        if (!ValidationUtil.isValidEmail(employee.getEmail())) {
            System.err.println("✗ Geçersiz email formatı!");
            return false;
        }
        
        // Telefon validasyonu (opsiyonel)
        if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
            if (!ValidationUtil.isValidPhone(employee.getPhone())) {
                System.err.println("✗ Geçersiz telefon formatı! (05551234567)");
                return false;
            }
        }
        
        // İşe başlama tarihi kontrolü
        if (employee.getHireDate() == null) {
            System.err.println("✗ İşe başlama tarihi boş olamaz!");
            return false;
        }
        
        if (employee.getHireDate().isAfter(LocalDate.now())) {
            System.err.println("✗ İşe başlama tarihi gelecekte olamaz!");
            return false;
        }
        
        // Maaş validasyonu
        if (employee.getBaseSalary() == null || 
            employee.getBaseSalary().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("✗ Maaş pozitif bir değer olmalı!");
            return false;
        }
        
        return true;
    }
}
