package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

import java.util.Random;

/**
 * @author xujie
 * @description 1
 * @create 2022-02-24 16:49
 **/
public class CharacterUtils {
    public static String getRandomString(int length) {
        String str = "0123456789";
        String strExclusionZero = "123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            if (i == 0) {
                int number = random.nextInt(strExclusionZero.length());
                sb.append(strExclusionZero.charAt(number));
            } else {
                int number = random.nextInt(str.length());
                sb.append(str.charAt(number));
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getRandomString(11));
    }

}
