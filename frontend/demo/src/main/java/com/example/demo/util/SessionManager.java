package com.example.demo.util;

public class SessionManager {
    private static String userName;
    private static Long userId;
    private static String userEmail; // NEW

    public static void setUser(String name, Long id, String email) {
        userName = name;
        userId = id;
        userEmail = email;
    }

    public static String getUserName() {
        return userName;
    }

    public static Long getUserId() {
        return userId;
    }

    public static String getUserEmail() {
        return userEmail;
    }
}
