package com.uniintern.portal.admin;

import java.util.regex.Pattern;

public class SecurityUtils {

    // 10 chars, at least one upper, one lower, one number, one special char
    private static final String PASSWORD_PATTERN = 
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{10,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isPasswordSecure(String password) {
        if (password == null) return false;
        return pattern.matcher(password).matches();
    }

    public static String getPasswordRequirementsMessage() {
        return "Password must be at least 10 characters long, containing at least one uppercase letter, one lowercase letter, one number, and one special character (@#$%^&+=!).";
    }
}
