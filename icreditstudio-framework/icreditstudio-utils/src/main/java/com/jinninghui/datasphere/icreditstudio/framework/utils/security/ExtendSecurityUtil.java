package com.jinninghui.datasphere.icreditstudio.framework.utils.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;

public class ExtendSecurityUtil {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static KeyStore getKeyStore(String keyStoreFilePath, String keyStorePassword, String keyStoreType)
			throws Exception {
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(new FileInputStream(keyStoreFilePath), keyStorePassword.toCharArray());
		return keyStore;
	}

	public static PrivateKey getPrivateKey(KeyStore keystore, String alias, char[] password) throws Exception {
		PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password);
		return privateKey;
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

	public static PublicKey generatePublicKeyByCertString(String certString) throws Exception {
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(certString.getBytes());
		return keyfactory.generatePublic(keySpec);
	}

	public static byte[] sign(byte[] signData, String algorithm, PrivateKey privateKey) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(signData);
		return signature.sign();
	}

	public static boolean verify(byte[] signData, String algorithm, PublicKey publicKey, byte[] sign) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(signData);
		return signature.verify(sign);
	}
}