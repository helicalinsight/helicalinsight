package com.helicalinsight.export.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.export.utils.ResourceFileUtils;
/**
 * Handles encryption and decryption of files using the CryptoUtility class.
 * @Component Indicates that this class is a Spring component.
 */
@Component
public class FileCryptoHandler {

	@Autowired
	private CryptoUtility cryptoUtility;
	@Autowired
	private ResourceFileUtils fileUtils;

	private static final Logger LOG = LoggerFactory.getLogger(FileCryptoHandler.class);
	/**
     * Encrypts the content of the given file.
     *
     * @param file 								file to be encrypted.
     * @return The encrypted content as a byte array.
     * @throws InvalidKeyException                If the encryption key is invalid.
     * @throws NoSuchAlgorithmException           If the specified algorithm is not available.
     * @throws NoSuchPaddingException             If the specified padding scheme is not available.
     * @throws IllegalBlockSizeException          If the block size is invalid.
     * @throws BadPaddingException                If the padding is invalid.
     * @throws InvalidAlgorithmParameterException If the algorithm parameters are invalid.
     */
	public byte[] encrypt(File file) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		byte[] data = fileUtils.getAllBytes(file);
		return cryptoUtility.encrypt(data);
	}
	/**
     * Decrypts the content of the given file.
     *
     * @param file 								  file to be decrypted.
     * @return decrypted content as a byte array.
     * @throws InvalidKeyException                If the decryption key is invalid.
     * @throws NoSuchAlgorithmException           If the specified algorithm is not available.
     * @throws NoSuchPaddingException             If the specified padding scheme is not available.
     * @throws IllegalBlockSizeException          If the block size is invalid.
     * @throws BadPaddingException                If the padding is invalid.
     * @throws InvalidAlgorithmParameterException If the algorithm parameters are invalid.
     */
	public byte[] decrypt(File file) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		byte[] encryptedData = fileUtils.getAllBytes(file);
		return cryptoUtility.decrypt(encryptedData);
	}
	/**
     * It Encrypts all files in the specified directory and its sub-directories.
     *
     * @param root 								  root directory to start the batch encryption.
     * @throws InvalidKeyException                If the encryption key is invalid.
     * @throws NoSuchAlgorithmException           If the specified algorithm is not available.
     * @throws NoSuchPaddingException             If the specified padding scheme is not available.
     * @throws IllegalBlockSizeException          If the block size is invalid.
     * @throws BadPaddingException                If the padding is invalid.
     * @throws InvalidAlgorithmParameterException If the algorithm parameters are invalid.
     */
	public void encryptBatch(String root) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		File file = new File(root);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File resourceFile : files) {
				if (resourceFile.isDirectory()) {
					encryptBatch(root + File.separator + resourceFile.getName());
				} else {
					byte[] encrypted = encrypt(resourceFile);
					try (FileOutputStream fos = new FileOutputStream(resourceFile, false)) {
						fos.write(encrypted);
					} catch (IOException e) {
						LOG.error("Error Occured : {}", e.getMessage());
					}
				}
			}
		}
	}
	/**
     * Decrypts all files in the specified directory and its sub-directories.
     *
     * @param root 			 root directory to start the batch decryption.
     * @throws Exception If an error occurs during decryption.
     */
	public void decryptBatch(String root) throws Exception {
		File file = new File(root);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File resourceFile : files) {
				if (resourceFile.isDirectory()) {
					decryptBatch(root + File.separator + resourceFile.getName());
				} else {
					byte[] decrypted = decrypt(resourceFile);
					try (FileOutputStream fos = new FileOutputStream(resourceFile, false)) {
						fos.write(decrypted);
					} catch (IOException e) {
						LOG.error("Error Occured : {}", e.getMessage());
					}
				}
			}
		}
	}

}
