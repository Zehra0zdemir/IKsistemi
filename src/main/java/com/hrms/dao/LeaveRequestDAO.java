package com.hrms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.LeaveRequest;
import com.hrms.util.DatabaseConnection;

/**
 * İzin talebi veritabanı işlemleri için DAO sınıfı
 */
public class LeaveRequestDAO {
    
    private Connection connection;
    
    public LeaveRequestDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Yeni izin talebi ekler
     */
    public boolean insert(LeaveRequest leaveRequest) {
        String sql = "INSERT INTO LEAVE_REQUESTS (employee_id, leave_type, start_date, end_date, " +
                    "reason, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, leaveRequest.getEmployeeId());
            stmt.setString(2, leaveRequest.getLeaveType());
            stmt.setDate(3, Date.valueOf(leaveRequest.getStartDate()));
            stmt.setDate(4, Date.valueOf(leaveRequest.getEndDate()));
            stmt.setString(5, leaveRequest.getReason());
            stmt.setString(6, leaveRequest.getStatus() != null ? leaveRequest.getStatus() : "PENDING");
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    leaveRequest.setLeaveId(rs.getInt(1));
                }
                System.out.println("✓ İzin talebi oluşturuldu");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin talebi ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * İzin talebini günceller
     */
    public boolean update(LeaveRequest leaveRequest) {
        String sql = "UPDATE LEAVE_REQUESTS SET leave_type = ?, start_date = ?, end_date = ?, " +
                    "reason = ?, status = ? WHERE leave_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, leaveRequest.getLeaveType());
            stmt.setDate(2, Date.valueOf(leaveRequest.getStartDate()));
            stmt.setDate(3, Date.valueOf(leaveRequest.getEndDate()));
            stmt.setString(4, leaveRequest.getReason());
            stmt.setString(5, leaveRequest.getStatus());
            stmt.setInt(6, leaveRequest.getLeaveId());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ İzin talebi güncellendi");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin talebi güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * İzin talebini onaylar
     */
    public boolean approve(int leaveId, int approverId) {
        String sql = "UPDATE LEAVE_REQUESTS SET status = 'APPROVED', approved_by = ?, " +
                    "approval_date = CURRENT_TIMESTAMP WHERE leave_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, approverId);
            stmt.setInt(2, leaveId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ İzin talebi onaylandı");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin onaylama hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * İzin talebini reddeder
     */
    public boolean reject(int leaveId, int approverId) {
        String sql = "UPDATE LEAVE_REQUESTS SET status = 'REJECTED', approved_by = ?, " +
                    "approval_date = CURRENT_TIMESTAMP WHERE leave_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, approverId);
            stmt.setInt(2, leaveId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ İzin talebi reddedildi");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin reddetme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * İzin talebini siler
     */
    public boolean delete(int leaveId) {
        String sql = "DELETE FROM LEAVE_REQUESTS WHERE leave_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, leaveId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ İzin talebi silindi");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin talebi silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * ID'ye göre izin talebini getirir
     */
    public LeaveRequest findById(int leaveId) {
        String sql = "SELECT lr.*, " +
                    "CONCAT(e.first_name, ' ', e.last_name) as employee_name, " +
                    "CONCAT(a.first_name, ' ', a.last_name) as approver_name " +
                    "FROM LEAVE_REQUESTS lr " +
                    "JOIN EMPLOYEES e ON lr.employee_id = e.employee_id " +
                    "LEFT JOIN EMPLOYEES a ON lr.approved_by = a.employee_id " +
                    "WHERE lr.leave_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, leaveId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractLeaveRequestFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin talebi bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Çalışanın tüm izin taleplerini getirir
     */
    public List<LeaveRequest> findByEmployee(int employeeId) {
        List<LeaveRequest> leaves = new ArrayList<>();
        String sql = "SELECT lr.*, " +
                    "CONCAT(e.first_name, ' ', e.last_name) as employee_name, " +
                    "CONCAT(a.first_name, ' ', a.last_name) as approver_name " +
                    "FROM LEAVE_REQUESTS lr " +
                    "JOIN EMPLOYEES e ON lr.employee_id = e.employee_id " +
                    "LEFT JOIN EMPLOYEES a ON lr.approved_by = a.employee_id " +
                    "WHERE lr.employee_id = ? " +
                    "ORDER BY lr.request_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                leaves.add(extractLeaveRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ İzin talepleri listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return leaves;
    }
    
    /**
     * Bekleyen izin taleplerini getirir
     */
    public List<LeaveRequest> findPendingRequests() {
        List<LeaveRequest> leaves = new ArrayList<>();
        String sql = "SELECT lr.*, " +
                    "CONCAT(e.first_name, ' ', e.last_name) as employee_name, " +
                    "CONCAT(a.first_name, ' ', a.last_name) as approver_name " +
                    "FROM LEAVE_REQUESTS lr " +
                    "JOIN EMPLOYEES e ON lr.employee_id = e.employee_id " +
                    "LEFT JOIN EMPLOYEES a ON lr.approved_by = a.employee_id " +
                    "WHERE lr.status = 'PENDING' " +
                    "ORDER BY lr.request_date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                leaves.add(extractLeaveRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Bekleyen izin talepleri hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return leaves;
    }
    
    /**
     * Duruma göre izin taleplerini getirir
     */
    public List<LeaveRequest> findByStatus(String status) {
        List<LeaveRequest> leaves = new ArrayList<>();
        String sql = "SELECT lr.*, " +
                    "CONCAT(e.first_name, ' ', e.last_name) as employee_name, " +
                    "CONCAT(a.first_name, ' ', a.last_name) as approver_name " +
                    "FROM LEAVE_REQUESTS lr " +
                    "JOIN EMPLOYEES e ON lr.employee_id = e.employee_id " +
                    "LEFT JOIN EMPLOYEES a ON lr.approved_by = a.employee_id " +
                    "WHERE lr.status = ? " +
                    "ORDER BY lr.request_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                leaves.add(extractLeaveRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Durum bazlı izin talepleri hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return leaves;
    }
    
    /**
     * ResultSet'ten LeaveRequest nesnesi oluşturur
     */
    private LeaveRequest extractLeaveRequestFromResultSet(ResultSet rs) throws SQLException {
        LeaveRequest leave = new LeaveRequest();
        leave.setLeaveId(rs.getInt("leave_id"));
        leave.setEmployeeId(rs.getInt("employee_id"));
        leave.setLeaveType(rs.getString("leave_type"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            leave.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            leave.setEndDate(endDate.toLocalDate());
        }
        
        leave.setTotalDays(rs.getInt("total_days"));
        leave.setStatus(rs.getString("status"));
        leave.setReason(rs.getString("reason"));
        
        int approverId = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            leave.setApprovedBy(approverId);
        }
        
        Timestamp requestTs = rs.getTimestamp("request_date");
        if (requestTs != null) {
            leave.setRequestDate(requestTs.toLocalDateTime());
        }
        
        Timestamp approvalTs = rs.getTimestamp("approval_date");
        if (approvalTs != null) {
            leave.setApprovalDate(approvalTs.toLocalDateTime());
        }
        
        leave.setEmployeeName(rs.getString("employee_name"));
        leave.setApproverName(rs.getString("approver_name"));
        
        return leave;
    }
}