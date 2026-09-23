package com.helicalinsight.admin.customauth;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.admin.exception.AuthenticationException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CipherUtilsModuleTest {

    @After
    public void clearOverride() {
        CipherUtils.setProjectPropertiesOverride(null);
    }

    @Test
    public void hiEncryptDecryptRoundTrip() {
        String plain = "hi-password-secret";
        String encrypted = CipherUtils.encrypt(plain);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(plain, CipherUtils.decrypt(encrypted));
    }

    @Test
    public void ssoKeyRotationDoesNotBreakHiDecrypt() {
        Map<String, String> props = baseProps();
        props.put("cipherKey", "HSpnzzfCLqrBn8Lk");
        props.put("sso.cipherKey", "OldSsoKey1234567");
        CipherUtils.setProjectPropertiesOverride(props);

        String hiSecret = "stored-db-password";
        String hiCipher = CipherUtils.encrypt(hiSecret);

        // Rotate only SSO key
        props.put("sso.cipherKey", "NewSsoKey1234567");
        CipherUtils.setProjectPropertiesOverride(props);

        Assert.assertEquals(hiSecret, CipherUtils.decrypt(hiCipher));
    }

    @Test
    public void decryptStringUsesSsoModuleKey() {
        Map<String, String> props = baseProps();
        props.put("cipherKey", "BaseKeyForHi1234");
        props.put("sso.cipherKey", "SsoOnlyKey123456");
        CipherUtils.setProjectPropertiesOverride(props);

        String token = "username=alice|role=ROLE_USER";
        String encryptedWithSso = CipherUtils.encrypt(token, CipherUtils.MODULE_SSO);
        Assert.assertEquals(token, CipherUtils.decryptString(encryptedWithSso));

        // HI key cannot decrypt SSO ciphertext
        String withBase = CipherUtils.decrypt(encryptedWithSso);
        Assert.assertTrue(withBase == null || !token.equals(withBase));
    }

    @Test(expected = AuthenticationException.class)
    public void decryptStringRejectsInvalidToken() {
        CipherUtils.decryptString("segosegekfrek");
    }

    @Test
    public void exportByteRoundTripUsesCbcModule() throws Exception {
        Map<String, String> props = baseProps();
        props.put("export.cipherAlgorithm", "AES");
        props.put("export.cipherMode", "CBC");
        props.put("export.cipherPadding", "PKCS5Padding");
        props.put("export.cipherKey", "1234567890123456");
        CipherUtils.setProjectPropertiesOverride(props);

        byte[] original = "export-package-bytes".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = CipherUtils.encrypt(original, CipherUtils.MODULE_EXPORT);
        byte[] decrypted = CipherUtils.decrypt(encrypted, CipherUtils.MODULE_EXPORT);
        Assert.assertArrayEquals(original, decrypted);
    }

    @Test
    public void exportFallsBackToEncryptionSecretAlias() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put("cipherAlgorithm", "AES");
        props.put("cipherMode", "ECB");
        props.put("cipherPadding", "PKCS5Padding");
        props.put("cipherKey", "HSpnzzfCLqrBn8Lk");
        props.put("encryptionAlgorithm", "AES");
        props.put("encryptionSecret", "1234567890123456");
        CipherUtils.setProjectPropertiesOverride(props);

        CipherUtils.CipherConfig config = CipherUtils.resolveCipherConfig(CipherUtils.MODULE_EXPORT);
        Assert.assertEquals("1234567890123456", config.key);
        Assert.assertEquals("AES", config.algorithm);
        Assert.assertEquals("CBC", config.mode);

        byte[] original = "legacy-alias".getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(original,
                CipherUtils.decrypt(CipherUtils.encrypt(original, CipherUtils.MODULE_EXPORT), CipherUtils.MODULE_EXPORT));
    }

    @Test
    public void datasourceModuleUsesSeparateKey() {
        Map<String, String> props = baseProps();
        props.put("cipherKey", "BaseKeyForHi1234");
        props.put("datasource.cipherKey", "USqrufCLahfrBn5s");
        CipherUtils.setProjectPropertiesOverride(props);

        String plain = "drill-password";
        String encrypted = CipherUtils.encrypt(plain, CipherUtils.MODULE_DATASOURCE);
        Assert.assertEquals(plain, CipherUtils.decrypt(encrypted, CipherUtils.MODULE_DATASOURCE));
        Assert.assertNotEquals(plain, CipherUtils.decrypt(encrypted));
    }

    @Test
    public void moduleOverrideFallsBackToBaseWhenMissing() {
        Map<String, String> props = baseProps();
        props.put("cipherKey", "SharedFallbackKy");
        props.remove("sso.cipherKey");
        CipherUtils.setProjectPropertiesOverride(props);

        CipherUtils.CipherConfig sso = CipherUtils.resolveCipherConfig(CipherUtils.MODULE_SSO);
        Assert.assertEquals("SharedFallbackKy", sso.key);
    }

    private static Map<String, String> baseProps() {
        Map<String, String> props = new HashMap<>();
        props.put("cipherAlgorithm", "AES");
        props.put("cipherMode", "ECB");
        props.put("cipherPadding", "PKCS5Padding");
        props.put("cipherKey", "HSpnzzfCLqrBn8Lk");
        props.put("sso.cipherKey", "HSpnzzfCLqrBn8Lk");
        props.put("datasource.cipherKey", "USqrufCLahfrBn5s");
        props.put("export.cipherAlgorithm", "AES");
        props.put("export.cipherMode", "CBC");
        props.put("export.cipherPadding", "PKCS5Padding");
        props.put("export.cipherKey", "abcd@#$%+-_|1234");
        return props;
    }
}
