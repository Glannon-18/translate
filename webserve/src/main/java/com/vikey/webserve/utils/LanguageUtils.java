package com.vikey.webserve.utils;

public class LanguageUtils {

    public static boolean isVietnamString(String str) {
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (isVietnamChar(arr[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVietnamChar(char ch) {
        if ((ch >= 0x00C0 && ch <= 0x00C3) ||
                (ch >= 0x00C8 && ch <= 0x00CA) ||
                (ch >= 0x00CC && ch <= 0x00CD) ||
                (ch >= 0x00D2 && ch <= 0x00D5) ||
                (ch >= 0x00D9 && ch <= 0x00DA) ||
                (ch >= 0x00DD && ch <= 0x00DD) ||
                (ch >= 0x00E0 && ch <= 0x00E3) ||
                (ch >= 0x00E8 && ch <= 0x00EA) ||
                (ch >= 0x00EC && ch <= 0x00ED) ||
                (ch >= 0x00F2 && ch <= 0x00F5) ||
                (ch >= 0x00F9 && ch <= 0x00FA) ||
                (ch >= 0x00FD && ch <= 0x00FD) ||
                (ch >= 0x0102 && ch <= 0x0103) ||
                (ch >= 0x0110 && ch <= 0x0111) ||
                (ch >= 0x0128 && ch <= 0x0129) ||
                (ch >= 0x0168 && ch <= 0x0169) ||
                (ch >= 0x01A0 && ch <= 0x01A1) ||
                (ch >= 0x01AF && ch <= 0x01B0) ||
                (ch >= 0x1EA0 && ch <= 0x1EF9))
            return true;

        return false;
    }
}
