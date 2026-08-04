package com.helicalinsight.export.unit;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.mockito.MockedStatic;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.jaxb.Security;

/**
 * Test helper for mocking SecurityUtils static initialization dependencies.
 */
public final class ExportTestSecuritySupport {

	private ExportTestSecuritySupport() {
	}

	public static MockedStatic<ApplicationContextAccessor> mockApplicationContextForSecurity() {
		MockedStatic<ApplicationContextAccessor> appMock = mockStatic(ApplicationContextAccessor.class);
		appMock.when(() -> ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class))
				.thenReturn(mock(ResourcePermissionLevelsHolder.class));
		return appMock;
	}

	public static Security mockSecurityWithCreatedBy(String createdBy) {
		Security security = mock(Security.class);
		when(security.getCreatedBy()).thenReturn(createdBy);
		return security;
	}

}
