package com.agritrace.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class CodeGenerator {

    private CodeGenerator() {}

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generateTraceCode() {
        return "TRC" + LocalDateTime.now().format(DATE_FORMAT) + randomSuffix(4);
    }

    public static String generateBatchNo() {
        return "B" + LocalDateTime.now().format(DATE_FORMAT) + randomSuffix(2);
    }

    public static String generateCertNo() {
        return "CQ" + LocalDateTime.now().format(DATE_FORMAT) + randomSuffix(4);
    }

    public static String generateSessionId() {
        return "sess_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DATE_FORMAT) + randomSuffix(6);
    }

    private static String randomSuffix(int length) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}
