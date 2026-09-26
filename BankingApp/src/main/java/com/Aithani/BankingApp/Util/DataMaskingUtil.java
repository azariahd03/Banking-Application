package com.Aithani.BankingApp.Util;

public class DataMaskingUtil {
    public static String maskMobile(String mobile) {

        if (mobile == null || mobile.length() < 4) {
            return "****";
        }

        return "*".repeat(mobile.length() - 4)
                + mobile.substring(mobile.length() - 4);
    }

    public static String maskPan(String pan) {

        if (pan == null || pan.length() < 5) {
            return "****";
        }

        return pan.substring(0, 3)
                + "*".repeat(pan.length() - 5)
                + pan.substring(pan.length() - 2);
    }
}
