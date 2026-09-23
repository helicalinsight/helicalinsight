package com.helicalinsight.export.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Component;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;

/**
 * Export/import file crypto. Delegates to {@link CipherUtils} with the export module
 * (AES/CBC via {@code export.*} / legacy {@code encryptionSecret}).
 */
@Component
public class CryptoUtility {

	/**
	 * Encrypts the given content using the export module cipher settings.
	 */
	public byte[] encrypt(byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		return CipherUtils.encrypt(content, CipherUtils.MODULE_EXPORT);
	}

	/**
	 * Decrypts the given encrypted content using the export module cipher settings.
	 */
	public byte[] decrypt(byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		return CipherUtils.decrypt(encrypted, CipherUtils.MODULE_EXPORT);
	}

	/**
	 * Encodes the given byte array using Base64 encoding.
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
