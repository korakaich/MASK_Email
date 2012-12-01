package org.mask.outsourcedemail;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import java.security.Key;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

public class LocalEncrypter {

     private static String algorithm = "DES";
     private static Key key = null;
     //private static byte[] ivBytes = new byte[] { 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };
 	 private static byte[] keyBytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef };
     private static Cipher cipher = null;
     
     public static void setUp() throws Exception {
         //key = "com.sun.crypto.provider.DESedeKey@4f9654ce"; //
    	 //key=KeyGenerator.getInstance(algorithm).generateKey();
    	 
    	 key = new SecretKeySpec(keyBytes, "DES");
         cipher = Cipher.getInstance(algorithm);
         System.out.println(key);
     }

     public static void main(String[] args) 
        throws Exception {
         setUp();
         /*if (args.length !=1) {
             System.out.println(
               "USAGE: java LocalEncrypter " +
                                      "[String]");
             System.exit(1);
         }*/
         byte[] encryptionBytes = null;
         String input = "This is input";//args[0];
         System.out.println("Entered: " + input);
         encryptionBytes = encrypt(input);
         System.out.println(
           "Recovered: " + decrypt(encryptionBytes).equals(input));
     }

     public static byte[] encrypt(String input)
         throws InvalidKeyException, 
                BadPaddingException,
                IllegalBlockSizeException {
         cipher.init(Cipher.ENCRYPT_MODE, key);
         byte[] inputBytes = input.getBytes();
         return cipher.doFinal(inputBytes);
     }

     
    
     
     
     public static String decrypt(byte[] encryptionBytes)
         throws InvalidKeyException, 
                BadPaddingException,
                IllegalBlockSizeException {
         cipher.init(Cipher.DECRYPT_MODE, key);
         byte[] recoveredBytes = 
           cipher.doFinal(encryptionBytes);
         String recovered = 
           new String(recoveredBytes);
         return recovered;
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

}