package me.linx.vchat.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String generateSalt(){
        return BCrypt.gensalt();
    }

    public static String generate(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }

    /**
     *  比较密码
     * @param salt 盐
     * @param pwd1 未加密的密码
     * @param pwd2 加密后的密码
     * @return 是否匹配
     */
    public static boolean check(String pwd1, String pwd2) {
        return BCrypt.checkpw(pwd1, pwd2);
    }
}
