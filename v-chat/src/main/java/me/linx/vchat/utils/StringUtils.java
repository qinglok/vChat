package me.linx.vchat.utils;

public class StringUtils {

    public static boolean isNotTrimEmpty(String s) {
        return s != null && s.trim().length() > 0;
    }

    public static boolean isTrimEmpty(String s) {
        return !isNotTrimEmpty(s);
    }
}
