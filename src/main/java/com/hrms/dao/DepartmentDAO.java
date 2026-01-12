package com.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Department;
import com.hrms.util.DatabaseConnection;

/**
 * Departman veritabanı işlemleri için DAO sınıfı
 */
public class DepartmentDAO {
    
    private Connection connection;
    
    public DepartmentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Yeni departman ekler
     */
    public boolean insert(Department department) {
        String sql = "INSERT INTO DEPARTMENTS (department_name, description, budget) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getDescription());
            stmt.setBigDecimal(3, department.getBudget());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    department.setDepartmentId(rs.getInt(1));
                }
                System.out.println("✓ Departman eklendi: " + department.getDepartmentName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Departman ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Departman günceller
     */
    public boolean update(Department department) {
        String sql = "UPDATE DEPARTMENTS SET department_name = ?, description = ?, " +
                    "budget = ? WHERE department_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getDescription());
            stmt.setBigDecimal(3, department.getBudget());
            stmt.setInt(4, department.getDepartmentId());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Departman güncellendi: " + department.getDepartmentName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Departman güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Departman siler
     */
    public boolean delete(int departmentId) {
        String sql = "DELETE FROM DEPARTMENTS WHERE department_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Departman silindi: ID=" + departmentId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Departman silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * ID'ye göre departman getirir
     */
    public Department findById(int departmentId) {
        String sql = "SELECT d.*, CONCAT(e.first_name, ' ', e.last_name) as manager_name " +
                    "FROM DEPARTMENTS d " +
                    "LEFT JOIN EMPLOYEES e ON d.manager_id = e.employee_id " +
                    "WHERE d.department_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractDepartmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Departman bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Tüm departmanları getirir
     */
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM DEPARTMENTS ORDER BY department_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                departments.add(extractDepartmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Departmanları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return departments;
    }
    
    /**
     * İsme göre departman arar
     */
    public List<Department> searchByName(String name) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM DEPARTMENTS " +
                    "WHERE department_name LIKE ? " +
                    "ORDER BY department_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                departments.add(extractDepartmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Departman arama hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return departments;
    }
    
    /**
     * Departmandaki çalışan sayısını getirir
     */
    public int getEmployeeCount(int departmentId) {
        String sql = "SELECT COUNT(*) as count FROM EMPLOYEES WHERE department_id = ? AND status = 'ACTIVE'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("✗ Çalışan sayısı hatası: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * ResultSet'ten Department nesnesi oluşturur
     */
    private Department extractDepartmentFromResultSet(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDepartmentId(rs.getInt("department_id"));
        dept.setDepartmentName(rs.getString("department_name"));
        
        
        return dept;
    }
}