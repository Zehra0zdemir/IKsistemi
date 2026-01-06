package com.hrms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.hrms.dao.LeaveRequestDAO;
import com.hrms.model.Attendance;
import com.hrms.model.LeaveRequest;
import com.hrms.services.AttendanceService;

/**
 * Devam takibi için Controller sınıfı
 */
public class AttendanceController {

    public record AttendanceResult(boolean success, String message, Attendance attendance) {}
    public record ListResult(boolean success, String message, List<Attendance> attendances) {}
    public record LeaveResult(boolean success, String message, LeaveRequest leave) {}
    public record LeaveListResult(boolean success, String message, List<LeaveRequest> leaves) {}

    private final AttendanceService attendanceService;
    private final LeaveRequestDAO leaveRequestDAO;

    public AttendanceController() {
        this.attendanceService = new AttendanceService();
        this.leaveRequestDAO = new LeaveRequestDAO();
    }

    /**
     * Giriş kaydı yapar (Check-in)
     */
    public AttendanceResult checkIn(int employeeId) {
        try {
            boolean success = attendanceService.checkIn(employeeId);
            if (success) {
                Attendance attendance = attendanceService.getTodayAttendance(employeeId);
                return new AttendanceResult(true, "Giriş kaydedildi", attendance);
            } else {
                return new AttendanceResult(false, "Giriş kaydı yapılamadı", null);
            }
        } catch (Exception e) {
            return new AttendanceResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Çıkış kaydı yapar (Check-out)
     */
    public AttendanceResult checkOut(int employeeId) {
        try {
            boolean success = attendanceService.checkOut(employeeId);
            if (success) {
                Attendance attendance = attendanceService.getTodayAttendance(employeeId);
                return new AttendanceResult(true, "Çıkış kaydedildi", attendance);
            } else {
                return new AttendanceResult(false, "Çıkış kaydı yapılamadı", null);
            }
        } catch (Exception e) {
            return new AttendanceResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Manuel devam kaydı ekler
     */
    public AttendanceResult addManualAttendance(
            int employeeId,
            LocalDate date,
            LocalTime checkIn,
            LocalTime checkOut,
            String notes
    ) {
        try {
            Attendance attendance = new Attendance(employeeId, date, checkIn);
            attendance.setCheckOut(checkOut);
            attendance.setNotes(notes);

            boolean success = attendanceService.addManualAttendance(attendance);

            if (success) {
                return new AttendanceResult(true, "Devam kaydı eklendi", attendance);
            } else {
                return new AttendanceResult(false, "Kayıt eklenemedi", null);
            }
        } catch (Exception e) {
            return new AttendanceResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Çalışanın bugünkü devam durumunu kontrol eder
     */
    public AttendanceResult getTodayStatus(int employeeId) {
        try {
            Attendance attendance = attendanceService.getTodayAttendance(employeeId);
            if (attendance != null) {
                String message = attendance.hasCheckedOut() 
                    ? "Giriş ve çıkış yapıldı" 
                    : "Sadece giriş yapıldı";
                return new AttendanceResult(true, message, attendance);
            } else {
                return new AttendanceResult(false, "Bugün için kayıt yok", null);
            }
        } catch (Exception e) {
            return new AttendanceResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Çalışanın tüm devam kayıtlarını getirir
     */
    public ListResult getEmployeeAttendances(int employeeId) {
        try {
            List<Attendance> attendances = attendanceService.getEmployeeAttendances(employeeId);
            return new ListResult(true, "Kayıtlar getirildi", attendances);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Tarih aralığındaki devam kayıtlarını getirir
     */
    public ListResult getAttendancesByDateRange(
            int employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            List<Attendance> attendances = attendanceService
                .getAttendancesByDateRange(employeeId, startDate, endDate);
            return new ListResult(true, "Kayıtlar getirildi", attendances);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Bugünkü tüm devam kayıtlarını getirir
     */
    public ListResult getTodayAttendances() {
        try {
            List<Attendance> attendances = attendanceService.getTodayAttendances();
            return new ListResult(true, "Bugünkü kayıtlar", attendances);
        } catch (Exception e) {
            return new ListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Toplam çalışma saatini hesaplar
     */
    public record HoursResult(boolean success, String message, BigDecimal hours) {}

    public HoursResult calculateTotalHours(
            int employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            BigDecimal hours = attendanceService.calculateTotalHours(
                employeeId, startDate, endDate
            );
            return new HoursResult(true, "Hesaplandı", hours);
        } catch (Exception e) {
            return new HoursResult(false, "Hata: " + e.getMessage(), BigDecimal.ZERO);
        }
    }

    /**
     * Mesai saatini hesaplar
     */
    public HoursResult calculateOvertimeHours(
            int employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            BigDecimal hours = attendanceService.calculateOvertimeHours(
                employeeId, startDate, endDate
            );
            return new HoursResult(true, "Hesaplandı", hours);
        } catch (Exception e) {
            return new HoursResult(false, "Hata: " + e.getMessage(), BigDecimal.ZERO);
        }
    }

    /**
     * Devam oranını hesaplar
     */
    public record RateResult(boolean success, String message, double rate) {}

    public RateResult calculateAttendanceRate(
            int employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            double rate = attendanceService.calculateAttendanceRate(
                employeeId, startDate, endDate
            );
            return new RateResult(true, "Hesaplandı", rate);
        } catch (Exception e) {
            return new RateResult(false, "Hata: " + e.getMessage(), 0.0);
        }
    }

    // ============================================
    // İZİN TALEPLERİ (LEAVE REQUESTS)
    // ============================================

    /**
     * Yeni izin talebi oluşturur
     */
    public LeaveResult createLeaveRequest(
            int employeeId,
            String leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String reason
    ) {
        try {
            if (startDate.isAfter(endDate)) {
                return new LeaveResult(false, "Bitiş tarihi başlangıçtan önce olamaz", null);
            }

            LeaveRequest leave = new LeaveRequest(
                employeeId, leaveType, startDate, endDate, reason
            );

            boolean success = leaveRequestDAO.insert(leave);

            if (success) {
                return new LeaveResult(true, "İzin talebi oluşturuldu", leave);
            } else {
                return new LeaveResult(false, "Talep oluşturulamadı", null);
            }
        } catch (Exception e) {
            return new LeaveResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * İzin talebini onaylar
     */
    public LeaveResult approveLeaveRequest(int leaveId, int approverId) {
        try {
            boolean success = leaveRequestDAO.approve(leaveId, approverId);
            if (success) {
                LeaveRequest leave = leaveRequestDAO.findById(leaveId);
                return new LeaveResult(true, "İzin onaylandı", leave);
            } else {
                return new LeaveResult(false, "Onaylama başarısız", null);
            }
        } catch (Exception e) {
            return new LeaveResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * İzin talebini reddeder
     */
    public LeaveResult rejectLeaveRequest(int leaveId, int approverId) {
        try {
            boolean success = leaveRequestDAO.reject(leaveId, approverId);
            if (success) {
                LeaveRequest leave = leaveRequestDAO.findById(leaveId);
                return new LeaveResult(true, "İzin reddedildi", leave);
            } else {
                return new LeaveResult(false, "Reddetme başarısız", null);
            }
        } catch (Exception e) {
            return new LeaveResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Çalışanın izin taleplerini getirir
     */
    public LeaveListResult getEmployeeLeaveRequests(int employeeId) {
        try {
            List<LeaveRequest> leaves = leaveRequestDAO.findByEmployee(employeeId);
            return new LeaveListResult(true, "İzinler getirildi", leaves);
        } catch (Exception e) {
            return new LeaveListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Bekleyen izin taleplerini getirir
     */
    public LeaveListResult getPendingLeaveRequests() {
        try {
            List<LeaveRequest> leaves = leaveRequestDAO.findPendingRequests();
            return new LeaveListResult(true, "Bekleyen izinler", leaves);
        } catch (Exception e) {
            return new LeaveListResult(false, "Hata: " + e.getMessage(), null);
        }
    }

    /**
     * Duruma göre izin taleplerini getirir
     */
    public LeaveListResult getLeaveRequestsByStatus(String status) {
        try {
            List<LeaveRequest> leaves = leaveRequestDAO.findByStatus(status);
            return new LeaveListResult(true, "İzinler getirildi", leaves);
        } catch (Exception e) {
            return new LeaveListResult(false, "Hata: " + e.getMessage(), null);
        }
    }
}