package com.jinninghui.datasphere.icreditstudio.framework.utils;

public class HexUtil
{
  public static String bytes2HexString(byte[] buffer)
  {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < buffer.length; i++)
    {
      String hex = Integer.toHexString(buffer[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      result.append(hex.toUpperCase());
    }
    return result.toString();
  }
  
  public static byte[] hexString2Bytes(String src)
  {
    int l = src.length() / 2;
    byte[] ret = new byte[l];
    for (int i = 0; i < l; i++) {
      ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
    }
    return ret;
  }
}
