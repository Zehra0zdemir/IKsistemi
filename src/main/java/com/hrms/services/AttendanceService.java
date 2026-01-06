package com.hrms.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.hrms.dao.AttendanceDAO;
import com.hrms.model.Attendance;

/**
 * Devam takibi iş mantığı için Service sınıfı
 */
public class AttendanceService {
    
    private AttendanceDAO attendanceDAO;
    
    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }
    
    /**
     * Giriş kaydı yapar
     */
    public boolean checkIn(int employeeId) {
        return checkIn(employeeId, LocalDate.now(), LocalTime.now());
    }
    
    /**
     * Belirli tarih ve saat ile giriş kaydı yapar
     */
    public boolean checkIn(int employeeId, LocalDate date, LocalTime time) {
        // Aynı gün için kayıt var mı kontrol et
        if (attendanceDAO.existsForDate(employeeId, date)) {
            System.err.println("✗ Bu tarih için zaten giriş kaydı mevcut!");
            return false;
        }
        
        return attendanceDAO.checkIn(employeeId, date, time);
    }
    
    /**
     * Çıkış kaydı yapar
     */
    public boolean checkOut(int employeeId) {
        return checkOut(employeeId, LocalTime.now());
    }
    
    /**
     * Belirli saat ile çıkış kaydı yapar
     */
    public boolean checkOut(int employeeId, LocalTime time) {
        // Bugünkü kaydı bul
        Attendance attendance = attendanceDAO.getTodayAttendance(employeeId);
        
        if (attendance == null) {
            System.err.println("✗ Bugün için giriş kaydı bulunamadı!");
            return false;
        }
        
        if (attendance.getCheckOut() != null) {
            System.err.println("✗ Çıkış kaydı zaten yapılmış!");
            return false;
        }
        
        return attendanceDAO.checkOut(attendance.getAttendanceId(), time);
    }
    
    /**
     * Manuel devam kaydı ekler
     */
    public boolean addManualAttendance(Attendance attendance) {
        // Validasyon
        if (attendance.getEmployeeId() <= 0) {
            System.err.println("✗ Geçersiz çalışan ID!");
            return false;
        }
        
        if (attendance.getAttendanceDate() == null) {
            System.err.println("✗ Tarih boş olamaz!");
            return false;
        }
        
        // Aynı tarih için kayıt var mı kontrol
        if (attendanceDAO.existsForDate(attendance.getEmployeeId(), attendance.getAttendanceDate())) {
            System.err.println("✗ Bu tarih için zaten kayıt mevcut!");
            return false;
        }
        
        return attendanceDAO.insert(attendance);
    }
    
    /**
     * Devam kaydını günceller
     */
    public boolean updateAttendance(Attendance attendance) {
        if (attendance.getAttendanceId() <= 0) {
            System.err.println("✗ Geçersiz kayıt ID!");
            return false;
        }
        
        return attendanceDAO.update(attendance);
    }
    
    /**
     * Devam kaydını siler
     */
    public boolean deleteAttendance(int attendanceId) {
        return attendanceDAO.delete(attendanceId);
    }
    
    /**
     * ID'ye göre devam kaydı getirir
     */
    public Attendance getAttendanceById(int attendanceId) {
        return attendanceDAO.findById(attendanceId);
    }
    
    /**
     * Çalışanın tüm devam kayıtlarını getirir
     */
    public List<Attendance> getEmployeeAttendances(int employeeId) {
        return attendanceDAO.findByEmployee(employeeId);
    }
    
    /**
     * Tarih aralığındaki devam kayıtlarını getirir
     */
    public List<Attendance> getAttendancesByDateRange(int employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceDAO.findByDateRange(employeeId, startDate, endDate);
    }
    
    /**
     * Bugünkü tüm devam kayıtlarını getirir
     */
    public List<Attendance> getTodayAttendances() {
        return attendanceDAO.findToday();
    }
    
    /**
     * Çalışanın bugünkü devam kaydını getirir
     */
    public Attendance getTodayAttendance(int employeeId) {
        return attendanceDAO.getTodayAttendance(employeeId);
    }
    
    /**
     * Çalışanın bugün giriş yapıp yapmadığını kontrol eder
     */
    public boolean hasCheckedInToday(int employeeId) {
        return attendanceDAO.existsForDate(employeeId, LocalDate.now());
    }
    
    /**
     * Toplam çalışma saatini hesaplar
     */
    public BigDecimal calculateTotalHours(int employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceDAO.getTotalHoursWorked(employeeId, startDate, endDate);
    }
    
    /**
     * Aylık toplam çalışma saatini hesaplar
     */
    public BigDecimal calculateMonthlyHours(int employeeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        return attendanceDAO.getTotalHoursWorked(employeeId, startDate, endDate);
    }
    
    /**
     * Mesai saatini hesaplar (8 saatten fazla)
     */
    public BigDecimal calculateOvertimeHours(int employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceDAO.findByDateRange(employeeId, startDate, endDate);
        
        BigDecimal overtimeTotal = BigDecimal.ZERO;
        BigDecimal standardHours = new BigDecimal("8.0");
        
        for (Attendance att : attendances) {
            if (att.getHoursWorked() != null && att.getHoursWorked().compareTo(standardHours) > 0) {
                BigDecimal overtime = att.getHoursWorked().subtract(standardHours);
                overtimeTotal = overtimeTotal.add(overtime);
            }
        }
        
        return overtimeTotal;
    }
    
    /**
     * Devam oranını hesaplar (%)
     */
    public double calculateAttendanceRate(int employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceDAO.findByDateRange(employeeId, startDate, endDate);
        
        if (attendances.isEmpty()) {
            return 0.0;
        }
        
        long presentDays = attendances.stream()
            .filter(Attendance::isPresent)
            .count();
        
        return (presentDays * 100.0) / attendances.size();
    }
    
    /**
     * Devamsızlık sayısını hesaplar
     */
    public int calculateAbsentDays(int employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceDAO.findByDateRange(employeeId, startDate, endDate);
        
        return (int) attendances.stream()
            .filter(att -> "ABSENT".equals(att.getStatus()))
            .count();
    }
    
    /**
     * Geç kalma sayısını hesaplar
     */
    public int calculateLateDays(int employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceDAO.findByDateRange(employeeId, startDate, endDate);
        
        return (int) attendances.stream()
            .filter(att -> "LATE".equals(att.getStatus()))
            .count();
    }
}