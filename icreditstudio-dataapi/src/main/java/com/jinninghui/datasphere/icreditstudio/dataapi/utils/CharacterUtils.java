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

    public static String getGUID(int length) {
        StringBuilder uid = new StringBuilder();
        Random rd = new Random();
        for (int i = 0; i < length; ++i) {
            int type = rd.nextInt(3);
            switch (type) {

                case 0:
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                case 2:
                    uid.append((char) (rd.nextInt(25) + 97));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }

    public static void main(String[] args) {
        String uuidStr = getGUID(11);
        System.out.println(uuidStr);
    }

}
