package cn.visolink.common.permission;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 * 提供多种加密方式
 */
public class EncryptUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String RSA_ALGORITHM = "RSA";
    
    // 默认AES密钥（实际使用时请配置到配置文件中）
    private static final String DEFAULT_AES_KEY = "visolink2024key!";
    
    // 默认RSA公钥（实际使用时请配置到配置文件中）
    private static final String DEFAULT_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC...";

    /**
     * AES加密
     */
    public static String aesEncrypt(String data) {
        return aesEncrypt(data, DEFAULT_AES_KEY);
    }

    /**
     * AES加密
     */
    public static String aesEncrypt(String data, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * AES解密
     */
    public static String aesDecrypt(String encryptedData, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encryptedData;
        }
    }

    /**
     * RSA加密
     */
    public static String rsaEncrypt(String data) {
        return rsaEncrypt(data, DEFAULT_RSA_PUBLIC_KEY);
    }

    /**
     * RSA加密
     */
    public static String rsaEncrypt(String data, String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String data) {
        try {
            return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String encodedData) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encodedData;
        }
    }

    /**
     * 简单异或加密（仅作示例，安全性较低）
     */
    public static String xorEncrypt(String data, String key) {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[dataBytes.length];
            
            for (int i = 0; i < dataBytes.length; i++) {
                result[i] = (byte) (dataBytes[i] ^ keyBytes[i % keyBytes.length]);
            }
            
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * 简单异或解密
     */
    public static String xorDecrypt(String encryptedData, String key) {
        try {
            byte[] dataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[dataBytes.length];
            
            for (int i = 0; i < dataBytes.length; i++) {
                result[i] = (byte) (dataBytes[i] ^ keyBytes[i % keyBytes.length]);
            }
            
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encryptedData;
        }
    }

    /**
     * 生成AES密钥
     */
    public static String generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            return DEFAULT_AES_KEY;
        }
    }
}
