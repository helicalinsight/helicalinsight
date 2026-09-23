package com.helicalinsight.admin.controller;

import org.junit.Test;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.exception.AuthenticationException;

public class CipherUtilsTest {

	@Test(expected = AuthenticationException.class)
	public void testdecryptString() {
		CipherUtils.decryptString("segosegekfrek");
	}
}
