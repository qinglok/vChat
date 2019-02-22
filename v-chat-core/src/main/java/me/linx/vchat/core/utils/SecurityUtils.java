package me.linx.vchat.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtils {

    public static class AES {

        /**
         * 生成秘钥
         */
        public static SecretKeySpec newKey() throws Exception {
            return newKey(null);
        }

        /**
         * 生成秘钥
         */
        public static SecretKeySpec newKey(final String seed) throws Exception {
            //返回生成指定算法密钥生成器的 KeyGenerator 对象
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            if (seed == null) {
                keyGenerator.init(128);
            } else {
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(seed.getBytes());
                keyGenerator.init(128, secureRandom);
            }

            //生成一个密钥
            SecretKey secretKey = keyGenerator.generateKey();

            return new SecretKeySpec(secretKey.getEncoded(), "AES");// 转换为AES专用密钥
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
            return new SecretKeySpec(keySpec, "AES");
        }

        /**
         * AES 加密操作
         */
        public static byte[] encrypt(byte[] data, SecretKeySpec key) throws Exception, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

            ByteBuf base64Buf = Base64.encode(Unpooled.wrappedBuffer(data));
            data = new byte[base64Buf.readableBytes()];
            base64Buf.readBytes(data);

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
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] base64Data = cipher.doFinal(data);
            ByteBuf dataBuf = Base64.decode(Unpooled.wrappedBuffer(base64Data));
            data = new byte[dataBuf.readableBytes()];
            dataBuf.readBytes(data);
            return data;
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
//        SecretKeySpec secretKey = newKey();
//
//        byte[] keyBytes = getKeyBytes(secretKey);
//
//        byte[] encrypt = encrypt(s.getBytes(), secretKey);
//
//        byte[] decrypt = decrypt(encrypt, keyBytes);
//        System.out.println(new String(decrypt));
//    }
    }

    public static class RSA {

        /**
         * 生成密钥对
         */
        public static KeyPair getKeyPair() throws Exception {
            return getKeyPair(null);
        }

        /**
         * 生成密钥对
         */
        public static KeyPair getKeyPair(String seed) throws Exception {
            //实例化密钥生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            //初始化密钥生成器
            if (seed == null) {
                keyPairGenerator.initialize(1024);
            } else {
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(seed.getBytes());
                keyPairGenerator.initialize(1024, secureRandom);
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
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
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
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }

        /**
         * 公钥加密
         */
        public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey) throws Exception {
            //数据加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            return cipher.doFinal(data);
            int len = data.length;
            int maxLen = 117;
            return getBytes(data, cipher, len, maxLen);
        }

        private static byte[] getBytes(byte[] data, Cipher cipher, int len, int maxLen) throws IllegalBlockSizeException, BadPaddingException {
            int count = len / maxLen;
            if (count > 0) {
                byte[] ret = new byte[0];
                byte[] buff = new byte[maxLen];
                int index = 0;
                for (int i = 0; i < count; i++) {
                    System.arraycopy(data, index, buff, 0, maxLen);
                    ret = joins(ret, cipher.doFinal(buff));
                    index += maxLen;
                }
                if (index != len) {
                    int restLen = len - index;
                    buff = new byte[restLen];
                    System.arraycopy(data, index, buff, 0, restLen);
                    ret = joins(ret, cipher.doFinal(buff));
                }
                return ret;
            } else {
                return cipher.doFinal(data);
            }
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
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//            return cipher.doFinal(data);
            int len = data.length;
            int maxLen = 128;
            return getBytes(data, cipher, len, maxLen);
        }

        /**
         * 私钥解密
         */
        public static byte[] decryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
            return decryptByPrivateKey(data, getPrivateKey(privateKey));
        }

        private static byte[] joins(final byte[] prefix, final byte[] suffix) {
            byte[] ret = new byte[prefix.length + suffix.length];
            System.arraycopy(prefix, 0, ret, 0, prefix.length);
            System.arraycopy(suffix, 0, ret, prefix.length, suffix.length);
            return ret;
        }

//    /**
//     * 测试加解密方法
//     *
//     * @param args
//     * @throws Exception
//     */
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

//    public static void main(String[] args) throws Exception {
//        SecretKeySpec secretKeySpec = AES.newKey();
//        KeyPair keyPair = RSA.getKeyPair();
//
//        byte[] aesData = secretKeySpec.getEncoded();
//
//        byte[] aesEncrypt = RSA.encryptByPublicKey(aesData, keyPair.getPublic());
//
//        byte[] aesDecrypt = RSA.decryptByPrivateKey(aesEncrypt, keyPair.getPrivate().getEncoded());
//
//        System.out.println(1);
//    }
}
