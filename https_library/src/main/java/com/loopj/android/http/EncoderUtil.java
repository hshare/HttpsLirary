package com.loopj.android.http;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class EncoderUtil {

	private EncoderUtil() {

	}

	private static byte[] md5(String strSrc) {
		byte[] returnByte = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			returnByte = md5.digest(strSrc.getBytes("GBK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnByte;
	}


	private static byte[] getEnKey(String spKey) {
		byte[] desKey = null;
		try {
			byte[] desKey1 = md5(spKey);
			desKey = new byte[24];
			int i = 0;
			while (i < desKey1.length && i < 24) {
				desKey[i] = desKey1[i];
				i++;
			}
			if (i < 24) {
				desKey[i] = 0;
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return desKey;
	}


	public static byte[] encrypt(byte[] src, byte[] enKey) {
		byte[] encryptedData = null;
		try {
			DESedeKeySpec dks = new DESedeKeySpec(enKey);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey key = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encryptedData = cipher.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;
	}

	public static String get3DESEncrypt(String src, String spkey) {
		String requestValue = "";
		try {
			// 得到3-DES的密钥匙
			byte[] enKey = getEnKey(spkey);
			// 要进�?3-DES加密的内容在进行/"UTF-16LE/"取字�?
			byte[] src2 = src.getBytes("UTF-16LE");
			// 进行3-DES加密后的内容的字�?
			byte[] encryptedData = encrypt(src2, enKey);
			// 进行3-DES加密后的内容进行BASE64编码
			StringBuilder sb = new StringBuilder();
			for (byte oneByte : encryptedData) {
				sb.append(oneByte);
				sb.append(",");
			}
			requestValue = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestValue;
	}


	public static String deCrypt(byte[] debase64, String spKey) {
		String strDe = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DESede");
			byte[] key = getEnKey(spKey);
			DESedeKeySpec dks = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey sKey = keyFactory.generateSecret(dks);
			cipher.init(Cipher.DECRYPT_MODE, sKey);
			byte ciphertext[] = cipher.doFinal(debase64);
			strDe = new String(ciphertext, "UTF-16LE");
		} catch (Exception ex) {
			strDe = "";
			ex.printStackTrace();
		}
		return strDe;
	}


	public static String get3DESDecrypt(String src, String spkey) {
		String requestValue = "";
		try {
			// 得到3-DES的密钥匙
			// URLDecoder.decodeTML控制码进行转义的过程
			// 进行3-DES加密后的内容进行BASE64编码
			String[] temp = src.split(",");
			byte[] base64DValue = new byte[temp.length];
			int index = 0;
			for (String oneByte : temp) {
				base64DValue[index++] = Byte.parseByte(oneByte);
			}
			// 要进�?3-DES加密的内容在进行/"UTF-16LE/"取字�?
			requestValue = deCrypt(base64DValue, spkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestValue;
	}

	public static void main(String[] args) {
		String oldString = "毒素�?";
		String SPKEY = "gy1234";
		System.out.println("1。分配的SPKEY�?: " + SPKEY);
		System.out.println("2。的内容�?: " + oldString);
		String reValue = get3DESEncrypt(oldString, SPKEY);
		System.out.println("进行3-DES加密后的内容: " + reValue);
		String reValue2 = get3DESDecrypt(reValue, SPKEY);
		System.out.println("进行3-DES解密后的内容: " + reValue2);
	}
}
