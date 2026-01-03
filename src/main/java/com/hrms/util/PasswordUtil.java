package com.hrms.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final SecureRandom RNG = new SecureRandom();

    // İstersen artır: daha güvenli ama daha yavaş.
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_BYTES = 16;

    private PasswordUtil() {}

    // Format: pbkdf2$<iter>$<saltB64>$<hashB64>
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank())
            throw new IllegalArgumentException("Şifre boş olamaz.");

        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);

        byte[] dk = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        return "pbkdf2$" + ITERATIONS + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(dk);
    }

    public static boolean verify(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) return false;

        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) return false;
            if (!parts[0].equals("pbkdf2")) return false;

            int iter = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iter, expected.length * 8);

            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iter, int keyLenBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iter, keyLenBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("Hash üretilemedi: " + e.getMessage(), e);
        }
    }
}
