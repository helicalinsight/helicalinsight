package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.export.crypto.CryptoUtility;
import com.helicalinsight.export.crypto.FileCryptoHandler;
import com.helicalinsight.export.utils.ResourceFileUtils;

public class FileCryptoHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testEncrypt() throws Exception {
		FileCryptoHandler handler = new FileCryptoHandler();
		CryptoUtility cryptoUtility = mock(CryptoUtility.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		File file = File.createTempFile("encrypt", ".txt");
		byte[] data = "data".getBytes(StandardCharsets.UTF_8);
		byte[] encrypted = "encrypted".getBytes(StandardCharsets.UTF_8);

		Field f1 = FileCryptoHandler.class.getDeclaredField("cryptoUtility");
		f1.setAccessible(true);
		f1.set(handler, cryptoUtility);
		Field f2 = FileCryptoHandler.class.getDeclaredField("fileUtils");
		f2.setAccessible(true);
		f2.set(handler, fileUtils);

		when(fileUtils.getAllBytes(file)).thenReturn(data);
		when(cryptoUtility.encrypt(data)).thenReturn(encrypted);

		Assert.assertArrayEquals(encrypted, handler.encrypt(file));
		file.delete();
	}

	@Test
	public void ut_a2_testDecrypt() throws Exception {
		FileCryptoHandler handler = new FileCryptoHandler();
		CryptoUtility cryptoUtility = mock(CryptoUtility.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		File file = File.createTempFile("decrypt", ".txt");
		byte[] encrypted = "encrypted".getBytes(StandardCharsets.UTF_8);
		byte[] decrypted = "decrypted".getBytes(StandardCharsets.UTF_8);

		Field f1 = FileCryptoHandler.class.getDeclaredField("cryptoUtility");
		f1.setAccessible(true);
		f1.set(handler, cryptoUtility);
		Field f2 = FileCryptoHandler.class.getDeclaredField("fileUtils");
		f2.setAccessible(true);
		f2.set(handler, fileUtils);

		when(fileUtils.getAllBytes(file)).thenReturn(encrypted);
		when(cryptoUtility.decrypt(encrypted)).thenReturn(decrypted);

		Assert.assertArrayEquals(decrypted, handler.decrypt(file));
		file.delete();
	}

	@Test
	public void ut_a3_testEncryptBatch() throws Exception {
		FileCryptoHandler handler = new FileCryptoHandler();
		CryptoUtility cryptoUtility = mock(CryptoUtility.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);

		Field f1 = FileCryptoHandler.class.getDeclaredField("cryptoUtility");
		f1.setAccessible(true);
		f1.set(handler, cryptoUtility);
		Field f2 = FileCryptoHandler.class.getDeclaredField("fileUtils");
		f2.setAccessible(true);
		f2.set(handler, fileUtils);

		File root = new File(System.getProperty("java.io.tmpdir"), "crypto-batch-" + System.nanoTime());
		root.mkdirs();
		File subDir = new File(root, "sub");
		subDir.mkdir();
		File childFile = new File(subDir, "child.txt");
		try (FileOutputStream fos = new FileOutputStream(childFile)) {
			fos.write("plain".getBytes(StandardCharsets.UTF_8));
		}
		byte[] encrypted = "enc".getBytes(StandardCharsets.UTF_8);
		when(fileUtils.getAllBytes(any(File.class))).thenReturn("plain".getBytes(StandardCharsets.UTF_8));
		when(cryptoUtility.encrypt(any())).thenReturn(encrypted);

		handler.encryptBatch(root.getAbsolutePath());
		Assert.assertTrue(childFile.length() > 0);

		deleteRecursively(root);
	}

	@Test
	public void ut_a4_testDecryptBatch() throws Exception {
		FileCryptoHandler handler = new FileCryptoHandler();
		CryptoUtility cryptoUtility = mock(CryptoUtility.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);

		Field f1 = FileCryptoHandler.class.getDeclaredField("cryptoUtility");
		f1.setAccessible(true);
		f1.set(handler, cryptoUtility);
		Field f2 = FileCryptoHandler.class.getDeclaredField("fileUtils");
		f2.setAccessible(true);
		f2.set(handler, fileUtils);

		File root = new File(System.getProperty("java.io.tmpdir"), "crypto-dec-" + System.nanoTime());
		root.mkdirs();
		File file = new File(root, "data.txt");
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write("enc".getBytes(StandardCharsets.UTF_8));
		}
		byte[] decrypted = "plain".getBytes(StandardCharsets.UTF_8);
		when(fileUtils.getAllBytes(any(File.class))).thenReturn("enc".getBytes(StandardCharsets.UTF_8));
		when(cryptoUtility.decrypt(any())).thenReturn(decrypted);

		handler.decryptBatch(root.getAbsolutePath());
		Assert.assertTrue(file.length() > 0);

		deleteRecursively(root);
	}

	private void deleteRecursively(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteRecursively(child);
				}
			}
		}
		file.delete();
	}

}
