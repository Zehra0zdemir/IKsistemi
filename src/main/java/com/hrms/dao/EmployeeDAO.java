package com.hrms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Employee;
import com.hrms.util.DatabaseConnection;

/**
 * Çalışan veritabanı işlemleri için DAO sınıfı
 */
public class EmployeeDAO {
    
    private Connection connection;
    
    public EmployeeDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Yeni çalışan ekler
     */
    public boolean insert(Employee employee) {
        String sql = "INSERT INTO EMPLOYEES (first_name, last_name, email, phone, hire_date, " +
                    "department_id, position_id, manager_id, base_salary, birth_date, address, " +
                    "password_hash, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
            stmt.setDate(5, Date.valueOf(employee.getHireDate()));
            
            if (employee.getDepartmentId() != null) {
                stmt.setInt(6, employee.getDepartmentId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (employee.getPositionId() != null) {
                stmt.setInt(7, employee.getPositionId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            if (employee.getManagerId() != null) {
                stmt.setInt(8, employee.getManagerId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setBigDecimal(9, employee.getBaseSalary());
            
            if (employee.getBirthDate() != null) {
                stmt.setDate(10, Date.valueOf(employee.getBirthDate()));
            } else {
                stmt.setNull(10, Types.DATE);
            }
            
            stmt.setString(11, employee.getAddress());
            stmt.setString(12, employee.getPasswordHash());
            stmt.setString(13, employee.getStatus() != null ? employee.getStatus() : "ACTIVE");
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    employee.setEmployeeId(rs.getInt(1));
                }
                System.out.println("✓ Çalışan eklendi: " + employee.getFullName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Çalışan günceller
     */
    public boolean update(Employee employee) {
        String sql = "UPDATE EMPLOYEES SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
                    "department_id = ?, position_id = ?, manager_id = ?, base_salary = ?, " +
                    "birth_date = ?, address = ?, status = ? WHERE employee_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
            
            if (employee.getDepartmentId() != null) {
                stmt.setInt(5, employee.getDepartmentId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (employee.getPositionId() != null) {
                stmt.setInt(6, employee.getPositionId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (employee.getManagerId() != null) {
                stmt.setInt(7, employee.getManagerId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setBigDecimal(8, employee.getBaseSalary());
            
            if (employee.getBirthDate() != null) {
                stmt.setDate(9, Date.valueOf(employee.getBirthDate()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            
            stmt.setString(10, employee.getAddress());
            stmt.setString(11, employee.getStatus());
            stmt.setInt(12, employee.getEmployeeId());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Çalışan güncellendi: " + employee.getFullName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Çalışan siler (Soft delete - status TERMINATED)
     */
    public boolean delete(int employeeId) {
        String sql = "UPDATE EMPLOYEES SET status = 'TERMINATED' WHERE employee_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Çalışan silindi (TERMINATED): ID=" + employeeId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * ID'ye göre çalışan getirir
     */
    public Employee findById(int employeeId) {
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "WHERE e.employee_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Tüm çalışanları getirir
     */
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışanları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    /**
     * Aktif çalışanları getirir
     */
    public List<Employee> findAllActive() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "WHERE e.status = 'ACTIVE' " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Aktif çalışanları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    /**
     * Departmana göre çalışanları getirir
     */
    public List<Employee> findByDepartment(int departmentId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "WHERE e.department_id = ? " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Departman çalışanları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    /**
     * İsme göre çalışan arar
     */
    public List<Employee> searchByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "WHERE CONCAT(e.first_name, ' ', e.last_name) LIKE ? " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan arama hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    /**
     * Email ile çalışan bulur (Login için)
     */
    public Employee findByEmail(String email) {
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "WHERE e.email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Email ile çalışan bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Yöneticiye göre çalışanları getirir
     */
    public List<Employee> findByManager(int managerId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, d.department_name, p.position_title, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as manager_name " +
                    "FROM EMPLOYEES e " +
                    "LEFT JOIN DEPARTMENTS d ON e.department_id = d.department_id " +
                    "LEFT JOIN POSITIONS p ON e.position_id = p.position_id " +
                    "LEFT JOIN EMPLOYEES m ON e.manager_id = m.employee_id " +
                    "WHERE e.manager_id = ? " +
                    "ORDER BY e.first_name, e.last_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Yönetici çalışanları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    /**
     * Çalışan sayısını getirir
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) as count FROM EMPLOYEES";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan sayısı hatası: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Aktif çalışan sayısını getirir
     */
    public int getActiveCount() {
        String sql = "SELECT COUNT(*) as count FROM EMPLOYEES WHERE status = 'ACTIVE'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("✗ Aktif çalışan sayısı hatası: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * ResultSet'ten Employee nesnesi oluşturur
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt("employee_id"));
        emp.setFirstName(rs.getString("first_name"));
        emp.setLastName(rs.getString("last_name"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            emp.setHireDate(hireDate.toLocalDate());
        }
        
        int deptId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            emp.setDepartmentId(deptId);
        }
        
        int posId = rs.getInt("position_id");
        if (!rs.wasNull()) {
            emp.setPositionId(posId);
        }
        
        int mgrId = rs.getInt("manager_id");
        if (!rs.wasNull()) {
            emp.setManagerId(mgrId);
        }
        
        emp.setBaseSalary(rs.getBigDecimal("base_salary"));
        emp.setStatus(rs.getString("status"));
        
        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            emp.setBirthDate(birthDate.toLocalDate());
        }
        
        emp.setAddress(rs.getString("address"));
        emp.setPasswordHash(rs.getString("password_hash"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            emp.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            emp.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        // JOIN bilgileri
        emp.setDepartmentName(rs.getString("department_name"));
        emp.setPositionTitle(rs.getString("position_title"));
        emp.setManagerName(rs.getString("manager_name"));
        
        return emp;
    }
}
