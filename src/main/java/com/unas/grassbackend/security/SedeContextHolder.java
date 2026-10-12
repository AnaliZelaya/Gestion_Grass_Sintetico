package com.unas.grassbackend.security;

public class SedeContextHolder {
    // ThreadLocal garantiza que cada petición HTTP tenga su propia variable aislada (Thread-safe)
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setSede(String sede) {
        contextHolder.set(sede);
    }

    public static String getSede() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}