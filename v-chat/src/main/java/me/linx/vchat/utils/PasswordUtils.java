package me.linx.vchat.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String generate(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String pwd1, String pwd2) {
        return BCrypt.checkpw(pwd1, pwd2);
    }
}
