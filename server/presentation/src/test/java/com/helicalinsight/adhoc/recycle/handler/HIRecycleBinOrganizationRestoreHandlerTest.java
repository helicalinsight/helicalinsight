package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinOrganization;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.OrganizationService;

public class HIRecycleBinOrganizationRestoreHandlerTest {

	@InjectMocks
	private HIRecycleBinOrganizationRestoreHandler handler;

	@Mock
	private OrganizationService organizationService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinRestoresOrganizationAndDeletesBinEntry() {
		Organization organization = new Organization();
		organization.setDeleted(true);

		HIRecycleBinOrganization binOrganization = new HIRecycleBinOrganization();
		binOrganization.setOrganization(organization);

		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(50L);
		bin.setHiRecycleBinOrganization(binOrganization);

		Map<Long, Boolean> completed = new HashMap<>();

		assertTrue(handler.handle(bin, completed));

		verify(organizationService).edit(organization);
		assertFalse(organization.isDeleted());
		verify(recycleBinService).delete(50L);
		assertEquals(Boolean.TRUE, completed.get(50L));
	}

	@Test
	public void handleRecycleBinDtoRestoresOrganizationAndDeletesBinEntry() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(51L);
		bin.setResourceId(301);

		Map<Long, Boolean> completed = new HashMap<>();

		assertTrue(handler.handle(bin, completed));

		verify(organizationService).restoreOrganization(301);
		verify(recycleBinService).delete(51L);
		assertEquals(Boolean.TRUE, completed.get(51L));
	}
}
