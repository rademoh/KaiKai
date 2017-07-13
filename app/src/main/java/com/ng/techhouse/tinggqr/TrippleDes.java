
package com.ng.techhouse.tinggqr;

import android.util.Base64;

import com.ng.techhouse.tinggqr.util.EndPoint;

import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;



public class TrippleDes {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private static KeySpec ks;
    private static SecretKeyFactory skf;
    private static Cipher cipher;
   static  byte []  arrayBytes;
    private static String myEncryptionKey;
    private static String myEncryptionScheme;
    static SecretKey key;


    public static String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            myEncryptionKey = new EndPoint().getSeedValue();
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);


            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = Base64.encodeToString(encryptedText, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public static String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            myEncryptionKey = new EndPoint().getSeedValue();
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decode(encryptedString.getBytes(),Base64.DEFAULT);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    public static String lastChars(String originalString) {
        String newString = originalString.substring(originalString.length() - 4);
        return newString;
    }


}
