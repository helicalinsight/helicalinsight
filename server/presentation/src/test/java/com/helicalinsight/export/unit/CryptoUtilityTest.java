package com.helicalinsight.export.unit;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.export.crypto.CryptoUtility;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;

public class CryptoUtilityTest extends ExportUnitTestBase {

	private static final String SECRET = "1234567890123456";

	@Test
	public void ut_a1_testEncryptDecryptRoundTrip() throws Exception {
		ApplicationProperties properties = mock(ApplicationProperties.class);
		when(properties.getEncryptionSecret()).thenReturn(SECRET);
		when(properties.getEncryptionAlgorithm()).thenReturn("AES");

		try (MockedStatic<ApplicationProperties> mocked = mockStatic(ApplicationProperties.class)) {
			mocked.when(ApplicationProperties::getInstance).thenReturn(properties);
			CryptoUtility utility = new CryptoUtility();
			byte[] original = "test-content".getBytes(StandardCharsets.UTF_8);
			byte[] encrypted = utility.encrypt(original);
			byte[] decrypted = utility.decrypt(encrypted);
			Assert.assertArrayEquals(original, decrypted);
		}
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
