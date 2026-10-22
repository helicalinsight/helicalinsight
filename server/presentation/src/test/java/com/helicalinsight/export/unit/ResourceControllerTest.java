package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.controller.ResourceController;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.validation.ImportRequestValidator;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.handler.importres.ImportResourceManager;
import com.helicalinsight.export.upload.ResumableUploadService;
import com.helicalinsight.export.utils.JsonMapperUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ResourceControllerTest extends ExportUnitTestBase {

	@Test
	public void ut_a2_testImportResourceWithUpload() throws Exception {
		ResourceController controller = new ResourceController();
		ImportResourceManager importManager = mock(ImportResourceManager.class);
		JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		ImportRequestValidator validator = mock(ImportRequestValidator.class);
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ImportRequest importRequest = new ImportRequest();
		importRequest.setUpload(true);

		setField(controller, "importManager", importManager);
		setField(controller, "jsonMapperUtils", jsonMapperUtils);

		when(httpRequest.getParameter("resumable")).thenReturn(null);
		when(jsonMapperUtils.mapToDTO(anyString(), any())).thenReturn(importRequest);
		when(importManager.importFile(any(), any(), any())).thenReturn("success");

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(ImportRequestValidator.class)).thenReturn(validator);
			String result = controller.importResource(null, "{}", httpRequest, response);
			Assert.assertTrue(result.contains("\"status\":1"));
			Assert.assertTrue(result.contains("success"));
		}
	}

	@Test
	public void ut_a3_testImportResourceWithoutUpload() throws Exception {
		ResourceController controller = new ResourceController();
		ImportResourceManager importManager = mock(ImportResourceManager.class);
		JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		ImportRequestValidator validator = mock(ImportRequestValidator.class);
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ImportRequest importRequest = new ImportRequest();
		importRequest.setUpload(false);

		setField(controller, "importManager", importManager);
		setField(controller, "jsonMapperUtils", jsonMapperUtils);

		when(httpRequest.getParameter("resumable")).thenReturn(null);
		when(jsonMapperUtils.mapToDTO(anyString(), any())).thenReturn(importRequest);
		when(importManager.importFile(any(), any(), any())).thenReturn("direct");

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(ImportRequestValidator.class)).thenReturn(validator);
			String result = controller.importResource(null, "{}", httpRequest, response);
			Assert.assertEquals("direct", result);
		}
	}

	@Test(expected = ResourceImportException.class)
	public void ut_a4_testImportResourceThrows() throws Exception {
		ResourceController controller = new ResourceController();
		ImportResourceManager importManager = mock(ImportResourceManager.class);
		JsonMapperUtils jsonMapperUtils = mock(JsonMapperUtils.class);
		ImportRequestValidator validator = mock(ImportRequestValidator.class);
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ImportRequest importRequest = new ImportRequest();
		importRequest.setUpload(true);

		setField(controller, "importManager", importManager);
		setField(controller, "jsonMapperUtils", jsonMapperUtils);

		when(httpRequest.getParameter("resumable")).thenReturn(null);
		when(jsonMapperUtils.mapToDTO(anyString(), any())).thenReturn(importRequest);
		when(importManager.importFile(any(), any(), any())).thenThrow(new ResourceImportException("fail"));

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(ImportRequestValidator.class)).thenReturn(validator);
			controller.importResource(null, "{}", httpRequest, response);
		}
	}

	@Test
	public void ut_a5_testImportResourceResumablePrepare() throws Exception {
		ResourceController controller = new ResourceController();
		ResumableUploadService resumableUploadService = mock(ResumableUploadService.class);
		HttpServletRequest httpRequest = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		setField(controller, "resumableUploadService", resumableUploadService);

		when(httpRequest.getParameter("resumable")).thenReturn("true");
		when(resumableUploadService.prepareFromHttpRequest(httpRequest, "/importResource"))
				.thenReturn("{\"ticket\":\"t1\"}");

		String result = controller.importResource(null, null, httpRequest, response);
		Assert.assertEquals("{\"ticket\":\"t1\"}", result);
		verify(resumableUploadService).prepareFromHttpRequest(httpRequest, "/importResource");
	}

}
