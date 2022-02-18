package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author : zhouhuaming
 * @Date : 2017/9/30 14:51
 * @Description : MD5加密
 */
public class MD5Utils {

	// 生成MD5
	public static String getMD5(String message) {
		String md5 = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5"); // 创建一个md5算法对象
			byte[] messageByte = message.getBytes("UTF-8");
			byte[] md5Byte = md.digest(messageByte); // 获得MD5字节数组,16*8=128位
			md5 = bytesToHex(md5Byte); // 转换为16进制字符串
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5;
	}

	// 二进制转十六进制
	public static String bytesToHex(byte[] bytes) {
		StringBuffer hexStr = new StringBuffer();
		int num;
		for (int i = 0; i < bytes.length; i++) {
			num = bytes[i];
			if (num < 0) {
				num += 256;
			}
			if (num < 16) {
				hexStr.append("0");
			}
			hexStr.append(Integer.toHexString(num));
		}
		return hexStr.toString();
	}

	public static String MD5(String sourceStr) throws NoSuchAlgorithmException {
		String result = "";// 通过result返回结果值
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");// 1.初始化MessageDigest信息摘要对象,并指定为MD5不分大小写都可以
			md.update(sourceStr.getBytes());// 2.传入需要计算的字符串更新摘要信息，传入的为字节数组byte[],将字符串转换为字节数组使用getBytes()方法完成
			byte b[] = md.digest();// 3.计算信息摘要digest()方法,返回值为字节数组

			int i;// 定义整型
			// 声明StringBuffer对象
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];// 将首个元素赋值给i
				if (i < 0) {
                    i += 256;
                }
				if (i < 16) {
                    buf.append("0");// 前面补0
                }
				buf.append(Integer.toHexString(i));// 转换成16进制编码
			}
			result = buf.toString();// 转换成字符串
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
		return result;// 返回结果
	}

	public static String Bit16(String SourceString) throws Exception {
		return MD5(SourceString).substring(8, 24);
	}

	public static String Bit32(String SourceString) throws Exception {
		return MD5(SourceString);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(MD5Utils.Bit32("6217857000005361108"));
		System.out.println(MD5Utils.Bit16("6217857000005361108"));
	}
}
