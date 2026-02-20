package com.example.music.security;

import io.jsonwebtoken.Claims;
//ThreadLocal 保存当前登录用户
public class AuthContext {
    private static final ThreadLocal<Claims> TL = new ThreadLocal<>();

    public static void set(Claims claims) {
        TL.set(claims);
    }

    public static Claims get() {
        return TL.get();
    }

    public static Long getUserId() {
        Claims c = TL.get();
        if (c == null) return null;
        Object v = c.get("uid");
        if (v == null) return null;
        return Long.parseLong(String.valueOf(v));
    }

    public static String getRole() {
        Claims c = TL.get();
        if (c == null) return null;
        Object v = c.get("role");
        return v == null ? null : String.valueOf(v);
    }

    public static void clear() {
        TL.remove();
    }
}
