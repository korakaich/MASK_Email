package org.mask.outsourcedemail;
import java.io.*;
import java.util.*;

import java.net.*;

import java.security.MessageDigest;
import java.security.Security;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class Crypt {
	private static byte[] ivBytes = new byte[] { 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };
	private static byte[] keyBytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef };
	private static SecretKeySpec key;
	public static void addSecProvider(){
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static String encryption(String textToEncrypt)
	{
		byte[] input = textToEncrypt.getBytes();
		try{
			key = new SecretKeySpec(keyBytes, "DES");
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding", "BC");			
			// encryption pass

			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

			byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

			int ctLength = cipher.update(input, 0, input.length, cipherText, 0);

			ctLength += cipher.doFinal(cipherText, ctLength);

			return (new String(cipherText) );
			//return cipherText;

		}
		catch(Exception e){
			return null;
		}
	}

	public static String decryption(String textToDecrypt)
	{
		      
		byte[] input = textToDecrypt.getBytes();
		byte[] keyBytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd,
				(byte) 0xef };
		byte[] ivBytes = new byte[] { 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };

		try{			
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding", "BC");
		
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

			byte[] plainText = new byte[cipher.getOutputSize(input.length)];
			int ptLength = cipher.update(input, 0, input.length, plainText, 0);
			ptLength += cipher.doFinal(plainText, ptLength);
			return (new String(plainText));
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static String calcHash(String textToHash)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");        
			md.reset();
			md.update(keyBytes);
			md.update(textToHash.getBytes("UTF-8"));
			byte[] digest = md.digest();
			/*Remove from here. This part is only for testing*/
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++)
			{
				sb.append(Integer.toHexString(0xFF & digest[i]));
			}

			System.out.println("Storing : "+textToHash +":" + sb.toString());        
			String contentHash = sb.toString();
			return contentHash;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null;
		}
	}
	public static void main(String args[]){
		Crypt.addSecProvider();
		String encrypted=Crypt.encryption("this is legible ");
		String decrypted=Crypt.decryption(encrypted);
		System.out.println(decrypted);
	}
}


