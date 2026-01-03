package com.hrms.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    private DateUtil() {}

    private static final DateTimeFormatter TR = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String formatTR(LocalDateTime dt) {
        if (dt == null) return "-";
        return TR.format(dt);
    }
}
