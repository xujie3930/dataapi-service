package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登陆账号校验
 * @author jidonglin
 *
 */
public class LoginAccount {
	
	/**
	 * 检测邮箱
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
		       String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 检测手机号
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			String REGEX_MOBILE = "^1[3456789]\\d{9}$";
			return Pattern.matches(REGEX_MOBILE, mobileNumber);
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 	生成指定长度的密码，包含数字、大小写字母、特殊字符的密码
	 * @param len
	 * @return
	 */
	public static String makeRandomPassword(int len){
        char charr[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*.?".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
 
        for (int x = 0; x < len; ++x) {
            sb.append(charr[r.nextInt(charr.length)]);
        }
        return sb.toString();
    }
	
	public static String getRandomPassword(int len) {
        String result = null;
            result = makeRandomPassword(len);
            if (result.matches(".*[a-z]{1,}.*") && result.matches(".*[A-Z]{1,}.*") && result.matches(".*[0-9]{1,}.*") && result.matches(".*[~!@#$%^&*\\.?]{1,}.*")) {
                return result;
            }
            return getRandomPassword(len);
    }
	
	public static void main(String[] args) {
//		System.out.println(LoginAccount.getRandomPassword(12));
		System.out.println(LoginAccount.checkMobileNumber("18813750711"));
//		System.out.println(LoginAccount.makeRandomPassword(12));
		if (!LoginAccount.checkEmail("18362097645")) {
			if (!LoginAccount.checkMobileNumber("18362097645")) {
				System.out.println("aa");
			}
		}
		
	}
}
