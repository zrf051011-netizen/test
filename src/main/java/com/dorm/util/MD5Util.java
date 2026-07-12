package com.dorm.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;

public class MD5Util {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    private MD5Util() {
    }

    public static String generateSalt() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            builder.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return builder.toString();
    }

    public static String encrypt(String password, String salt) {
        return DigestUtils.md5Hex((password == null ? "" : password) + (salt == null ? "" : salt));
    }

    public static String encryptPlain(String password) {
        return DigestUtils.md5Hex(password == null ? "" : password);
    }
}
