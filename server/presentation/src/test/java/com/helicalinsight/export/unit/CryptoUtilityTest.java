package com.helicalinsight.export.unit;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.export.crypto.CryptoUtility;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;

public class CryptoUtilityTest extends ExportUnitTestBase {

	private static final String SECRET = "1234567890123456";

	@After
	public void clearOverride() {
		CipherUtils.setProjectPropertiesOverride(null);
	}

	@Test
	public void ut_a1_testEncryptDecryptRoundTrip() throws Exception {
		Map<String, String> props = new HashMap<>();
		props.put("export.cipherAlgorithm", "AES");
		props.put("export.cipherMode", "CBC");
		props.put("export.cipherPadding", "PKCS5Padding");
		props.put("export.cipherKey", SECRET);
		CipherUtils.setProjectPropertiesOverride(props);

		CryptoUtility utility = new CryptoUtility();
		byte[] original = "test-content".getBytes(StandardCharsets.UTF_8);
		byte[] encrypted = utility.encrypt(original);
		byte[] decrypted = utility.decrypt(encrypted);
		Assert.assertArrayEquals(original, decrypted);
	}

	@Test
	public void ut_a2_testEncode() {
		CryptoUtility utility = new CryptoUtility();
		byte[] input = "hello".getBytes(StandardCharsets.UTF_8);
		byte[] encoded = utility.encode(input);
		Assert.assertArrayEquals(Base64.getEncoder().encode(input), encoded);
	}

	@Test
	public void ut_a3_testDecode() {
		CryptoUtility utility = new CryptoUtility();
		byte[] input = "hello".getBytes(StandardCharsets.UTF_8);
		byte[] encoded = Base64.getEncoder().encode(input);
		byte[] decoded = utility.decode(encoded);
		Assert.assertArrayEquals(input, decoded);
	}

	@Test(expected = ResourceExportException.class)
	public void ut_a4_testEncodeThrowsOnInvalidInput() {
		CryptoUtility utility = new CryptoUtility() {
			@Override
			public byte[] encode(byte[] byteArray) {
				throw new ResourceExportException("Error occurred while encoding the content due to  :: fail");
			}
		};
		utility.encode("x".getBytes(StandardCharsets.UTF_8));
	}

	@Test(expected = ResourceImportException.class)
	public void ut_a5_testDecodeThrowsOnInvalidBase64() {
		CryptoUtility utility = new CryptoUtility();
		utility.decode("not-valid-base64!!!".getBytes(StandardCharsets.UTF_8));
	}

}
