package com.jinninghui.datasphere.icreditstudio.framework.utils;


import java.util.Map;


/**
 * 描述 ：新的加解密接口
 *
 * @author lidab
 * @date 2017/12/21.
 */
public interface SecurityService {

    public static String ALGORITHM_AES256 = "AES256";
    public static String ALGORITHM_AES128 = "AES128";
    public static String ALGORITHM_AES192 = "AES192";
    public static String ALGORITHM_DES = "DES";

    /**
     * 加密数据（需加密的数据明文，算法[AES128/AES192/AES256/DES]）
     *
     * @param data      需要加密的明文数据
     * @param algorithm 加密算法
     * @return
     */
    public String encryptData(String data, String algorithm);

    /**
     * 批量加密数据
     *
     * @param dataMap   value中存放需要加密的明文数据
     * @param algorithm 加密算法
     * @return
     */
    public Map<String, String> encryptDataMap(Map<String, String> dataMap, String algorithm);

    /**
     * 解密数据
     *
     * @param data 需要解密的密文数据
     * @return
     */
    public String decryptData(String data);

    /**
     * 批量解密数据
     *
     * @param dataMap value中存放需要解密的密文数据
     * @return
     */
    public Map<String, String> decryptDataMap(Map<String, String> dataMap);
}