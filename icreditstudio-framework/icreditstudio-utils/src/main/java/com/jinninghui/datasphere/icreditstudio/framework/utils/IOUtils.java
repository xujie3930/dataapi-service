package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	private final static int CRYPTED_BYTES = 1024;
	private final static String HEAD = "V1";

	protected static String encrypt2Str(byte[] buff) {
		// Hex处理
		String result = bytes2HexString(buff);
		// 加密服务，demo，往字符串后面加"000";
		result = result + "000";
		//
		return result;
	}

	protected static byte[] decrypt2Bytes(String sEncryptedData) {
		// 解密服务，demo：减去尾巴上：000
		sEncryptedData = sEncryptedData.substring(0, sEncryptedData.length() - 3);
		// Hex处理
		return hexString2Bytes(sEncryptedData);
	}

	public static void encrypt(InputStream is, OutputStream os) throws IOException {
		final int sbytes = is.available() > CRYPTED_BYTES ? CRYPTED_BYTES : is.available();
		byte[] buff = new byte[sbytes];
		int len = is.read(buff, 0, sbytes);
		String encryptedData = encrypt2Str(buff);

		os.write(HEAD.getBytes());
		os.write(intToBytes(encryptedData.getBytes().length));
		os.write(encryptedData.getBytes());
		//
		byte[] buff2 = new byte[1024];
		len = is.read(buff2);
		while (len > 0) {
			os.write(buff2, 0, len);
			len = is.read(buff2);
		}
	}

	public static void decrypt(InputStream is, OutputStream os) throws IOException {
		byte headerBytes[] = new byte[HEAD.length()];
		int len = is.read(headerBytes);
		boolean isEncryptedData = false;
		if (len < 2 || !HEAD.equals(new String(headerBytes))) { // 判断是否加密数据
			isEncryptedData = false;
		} else {
			isEncryptedData = true;
		}
		if (!isEncryptedData) {
			os.write(headerBytes);

			byte[] buff2 = new byte[1024];
			len = is.read(buff2);
			while (len > 0) {
				os.write(buff2, 0, len);
				len = is.read(buff2);
			}
			return;
		} else {
			byte[] lenBytes = new byte[4];
			is.read(lenBytes);
			int slen = bytesToInt(lenBytes);
			byte[] encryptedData = new byte[slen];
			len = is.read(encryptedData);
			String sEncryptedData = new String(encryptedData);

			byte[] sEncryptedData2 = decrypt2Bytes(sEncryptedData);
			os.write(sEncryptedData2);
			//
			byte[] buff2 = new byte[1024];
			len = is.read(buff2);
			while (len > 0) {
				os.write(buff2, 0, len);
				len = is.read(buff2);
			}
			return;
		}

	}

	protected static byte[] intToBytes(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	// 将低字节在前转为int，高字节在后的byte数组(与IntToByteArray1想对应)
	protected static int bytesToInt(byte[] bArr) {
		if (bArr.length != 4) {
			return -1;
		}
		return (int) ((((bArr[3] & 0xff) << 24) | ((bArr[2] & 0xff) << 16) | ((bArr[1] & 0xff) << 8)
				| ((bArr[0] & 0xff) << 0)));
	}

	protected static String bytes2HexString(byte[] buffer) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result.append(hex.toUpperCase());
		}
		return result.toString();
	}

	protected static byte[] hexString2Bytes(String src) {
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
		}
		return ret;
	}
}
