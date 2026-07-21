package com.helicalinsight.resourcedb.hcr.adv;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.management.SettingsLoader;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import com.lowagie.text.pdf.PdfReader;

import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRTableTest {
	
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	static String tempEfwdUUID = "";
	
	@Test
	public void hcr_a1_save_datasource_state() throws Exception {

		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\"\",\"parameters\":[]}}]}}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JsonObject jsonObject = GsonUtility.parseString(result.getResponse().getContentAsString(), JsonObject.class);
		tempEfwdUUID = jsonObject.getAsJsonObject("response").get("temp_uuid").getAsString();
		Assert.assertNotEquals(tempEfwdUUID, "");
	}

	

	@Test
	public void hcr_a2_adv_generateTable_with_resultset() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1400,\"pageHeight\":1000,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"crosstab\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"positionType\":\"FixRelativeTop\",\"tableWidth\":500,\"tableHeight\":250,\"mode\":\"Transparent\",\"printWhenDetailOverflows\":false,\"printInFirstWholeBand\":false,\"dataSourceExpression\":\"\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"columns\":[{\"width\":300,\"tableHeader\":{\"enabled\":true,\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"dim_id\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{dim_id}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"date_key\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{date_key}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"day_number\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{day_number}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"fiscal_year\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{fiscal_year}\",\"textWidth\":200,\"textHeight\":50}]}}]}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}],\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\"\"},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() > 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
		
		// verify jrxml file
		
		CompletableFuture<Void>  xmlTask = CompletableFuture.runAsync(() -> {
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
				assertNotNull("JRXML input stream should not be null",jrxmlStream);
				JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
				assertNotNull("JasperDesign must be loaded", jasperDesign);
				assertEquals("Untitled 1", jasperDesign.getName());
				assertEquals(1400, jasperDesign.getPageWidth());
				assertEquals(1000, jasperDesign.getPageHeight());
				
				Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
				assertTrue(!dataSetMap.isEmpty());
				assertTrue(dataSetMap.containsKey("MainDataset"));
				
				JRDataset mainDataSet =  dataSetMap.get("MainDataset");
				assertEquals("select * from \"HIUSER\".\"dimdate\"", mainDataSet.getQuery().getText());
				
				Set<String> expectedFields =  Set.of("dim_id","date_key","day_number","fiscal_year");
				Set<String> actualFields =  Arrays.stream(mainDataSet.getFields())
				.map(it -> it.getName())
				.collect(Collectors.toSet());
				
				assertTrue(expectedFields.containsAll(actualFields));
				
				JRBand summaryBand =  jasperDesign.getSummary();;
				
				assertNotNull("Summary band should not be null",summaryBand);
				assertTrue("Summary height must be more than 500",summaryBand.getHeight() >= 500);
				
				List<JRChild>  children =  summaryBand.getChildren();
				assertTrue(children.size() == 1);
				
				JRDesignComponentElement firstChild  =  (JRDesignComponentElement) children.get(0);
				assertNotNull("First child of Summary band should not be null", firstChild);
				StandardTable simpleTable =  (StandardTable) firstChild.getComponent();
				assertNotNull("table object should not be null", simpleTable);
				JRDesignDatasetRun datasetRun  = (JRDesignDatasetRun) simpleTable.getDatasetRun();
				assertEquals("MainDataset", datasetRun.getDatasetName());
				JRExpressionChunk[]  chunks =  datasetRun.getDataSourceExpression().getChunks();
				assertTrue(chunks.length > 0);
				JRExpressionChunk firstChunk = chunks[0];
//				assertEquals("MAIN_DATASET",firstChunk.getText());
				
		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
		});
		
		CompletableFuture<Void> pdfTask = CompletableFuture.runAsync(() -> {
			try (InputStream pdfFileStream = Files.newInputStream(Path.of(filePath + ".pdf"));
					PdfReader pdfReader = new PdfReader(pdfFileStream)) {
				
				assertNotNull("Pdf file stream should not be null",pdfFileStream);
				assertNotNull("pdfReader should not be null", pdfReader);
				assertTrue(pdfReader.getNumberOfPages() > 1 );	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		});

		CompletableFuture.allOf(xmlTask, pdfTask).join();

	}
	
	
	@Test
	public void hcr_a3_1_save_resultSet_hcr_table() throws Exception {
		integrationTestUtility.createFolder("HCR_TABLE");		
		String formData = "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":[{\"id\":1,\"name\":\"Query1\",\"config\":\"select  * from \\\"dimdate\\\"\",\"connectionDetails\":{\"id\":\"1\",\"baseType\":\"global.jdbc\",\"dataSourceType\":\"Managed DataSource\",\"name\":\"SampleTravelDataDerby\",\"type\":\"dynamicDataSource\"},\"executeQueryData\":{\"data\":[{\"dim_id\":1,\"fiscal_year\":\"2013-01-01\",\"modified_date\":\"2018-06-01 09:07:21.1\",\"date_key\":\"2013-01-01\",\"day_number\":\"1\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-01 01:56:47\",\"created_time\":\"09:01:24\",\"rating\":\"0.1\"},{\"dim_id\":2,\"fiscal_year\":\"2013-01-02\",\"modified_date\":\"2018-06-07 19:07:21.1\",\"date_key\":\"2013-01-02\",\"day_number\":\"2\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 11:56:47\",\"created_time\":\"19:07:21\",\"rating\":\"0.2\"},{\"dim_id\":3,\"fiscal_year\":\"2013-01-03\",\"modified_date\":\"2018-06-07 19:20:44.11\",\"date_key\":\"2013-01-03\",\"day_number\":\"3\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 12:07:53\",\"created_time\":\"19:20:44\",\"rating\":\"0.3\"},{\"dim_id\":4,\"fiscal_year\":\"2013-01-04\",\"modified_date\":\"2018-06-11 11:46:09.12\",\"date_key\":\"2013-01-04\",\"day_number\":\"4\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 12:10:06\",\"created_time\":\"11:46:09\",\"rating\":\"0.4\"},{\"dim_id\":5,\"fiscal_year\":\"2013-01-05\",\"modified_date\":\"2018-06-11 12:23:09.13\",\"date_key\":\"2013-01-05\",\"day_number\":\"5\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 12:22:51\",\"created_time\":\"12:23:09\",\"rating\":\"0.5\"},{\"dim_id\":6,\"fiscal_year\":\"2013-01-06\",\"modified_date\":\"2018-06-11 13:11:26.15\",\"date_key\":\"2013-01-06\",\"day_number\":\"6\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 12:37:15\",\"created_time\":\"13:11:26\",\"rating\":\"0.6\"},{\"dim_id\":7,\"fiscal_year\":\"2013-01-07\",\"modified_date\":\"2018-06-12 18:16:40.17\",\"date_key\":\"2013-01-07\",\"day_number\":\"7\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 13:31:16\",\"created_time\":\"18:16:40\",\"rating\":\"0.7\"},{\"dim_id\":8,\"fiscal_year\":\"2013-01-08\",\"modified_date\":\"2018-06-12 19:29:17.22\",\"date_key\":\"2013-01-08\",\"day_number\":\"8\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 17:08:27\",\"created_time\":\"19:29:17\",\"rating\":\"0.8\"},{\"dim_id\":9,\"fiscal_year\":\"2013-01-09\",\"modified_date\":\"2018-06-12 19:43:53.34\",\"date_key\":\"2013-01-09\",\"day_number\":\"9\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 17:20:52\",\"created_time\":\"19:43:53\",\"rating\":\"0.9\"},{\"dim_id\":10,\"fiscal_year\":\"2013-01-10\",\"modified_date\":\"2018-06-12 19:47:13.4\",\"date_key\":\"2013-01-10\",\"day_number\":\"10\",\"fiscal_month_name\":\"3\",\"fiscal_month_label\":\"FY2013-Jan\",\"created_date\":\"2018-06-07 17:37:22\",\"created_time\":\"19:47:13\",\"rating\":\"1\"}],\"field\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"},{\"name\":\"modified_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_month_name\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_month_label\",\"clazz\":\"java.lang.String\"},{\"name\":\"created_date\",\"clazz\":\"java.lang.String\"},{\"name\":\"created_time\",\"clazz\":\"java.lang.String\"},{\"name\":\"rating\",\"clazz\":\"java.lang.String\"}]},\"parameterList\":[],\"temp_uuid\":\""+tempEfwdUUID+"\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"39a13b5e-4ca5-45ca-b559-5730ddc3fbef\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"dim_id\",\"width\":100,\"height\":30,\"label\":\"$F{dim_id}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.Integer\",\"id\":\"node-97bbfcab-aa9f-4be9-b060-315883567f1b\",\"x\":60,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null},{\"name\":\"fiscal_year\",\"width\":100,\"height\":30,\"label\":\"$F{fiscal_year}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.sql.Date\",\"id\":\"node-7c808e29-c783-43b3-9c63-46581f7eddca\",\"x\":201.6864,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null}]},\"name\":\"CannedReport_formdata\",\"dir\":\"HCR_TABLE\",\"previewFormData\":{\"format\":\"html\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"groups\":[],\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"},{\"name\":\"modified_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_month_name\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_month_label\",\"clazz\":\"java.lang.String\"},{\"name\":\"created_date\",\"clazz\":\"java.lang.String\"},{\"name\":\"created_time\",\"clazz\":\"java.lang.String\"},{\"name\":\"rating\",\"clazz\":\"java.lang.String\"}],\"designerStyle\":[{\"name\":\"TABLE_TH\",\"mode\":\"Opaque\",\"backColor\":\"#F9F4F2\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"bottomPadding\":2,\"topPadding\":2,\"leftPadding\":2,\"rightPadding\":2,\"padding\":2,\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}},{\"name\":\"TABLE_CH\",\"mode\":\"Opaque\",\"backColor\":\"#F9F4F2\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"bottomPadding\":2,\"topPadding\":2,\"leftPadding\":2,\"rightPadding\":2,\"padding\":2,\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}},{\"name\":\"TABLE_TD\",\"mode\":\"Opaque\",\"backColor\":\"#F9F4F2\",\"foreColor\":\"#000000\",\"lineStyle\":{\"penLineWidth\":\"1\"},\"border\":{\"bottomPadding\":2,\"topPadding\":2,\"leftPadding\":2,\"rightPadding\":2,\"padding\":2,\"line\":{\"leftLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"rightLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"topLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"},\"bottomLine\":{\"lineWidth\":1,\"lineColor\":\"#000103\"}}}}],\"parameters\":[{\"name\":\"MAIN_REPORT\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_REPORT}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}],\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\"\",\"summary\":{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_REPOPRT}\"},\"positionType\":\"FixRelativeTop\",\"mode\":\"Transparent\",\"printWhenDetailOverflows\":false,\"printInFirstWholeBand\":false,\"dataSourceExpression\":\"\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"columns\":[{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"dim_id\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{dim_id}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"date_key\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{date_key}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"day_number\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{day_number}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"fiscal_year\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{fiscal_year}\",\"textWidth\":200,\"textHeight\":50}]}}]}]}},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"generateXML\":false},\"saveUUID\":\"6e4d7e24-8b80-4c29-a89a-4f3a821b177a\"}";
		String response = integrationTestUtility.saveHCRReportState(formData);
		List<Cache> cacheList =  cacheService.findAllReportedCaches(tempEfwdUUID);
		JsonObject rawNode = GsonUtility.parseString(response, JsonObject.class);
		JsonObject responseNode = rawNode.getAsJsonObject("response");
		Assert.assertEquals("CannedReport_formdata.hcr", responseNode.get("uuid").getAsString());
		Assert.assertEquals("Report saved successfully", responseNode.get("message").getAsString());
		if ( CacheUtils.isCacheEnabled()) {
			Assert.assertTrue(cacheList.size() >= 1);
		}
	}
	
	// Not required.
//	@Test
	public void hcr_a3_adv_generateTable_with_reportData() throws Exception {
		
		
		//  update setting.xml file
		
		File settingXmlFile = new File(ApplicationProperties.getInstance().getSettingPath());
		Path copyFilePath = Files.createTempFile("setting-backup", ".xml");
		
		Files.copy(settingXmlFile.toPath(), copyFilePath, StandardCopyOption.REPLACE_EXISTING);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder =  factory.newDocumentBuilder();
		Document doc = builder.parse(settingXmlFile);
		NodeList generationTypeNode = doc.getElementsByTagName("HCRDefaultGeneratorType");
		if(generationTypeNode.getLength() > 0 ) {
			generationTypeNode.item(0).setTextContent("bean-datasource");
		}
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(settingXmlFile);
	    transformer.transform(source, result);
		
		
		SettingsLoader newSettingLoader = new SettingsLoader(ApplicationProperties.INSTANCE);
		newSettingLoader.loadApplicationSettings();
		
		
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"net.sf.jasperreports.engine.JRDataSource\",\"value\":{\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}}],\"variables\":[],\"pageWidth\":1400,\"pageHeight\":1000,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"crosstab\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"positionType\":\"FixRelativeTop\",\"tableWidth\":500,\"tableHeight\":250,\"mode\":\"Transparent\",\"printWhenDetailOverflows\":false,\"printInFirstWholeBand\":false,\"dataSourceExpression\":\"\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"columns\":[{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"dim_id\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{dim_id}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"date_key\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{date_key}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"day_number\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{day_number}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"fiscal_year\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{fiscal_year}\",\"textWidth\":200,\"textHeight\":50}]}}]}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[],\"variables\":[]}]},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue("Page length must be morethan 1",reportPageInfo.get("totalPageCount").getAsInt() > 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
		
		// verify jrxml file
		
		CompletableFuture<Void>  xmlTask = CompletableFuture.runAsync(() -> {
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
				assertNotNull("JRXML input stream should not be null",jrxmlStream);
				JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
				assertNotNull("JasperDesign must be loaded", jasperDesign);
				assertEquals("Untitled 1", jasperDesign.getName());
				assertEquals(1400, jasperDesign.getPageWidth());
				assertEquals(1000, jasperDesign.getPageHeight());
				
				Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
				assertTrue(!dataSetMap.isEmpty());
				assertTrue(dataSetMap.containsKey("MainDataset"));
				
				JRDataset mainDataSet =  dataSetMap.get("MainDataset");
				
				Set<String> expectedFields =  Set.of("dim_id","date_key","day_number","fiscal_year");
				Set<String> actualFields =  Arrays.stream(mainDataSet.getFields())
				.map(it -> it.getName())
				.collect(Collectors.toSet());
				
				assertTrue(expectedFields.containsAll(actualFields));
				
				JRBand summaryBand =  jasperDesign.getSummary();
				
				assertNotNull("Summary band should not be null",summaryBand);
				assertTrue("Summary height must be more than 500",summaryBand.getHeight() >= 500);
				
				List<JRChild>  children =  summaryBand.getChildren();
				assertTrue(children.size() == 1);
				
				JRDesignComponentElement firstChild  =  (JRDesignComponentElement) children.get(0);
				assertNotNull("First child of Summary band should not be null", firstChild);
				StandardTable simpleTable =  (StandardTable) firstChild.getComponent();
				assertNotNull("table object should not be null", simpleTable);
				JRDesignDatasetRun datasetRun  = (JRDesignDatasetRun) simpleTable.getDatasetRun();
				assertEquals("MainDataset", datasetRun.getDatasetName());
				JRExpressionChunk[]  chunks =  datasetRun.getDataSourceExpression().getChunks();
				assertTrue(chunks.length > 0);
				JRExpressionChunk firstChunk = chunks[0];
				StringBuilder dsExpr = new StringBuilder();
				for (JRExpressionChunk chunk : chunks) {
					dsExpr.append(chunk.getText());
				}
				assertTrue(dsExpr.toString().contains("LazySubDatasetDataSourceFactory.resolve"));
				assertTrue(dsExpr.toString().contains("MAIN_DATASET"));
				
		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
		});
		
		CompletableFuture<Void> pdfTask = CompletableFuture.runAsync(() -> {
			try (InputStream pdfFileStream = Files.newInputStream(Path.of(filePath + ".pdf"));
					PdfReader pdfReader = new PdfReader(pdfFileStream)) {
				
				assertNotNull("Pdf file stream should not be null",pdfFileStream);
				assertNotNull("pdfReader should not be null", pdfReader);
				assertTrue(pdfReader.getNumberOfPages() > 1 );	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		});
		CompletableFuture.allOf(xmlTask, pdfTask).join();
		Files.copy(copyFilePath, settingXmlFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
		newSettingLoader.loadApplicationSettings();
	}
	
	private static String TESTURL = "";
	static String fileName = "";
	
	
//	@Test
	public void hcr_a4_adv_exportHCRReport() throws Exception {
		String request = "{\"dir\": \"HCR_TABLE\",\"file\" : \"CannedReport_formdata.hcr\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = integrationTestUtility.exportResource(request, TESTURL );
		Assert.assertNotEquals("",fileName);;
	}
	
	
	@Test
	public void hcr_a5_save_datasource_state() throws Exception {

		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"travel_details\\\" order by \\\"mode_of_payment\\\", \\\"source\\\"\",\"parameters\":[]}}]}}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JsonObject jsonObject = GsonUtility.parseString(result.getResponse().getContentAsString(), JsonObject.class);
		tempEfwdUUID = jsonObject.getAsJsonObject("response").get("temp_uuid").getAsString();
		Assert.assertNotEquals(tempEfwdUUID, "");
	}
	
	@Test
	public void hcr_a6_adv_table_groupHeaderFooter() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":2500,\"pageHeight\":1000,\"orientation\":\"Landscape\",\"columnCount\":1,\"summary\":{\"bandHeight\":700,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"crosstab\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"positionType\":\"FixRelativeTop\",\"tableWidth\":2400,\"tableHeight\":600,\"mode\":\"Transparent\",\"printWhenDetailOverflows\":false,\"printInFirstWholeBand\":false,\"dataSourceExpression\":\"\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":600,\"width\":2500,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"columns\":[{\"width\":300,\"tableGroupHeaders\":[{\"name\":\"mode_of_payment\",\"styleNameReference\":\"TABLE_CH\",\"height\":50,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"Mode of Payment:\\\"\",\"textWidth\":200,\"textHeight\":34,\"isBold\":true}]},{\"name\":\"source\",\"styleNameReference\":\"TABLE_CH\",\"height\":50,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"Source:\\\"\",\"textWidth\":200,\"textHeight\":34,\"isBold\":true}]}],\"tableHeader\":{\"enabled\":false},\"columnData\":{\"enabled\":false},\"columnHeaderOfTable\":{\"enabled\":false},\"columnFooterOfTable\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false}},{\"width\":300,\"tableGroupHeaders\":[{\"name\":\"mode_of_payment\",\"styleNameReference\":\"TABLE_CH\",\"height\":50,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{mode_of_payment}\",\"textWidth\":200,\"textHeight\":34,\"isBold\":true}]},{\"name\":\"source\",\"styleNameReference\":\"TABLE_CH\",\"height\":50,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{source}\",\"textWidth\":95,\"textHeight\":34,\"isBold\":true}]}],\"tableHeader\":{\"enabled\":true,\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"travel_date\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{travel_date}\",\"textWidth\":200,\"textHeight\":50}]},\"columnHeaderOfTable\":{\"enabled\":false},\"columnFooterOfTable\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"mode_of_payment\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{mode_of_payment}\",\"textWidth\":200,\"textHeight\":50}]},\"columnHeaderOfTable\":{\"enabled\":false},\"columnFooterOfTable\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"source\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{source}\",\"textWidth\":200,\"textHeight\":50}]},\"columnHeaderOfTable\":{\"enabled\":false},\"columnFooterOfTable\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false}},{\"width\":300,\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"destination\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{destination}\",\"textWidth\":200,\"textHeight\":50}]},\"columnHeaderOfTable\":{\"enabled\":false},\"columnFooterOfTable\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false}},{\"width\":300,\"tableGroupFooters\":[{\"name\":\"source\",\"styleNameReference\":\"TABLE_CH\",\"height\":45,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$V{Total_Travel_Cost}\",\"textWidth\":180,\"textHeight\":30,\"isBold\":true}]}],\"tableHeader\":{\"styleNameReference\":\"TABLE_CH\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"travel_cost\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"styleNameReference\":\"TABLE_TD\",\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{travel_cost}\",\"textWidth\":200,\"textHeight\":50}]},\"columnHeaderOfTable\":{\"enabled\":false},\"columnFooterOfTable\":{\"enabled\":false},\"tableFooter\":{\"enabled\":false}}]}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"}],\"parameters\":[],\"groups\":[{\"name\":\"mode_of_payment\",\"expression\":\"$F{mode_of_payment}\"},{\"name\":\"source\",\"expression\":\"$F{source}\"}],\"variables\":[{\"name\":\"Total_Travel_Cost\",\"className\":\"java.lang.Integer\",\"resetType\":\"Group\",\"resetGroup\":\"source\",\"calculation\":\"Sum\",\"expression\":\"$F{travel_cost}\"}]}],\"query\":\"select * from \\\"HIUSER\\\".\\\"travel_details\\\" order by \\\"mode_of_payment\\\", \\\"source\\\"\"},\"type\":\"pdf\",\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":true,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() > 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
	}
	
	//@Test
	public void hcr_a7_adv_table_emptyTableTest() throws Exception {
		String formData = "{\"format\":\"html\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"dataSets\":[{\"name\":\"Query1\",\"isMainDataset\":false,\"dataSetExpression\":\"$P{REPORT_CONNECTION}\",\"connectionDetails\":{},\"fields\":[],\"parameters\":[]}],\"summary\":{\"bandHeight\":125,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"Query1\",\"dataSetExpression\":\"$P{REPORT_CONNECTION}\"},\"columns\":[],\"printRepeatedValues\":true,\"componentElementProperties\":{\"X\":94.18639999999999,\"Y\":0,\"width\":300,\"height\":125}}],\"crosstab\":[],\"chart\":[]}},\"type\":\"html\",\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}\r\n";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertEquals(1,reportPageInfo.get("totalPageCount").getAsInt());
	}

}