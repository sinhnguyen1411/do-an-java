package com.elearning.util;

import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validator() {
    }

    public static boolean isEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
