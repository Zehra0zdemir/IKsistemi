package com.hrms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Tarih işlemleri için utility sınıfı
 */
public final class DateUtil {
    
    private DateUtil() {}

    // Mevcut formatter
    private static final DateTimeFormatter TR = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    // Yeni formatterlar
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter SQL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Mevcut metod - korundu
     */
    public static String formatTR(LocalDateTime dt) {
        if (dt == null) return "-";
        return TR.format(dt);
    }
    
    /**
     * LocalDate'i SQL formatına çevirir
     */
    public static String toSqlDate(LocalDate date) {
        return date.format(SQL_DATE_FORMATTER);
    }
    
    /**
     * String tarihi LocalDate'e çevirir
     */
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    /**
     * İki tarih arasındaki gün sayısını hesaplar
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Bugünün tarihini döndürür
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Tarihi kullanıcı dostu formatta gösterir
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * DateTime'ı formatlar
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
}