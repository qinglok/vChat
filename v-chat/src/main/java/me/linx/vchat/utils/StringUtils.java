package me.linx.vchat.utils;

public class StringUtils {

    public static boolean isTrimNotEmpty(String s) {
        return s != null && s.trim().length() > 0;
    }

    public static boolean isTrimEmpty(String s) {
        return !isTrimNotEmpty(s);
    }
}
