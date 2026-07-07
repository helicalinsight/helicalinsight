package com.helicalinsight.adhoc.jreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.HCRJsonDataSource;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRHelperTest {

	@Test
	public void ut_a1_test_prepareFormDataForHCRParameters() {
		JsonObject urlParametersJson = new JsonObject();
		JsonObject hcrJsonData = new JsonObject();
		JsonObject prepareFormDataForHCRParameters = HCRHelper.prepareFormDataForHCRParameters(urlParametersJson,
				hcrJsonData);
		assertEquals(hcrJsonData, prepareFormDataForHCRParameters);
	}

	@Test
	public void ut_a2_test_prepareFormDataForHCRParameters() {
		JsonObject urlParametersJson = new JsonObject();
		urlParametersJson.addProperty("name", "value");
		JsonObject hcrJsonData = new JsonObject();
		JsonObject connectionDetailsObject = new JsonObject();
		hcrJsonData.add("connectionDetails", connectionDetailsObject);
		JsonObject designerProperties = new JsonObject();
		JsonArray parametersJsonArray = new JsonArray();
		JsonObject item = new JsonObject();
		item.addProperty("name", "name");
		parametersJsonArray.add(item);
		designerProperties.add("parameters", parametersJsonArray);
		hcrJsonData.add("designerProperties", designerProperties);

		JsonObject prepareFormDataForHCRParameters = HCRHelper.prepareFormDataForHCRParameters(urlParametersJson,
				hcrJsonData);
		assertEquals(hcrJsonData, prepareFormDataForHCRParameters);
	}

	@Test(expected = HCRException.class)
	public void ut_a3_test_provideJasperPrint() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		JsonArray genericQueryResult = new JsonArray();
		hcrHelper.provideJasperPrint(formData, genericQueryResult);

	}

	@Test
	public void ut_a4_test_provideJasperPrint() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		;
		JsonArray genericQueryResult = new JsonArray();

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("ClassName");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class, (mock, context) -> {
			when(mock.createDesigner()).thenReturn(jasperDesign);
			when(mock.prepareParameterForReportExecution(any())).thenReturn(requiredParamsExtractedFromFromData);

		})) {
			try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
				try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
					mockedStatic2.when(() -> JasperFillManager.fillReport(any(String.class), any(Map.class),
							any(HCRJsonDataSource.class))).thenReturn(jasperPrint);
					mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign)).thenReturn(jasperReport);
					hcrHelper.provideJasperPrint(formData, genericQueryResult);
				}

			}

		}

	}

	@Test
	public void ut_a5_test_provideJasperPrint() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		JsonArray genericQueryResult = new JsonArray();

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class, (mock, context) -> {
			when(mock.createDesigner()).thenReturn(jasperDesign);
			when(mock.prepareParameterForReportExecution(any())).thenReturn(requiredParamsExtractedFromFromData);

		})) {
			try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
				try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
					mockedStatic2.when(() -> JasperFillManager.fillReport(any(String.class), any(Map.class),
							any(HCRJsonDataSource.class))).thenReturn(jasperPrint);
					mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign)).thenReturn(jasperReport);
					hcrHelper.provideJasperPrint(formData, genericQueryResult);
				}

			}

		}

	}

	@Test
	public void ut_a6_test_provideJasperPrint() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		ResultSet genericQueryResult = mock(ResultSet.class);

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class, (mock, context) -> {
			when(mock.createDesigner()).thenReturn(jasperDesign);
			when(mock.prepareParameterForReportExecution(any())).thenReturn(requiredParamsExtractedFromFromData);

		})) {
			try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
				try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
					mockedStatic2.when(() -> JasperFillManager.fillReport(any(String.class), any(Map.class),
							any(HCRJsonDataSource.class))).thenReturn(jasperPrint);
					mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign)).thenReturn(jasperReport);
					hcrHelper.provideJasperPrint(formData, genericQueryResult);
				}

			}

		}

	}

	@Test
	public void ut_a7_test_provideJasperPrint() throws IOException {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("key", "value");
		formData.add("connectionDetails", connectionDetails);

		Object genericQueryResult = null;

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		
		File file = new File(TempDirectoryCleaner.getTempDirectory()+File.separator+"\\saveId.jrxml");
		file.createNewFile();
		try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class, (mock, context) -> {
			when(mock.createDesigner()).thenReturn(jasperDesign);
			when(mock.prepareParameterForReportExecution(any())).thenReturn(requiredParamsExtractedFromFromData);

		})) {
			try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
				try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
					try (MockedStatic<JRXmlLoader> mockedStatic3 = mockStatic(JRXmlLoader.class)) {
						mockedStatic3.when(() -> JRXmlLoader.load(anyString())).thenReturn(jasperDesign);

						mockedStatic2.when(() -> JasperFillManager.fillReport(any(String.class), any(Map.class),
								any(HCRJsonDataSource.class))).thenReturn(jasperPrint);
						mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign))
								.thenReturn(jasperReport);
						hcrHelper.provideJasperPrint(formData, genericQueryResult);
					}
				}
			}

		} finally {
			file.delete();
		}

	}

	@Test
	public void ut_a8_test_prepareResponse() {
		HCRHelper hcrHelper = new HCRHelper();

		Object genericQueryResult = null;
		JasperPrint originalJasperPrint = null;
		String printUUID = "printUUID";

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", true);
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("key", "value");
		formData.add("connectionDetails", connectionDetails);

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {

				})) {
			try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class,
					(mock, context) -> {
						when(mock.createDesigner()).thenReturn(jasperDesign);
						when(mock.prepareParameterForReportExecution(any()))
								.thenReturn(requiredParamsExtractedFromFromData);

					})) {
				try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
					try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
						mockedStatic2.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(Map.class),
								any(JREmptyDataSource.class))).thenReturn(jasperPrint);
						mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign))
								.thenReturn(jasperReport);

						hcrHelper.prepareResponse(formData, genericQueryResult, originalJasperPrint, printUUID);

					}

				}

			}

		}

	}

	@Test
	public void ut_a9_test_prepareResponse() {
		HCRHelper hcrHelper = new HCRHelper();

		Object genericQueryResult = null;
		JasperPrint originalJasperPrint = null;
		String printUUID = "printUUID";

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", true);
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("key", "value");
		formData.add("connectionDetails", connectionDetails);

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);

		try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class, (mock, context) -> {
			when(mock.createDesigner()).thenReturn(jasperDesign);
			when(mock.prepareParameterForReportExecution(any())).thenReturn(requiredParamsExtractedFromFromData);

		})) {
			try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
				try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
					mockedStatic2.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(Map.class),
							any(JREmptyDataSource.class))).thenReturn(jasperPrint);
					mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign)).thenReturn(jasperReport);

					hcrHelper.prepareResponse(formData, genericQueryResult, originalJasperPrint, printUUID);

				}

			}

		}

	}

	@Test
	public void ut_b1_test_prepareResponse() {
		HCRHelper hcrHelper = new HCRHelper();

		Object genericQueryResult = null;
		JasperPrint originalJasperPrint = null;
		String printUUID = "printUUID";

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		jrxmlDataJson.addProperty("jrxmlDir", "dir");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", true);
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("key", "value");
		formData.add("connectionDetails", connectionDetails);
		JsonArray format = new JsonArray();
		formData.add("format", format);

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		when(jasperPrint.getProperty("lastModifiedCache")).thenReturn("100");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					// when(mock.handleMultiExport(any(),any(), any())).thenReturn("result");
				})) {
			try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class,
					(mock, context) -> {
						when(mock.createDesigner()).thenReturn(jasperDesign);
						when(mock.prepareParameterForReportExecution(any()))
								.thenReturn(requiredParamsExtractedFromFromData);

					})) {
				try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
					try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
						mockedStatic2.when(() -> JasperFillManager.fillReport(any(JasperReport.class), any(Map.class),
								any(JREmptyDataSource.class))).thenReturn(jasperPrint);
						mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign))
								.thenReturn(jasperReport);

						hcrHelper.prepareResponse(formData, genericQueryResult, originalJasperPrint, printUUID);

					}

				}

			}

		}

	}

	@Test(expected = HCRException.class)
	public void ut_b2_test_prepareResponse() throws IOException {
		HCRHelper hcrHelper = new HCRHelper();

		Object genericQueryResult = null;
		JasperPrint originalJasperPrint = null;
		String printUUID = "printUUID";

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", false);

		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("key", "value");
		formData.add("connectionDetails", connectionDetails);
		JsonArray format = new JsonArray();
		formData.add("format", format);

		JsonObject requiredParamsExtractedFromFromData = new JsonObject();
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("name", 21);

		requiredParamsExtractedFromFromData.add("parameters", requestParameters);

		JasperDesign jasperDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		JRParameter param = mock(JRParameter.class);
		JRExpression jrExpression = mock(JRExpression.class);
		JasperPrint jasperPrint = mock(JasperPrint.class);
		JRParameter[] parametersArray = { param };

		when(param.getName()).thenReturn("name");
		when(param.getValueClassName()).thenReturn("java.lang.Integer");
		when(param.getDescription()).thenReturn("Description");
		when(param.getDefaultValueExpression()).thenReturn(jrExpression);
		when(param.getNestedTypeName()).thenReturn("NestedTypeName");
		when(jasperReport.getParameters()).thenReturn(parametersArray);
		when(jasperPrint.getProperty("lastModifiedCache")).thenReturn("lastModifiedCache");

		File file = new File(TempDirectoryCleaner.getTempDirectory()+File.separator+"\\saveId.jrxml");
		file.createNewFile();
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
				})) {
			try (MockedConstruction<JDesignHelper> construction = mockConstruction(JDesignHelper.class,
					(mock, context) -> {
						when(mock.createDesigner()).thenReturn(jasperDesign);
						when(mock.prepareParameterForReportExecution(any()))
								.thenReturn(requiredParamsExtractedFromFromData);

					})) {
				try (MockedStatic<JasperCompileManager> mockedStatic = mockStatic(JasperCompileManager.class)) {
					try (MockedStatic<JasperFillManager> mockedStatic2 = mockStatic(JasperFillManager.class)) {
						try (MockedStatic<JRXmlLoader> mockedStatic3 = mockStatic(JRXmlLoader.class)) {
							mockedStatic3.when(() -> JRXmlLoader.load(anyString())).thenReturn(jasperDesign);

							mockedStatic2.when(() -> JasperFillManager.fillReport(any(JasperReport.class),
									any(Map.class), any(JRResultSetDataSource.class))).thenReturn(jasperPrint);
							mockedStatic.when(() -> JasperCompileManager.compileReport(jasperDesign))
									.thenReturn(jasperReport);

							hcrHelper.prepareResponse(formData, genericQueryResult, originalJasperPrint, printUUID);

						}
					}

				}

			}

		} finally {
			file.delete();
		}

	}

	@Test
	public void ut_b3_test_provideResponseUsingPrint() {
		HCRHelper hcrHelper = new HCRHelper();

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", true);

		JasperPrint cacheJasperPrint = mock(JasperPrint.class);

		when(cacheJasperPrint.getProperty("lastModifiedCache")).thenReturn("100");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
					when(mock.exportIntoFiles(anyString(), anyInt(), any())).thenReturn(true);
				})) {
			hcrHelper.provideResponseUsingPrint(formData, cacheJasperPrint, 10);

		}
	}

	@Test
	public void ut_b4_test_provideResponseUsingPrint() {
		HCRHelper hcrHelper = new HCRHelper();

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", true);

		JasperPrint cacheJasperPrint = mock(JasperPrint.class);

		when(cacheJasperPrint.getProperty("lastModifiedCache")).thenReturn("100");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
					when(mock.exportIntoFiles(anyString(), anyInt(), any())).thenReturn(false);
				})) {
			hcrHelper.provideResponseUsingPrint(formData, cacheJasperPrint, 10);

		}
	}

	@Test
	public void ut_b5_test_provideResponseUsingPrint() {
		HCRHelper hcrHelper = new HCRHelper();

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", true);
		JsonArray format = new JsonArray();
		formData.add("format", format);
		JasperPrint cacheJasperPrint = mock(JasperPrint.class);

		when(cacheJasperPrint.getProperty("lastModifiedCache")).thenReturn("100");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
					when(mock.exportIntoFiles(anyString(), anyInt(), any())).thenReturn(false);
				})) {
			hcrHelper.provideResponseUsingPrint(formData, cacheJasperPrint, 10);

		}
	}

	@Test
	public void ut_b6_test_provideResponseUsingPrint() {
		HCRHelper hcrHelper = new HCRHelper();

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", false);
		JsonArray format = new JsonArray();
		formData.add("format", format);
		JasperPrint cacheJasperPrint = mock(JasperPrint.class);

		when(cacheJasperPrint.getProperty("lastModifiedCache")).thenReturn("100");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
					when(mock.exportIntoFiles(anyString(), anyInt(), any())).thenReturn(false);
				})) {
			hcrHelper.provideResponseUsingPrint(formData, cacheJasperPrint, 10);

		}
	}

	@Test
	public void ut_b7_test_provideResponseUsingPrint() {
		HCRHelper hcrHelper = new HCRHelper();

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", false);
		JsonArray format = new JsonArray();
		formData.add("format", format);
		JasperPrint cacheJasperPrint = mock(JasperPrint.class);

		when(cacheJasperPrint.getProperty("lastModifiedCache")).thenReturn("100");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
					when(mock.exportIntoFiles(anyString(), anyInt(), any())).thenReturn(false);
				})) {
			hcrHelper.provideResponseUsingPrint(formData, cacheJasperPrint, null);

		}
	}

	@Test(expected = HCRException.class)
	public void ut_b8_test_provideResponseUsingPrint() {
		HCRHelper hcrHelper = new HCRHelper();

		JsonObject formData = new JsonObject();
		formData.addProperty("key", "value");
		JsonObject jrxmlDataJson = new JsonObject();
		jrxmlDataJson.addProperty("uuid", "uuid");
		formData.add("jrxmlData", jrxmlDataJson);
		formData.addProperty("isFromSave", true);
		formData.addProperty("saveId", "saveId");
		formData.addProperty("generateXML", true);
		formData.addProperty("dir", "dir");
		formData.addProperty("isExport", false);
		formData.addProperty("format", "html");
		JasperPrint cacheJasperPrint = mock(JasperPrint.class);

		when(cacheJasperPrint.getProperty("lastModifiedCache")).thenReturn("lastModifiedCache");
		try (MockedConstruction<HCRExportHelper> construction1 = mockConstruction(HCRExportHelper.class,
				(mock, context) -> {
					when(mock.exportInBytes(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(new byte[1]);
					when(mock.exportIntoFiles(anyString(), anyInt(), any())).thenReturn(false);
				})) {
			hcrHelper.provideResponseUsingPrint(formData, cacheJasperPrint, 100);

		}
	}

	@Test
	public void ut_b9_test_prepareQueryForReport() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		formData.add("connectionDetails", connectionDetails);
		JsonObject designerProperties = new JsonObject();
		formData.add("designerProperties", designerProperties);
		JsonObject connectionFormData = new JsonObject();
		connectionFormData.addProperty("temp_uuid", "temp_uuid");
		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "type");
		connectionPart.addProperty("id", "id");
		JsonObject dataMapTagContent = new JsonObject();
		dataMapTagContent.addProperty("Parameters", "Parameters");
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getConnectionPartFromTemp()).thenReturn(connectionPart);
					when(mock.getUnProcessedQueryFromTemp()).thenReturn("${query}");
					when(mock.getDataMapTagContentFromTemp()).thenReturn(dataMapTagContent);
				})) {

			hcrHelper.prepareQueryForReport(formData, connectionFormData, null);
		}

	}

	@Test
	public void ut_c1_test_prepareQueryForReport() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject formData = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		formData.add("connectionDetails", connectionDetails);
		JsonObject designerProperties = new JsonObject();
		formData.add("designerProperties", designerProperties);
		JsonObject connectionFormData = new JsonObject();
		connectionFormData.addProperty("temp_uuid", "temp_uuid");
		connectionFormData.addProperty("dir", "dir");
		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "type");
		connectionPart.addProperty("id", "id");
		JsonObject dataMapTagContent = new JsonObject();
		dataMapTagContent.addProperty("Parameters", "Parameters");
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getConnectionPart()).thenReturn(connectionPart);
					when(mock.getUnProcessedQuery()).thenReturn("${query}");
					when(mock.getDataMapTagContent()).thenReturn(dataMapTagContent);
				})) {

			hcrHelper.prepareQueryForReport(formData, connectionFormData, null);
		}

	}

	@Test
	public void ut_c2_test_getExecutedData() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject connectionFormData = new JsonObject();
		connectionFormData.addProperty("temp_uuid", "temp_uuid");

		JsonObject resultSet = new JsonObject();
		JsonArray data = new JsonArray();
		resultSet.add("data", data);
		;
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getResultSetFromTemp()).thenReturn(resultSet);
				})) {
			hcrHelper.getExecutedData(connectionFormData);
		}
	}

	@Test
	public void ut_c3_test_getExecutedData() {
		HCRHelper hcrHelper = new HCRHelper();
		JsonObject connectionFormData = new JsonObject();
		connectionFormData.addProperty("temp_uuid", "temp_uuid");
		connectionFormData.addProperty("dir", "dir");

		JsonObject resultSet = new JsonObject();

		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getResultSet()).thenReturn(resultSet);
				})) {
			hcrHelper.getExecutedData(connectionFormData);
		}
	}

	@Test
	public void ut_c4_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.lang.Double";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("double", 12.23);
		String parameterName = "double";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertEquals(12.23, prepareGenericValue);
	}

	@Test
	public void ut_c5_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "net.sf.jasperreports.engine.JasperReport";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("jasperReport", "jasperReport");
		String parameterName = "jasperReport";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		JsonObject parameters = new JsonObject();
		parameters.addProperty("jasperReport", "jasperReport");
		requiredParamsExtractedFromFormData.add("parameters", parameters);
		String uuid = "";
		JasperDesign jasperSubDesign = mock(JasperDesign.class);
		JasperReport jasperReport = mock(JasperReport.class);
		try (MockedStatic<JRXmlLoader> mockedStatic = mockStatic(JRXmlLoader.class)) {
			try (MockedStatic<JasperCompileManager> mockedStatic1 = mockStatic(JasperCompileManager.class)) {
				mockedStatic1.when(() -> JasperCompileManager.compileReport(any(JasperDesign.class)))
						.thenReturn(jasperReport);

				mockedStatic.when(() -> JRXmlLoader.load(anyString())).thenReturn(jasperSubDesign);

				Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters,
						parameterName, requiredParamsExtractedFromFormData, uuid);
				assertEquals(jasperReport,prepareGenericValue);
			}
		}

	}
	@Test
	public void ut_c6_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "net.sf.jasperreports.engine.JasperReport";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("jasperReport", "jasperReport");
		String parameterName = "jasperReport";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		JsonObject parameters = new JsonObject();
		parameters.addProperty("jasperReport", "jasperReport");
		requiredParamsExtractedFromFormData.add("parameters", parameters);
		String uuid = "";
		JasperDesign jasperSubDesign = mock(JasperDesign.class);
		try (MockedStatic<JRXmlLoader> mockedStatic = mockStatic(JRXmlLoader.class)) {
			try (MockedStatic<JasperCompileManager> mockedStatic1 = mockStatic(JasperCompileManager.class)) {
				mockedStatic1.when(() -> JasperCompileManager.compileReport(any(JasperDesign.class)))
						.thenThrow(new JRException("mockito exception"));

				mockedStatic.when(() -> JRXmlLoader.load(anyString())).thenReturn(jasperSubDesign);

				hcrHelper.prepareGenericValue(valueClassName, requestParameters,
						parameterName, requiredParamsExtractedFromFormData, uuid);
				
			}
		}

	}
	
	@Test
	public void ut_c7_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.util.Date";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("date", "15-06-2024 10:20:30");
		String parameterName = "date";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertNotNull(prepareGenericValue);
	}
	@Test
	public void ut_c8_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.util.Date";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("date", "15-06-2024");
		String parameterName = "date";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertNull(prepareGenericValue);
	}
	
	@Test
	public void ut_c9_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.util.Collection";
		JsonObject requestParameters = new JsonObject();
		JsonArray arr = new JsonArray();
		requestParameters.add("collection", arr);
		String parameterName = "collection";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertEquals(Collections.emptyList(), prepareGenericValue);
	}
	
	@Test
	public void ut_d1_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.util.Collection";
		JsonObject requestParameters = new JsonObject();
		JsonObject obj = new JsonObject();
		requestParameters.add("collection", obj);
		String parameterName = "collection";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertEquals(Collections.emptyList(), prepareGenericValue);
	}
	
	@Test
	public void ut_d2_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.util.Collection";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("collection", "collection");
		String parameterName = "collection";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertEquals(Collections.singletonList("collection"), prepareGenericValue);
	}

	@Test
	public void ut_d2b_test_prepareGenericValue_collectionPreservesNumericElements() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.util.Collection";
		JsonObject requestParameters = new JsonObject();
		JsonArray array = new JsonArray();
		array.add("NetBanking");
		array.add("Cash");
		requestParameters.add("Payment", array);
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, "Payment",
				requiredParamsExtractedFromFormData, "");
		assertEquals(List.of("NetBanking", "Cash"), prepareGenericValue);
	}
	
