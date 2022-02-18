package com.jinninghui.datasphere.icreditstudio.framework.utils.security;

import java.util.Date;

public class CertificateInfo {
	private String version;
	private String serialNumber;
	private Date notBefore;
	private Date notAfter;
	private String issuerDN;
	private String subjectDN;
	private String publicKey;
	private String algorithm;
	private String sign;

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getNotBefore() {
		return this.notBefore;
	}

	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	public Date getNotAfter() {
		return this.notAfter;
	}

	public void setNotAfter(Date notAfter) {
		this.notAfter = notAfter;
	}

	public String getIssuerDN() {
		return this.issuerDN;
	}

	public void setIssuerDN(String issuerDN) {
		this.issuerDN = issuerDN;
	}

	public String getSubjectDN() {
		return this.subjectDN;
	}

	public void setSubjectDN(String subjectDN) {
		this.subjectDN = subjectDN;
	}

	public String getPublicKey() {
		return this.publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getAlgorithm() {
		return this.algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}