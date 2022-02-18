package com.jinninghui.datasphere.icreditstudio.framework.utils;

public class SensitiveData {

	/**
	 * 加密数据
	 * @param data
	 * @return
	 */
	public static String encryptData(String data) {
		return new String(Base64.encode(data.getBytes()));
	}
	
	/**
	 * 解密数据
	 * @param data
	 * @return
	 */
	public static String decrypt(String data) {
		return new String(Base64.decode(data));
	}
}
