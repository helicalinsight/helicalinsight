package com.helicalinsight.admin.controller;

import org.junit.Test;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.exception.AuthenticationException;

public class CipherUtilsTest {

	@Test(expected = AuthenticationException.class)
	public void testdecryptString() {
		CipherUtils cipherUtils = new CipherUtils();
		String decrypt = "segosegekfrek";
		String decryptString = CipherUtils.decryptString(decrypt);
		System.out.println(decryptString);
	}
}
