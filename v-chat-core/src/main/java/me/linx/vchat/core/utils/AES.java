package me.linx.vchat.core.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Security;

public class AES {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";

    //字符编码
    private static final String CHARSET = "UTF-8";

    /**
     * 生成秘钥
     */
    public static SecretKeySpec newSecretKey() throws Exception {
        return newSecretKey(null);
    }

    /**
     * 生成秘钥
     */
    public static SecretKeySpec newSecretKey(final String password) throws Exception {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM, "BC");

        if (password == null) {
            keyGenerator.init(128);
        } else {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes(CHARSET));
            keyGenerator.init(128, secureRandom);
        }

        //生成一个密钥
        SecretKey secretKey = keyGenerator.generateKey();

        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);// 转换为AES专用密钥
    }

    /**
     * 秘钥转byts
     */
    public static byte[] getKeyBytes(SecretKeySpec keySpec) {
        return keySpec.getEncoded();
    }

    /**
     * bytes转秘钥
     */
    public static SecretKeySpec getKey(byte[] keySpec) {
        return new SecretKeySpec(keySpec, ALGORITHM);
    }

    /**
     * AES 加密操作
     */
    public static byte[] encrypt(byte[] data, SecretKeySpec key) throws Exception, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
        return cipher.doFinal(data);// 加密
    }

    /**
     * AES 加密操作
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return encrypt(data, getKey(key));
    }

    /**
     * AES 解密操作
     */
    public static byte[] decrypt(byte[] data, SecretKeySpec key) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");

        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * AES 解密操作
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, getKey(key));
    }

//    public static void main(String[] args) throws Exception {
//        String s = "hello,您好";
//
//        System.out.println(s);
//
//        SecretKeySpec secretKey = newSecretKey();
//
//        byte[] keyBytes = getKeyBytes(secretKey);
//
//        byte[] encrypt = encrypt(s.getBytes(), secretKey);
//
//        byte[] decrypt = decrypt(encrypt, keyBytes);
//        System.out.println(new String(decrypt));
//    }
}
