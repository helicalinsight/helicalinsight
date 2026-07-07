package com.helicalinsight.externalauth;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helicalinsight.externalauth.cas.ExternalAdminUsers;

public class ExternalAdminUsersTest {

	@Test
	public void testGetExternalOrganization() {
		ExternalAdminUsers adminUsers = new ExternalAdminUsers();
		adminUsers.setExternalOrganization("userOrg");
		String externalOrganization = adminUsers.getExternalOrganization();
		assertEquals(externalOrganization, "userOrg");
	}
	
	@Test
	public void testGetAdminUsers() {
		ExternalAdminUsers adminUsers = new ExternalAdminUsers();
		List<String> list = new ArrayList<>();
		list.add("admin");
		adminUsers.setAdminUsers(list);
		adminUsers.getAdminUsers();
	}
	
}
