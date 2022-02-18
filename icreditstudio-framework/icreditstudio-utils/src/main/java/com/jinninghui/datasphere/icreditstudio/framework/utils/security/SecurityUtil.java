package com.jinninghui.datasphere.icreditstudio.framework.utils.security;


import com.jinninghui.datasphere.icreditstudio.framework.utils.Base64;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtil {
	public static KeyStore getKeyStore(String keyStoreFilePath, String keyStorePassword, String keyStoreType)
			throws Exception {
		if ((!"JKS".equals(keyStoreType)) && (!"JCEKS".equals(keyStoreType)) && (!"PKCS12".equals(keyStoreType))) {
			throw new Exception("不支持的keystore type");
		}
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(new FileInputStream(keyStoreFilePath), keyStorePassword.toCharArray());
		return keyStore;
	}

	public static PrivateKey getPrivateKey(KeyStore keystore, String alias, char[] password) throws Exception {
		PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password);
		return privateKey;
	}

	public static PrivateKey getPrivateKey(String priKeyFile) throws Exception {
		File file = new File(priKeyFile);
		if (!file.exists()) {
			throw new RuntimeException("私钥文件[" + priKeyFile + "]不存在");
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		String s = br.readLine();
		String str = "";
		s = br.readLine();
		while (s.charAt(0) != '-') {
			str = str + s + "\r";
			s = br.readLine();
		}
		byte[] b = Base64.decode(str);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(b);
		RSAPrivateKey priKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		return priKey;
	}

	public static PublicKey getPublicKey(KeyStore keystore, String alias) throws Exception {
		Certificate certificate = keystore.getCertificate(alias);
		return certificate.getPublicKey();
	}

	public static PublicKey getPublicKeyByCertFile(String certFilePath) throws Exception {
		FileInputStream in = new FileInputStream(certFilePath);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(in);
		PublicKey publicKey = cert.getPublicKey();
		return publicKey;
	}

	public static PublicKey generatePublicKeyByCertByte(byte[] cert) throws Exception {
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(cert);
		return keyfactory.generatePublic(keySpec);
	}

	public static PublicKey generatePublicKeyByX509Base64String(String Base64X509String) throws Exception {
		byte[] buffer = Base64X509String.getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(in);
		PublicKey key = cert.getPublicKey();
		return key;
	}

	public static Certificate getCertificate(KeyStore keystore, String alias) throws Exception {
		Certificate certificate = keystore.getCertificate(alias);
		return certificate;
	}

	public static Certificate getCertificateByCertFile(String certFilePath) throws Exception {
		FileInputStream in = new FileInputStream(certFilePath);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(in);
		return cert;
	}

	public static Certificate generateCertificateByX509Base64String(String Base64X509String) throws Exception {
		byte[] buffer = Base64X509String.getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = cf.generateCertificate(in);
		return cert;
	}

	public static byte[] sign(byte[] signData, String algorithm, PrivateKey privateKey) throws Exception {
		if ((!"MD2withRSA".equals(algorithm)) && (!"MD5withRSA".equals(algorithm)) && (!"SHA1withRSA".equals(algorithm))
				&& (!"SHA256withRSA".equals(algorithm)) && (!"SHA1withDSA".equals(algorithm))) {
			throw new Exception("不支持的signature algorithm");
		}
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(signData);
		return signature.sign();
	}

	public static boolean verify(byte[] signData, String algorithm, PublicKey publicKey, byte[] sign) throws Exception {
		if ((!"MD2withRSA".equals(algorithm)) && (!"MD5withRSA".equals(algorithm)) && (!"SHA1withRSA".equals(algorithm))
				&& (!"SHA256withRSA".equals(algorithm)) && (!"SHA1withDSA".equals(algorithm))) {
			throw new Exception("不支持的signature algorithm");
		}
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(signData);
		return signature.verify(sign);
	}

}