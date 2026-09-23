package com.helicalinsight.admin.customauth;

import com.helicalinsight.admin.exception.AuthenticationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Central encrypt/decrypt utility. Loads cipher settings from {@code project.properties}
 * with optional module overrides ({@code sso.*}, {@code export.*}, {@code datasource.*}),
 * falling back to {@code customAuthentication.properties} (and datasource/export legacy keys).
 */
public class CipherUtils {

    public static final String MODULE_SSO = "sso";
    public static final String MODULE_EXPORT = "export";
    public static final String MODULE_DATASOURCE = "datasource";

    private static final String PROP_CIPHER_KEY = "cipherKey";
    private static final String PROP_CIPHER_ALGORITHM = "cipherAlgorithm";
    private static final String PROP_CIPHER_MODE = "cipherMode";
    private static final String PROP_CIPHER_PADDING = "cipherPadding";

    /** Test hook: when non-null, used instead of classpath project.properties. */
    static volatile Map<String, String> projectPropertiesOverride;

    /** Test-only: override project.properties resolution. Pass null to clear. */
    public static void setProjectPropertiesOverride(Map<String, String> override) {
        projectPropertiesOverride = override;
    }

    public static String encrypt(String strToEncrypt) {
        return encrypt(strToEncrypt, null);
    }

    public static String encrypt(String strToEncrypt, String module) {
        if (strToEncrypt == null) {
            return null;
        }
        try {
            CipherConfig config = resolveCipherConfig(module);
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, config);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        return decrypt(strToDecrypt, null);
    }

    public static String decrypt(String strToDecrypt, String module) {
        if (strToDecrypt == null) {
            return null;
        }
        try {
            CipherConfig config = resolveCipherConfig(module);
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, config);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException blockException) {
            return strToDecrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypts an SSO auth token using the {@code sso} module cipher settings.
     */
    public static String decryptString(String strToDecrypt) {
        return decryptStrict(strToDecrypt, MODULE_SSO);
    }

    /**
     * Decrypts a string; throws {@link AuthenticationException} on illegal block size
     * (used by SSO and datasource token-style decrypt).
     */
    public static String decryptStrict(String strToDecrypt, String module) {
        try {
            CipherConfig config = resolveCipherConfig(module);
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, config);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException blockException) {
            throw new AuthenticationException("Invalid Token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encrypts binary content for a module (e.g. {@link #MODULE_EXPORT}).
     * CBC uses key bytes as IV; output is standard Base64 (export package compatible).
     */
    public static byte[] encrypt(byte[] content, String module) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        CipherConfig config = resolveCipherConfig(module);
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, config);
        return java.util.Base64.getEncoder().encode(cipher.doFinal(content));
    }

    /**
     * Decrypts binary content for a module (e.g. {@link #MODULE_EXPORT}).
     */
    public static byte[] decrypt(byte[] encrypted, String module) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        CipherConfig config = resolveCipherConfig(module);
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, config);
        byte[] decoded = java.util.Base64.getDecoder().decode(encrypted);
        return cipher.doFinal(decoded);
    }

    static CipherConfig resolveCipherConfig(String module) {
        Map<String, String> project = loadProjectProperties();
        Properties customAuth = loadClasspathProperties("/customAuthentication.properties");
        Properties datasourceProps = MODULE_DATASOURCE.equals(module)
                ? loadClasspathProperties("/dataSourceEncryption.properties")
                : null;

        String algorithm = resolveProperty(module, PROP_CIPHER_ALGORITHM, project, customAuth, datasourceProps);
        String mode = resolveProperty(module, PROP_CIPHER_MODE, project, customAuth, datasourceProps);
        String padding = resolveProperty(module, PROP_CIPHER_PADDING, project, customAuth, datasourceProps);
        String key = resolveProperty(module, PROP_CIPHER_KEY, project, customAuth, datasourceProps);

        if (StringUtils.isBlank(algorithm) || StringUtils.isBlank(mode) || StringUtils.isBlank(padding)
                || StringUtils.isBlank(key)) {
            throw new IllegalStateException("Incomplete cipher configuration for module: "
                    + (module == null ? "base" : module));
        }
        return new CipherConfig(algorithm, mode, padding, key);
    }

    static String resolveProperty(String module, String name, Map<String, String> project, Properties customAuth,
                                  Properties datasourceProps) {
        if (StringUtils.isNotBlank(module)) {
            String prefixed = project.get(module + "." + name);
            if (StringUtils.isNotBlank(prefixed)) {
                return prefixed;
            }
        }

        // Export legacy aliases and CBC defaults before falling back to HI base cipher*
        if (MODULE_EXPORT.equals(module)) {
            if (PROP_CIPHER_KEY.equals(name)) {
                String secret = project.get("encryptionSecret");
                if (StringUtils.isNotBlank(secret)) {
                    return secret;
                }
            }
            if (PROP_CIPHER_ALGORITHM.equals(name)) {
                String alg = project.get("encryptionAlgorithm");
                if (StringUtils.isNotBlank(alg)) {
                    return alg;
                }
            }
            if (PROP_CIPHER_MODE.equals(name)) {
                return "CBC";
            }
            if (PROP_CIPHER_PADDING.equals(name)) {
                return "PKCS5Padding";
            }
        }

        String base = project.get(name);
        if (StringUtils.isNotBlank(base)) {
            return base;
        }

        if (customAuth != null) {
            String fromCustom = customAuth.getProperty(name);
            if (StringUtils.isNotBlank(fromCustom)) {
                return fromCustom.trim();
            }
        }

        if (MODULE_DATASOURCE.equals(module) && datasourceProps != null) {
            String fromDs = datasourceProps.getProperty(name);
            if (StringUtils.isNotBlank(fromDs)) {
                return fromDs.trim();
            }
        }

        return null;
    }

    private static Cipher initCipher(int opMode, CipherConfig config) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(config.algorithm + "/" + config.mode + "/" + config.padding);
        byte[] keyBytes = config.key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, config.algorithm);
        try {
	        if ("CBC".equalsIgnoreCase(config.mode)) {
	            IvParameterSpec iv = new IvParameterSpec(keyBytes);
	            cipher.init(opMode, secretKey, iv);
	        } else {
	            cipher.init(opMode, secretKey);
	        }
        return cipher;
        }
        catch (InvalidAlgorithmParameterException e) {
        	e.printStackTrace();
			throw new EfwServiceException(e);
		}
        
    }

    private static Map<String, String> loadProjectProperties() {
        if (projectPropertiesOverride != null) {
            return projectPropertiesOverride;
        }
        try {
            return ConfigurationFileReader.getProjectPropertiesFile();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static Properties loadClasspathProperties(String resourcePath) {
        Properties properties = new Properties();
        try (InputStream input = CipherUtils.class.getResourceAsStream(resourcePath)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    static final class CipherConfig {
        final String algorithm;
        final String mode;
        final String padding;
        final String key;

        CipherConfig(String algorithm, String mode, String padding, String key) {
            this.algorithm = algorithm;
            this.mode = mode;
            this.padding = padding;
            this.key = key;
        }
    }
}
