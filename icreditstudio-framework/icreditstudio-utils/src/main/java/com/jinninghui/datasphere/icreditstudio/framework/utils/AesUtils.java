package com.jinninghui.datasphere.icreditstudio.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * 描述 ：
 *
 * @author lidab
 * @date 2017/12/21.
 */
public class AesUtils {

    public final static String CHARSET = "utf-8";

    /**
     * 密钥算法
     */
    public final static String KEY_ALGORITHM = "AES";

    /**
     * 加密算法/工作模式/填充方式
     */
    public final static String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    /**
     * 转换密钥
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        // 生成秘密密钥
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws Exception {
        // 还原密钥
        Key k = toKey(key.getBytes());
        // 实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化， 设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(data)));
    }

    /**
     * 加密
     *
     * @param data
     * @param key 16位
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        // 还原密钥
        Key k = toKey(key.getBytes());
        // 实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化， 设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(cipher.doFinal(data.getBytes())), CHARSET);

    }
    
    public static void main(String[] args) throws Exception {
    	//高级加密标准，是下一代的加密算法标准，速度快，安全级别高, key的长度为16位
		String encrypt = AesUtils.encrypt("11", "hashtechiright00");
		System.out.println(encrypt);
    }
}
