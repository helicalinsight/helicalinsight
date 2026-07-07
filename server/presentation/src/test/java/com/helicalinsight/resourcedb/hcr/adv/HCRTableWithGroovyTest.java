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
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
import net.sf.json.JSONObject;
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRTableWithGroovyTest {
	
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	
	private static String dbName = "";
	private static String jdbcUrl = "";

	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
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
	public void a1_create_a_folder_to_save_datasoure() throws Exception {
		integrationTestUtility.createFolder("HCRGroovyDatasource");
	}

	@Test
	public void a2_testDataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"HCRGroovyDatasource\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}
	
	static String firstDSId = "";

	
	@Test
	public void a3_createDatasource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"hiuser\\\");\\n        responseJson.put(\\\"pass\\\", \\\"hiuser\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"GroovyDataSource\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"HCRGroovyDatasource\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		firstDSId = responseObject.getString("dataSourceId");
	}
	
	@Test
	public void a4_save_datasource_state() throws Exception {

		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\""+firstDSId+"\",\"type\":\"sql.jdbc.groovy\",\"connDetails\":{\"efwdId\":\""+firstDSId+"\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\""+firstDSId+"\",\"type\":\"sql.groovy\",\"query\":\"import com.helicalinsight.efw.utility.GroovyUsersSession;\\r\\npublic String evalCondition() {\\r\\n \\r\\nString userName = GroovyUsersSession.getValue('${user}.name');\\r\\nuserName = userName.replaceAll(\\\"'\\\",\\\"\\\");\\r\\n \\r\\nString responseJson\\r\\n \\r\\nString selectClause = \\\"\\\"\\\"select (\\\"destination\\\") as \\\"destination\\\",\\\"travel_cost\\\" as \\\"travel_cost\\\"\\r\\nfrom \\\"travel_details\\\" \\\"\\\"\\\";\\r\\n \\r\\nif(userName.equals(\\\"hiadmin\\\"))\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Ambala')\\\"\\\"\\\"\\r\\nelse\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Paris')\\\"\\\"\\\"\\r\\n \\r\\nresponseJson = selectClause+ \\\"\\\" +whereClause;\\r\\n \\r\\nreturn responseJson;\\r\\n}\",\"parameters\":[]}}]}}";
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

	

	//@Test
	public void hcr_a2_adv_generateTable_with_resultset() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"CannedReport_1_groovy_plain\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[{\"name\":\"Query1\",\"className\":\"net.sf.jasperreports.engine.JRDataSource\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"applyCustomSettings\":true,\"customSettings\":{\"export.xls.exclude.origin.keep.first.band.1\":\"pageHeader\",\"export.xls.exclude.origin.band.1\":\"title\",\"export.xls.exclude.origin.band.2\":\"pageFooter\",\"export.xls.exclude.origin.keep.first.band.2\":\"columnHeader\",\"export.xls.exclude.origin.keep.first.band.3\":\"columnHeader\",\"export.xls.remove.empty.space.between.rows\":\"true\",\"export.xls.remove.empty.space.between.columns\":\"true\",\"export.xlsx.exclude.origin.keep.first.band.1\":\"pageHeader\",\"export.xlsx.exclude.origin.band.1\":\"title\",\"export.xlsx.exclude.origin.band.2\":\"pageFooter\",\"export.xlsx.exclude.origin.keep.first.band.2\":\"columnHeader\",\"export.xlsx.exclude.origin.keep.first.band.3\":\"columnHeader\",\"export.xlsx.remove.empty.space.between.rows\":\"true\",\"export.xlsx.remove.empty.space.between.columns\":\"true\"},\"dataSets\":[{\"name\":\"Query1\",\"isMainDataset\":false,\"dataSetExpression\":\"$P{Query1}\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"}],\"parameters\":[]}],\"summary\":{\"bandHeight\":240,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"Query1\",\"dataSetExpression\":\"$P{Query1}\"},\"columns\":[{\"tableHeader\":{\"enabled\":true,\"height\":25},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"destination\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-6307b9ec-7ccc-46e7-8aa2-6f43c843889e\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{destination}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-16715e07-8769-4df0-ab04-60842e7a8d1b\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true,\"height\":25},\"tableFooter\":{\"enabled\":true,\"height\":25},\"width\":100},{\"tableHeader\":{\"enabled\":true,\"height\":25},\"columnHeaderOfTable\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_cost\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-657a768a-2dfb-42bd-9120-5d0c53904a70\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_cost}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-8c0964c3-86c5-43ae-942a-82b33e001e43\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true,\"height\":25},\"tableFooter\":{\"enabled\":true,\"height\":25},\"width\":100}],\"componentElementProperties\":{\"X\":8.186399999999708,\"Y\":0,\"width\":510,\"height\":240,\"printRepeatedValues\":true,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}},\"padding\":{\"bottomPadding\":1,\"topPadding\":1,\"leftPadding\":1,\"rightPadding\":1}}}],\"crosstab\":[],\"chart\":[]}},\"type\":\"pdf\",\"isExport\":true,\"generateXML\":true,\"isPreview\":true,\"reportName\":\"CannedReport_1_groovy_plain\"}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() >= 1);
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
				assertEquals("CannedReport_1_groovy_plain", jasperDesign.getName());
				
				Map<String,JRDataset> dataSetMap =  jasperDesign.getDatasetMap();
				assertTrue(!dataSetMap.isEmpty());
				assertTrue(dataSetMap.containsKey("Query1"));
		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
		});
		
		CompletableFuture<Void> pdfTask = CompletableFuture.runAsync(() -> {
			try (InputStream pdfFileStream = Files.newInputStream(Path.of(filePath + ".pdf"));
					PdfReader pdfReader = new PdfReader(pdfFileStream)) {
				
				assertNotNull("Pdf file stream should not be null",pdfFileStream);
				assertNotNull("pdfReader should not be null", pdfReader);
				assertTrue(pdfReader.getNumberOfPages() >= 1 );	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		});

		CompletableFuture.allOf(xmlTask, pdfTask).join();

	}
}