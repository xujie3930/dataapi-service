package com.jinninghui.datasphere.icreditstudio.framework.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 *   读取证书文件为公私钥
 * @author jidonglin
 *
 */
public class KeyStoreUtil {

	private static KeyStore ks;
	private static String privateKeyStr;

	/**
	 * 加载私钥证书
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void loadPrivateCertKeyStore(String password, String privatePath) throws Exception {
		if (ks == null) {
			try {
				ks = KeyStore.getInstance("PKCS12");
				// 获得密钥库文件流
				FileInputStream is = new FileInputStream(privatePath);
				// 加载密钥库
				ks.load(is, password.toCharArray());
				// 关闭密钥库文件流
				is.close();
			} catch (Exception ex) {
				ks = null;
			}
		}

	}

	/**
	 * 获取证书私钥字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	public synchronized static String getPrimaryKey(String password, String privatePath) throws Exception {
		if (privateKeyStr != null) {
			return privateKeyStr;
		} else {
			loadPrivateCertKeyStore(password, privatePath);
			String keyAlias = getKeyAlias();
			PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());
			privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
			return privateKeyStr;
		}

	}

	/**
	 * 获取证书公钥字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey() throws Exception {
		String keyAlias = getKeyAlias();
		// 公钥
		Certificate certificate = ks.getCertificate(keyAlias);

		certificate.getPublicKey().getAlgorithm();
		String publicKeyStr = Base64.encodeBase64String(certificate.getPublicKey().getEncoded());
		return publicKeyStr;
	}

	public static String getKeyAlias() throws Exception {
		Enumeration<String> aliases = ks.aliases();
		String keyAlias = null;

		if (aliases.hasMoreElements()) {
			keyAlias = (String) aliases.nextElement();
		}
		return keyAlias;
	}

}
