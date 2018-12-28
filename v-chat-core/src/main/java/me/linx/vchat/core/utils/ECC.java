package me.linx.vchat.core.utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * ECC非对称加密算法工具类
 *
 * @author lixk
 */

public class ECC {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    //字符编码
    private static final String CHARSET = "UTF-8";

    /**
     * 生成密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        return getKeyPair(null);
    }

    /**
     * 生成密钥对
     */
    public static KeyPair getKeyPair(String password) throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        //初始化密钥生成器
        if (password == null) {
            keyPairGenerator.initialize(256);
        } else {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes(CHARSET));
            keyPairGenerator.initialize(256, secureRandom);
        }
        //生成密钥对
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 取得私钥
     */
    public static byte[] getPrivateKeyBytes(KeyPair privateKey) {
        return privateKey.getPrivate().getEncoded();
    }

    /**
     * 取得私钥
     */
    public static PrivateKey getPrivateKey(byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 取得公钥
     */
    public static byte[] getPublicKeyBytes(KeyPair keyPair) {
        return keyPair.getPublic().getEncoded();
    }

    /**
     * 取得公钥
     */
    public static PublicKey getPublicKey(byte[] publicKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 公钥加密
     */
    public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        //数据加密
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        return encryptByPublicKey(data, getPublicKey(publicKey));
    }

    /**
     * 私钥解密
     */
    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        //数据解密
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
        return decryptByPrivateKey(data, getPrivateKey(privateKey));
    }

    /**
     * 测试加解密方法
     *
     * @param args
     * @throws Exception
     */
//    public static void main(String[] args) throws Exception {
//        //生成密钥对，一般生成之后可以放到配置文件中
//        KeyPair keyPair = getKeyPair();
//
//        byte[] publicKeyBytes = getPublicKeyBytes(keyPair);
//        byte[] privateKeyBytes = getPrivateKeyBytes(keyPair);
//
//        String data = "dataXXXxxxqqqwww";
//        System.out.println(data);
////        {
////            System.out.println("\n===========私钥加密，公钥解密==============");
////            String s1 = encryptByPrivateKey(data, keyPair.getPrivate());
////            System.out.println("加密后的数据:" + s1);
////            String s2 = decryptByPublicKey(s1, keyPair.getPublic());
////            System.out.println("解密后的数据:" + s2 + "\n\n");
////        }
//
//        System.out.println("\n===========公钥加密，私钥解密==============");
//        byte[] bytes = encryptByPublicKey(data.getBytes(), publicKeyBytes);
//
//        byte[] bytes1 = decryptByPrivateKey(bytes, privateKeyBytes);
//
//        System.out.println("解密后的数据:" + (new String(bytes1)) + "\n\n");
//
//    }
}
