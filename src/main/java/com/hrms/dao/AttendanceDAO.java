package com.hrms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Attendance;
import com.hrms.util.DatabaseConnection;

/**
 * Devam kaydı veritabanı işlemleri için DAO sınıfı
 */
public class AttendanceDAO {
    
    private Connection connection;
    
    public AttendanceDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Giriş kaydı oluşturur (Check-in)
     */
    public boolean checkIn(int employeeId, LocalDate date, LocalTime time) {
        // Bugün için kayıt var mı kontrol et
        if (existsForDate(employeeId, date)) {
            System.err.println("✗ Bu tarih için zaten giriş kaydı mevcut!");
            return false;
        }
        
        String sql = "INSERT INTO ATTENDANCE (employee_id, attendance_date, check_in, status) " +
                    "VALUES (?, ?, ?, 'PRESENT')";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Giriş kaydı oluşturuldu: " + date + " " + time);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Giriş kaydı hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Çıkış kaydı yapar (Check-out)
     * Mesai saati trigger tarafından otomatik hesaplanacak
     */
    public boolean checkOut(int attendanceId, LocalTime time) {
        String sql = "UPDATE ATTENDANCE SET check_out = ? WHERE attendance_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTime(1, Time.valueOf(time));
            stmt.setInt(2, attendanceId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Çıkış kaydı yapıldı: " + time);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Çıkış kaydı hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Devam kaydı ekler (Manuel)
     */
    public boolean insert(Attendance attendance) {
        String sql = "INSERT INTO ATTENDANCE (employee_id, attendance_date, check_in, check_out, " +
                    "status, notes) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, attendance.getEmployeeId());
            stmt.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            
            if (attendance.getCheckIn() != null) {
                stmt.setTime(3, Time.valueOf(attendance.getCheckIn()));
            } else {
                stmt.setNull(3, Types.TIME);
            }
            
            if (attendance.getCheckOut() != null) {
                stmt.setTime(4, Time.valueOf(attendance.getCheckOut()));
            } else {
                stmt.setNull(4, Types.TIME);
            }
            
            stmt.setString(5, attendance.getStatus());
            stmt.setString(6, attendance.getNotes());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    attendance.setAttendanceId(rs.getInt(1));
                }
                System.out.println("✓ Devam kaydı eklendi");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Devam kaydı ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Devam kaydı günceller
     */
    public boolean update(Attendance attendance) {
        String sql = "UPDATE ATTENDANCE SET check_in = ?, check_out = ?, status = ?, notes = ? " +
                    "WHERE attendance_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (attendance.getCheckIn() != null) {
                stmt.setTime(1, Time.valueOf(attendance.getCheckIn()));
            } else {
                stmt.setNull(1, Types.TIME);
            }
            
            if (attendance.getCheckOut() != null) {
                stmt.setTime(2, Time.valueOf(attendance.getCheckOut()));
            } else {
                stmt.setNull(2, Types.TIME);
            }
            
            stmt.setString(3, attendance.getStatus());
            stmt.setString(4, attendance.getNotes());
            stmt.setInt(5, attendance.getAttendanceId());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Devam kaydı güncellendi");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Devam kaydı güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Devam kaydı siler
     */
    public boolean delete(int attendanceId) {
        String sql = "DELETE FROM ATTENDANCE WHERE attendance_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Devam kaydı silindi");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Devam kaydı silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * ID'ye göre devam kaydı getirir
     */
    public Attendance findById(int attendanceId) {
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM ATTENDANCE a " +
                    "JOIN EMPLOYEES e ON a.employee_id = e.employee_id " +
                    "WHERE a.attendance_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Devam kaydı bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Çalışanın tüm devam kayıtlarını getirir
     */
    public List<Attendance> findByEmployee(int employeeId) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM ATTENDANCE a " +
                    "JOIN EMPLOYEES e ON a.employee_id = e.employee_id " +
                    "WHERE a.employee_id = ? " +
                    "ORDER BY a.attendance_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Devam kayıtları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return attendances;
    }
    
    /**
     * Belirli tarih aralığındaki devam kayıtlarını getirir
     */
    public List<Attendance> findByDateRange(int employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM ATTENDANCE a " +
                    "JOIN EMPLOYEES e ON a.employee_id = e.employee_id " +
                    "WHERE a.employee_id = ? AND a.attendance_date BETWEEN ? AND ? " +
                    "ORDER BY a.attendance_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Tarih aralığı devam kayıtları hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return attendances;
    }
    
    /**
     * Bugünkü tüm devam kayıtlarını getirir
     */
    public List<Attendance> findToday() {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM ATTENDANCE a " +
                    "JOIN EMPLOYEES e ON a.employee_id = e.employee_id " +
                    "WHERE a.attendance_date = CURDATE() " +
                    "ORDER BY a.check_in";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                attendances.add(extractAttendanceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Bugünkü devam kayıtları hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return attendances;
    }
    
    /**
     * Çalışan için belirli tarihte kayıt var mı kontrol eder
     */
    public boolean existsForDate(int employeeId, LocalDate date) {
        String sql = "SELECT COUNT(*) as count FROM ATTENDANCE WHERE employee_id = ? AND attendance_date = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("✗ Devam kaydı kontrol hatası: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Çalışanın bugün için devam kaydını getirir
     */
    public Attendance getTodayAttendance(int employeeId) {
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) as employee_name " +
                    "FROM ATTENDANCE a " +
                    "JOIN EMPLOYEES e ON a.employee_id = e.employee_id " +
                    "WHERE a.employee_id = ? AND a.attendance_date = CURDATE()";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Bugünkü devam kaydı hatası: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Toplam çalışma saatini hesaplar
     */
    public BigDecimal getTotalHoursWorked(int employeeId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT SUM(hours_worked) as total FROM ATTENDANCE " +
                    "WHERE employee_id = ? AND attendance_date BETWEEN ? AND ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("✗ Toplam mesai hesaplama hatası: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * ResultSet'ten Attendance nesnesi oluşturur
     */
    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        Attendance att = new Attendance();
        att.setAttendanceId(rs.getInt("attendance_id"));
        att.setEmployeeId(rs.getInt("employee_id"));
        
        Date attDate = rs.getDate("attendance_date");
        if (attDate != null) {
            att.setAttendanceDate(attDate.toLocalDate());
        }
        
        Time checkIn = rs.getTime("check_in");
        if (checkIn != null) {
            att.setCheckIn(checkIn.toLocalTime());
        }
        
        Time checkOut = rs.getTime("check_out");
        if (checkOut != null) {
            att.setCheckOut(checkOut.toLocalTime());
        }
        
        att.setHoursWorked(rs.getBigDecimal("hours_worked"));
        att.setStatus(rs.getString("status"));
        att.setNotes(rs.getString("notes"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            att.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        att.setEmployeeName(rs.getString("employee_name"));
        
        return att;
    }
}