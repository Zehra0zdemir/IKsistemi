package com.hrms.util;

import java.util.regex.Pattern;

/**
 * Veri validasyonu için utility sınıfı
 */
public final class ValidationUtil {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^0[0-9]{10}$");

    private ValidationUtil() {}

    /**
     * Email formatını kontrol eder
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (e.isEmpty()) return false;
        return EMAIL.matcher(e).matches();
    }
    
    /**
     * Telefon numarası formatını kontrol eder (05551234567)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * String'in boş olup olmadığını kontrol eder
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Pozitif sayı kontrolü
     */
    public static boolean isPositive(double number) {
        return number > 0;
    }
    
    /**
     * İki değerin eşitliğini kontrol eder
     */
    public static boolean isEqual(String value1, String value2) {
        if (value1 == null || value2 == null) {
            return value1 == value2;
        }
        return value1.equals(value2);
    }
}
