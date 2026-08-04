package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.NullHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AbstractResourceWriterHandlerTest extends ExportUnitTestBase {

	private NullHandler createHandlerWithUserService(UserService userService) throws Exception {
		NullHandler handler = new NullHandler();
		setField(handler, "userService", userService);
		return handler;
	}

	@Test
	public void ut_a1_testAddOwner_withOwnerId() throws Exception {
		UserService userService = mock(UserService.class);
		NullHandler handler = createHandlerWithUserService(userService);
		User owner = mock(User.class);
		HIResourceFolder folder = new HIResourceFolder();
		folder.setCreatedBy(10);

		when(userService.findUser(10)).thenReturn(owner);

		ObjectNode result = handler.addOwner(folder, 10);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.has("createdBy"));
		verify(userService).findUser(10);
	}

	@Test
	public void ut_a2_testAddOwner_nullOwnerId() throws Exception {
		UserService userService = mock(UserService.class);
		NullHandler handler = createHandlerWithUserService(userService);
		HIResourceFolder folder = new HIResourceFolder();
		folder.setCreatedBy(10);

		ObjectNode result = handler.addOwner(folder, null);
		Assert.assertNotNull(result);
		verify(userService, never()).findUser(anyInt());
	}

	@Test
	public void ut_a3_testShare_enabled() throws Exception {
		NullHandler handler = new NullHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();
		options.setShare(true);
		ShareHandler shareHandler = mock(ShareHandler.class);

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(ShareHandler.class)).thenReturn(shareHandler);
			handler.share(resource, manifest, options, "exportDir");
			verify(shareHandler).write(eq(resource), eq("exportDir"), eq(manifest));
		}
	}

	@Test
	public void ut_a4_testShare_disabled() {
		NullHandler handler = new NullHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();
		options.setShare(false);

		handler.share(resource, manifest, options, "exportDir");
	}

	@Test
	public void ut_a5_testSchedule_enabled() throws Exception {
		NullHandler handler = new NullHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();
		options.setSchedules(true);
		ResourceIOHandler scheduleHandler = mock(ResourceIOHandler.class);

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean("scheduleIOHandler")).thenReturn(scheduleHandler);
			handler.schedule(resource, manifest, options, "exportDir");
			verify(scheduleHandler).write(eq(resource), eq("exportDir"), eq(manifest));
		}
	}

	@Test
	public void ut_a6_testSchedule_disabled() {
		NullHandler handler = new NullHandler();
		HIResourceDTO resource = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();
		options.setSchedules(false);

		handler.schedule(resource, manifest, options, "exportDir");
	}

}
