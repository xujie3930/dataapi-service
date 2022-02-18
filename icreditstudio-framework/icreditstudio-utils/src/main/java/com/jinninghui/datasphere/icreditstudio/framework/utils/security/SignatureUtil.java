package com.jinninghui.datasphere.icreditstudio.framework.utils.security;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Enumeration;

public class SignatureUtil {
	public static byte[] sign(byte[] signData, String algorithm, KeyStore keystore, String keyPassword)
			throws Exception {
		Enumeration<String> aliasSet = keystore.aliases();
		String alias = null;
		while (aliasSet.hasMoreElements()) {
			alias = (String) aliasSet.nextElement();
		}
		return sign(signData, algorithm, keystore, alias, keyPassword);
	}

	public static byte[] sign(byte[] signData, String algorithm, KeyStore keystore, String alias, String keyPassword)
			throws Exception {
		PrivateKey privateKey = SecurityUtil.getPrivateKey(keystore, alias, keyPassword.toCharArray());
		return sign(signData, algorithm, privateKey);
	}

	public static byte[] sign(byte[] signData, String algorithm, PrivateKey privateKey) throws Exception {
		if (("MD2withRSA".equals(algorithm)) || ("MD5withRSA".equals(algorithm)) || ("SHA1withRSA".equals(algorithm))
				|| ("SHA256withRSA".equals(algorithm)) || ("SHA1withDSA".equals(algorithm))) {
			return SecurityUtil.sign(signData, algorithm, privateKey);
		}
		return ExtendSecurityUtil.sign(signData, algorithm, privateKey);
	}

	public static byte[] sign(byte[] signData, String algorithm, String keyStoreFilePath, String keyStorePassword,
                              String keyStoreType, String keyPassword) throws Exception {
		KeyStore keyStore = SecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, keyStoreType);
		return sign(signData, algorithm, keyStore, keyPassword);
	}

	public static byte[] sign(byte[] signData, String algorithm, String keyStoreFilePath, String keyStorePassword,
                              String keyStoreType, String alias, String keyPassword) throws Exception {
		KeyStore keyStore = SecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, keyStoreType);
		return sign(signData, algorithm, keyStore, alias, keyPassword);
	}

	public static boolean verifyByCertFile(byte[] signData, String algorithm, String certFilePath, byte[] sign)
			throws Exception {
		PublicKey publicKey = SecurityUtil.getPublicKeyByCertFile(certFilePath);
		return verify(signData, algorithm, publicKey, sign);
	}

	public static boolean verifyByCertByte(byte[] signData, String algorithm, byte[] cert, byte[] sign)
			throws Exception {
		PublicKey publicKey = SecurityUtil.generatePublicKeyByCertByte(cert);
		return verify(signData, algorithm, publicKey, sign);
	}

	public static boolean verifyByKeyStore(byte[] signData, String algorithm, String keyStoreFilePath,
                                           String keyStorePassword, String keyStoreType, String alias, byte[] sign) throws Exception {
		KeyStore keystore = SecurityUtil.getKeyStore(keyStoreFilePath, keyStorePassword, keyStoreType);
		PublicKey publicKey = SecurityUtil.getPublicKey(keystore, alias);
		return verify(signData, algorithm, publicKey, sign);
	}

	public static boolean verifyByBase64X509(byte[] signData, String algorithm, String Base64X509String, byte[] sign)
			throws Exception {
		PublicKey publicKey = SecurityUtil.generatePublicKeyByX509Base64String(Base64X509String);
		return verify(signData, algorithm, publicKey, sign);
	}

	public static boolean verify(byte[] signData, String algorithm, PublicKey publicKey, byte[] sign) throws Exception {
		if (("MD2withRSA".equals(algorithm)) || ("MD5withRSA".equals(algorithm)) || ("SHA1withRSA".equals(algorithm))
				|| ("SHA256withRSA".equals(algorithm)) || ("SHA1withDSA".equals(algorithm))) {
			return SecurityUtil.verify(signData, algorithm, publicKey, sign);
		}
		return ExtendSecurityUtil.verify(signData, algorithm, publicKey, sign);
	}

}