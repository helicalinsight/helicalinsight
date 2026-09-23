package com.helicalinsight.admin.customauth;

/**
 * Drill/Spark datasource password crypto. Delegates to {@link CipherUtils} with the
 * {@link CipherUtils#MODULE_DATASOURCE} profile.
 */
public class DataSourceEncrypt {

    public static String encrypt(String strToEncrypt) {
        return CipherUtils.encrypt(strToEncrypt, CipherUtils.MODULE_DATASOURCE);
    }

    public static String decrypt(String strToDecrypt) {
        return CipherUtils.decrypt(strToDecrypt, CipherUtils.MODULE_DATASOURCE);
    }

    public static String decryptString(String strToDecrypt) {
        return CipherUtils.decryptStrict(strToDecrypt, CipherUtils.MODULE_DATASOURCE);
    }
}
