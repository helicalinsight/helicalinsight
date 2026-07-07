package com.helicalinsight.export.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;
/**
 * Utility class for performing encryption and decryption using AES/CBC/PKCS5Padding algorithm.
 * @Component Indicates that this class is a Spring component.
 */
@Component
public class CryptoUtility {

	private static final String PADDING = "AES/CBC/PKCS5Padding";
	/**
     * Encrypts the given content using the AES/CBC/PKCS5Padding algorithm.
     *
     * @param content 							  The content to be encrypted.
     * @return The encrypted content.
     * @throws NoSuchAlgorithmException           If the specified algorithm is not available.
     * @throws NoSuchPaddingException             If the specified padding scheme is not available.
     * @throws InvalidKeyException                If the encryption key is invalid.
     * @throws IllegalBlockSizeException          If the block size is invalid.
     * @throws BadPaddingException                If the padding is invalid.
     * @throws InvalidAlgorithmParameterException If the algorithm parameters are invalid.
     */
	public byte[] encrypt(byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		ApplicationProperties properties = ApplicationProperties.getInstance();
		IvParameterSpec iv = new IvParameterSpec(properties.getEncryptionSecret().getBytes(StandardCharsets.UTF_8));
		SecretKeySpec secretKeySpec = new SecretKeySpec(
				properties.getEncryptionSecret().getBytes(StandardCharsets.UTF_8), properties.getEncryptionAlgorithm());
		Cipher cipher = Cipher.getInstance(PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
		byte[] encrypted = cipher.doFinal(content);
		return encode(encrypted);
	}
	/**
     * Decrypts the given encrypted content using the AES/CBC/PKCS5Padding algorithm.
     *
     * @param encrypted 						  encrypted content to be decrypted.
     * @return The decrypted content.
     * @throws NoSuchAlgorithmException           If the specified algorithm is not available.
     * @throws NoSuchPaddingException             If the specified padding scheme is not available.
     * @throws InvalidKeyException                If the decryption key is invalid.
     * @throws IllegalBlockSizeException          If the block size is invalid.
     * @throws BadPaddingException                If the padding is invalid.
     * @throws InvalidAlgorithmParameterException If the algorithm parameters are invalid.
     */
	public byte[] decrypt(byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		ApplicationProperties properties = ApplicationProperties.getInstance();
		IvParameterSpec iv = new IvParameterSpec(properties.getEncryptionSecret().getBytes(StandardCharsets.UTF_8));
		SecretKeySpec secretKeySpec = new SecretKeySpec(
				properties.getEncryptionSecret().getBytes(StandardCharsets.UTF_8), properties.getEncryptionAlgorithm());
		Cipher cipher = Cipher.getInstance(PADDING);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
		byte[] decoded = decode(encrypted);
		return cipher.doFinal(decoded);
	}
	/**
     * Encodes the given byte array using Base64 encoding.
     *
     * @param byteArray 		 byte array to be encoded.
     * @return The encoded byte array.
     * @throws ResourceExportException If an error occurs during encoding.
     */
	public byte[] encode(byte[] byteArray) {
		try {
			return Base64.getEncoder().encode(byteArray);
		} catch (Exception ex) {
			throw new ResourceExportException("Error occurred while encoding the content due to  :: " + ex.getLocalizedMessage());
		}
	}
	/**
     * Decodes the given Base64 encoded byte array.
     *
     * @param encryptedByteArray 		 Base64 encoded byte array to be decoded.
     * @return The decoded byte array.
     * @throws ResourceImportException If an error occurs during decoding.
     */
	public byte[] decode(byte[] encryptedByteArray) {
		try {
			return Base64.getDecoder().decode(encryptedByteArray);
		} catch (Exception ex) {
			throw new ResourceImportException(
					"Error occurred while decoding the content due to  :: " + ex.getLocalizedMessage());
		}
	}
}
