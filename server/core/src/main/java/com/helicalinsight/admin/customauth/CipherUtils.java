package com.helicalinsight.admin.customauth;

import com.helicalinsight.admin.exception.AuthenticationException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CipherUtils {
    static Properties prop = new Properties();
    static InputStream input = CipherUtils.class.getResourceAsStream("/customAuthentication.properties");
    static String key = null;
    static String algorithm = null;
    static String mode = null;
    static String padding = null;

    public static String encrypt(String strToEncrypt) {
        if(strToEncrypt==null) return null;
        try {
            input = CipherUtils.class.getResourceAsStream("/customAuthentication.properties");

            prop.load(input);

            key = prop.getProperty("cipherKey");
            algorithm = prop.getProperty("cipherAlgorithm");
            mode = prop.getProperty("cipherMode");
            padding = prop.getProperty("cipherPadding");

            Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
            cipher.init(1, secretKey);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();

            try {
                input.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        if (strToDecrypt == null) {
            return null;
        }
        try {
            input = CipherUtils.class.getResourceAsStream("/customAuthentication.properties");

            prop.load(input);

            key = prop.getProperty("cipherKey");
            algorithm = prop.getProperty("cipherAlgorithm");
            mode = prop.getProperty("cipherMode");
            padding = prop.getProperty("cipherPadding");

            Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
            cipher.init(2, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
        } catch (IllegalBlockSizeException blockExcepttion) {
            //throw new EfwdServiceException("Failed to Decrypt the string.");
            return strToDecrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptString(String strToDecrypt) {
        try {
            input = CipherUtils.class.getResourceAsStream("/customAuthentication.properties");

            prop.load(input);

            key = prop.getProperty("cipherKey");
            algorithm = prop.getProperty("cipherAlgorithm");
            mode = prop.getProperty("cipherMode");
            padding = prop.getProperty("cipherPadding");

            Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
            cipher.init(2, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
        } catch (IllegalBlockSizeException blockExcepttion) {
            throw new AuthenticationException("Invalid Token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
