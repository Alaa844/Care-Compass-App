package com.example.demo.controller;

public class LoggedInUser {
    private static String name;
    private static Long id;

    public static void setUser(String userName, Long userId) {
        name = userName;
        id = userId;
    }

    public static String getName() {
        return name;
    }

    public static Long getId() {
        return id;
    }

    public static void clear() {
        name = null;
        id = null;
    }
}