//	@Test
	public void ut_d3_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.sql.Connection";
		JsonObject requestParameters = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("type", "java.sql.Connection");
		requestParameters.add("connection", connection);
		String parameterName = "connection";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "type");
		connectionPart.addProperty("id", "id");
		DriverConnection driverConnection = mock(DriverConnection.class);
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getConnectionPart()).thenReturn(connectionPart);
		})) {
			try(MockedStatic<DataSourceUtils> mockedStatic = mockStatic(DataSourceUtils.class)){
			try(MockedStatic<ConnectionProviderFactory> mockedStatic2 = mockStatic(ConnectionProviderFactory.class)){
				mockedStatic2.when(()-> ConnectionProviderFactory.getConnection(any(), any())).thenReturn(driverConnection);
			
			Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
					requiredParamsExtractedFromFormData, uuid);
			assertNull(prepareGenericValue);
			}
			}	
		}
		
	}
	
//	@Test
	public void ut_d4_test_prepareGenericValue() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "java.sql.Connection";
		JsonObject requestParameters = new JsonObject();
		JsonObject connection = new JsonObject();
		connection.addProperty("type", "java.sql.Connection");
		connection.addProperty("temp_uuid", "temp_uuid");
		requestParameters.add("connection", connection);
		String parameterName = "connection";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "type");
		connectionPart.addProperty("id", "id");
		DriverConnection driverConnection = mock(DriverConnection.class);
		try (MockedConstruction<EnhancedQueryExecutor> construction = mockConstruction(EnhancedQueryExecutor.class,
				(mock, context) -> {
					when(mock.getConnectionPartFromTemp()).thenReturn(connectionPart);
		})) {
			try(MockedStatic<DataSourceUtils> mockedStatic = mockStatic(DataSourceUtils.class)){
			try(MockedStatic<ConnectionProviderFactory> mockedStatic2 = mockStatic(ConnectionProviderFactory.class)){
				mockedStatic2.when(()-> ConnectionProviderFactory.getConnectionFromTemp(any(), any())).thenReturn(driverConnection);
			
			Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
					requiredParamsExtractedFromFormData, uuid);
			assertNull(prepareGenericValue);
			}
			}	
		}
		
	}
	
	@Test
	public void ut_d5_test_prepareGenericValue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "net.sf.jasperreports.engine.JRDataSource";
		JsonObject requestParameters = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		requestParameters.add("parameterName", connectionDetails);
		String parameterName = "parameterName";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		HCRQueryProcessCacheManager cacheManager = mock(HCRQueryProcessCacheManager.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
		
		Field field = HCRHelper.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(hcrHelper, cacheHelper);
		when(cacheManager.getResult()).thenReturn(new JsonArray());
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class); MockedStatic<JsonUtils> mockedJsonUtils = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> CacheUtils.getCacheManager("/hcr")).thenReturn(cacheManager);
			mockedJsonUtils.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("bean-datasource");
		
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertNotNull(prepareGenericValue);
		}
	}
	
	@Test
	public void ut_d6_test_prepareGenericValue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "net.sf.jasperreports.engine.JRDataSource";
		JsonObject requestParameters = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		requestParameters.add("parameterName", connectionDetails);
		String parameterName = "parameterName";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		CacheManager cacheManager = mock(CacheManager.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
		
		Field field = HCRHelper.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(hcrHelper, cacheHelper);
		
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class); MockedStatic<JsonUtils> mockedJsonUtils = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> CacheUtils.getCacheManager("/hcr")).thenReturn(cacheManager);
			mockedJsonUtils.when(() -> JsonUtils.getHCRGeneratorType()).thenReturn("bean-datasource");
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertNotNull(prepareGenericValue);
		}
	}
	
	@Test
	public void ut_d7_test_prepareGenericValue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "test";
		JsonObject requestParameters = new JsonObject();
		requestParameters.addProperty("parameterName", "test");
		String parameterName = "parameterName";
		JsonObject requiredParamsExtractedFromFormData = new JsonObject();
		String uuid = "";
		
		Object prepareGenericValue = hcrHelper.prepareGenericValue(valueClassName, requestParameters, parameterName,
				requiredParamsExtractedFromFormData, uuid);
		assertNotNull(prepareGenericValue);
		
	}

	@Test
	public void ut_d8_test_prepareGenericValue_lazySubdataset() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory";
		JsonObject requestParameters = new JsonObject();
		JsonObject parameterValue = new JsonObject();
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-lazy");
		connectionDetails.addProperty("map_id", 1);
		parameterValue.add("connectionDetails", connectionDetails);
		requestParameters.add("TravelData", parameterValue);

		Object value = hcrHelper.prepareGenericValue(valueClassName, requestParameters, "TravelData",
				new JsonObject(), "");

		assertNotNull(value);
		assertTrue(value instanceof LazySubDatasetDataSourceFactory);
		LazySubDatasetDataSourceFactory factory = (LazySubDatasetDataSourceFactory) value;
		assertEquals("temp-uuid-lazy", factory.getConnectionDetails().get("temp_uuid").getAsString());
	}

	@Test
	public void ut_d9_test_prepareGenericValue_lazySubdataset_emptyConnectionDetails() {
		HCRHelper hcrHelper = new HCRHelper();
		String valueClassName = "com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory";
		JsonObject requestParameters = new JsonObject();
		requestParameters.add("TravelData", new JsonObject());

		Object value = hcrHelper.prepareGenericValue(valueClassName, requestParameters, "TravelData",
				new JsonObject(), "");

		assertNull(value);
	}

	@Test
	public void ut_e1_test_fetchSubdatasetResultSet() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		ResultSet resultSet = mock(ResultSet.class);
		Cache cache = mock(Cache.class);

		Field field = HCRHelper.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(hcrHelper, cacheHelper);

		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-fetch");
		connectionDetails.addProperty("map_id", 1);

		try (MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
			when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
					.thenReturn(true);
			when(resultSetCacheManager.getResult()).thenReturn(resultSet);

			ResultSet actual = hcrHelper.fetchSubdatasetResultSet(connectionDetails);

			assertEquals(resultSet, actual);
		}
	}
}
