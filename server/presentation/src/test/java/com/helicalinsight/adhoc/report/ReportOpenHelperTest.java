//package com.helicalinsight.adhoc.report;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.mockito.MockedStatic;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.helicalinsight.adhoc.service.HICubeDAOService;
//import com.helicalinsight.admin.model.HIResource;
//import com.helicalinsight.admin.model.HIResourceHReport;
//import com.helicalinsight.admin.model.HIResourceInstantReport;
//import com.helicalinsight.admin.model.HIResourceReport;
//import com.helicalinsight.admin.service.HIResourceServiceDB;
//import com.helicalinsight.datasource.managed.jaxb.HCReport;
//import com.helicalinsight.efw.ApplicationProperties;
//import com.helicalinsight.efw.exceptions.EfwServiceException;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import com.helicalinsight.efw.utility.JaxbUtils;
//import com.helicalinsight.efw.utility.JsonUtils;
//import com.helicalinsight.resourcesecurity.IResource;
//import com.helicalinsight.resourcesecurity.jaxb.CanvasElements;
//import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class ReportOpenHelperTest {
//
//	@Test(expected = ReportFileNotFoundException.class)
//	public void ut_a1_test_getAdhocReport() {
//		ReportOpenHelper helper = new ReportOpenHelper();
//		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
//		when(applicationProperties.getSolutionDirectory()).thenReturn("solutionDir");
//		try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
//			mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
//			ReportOpenHelper.getAdhocReport("dir", "fName");
//
//		}
//	}
//
//	@Test(expected = EfwServiceException.class)
//	public void ut_a2_test_getAdhocReport() {
//		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
//		when(applicationProperties.getSolutionDirectory()).thenReturn("solutionDir");
//		try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
//			mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
//			ReportOpenHelper.getAdhocReport("", "fName");
//
//		}
//	}
//
//	@Test(expected = EfwServiceException.class)
//	public void ut_a3_test_getAdhocReport() throws IOException {
//		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
//		when(applicationProperties.getSolutionDirectory())
//				.thenReturn("/home/helical/Performance/hi/hi-repository/System");
//		String dir = "/home/helical/Performance/hi/hi-repository/System/Temp";
//		File directory = new File(dir);
//		directory.mkdir();
//		File fileInsideDir = new File(directory, "fName.txt");
//		fileInsideDir.createNewFile();
//
//		try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("report");
//				mockedStatic1.when(() -> JsonUtils.getHCRExtension()).thenReturn("hcr");
//				mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("hr");
//
//				mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
//				ReportOpenHelper.getAdhocReport("Temp", "fName.txt");
//			}
//		} finally {
//			fileInsideDir.delete();
//			directory.delete();
//		}
//	}
//
//	@Test
//	public void ut_a4_test_getAdhocReport() throws IOException {
//		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
//		when(applicationProperties.getSolutionDirectory())
//				.thenReturn(/home/helical/Performance/hi/hi-repository/System");
//		String dir = "/home/helical/Performance/hi/hi-repository/System/Temp";
//		File directory = new File(dir);
//		directory.mkdir();
//		File fileInsideDir = new File(directory, "fName.report");
//		fileInsideDir.createNewFile();
//		IResource iResource = mock(IResource.class);
//		try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {
//
//					mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("report");
//					mockedStatic1.when(() -> JsonUtils.getHCRExtension()).thenReturn("hcr");
//					mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("hr");
//
//					mockedStatic2.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(iResource);
//					mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
//
//					IResource adhocReport = ReportOpenHelper.getAdhocReport("Temp", "fName.report");
//					assertEquals(iResource, adhocReport);
//
//				}
//			}
//		} finally {
//			fileInsideDir.delete();
//			directory.delete();
//		}
//	}
//
//	@Test
//	public void ut_a5_test_getAdhocReport() throws IOException {
//		ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
//		when(applicationProperties.getSolutionDirectory())
//				.thenReturn(/home/helical/Performance/hi/hi-repository/System");
//		String dir = "/home/helical/Performance/hi/hi-repository/System/Temp";
//		File directory = new File(dir);
//		directory.mkdir();
//		File fileInsideDir = new File(directory, "fName.hcr");
//		fileInsideDir.createNewFile();
//		IResource iResource = mock(IResource.class);
//		try (MockedStatic<ApplicationProperties> mockedStatic = mockStatic(ApplicationProperties.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {
//
//					mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("report");
//					mockedStatic1.when(() -> JsonUtils.getHCRExtension()).thenReturn("hcr");
//					mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("hr");
//
//					mockedStatic2.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(iResource);
//					mockedStatic.when(() -> ApplicationProperties.getInstance()).thenReturn(applicationProperties);
//
//					IResource adhocReport = ReportOpenHelper.getAdhocReport("Temp", "fName.hcr");
//					assertEquals(iResource, adhocReport);
//
//				}
//			}
//		} finally {
//			fileInsideDir.delete();
//			directory.delete();
//		}
//	}
//
//	@Test(expected = ReportFileNotFoundException.class)
//	public void ut_a6_test_getAdhocReportDb() {
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(null);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//					.thenReturn(serviceDB);
//			ReportOpenHelper.getAdhocReportDb("dir", "fileName");
//
//		}
//
//	}
//
//	@Test(expected = EfwServiceException.class)
//	public void ut_a7_test_getAdhocReportDb() {
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource hiResource = mock(HIResource.class);
//
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(hiResource);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("hReport");
//				mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("hr");
//
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//						.thenReturn(serviceDB);
//				ReportOpenHelper.getAdhocReportDb("dir", "fileName.report");
//
//			}
//		}
//	}
//
//	@Test
//	public void ut_a8_test_getAdhocReportDb() {
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource hiResource = mock(HIResource.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		CanvasElements canvas = mock(CanvasElements.class);
//		MetadataReference metadataReference = mock(MetadataReference.class);
//		HIResourceReport adhocJpa = mock(HIResourceReport.class);
//
//		when(hiResource.getResourcePath()).thenReturn("path");
//		when(hiResource.getResourceURL()).thenReturn("url/file");
//
//		when(serviceDB.getHIResourceById(anyInt())).thenReturn(hiResource);
//		when(adhocJpa.getHiResourceMetadata()).thenReturn(123);
//		when(hiResource.getHiResourceReport()).thenReturn(adhocJpa);
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(hiResource);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("report");
//				mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("hr");
//
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//						.thenReturn(serviceDB);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class)).thenReturn(adhocReport);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class)).thenReturn(canvas);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
//						.thenReturn(metadataReference);
//				IResource adhocReportDb = ReportOpenHelper.getAdhocReportDb("dir", "fileName.report");
//				assertEquals(adhocReport, adhocReportDb);
//			}
//		}
//	}
//
//	@Test
//	public void ut_a9_test_getAdhocReportDb() {
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource hiResource = mock(HIResource.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		CanvasElements canvas = mock(CanvasElements.class);
//		MetadataReference metadataReference = mock(MetadataReference.class);
//		HIResourceReport adhocJpa = mock(HIResourceReport.class);
//		HIResourceHReport hiResourceHReport = mock(HIResourceHReport.class);
//
//		when(hiResource.getResourcePath()).thenReturn("path");
//		when(hiResource.getResourceURL()).thenReturn("url/file");
//
//		when(hiResource.getHiResourceHReport()).thenReturn(hiResourceHReport);
//		when(hiResourceHReport.getHiResourceMetadata()).thenReturn(123);
//		when(hiResourceHReport.getHiResourceCube()).thenReturn(456);
//
//		when(serviceDB.getHIResourceById(anyInt())).thenReturn(hiResource);
//		when(adhocJpa.getHiResourceMetadata()).thenReturn(123);
//		when(hiResource.getHiResourceReport()).thenReturn(adhocJpa);
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(hiResource);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("meta");
//				mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("report");
//
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//						.thenReturn(serviceDB);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class)).thenReturn(adhocReport);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class)).thenReturn(canvas);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
//						.thenReturn(metadataReference);
//				IResource adhocReportDb = ReportOpenHelper.getAdhocReportDb("dir", "fileName.report");
//				assertEquals(adhocReport, adhocReportDb);
//			}
//		}
//	}
//
//	@Test(expected = RuntimeException.class)
//	public void ut_b1_test_getAdhocReportDb() {
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource hiResource = mock(HIResource.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		CanvasElements canvas = mock(CanvasElements.class);
//		MetadataReference metadataReference = mock(MetadataReference.class);
//		HIResourceReport adhocJpa = mock(HIResourceReport.class);
//		HIResourceHReport hiResourceHReport = mock(HIResourceHReport.class);
//
//		when(hiResource.getResourcePath()).thenReturn("path");
//		when(hiResource.getResourceURL()).thenReturn("url/file");
//
//		when(hiResource.getHiResourceHReport()).thenThrow(new RuntimeException());
//		when(hiResourceHReport.getHiResourceMetadata()).thenReturn(123);
//		when(hiResourceHReport.getHiResourceCube()).thenReturn(456);
//
//		when(serviceDB.getHIResourceById(anyInt())).thenReturn(hiResource);
//		when(adhocJpa.getHiResourceMetadata()).thenReturn(123);
//		when(hiResource.getHiResourceReport()).thenReturn(adhocJpa);
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(hiResource);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//				mockedStatic1.when(() -> JsonUtils.getReportExtension()).thenReturn("meta");
//				mockedStatic1.when(() -> JsonUtils.getHrReportExtension()).thenReturn("report");
//
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//						.thenReturn(serviceDB);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class)).thenReturn(adhocReport);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class)).thenReturn(canvas);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
//						.thenReturn(metadataReference);
//				ReportOpenHelper.getAdhocReportDb("dir", "fileName.report");
//
//			}
//		}
//	}
//
//	@Test(expected = ReportFileNotFoundException.class)
//	public void ut_b2_test_getInstantReportDb() {
//
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(null);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//					.thenReturn(serviceDB);
//
//			ReportOpenHelper.getInstantReportDb("dir", "fileName");
//		}
//	}
//
//	@Test(expected = EfwServiceException.class)
//	public void ut_b3_test_getInstantReportDb() {
//
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource hiResource = mock(HIResource.class);
//
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(hiResource);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//
//				mockedStatic1.when(()-> JsonUtils.getInstantReportExtension()).thenReturn("hr");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//						.thenReturn(serviceDB);
//
//				ReportOpenHelper.getInstantReportDb("dir", "fileName.report");
//			}
//		}
//	}
//	
//	
//	@Test
//	public void ut_b4_test_getInstantReportDb() {
//
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource hiResource = mock(HIResource.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		CanvasElements canvas = mock(CanvasElements.class);
//		MetadataReference metadataReference = mock(MetadataReference.class);
//		HIResourceInstantReport hiResourceHReport = mock(HIResourceInstantReport.class);
//		
//		when(serviceDB.getHIResourceById(any())).thenReturn(hiResource);
//		when(hiResource.getResourcePath()).thenReturn("path");
//		when(hiResource.getResourceURL()).thenReturn("url/url");
//		
//		when(hiResource.getHiResourceInstantReport()).thenReturn(hiResourceHReport);
//		when(hiResourceHReport.getHiResourceMetadata()).thenReturn(123);
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(hiResource);
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			try (MockedStatic<JsonUtils> mockedStatic1 = mockStatic(JsonUtils.class)) {
//
//				mockedStatic1.when(()-> JsonUtils.getInstantReportExtension()).thenReturn("report");
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class))
//						.thenReturn(serviceDB);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(AdhocReport.class)).thenReturn(adhocReport);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(CanvasElements.class)).thenReturn(canvas);
//				mockedStatic.when(() -> ApplicationContextAccessor.getBean(MetadataReference.class))
//						.thenReturn(metadataReference);
//				IResource instantReportDb = ReportOpenHelper.getInstantReportDb("dir", "fileName.report");
//				assertEquals(adhocReport,instantReportDb);
//			}
//		}
//	}
//	@Test
//	public void ut_b5_test_reportContentAsJson() {
//		CanvasElements canvasElements = mock(CanvasElements.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		
//		when(canvasElements.getColumns()).thenReturn("column");
//		when(canvasElements.getGroups()).thenReturn("groups");
//		when(adhocReport.getCanvasElements()).thenReturn(canvasElements);
//		JsonObject reportContentAsJson = ReportOpenHelper.reportContentAsJson(adhocReport);
//		assertTrue(reportContentAsJson.has("data"));
//	}
//	
//	@Test
//	public void ut_b6_test_reportContentAsJson() {
//		CanvasElements canvasElements = mock(CanvasElements.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		MetadataReference metadataReference = mock(MetadataReference.class);
//		
//		
//		when(canvasElements.getColumns()).thenReturn("column");
//		when(canvasElements.getGroups()).thenReturn("groups");
//		when(adhocReport.getCanvasElements()).thenReturn(canvasElements);
//		when(adhocReport.getMetadataReference()).thenReturn(metadataReference);
//	
//		JsonObject reportContentAsJson = ReportOpenHelper.reportContentAsJson(adhocReport);
//		assertTrue(reportContentAsJson.has("data"));
//	}
//	
//	@Test
//	public void ut_b7_test_reportContentAsJson() {
//		CanvasElements canvasElements = mock(CanvasElements.class);
//		AdhocReport adhocReport = mock(AdhocReport.class);
//		MetadataReference metadataReference = mock(MetadataReference.class);
//		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
//		HIResource cubeResource = mock(HIResource.class);
//		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
//		
//		when(metadataReference.getCube()).thenReturn(true);
//		when(canvasElements.getColumns()).thenReturn("column");
//		when(canvasElements.getGroups()).thenReturn("groups");
//		when(adhocReport.getCanvasElements()).thenReturn(canvasElements);
//		when(adhocReport.getMetadataReference()).thenReturn(metadataReference);
//		when(serviceDB.getResourceByUrl(anyString())).thenReturn(cubeResource);
//		when(cubeResource.getResourceId()).thenReturn(123);
//		JsonObject cubeAsJsonObj = new JsonObject();
//		JsonObject mds = new JsonObject();
//		cubeAsJsonObj.add("metadata", mds);
//		JsonArray cubes = new JsonArray();
//		cubeAsJsonObj.add("cubes",cubes);
//		
//		
//		when(hiCubeDAOService.getCubeAsJsonObj(anyInt())).thenReturn(cubeAsJsonObj);
//		
//		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
//			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(serviceDB);
//			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HICubeDAOService.class)).thenReturn(hiCubeDAOService);
//			
//			JsonObject reportContentAsJson = ReportOpenHelper.reportContentAsJson(adhocReport);
//			assertTrue(reportContentAsJson.has("data"));
//		}
//	}
//	@Test
//	public void ut_b8_test_newReportContentAsJson() {
//		HCReport hcrReport = mock(HCReport.class);
//		
//		when(hcrReport.getDiagramData()).thenReturn("diagramData");
//		when(hcrReport.getDirectory()).thenReturn("Directory");
//		when(hcrReport.getFormData()).thenReturn("FormData");
//		when(hcrReport.getName()).thenReturn("Name");
//		when(hcrReport.getState()).thenReturn("State");
//	
//		JsonObject newReportContentAsJson = ReportOpenHelper.newReportContentAsJson(hcrReport);
//		assertTrue(newReportContentAsJson.has("diagramData"));
//	}
//}
