package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import com.lowagie.text.pdf.PdfReader;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRSubReportTest {
	
	
	
private  MockMvc efwMock;
	
	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}
	
	static String TESTURL="";
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");

		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");

		}
	}

	static String tempEfwdUUID = "";
	
	@Test
	public void hcr_subreport_a1_save_datasource_state() throws Exception {
		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"parameters\":[]}}]}}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JsonObject jsonObject = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
		tempEfwdUUID = jsonObject.getAsJsonObject("response").get("temp_uuid").getAsString();
		Assert.assertNotEquals(tempEfwdUUID, "");
	}
	
	static String xmlFilePath = "";
	
	
	static Integer crosstabResourceId = null;
	
	@Test
	public void hcr_subreport_a2_adv_saveCrosstab() throws Exception {
		integrationTestUtility.createFolder("HCR_CROSSTAB");		
		String formData = "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":[{\"id\":1,\"name\":\"Query1\",\"config\":\"select  * from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"connectionDetails\":{\"id\":\"1\",\"baseType\":\"global.jdbc\",\"dataSourceType\":\"Managed DataSource\",\"name\":\"SampleTravelDataDerby\",\"type\":\"dynamicDataSource\"},\"executeQueryData\":{\"data\":[{\"travel_id\":1,\"travel_date\":\"2015-09-04 08:22:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":8,\"source\":\"Bangalore\",\"destination_id\":16,\"destination\":\"Chennai\",\"travel_cost\":1350,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":29},{\"travel_id\":2,\"travel_date\":\"2015-09-25 10:22:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":57,\"source\":\"New Delhi\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":1200,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":3,\"travel_date\":\"2015-09-25 14:13:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":57,\"source\":\"New Delhi\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":1200,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":4,\"travel_date\":\"2015-10-20 11:33:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Cab\",\"source_id\":16,\"source\":\"Chennai\",\"destination_id\":19,\"destination\":\"Coimbatore\",\"travel_cost\":800,\"mode_of_payment\":\"Cash\",\"booking_platform\":\"Agent\",\"travelled_by\":11},{\"travel_id\":5,\"travel_date\":\"2015-08-12 13:19:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Misc\",\"source_id\":65,\"source\":\"Pune\",\"destination_id\":29,\"destination\":\"Hyderabad\",\"travel_cost\":1200,\"mode_of_payment\":\"Cash\",\"booking_platform\":\"Agent\",\"travelled_by\":14},{\"travel_id\":6,\"travel_date\":\"2015-10-09 13:18:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Train\",\"source_id\":55,\"source\":\"Nagpur\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":2400,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":23},{\"travel_id\":7,\"travel_date\":\"2015-05-21 10:23:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Flight\",\"source_id\":11,\"source\":\"Bhubaneshwar\",\"destination_id\":33,\"destination\":\"Jammu\",\"travel_cost\":10000,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":8,\"travel_date\":\"2015-07-08 13:29:00.0\",\"travel_type\":\"International\",\"travel_medium\":\"Flight\",\"source_id\":16,\"source\":\"Chennai\",\"destination_id\":63,\"destination\":\"Philippines\",\"travel_cost\":42000,\"mode_of_payment\":\"Net Banking\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":45},{\"travel_id\":9,\"travel_date\":\"2015-11-10 13:29:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Cab\",\"source_id\":8,\"source\":\"Bangalore\",\"destination_id\":54,\"destination\":\"Mysore\",\"travel_cost\":800,\"mode_of_payment\":\"Net Banking\",\"booking_platform\":\"Website\",\"travelled_by\":41},{\"travel_id\":10,\"travel_date\":\"2015-12-24 10:23:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Misc\",\"source_id\":15,\"source\":\"Chandigarh\",\"destination_id\":25,\"destination\":\"Gurgaon\",\"travel_cost\":1200,\"mode_of_payment\":\"Cheque\",\"booking_platform\":\"Agent\",\"travelled_by\":33}],\"field\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}]},\"parameterList\":[],\"temp_uuid\":\""+tempEfwdUUID+"\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"39a13b5e-4ca5-45ca-b559-5730ddc3fbef\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"mode_of_payment\",\"width\":100,\"height\":30,\"label\":\"$F{mode_of_payment}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-97bbfcab-aa9f-4be9-b060-315883567f1b\",\"x\":60,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null},{\"name\":\"travel_medium\",\"width\":100,\"height\":30,\"label\":\"$F{travel_medium}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-7c808e29-c783-43b3-9c63-46581f7eddca\",\"x\":201.6864,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null}]},\"name\":\"hcr_crosstab_compile\",\"dir\":\"HCR_CROSSTAB\",\"previewFormData\":{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":\"1\",\"dataSets\":[{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"dataSetExpression\":\"$P{MAIN_DATA}\",\"fields\":[{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_id\"},{\"clazz\":\"java.sql.Timestamp\",\"name\":\"travel_date\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_type\"},{\"clazz\":\"java.lang.String\",\"name\":\"travel_medium\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"source_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"source\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"destination_id\"},{\"clazz\":\"java.lang.String\",\"name\":\"destination\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travel_cost\"},{\"clazz\":\"java.lang.String\",\"name\":\"mode_of_payment\"},{\"clazz\":\"java.lang.String\",\"name\":\"booking_platform\"},{\"clazz\":\"java.lang.Integer\",\"name\":\"travelled_by\"}],\"isMainDataset\":false,\"name\":\"MainDataset\",\"parameters\":[]}],\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":\"1000\",\"pageWidth\":\"2500\",\"parameters\":[{\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"name\":\"MAIN_DATA\"}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":\"950\",\"break\":[],\"crosstab\":[{\"columnGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{mode_of_payment}\"},\"crosstabColumnHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{mode_of_payment}\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"crosstabTotalColumnHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total mode_of_payment\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"height\":\"80\",\"name\":\"mode_of_payment\",\"totalPosition\":\"END\"}],\"componentElementProperties\":{\"X\":\"0\",\"Y\":\"0\",\"backColor\":\"#ffffff\",\"columnBreakOffset\":\"0\",\"foreColor\":\"#000000\",\"height\":\"950\",\"horizontalPosition\":\"LEFT\",\"ignoreWidth\":false,\"mode\":\"Transparent\",\"printRepeatedValues\":false,\"repeatColumnHeaders\":false,\"repeatRowHeaders\":false,\"runDirection\":\"LTR\",\"stretchType\":\"NoStretch\",\"width\":\"2400\"},\"crosstabCells\":[{\"height\":\"80\",\"styleNameReference\":\"TABLE_TD\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"250\"},{\"columnTotalGroup\":\"mode_of_payment\",\"height\":\"80\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"250\"},{\"height\":\"80\",\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"250\"},{\"columnTotalGroup\":\"mode_of_payment\",\"height\":\"80\",\"rowTotalGroup\":\"travel_medium\",\"styleNameReference\":\"TABLE_CT\",\"textField\":[{\"textFieldExpression\":\"$V{travel_cost_MEASURE}\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}],\"width\":\"250\"}],\"dataSetRun\":{\"dataSetExpression\":\"$P{MAIN_DATA}\",\"dataSetName\":\"MainDataset\"},\"measures\":[{\"calculation\":\"Sum\",\"className\":\"java.lang.Integer\",\"measureExpression\":\"$F{travel_cost}\",\"name\":\"travel_cost_MEASURE\"}],\"rowGroups\":[{\"bucket\":{\"className\":\"java.lang.String\",\"expression\":\"$F{travel_medium}\"},\"crosstabRowHeader\":{\"styleNameReference\":\"TABLE_TH\",\"textField\":{\"textFieldExpression\":\"$V{travel_medium}\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"crosstabTotalRowHeader\":{\"styleNameReference\":\"TABLE_CT\",\"textField\":{\"textFieldExpression\":\"\\\"Total travel_medium\\\"\",\"textForecolor\":\"#000000\",\"textHeight\":\"60\",\"textWidth\":\"200\"}},\"name\":\"travel_medium\",\"totalPosition\":\"END\",\"width\":\"500\"}]}],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[]},\"variables\":[]},\"format\":\"html\",\"generateXML\":false,\"isExport\":false,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"html\"},\"saveUUID\":\"6e4d7e24-8b80-4c29-a89a-4f3a821b177a\"}";
		String response = integrationTestUtility.saveHCRReportState(formData);
		JsonObject rawNode = JsonParser.parseString(response).getAsJsonObject();
		JsonObject responseNode = rawNode.getAsJsonObject("response");
		JsonObject dataNode = responseNode.getAsJsonArray("data").get(0).getAsJsonObject();
		crosstabResourceId = dataNode.get("resourceId").getAsInt();
		Assert.assertEquals("hcr_crosstab_compile.hcr", responseNode.get("uuid").getAsString());
		Assert.assertEquals("Report saved successfully", responseNode.get("message").getAsString());
	}
	
	
	static String tableDatasourceId = "";
	
	@Test
	public void hcr_subreport_a3_save_datasource_state_for_table() throws Exception {

		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"dimdate\\\" fetch first 10 rows only\",\"parameters\":[]}}]}}";
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		Assert.assertEquals(200, result.getResponse().getStatus());
		JsonObject jsonObject = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
		tableDatasourceId = jsonObject.getAsJsonObject("response").get("temp_uuid").getAsString();
		Assert.assertNotEquals(tableDatasourceId, "");
	}

	
	
	static Integer tableHcrId = null;

	@Test
	public void hcr_subreport_a4_adv_saveTable() throws Exception {
		String formData2 = "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":[{\"id\":1,\"name\":\"Query1\",\"config\":\"select  * from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"connectionDetails\":{\"id\":\"1\",\"baseType\":\"global.jdbc\",\"dataSourceType\":\"Managed DataSource\",\"name\":\"SampleTravelDataDerby\",\"type\":\"dynamicDataSource\"},\"executeQueryData\":{\"data\":[{\"travel_id\":1,\"travel_date\":\"2015-09-04 08:22:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":8,\"source\":\"Bangalore\",\"destination_id\":16,\"destination\":\"Chennai\",\"travel_cost\":1350,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":29},{\"travel_id\":2,\"travel_date\":\"2015-09-25 10:22:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":57,\"source\":\"New Delhi\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":1200,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":3,\"travel_date\":\"2015-09-25 14:13:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Bus\",\"source_id\":57,\"source\":\"New Delhi\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":1200,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":4,\"travel_date\":\"2015-10-20 11:33:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Cab\",\"source_id\":16,\"source\":\"Chennai\",\"destination_id\":19,\"destination\":\"Coimbatore\",\"travel_cost\":800,\"mode_of_payment\":\"Cash\",\"booking_platform\":\"Agent\",\"travelled_by\":11},{\"travel_id\":5,\"travel_date\":\"2015-08-12 13:19:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Misc\",\"source_id\":65,\"source\":\"Pune\",\"destination_id\":29,\"destination\":\"Hyderabad\",\"travel_cost\":1200,\"mode_of_payment\":\"Cash\",\"booking_platform\":\"Agent\",\"travelled_by\":14},{\"travel_id\":6,\"travel_date\":\"2015-10-09 13:18:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Train\",\"source_id\":55,\"source\":\"Nagpur\",\"destination_id\":31,\"destination\":\"Jaipur\",\"travel_cost\":2400,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":23},{\"travel_id\":7,\"travel_date\":\"2015-05-21 10:23:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Flight\",\"source_id\":11,\"source\":\"Bhubaneshwar\",\"destination_id\":33,\"destination\":\"Jammu\",\"travel_cost\":10000,\"mode_of_payment\":\"Credit\",\"booking_platform\":\"Website\",\"travelled_by\":11},{\"travel_id\":8,\"travel_date\":\"2015-07-08 13:29:00.0\",\"travel_type\":\"International\",\"travel_medium\":\"Flight\",\"source_id\":16,\"source\":\"Chennai\",\"destination_id\":63,\"destination\":\"Philippines\",\"travel_cost\":42000,\"mode_of_payment\":\"Net Banking\",\"booking_platform\":\"Makemytrip\",\"travelled_by\":45},{\"travel_id\":9,\"travel_date\":\"2015-11-10 13:29:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Cab\",\"source_id\":8,\"source\":\"Bangalore\",\"destination_id\":54,\"destination\":\"Mysore\",\"travel_cost\":800,\"mode_of_payment\":\"Net Banking\",\"booking_platform\":\"Website\",\"travelled_by\":41},{\"travel_id\":10,\"travel_date\":\"2015-12-24 10:23:00.0\",\"travel_type\":\"Domestic\",\"travel_medium\":\"Misc\",\"source_id\":15,\"source\":\"Chandigarh\",\"destination_id\":25,\"destination\":\"Gurgaon\",\"travel_cost\":1200,\"mode_of_payment\":\"Cheque\",\"booking_platform\":\"Agent\",\"travelled_by\":33}],\"field\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}]},\"parameterList\":[],\"temp_uuid\":\""+tableDatasourceId+"\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"39a13b5e-4ca5-45ca-b559-5730ddc3fbef\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"mode_of_payment\",\"width\":100,\"height\":30,\"label\":\"$F{mode_of_payment}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-97bbfcab-aa9f-4be9-b060-315883567f1b\",\"x\":60,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null},{\"name\":\"travel_medium\",\"width\":100,\"height\":30,\"label\":\"$F{travel_medium}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-7c808e29-c783-43b3-9c63-46581f7eddca\",\"x\":201.6864,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null}]},\"name\":\"hcr_table_compile\",\"dir\":\"HCR_CROSSTAB\",\"previewFormData\":{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tableDatasourceId+"\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyle\":[],\"parameters\":[{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tableDatasourceId+"\",\"map_id\":1}}],\"variables\":[],\"pageWidth\":1400,\"pageHeight\":1000,\"orientation\":\"Portrait\",\"columnCount\":1,\"summary\":{\"bandHeight\":500,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"crosstab\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\"},\"positionType\":\"FixRelativeTop\",\"tableWidth\":500,\"tableHeight\":250,\"mode\":\"Transparent\",\"printWhenDetailOverflows\":false,\"printInFirstWholeBand\":false,\"dataSourceExpression\":\"\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":500,\"width\":900,\"stretchType\":\"NoStretch\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"columns\":[{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"dim_id\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{dim_id}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"date_key\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{date_key}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"day_number\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{day_number}\",\"textWidth\":200,\"textHeight\":50}]}},{\"width\":300,\"tableHeader\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"\\\"fiscal_year\\\"\",\"textWidth\":200,\"textHeight\":50}]},\"columnData\":{\"height\":60,\"rowSpan\":1,\"textField\":[{\"textFieldExpression\":\"$F{fiscal_year}\",\"textWidth\":200,\"textHeight\":50}]}}]}]},\"dataSets\":[{\"name\":\"MainDataset\",\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"isMainDataset\":false,\"connectionDetails\":{\"temp_uuid\":\""+tableDatasourceId+"\",\"map_id\":1},\"fields\":[{\"name\":\"dim_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"date_key\",\"clazz\":\"java.lang.String\"},{\"name\":\"day_number\",\"clazz\":\"java.lang.String\"},{\"name\":\"fiscal_year\",\"clazz\":\"java.sql.Date\"}],\"parameters\":[]}]},\"type\":\"html\",\"isExport\":false,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"generateXML\":false,\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}},\"saveUUID\":\"6e4d7e24-8b80-4c29-a89a-4f3a821b177a\"}";
		String response = integrationTestUtility.saveHCRReportState(formData2);
		JsonObject rawNode = JsonParser.parseString(response).getAsJsonObject();
		JsonObject responseNode = rawNode.getAsJsonObject("response");
		JsonObject dataNode = responseNode.getAsJsonArray("data").get(0).getAsJsonObject();
		tableHcrId = dataNode.get("resourceId").getAsInt();
		Assert.assertEquals("hcr_table_compile.hcr", responseNode.get("uuid").getAsString());
		Assert.assertEquals("Report saved successfully", responseNode.get("message").getAsString());
	}
	
	@Test
	public void hcr_subreport_a5_adv_addSubReportsInMainReport() throws Exception {
		String formData5 = "{\"connectionDetails\":{},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1500,\"pageWidth\":4000,\"parameters\":[{\"name\":\"MAIN_DATA\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}},{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tableDatasourceId+"\",\"map_id\":1}}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":900,\"break\":[],\"crosstab\":[],\"table\":[],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[],\"chart\":[],\"subReport\":[{\"runToBottom\":true,\"usingCache\":false,\"datasourceExpression\":\"new net.sf.jasperreports.engine.JREmptyDataSource()\",\"subReportExpression\":\""+crosstabResourceId+"\",\"overFlowType\":\"STRETCH\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":700,\"width\":1200,\"stretchType\":\"NoStretch\",\"positionType\":\"FixRelativeToTop\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"parameters\":[{\"name\":\"MAIN_DATA\",\"expression\":\"$P{MAIN_DATA}\"}]},{\"runToBottom\":true,\"usingCache\":false,\"datasourceExpression\":\"new net.sf.jasperreports.engine.JREmptyDataSource()\",\"subReportExpression\":\""+tableHcrId+"\",\"overFlowType\":\"STRETCH\",\"componentElementProperties\":{\"X\":2000,\"Y\":0,\"height\":700,\"width\":1200,\"stretchType\":\"NoStretch\",\"positionType\":\"FixRelativeToTop\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"parameters\":[{\"name\":\"MAIN_DATASET\",\"expression\":\"$P{MAIN_DATASET}\"}]}]},\"variables\":[]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"}";
		String resp = integrationTestUtility.generateHCRReport(formData5);
		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = GsonUtility.optJsonObject(GsonUtility.optJsonObject(jsonObject, "response"), "reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() >= 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
		
		CompletableFuture<Void>  xmlTask = CompletableFuture.runAsync(() -> {
		try (InputStream jrxmlStream = Files.newInputStream(Path.of(filePath+".jrxml"))) {
				assertNotNull("JRXML input stream should not be null",jrxmlStream);
				JasperDesign jasperDesign =  JRXmlLoader.load(jrxmlStream);	
				assertNotNull("JasperDesign must be loaded", jasperDesign);
				assertEquals("Untitled 1", jasperDesign.getName());
				assertEquals(4000, jasperDesign.getPageWidth());
				assertEquals(1500, jasperDesign.getPageHeight());
		
				JRBand summaryBand =  jasperDesign.getSummary();;
				assertNotNull("Summary band should not be null",summaryBand);
				List<JRChild>  children =  summaryBand.getChildren();
				assertTrue(children.size() == 2);
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
	
	
	@Test
	public void hcr_subreport_a8_adv_saveSubReport() throws Exception {
		String formData2 = "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":[{\"id\":1,\"name\":\"Query1\",\"config\":\"\",\"connectionDetails\":null,\"executeQueryData\":{\"data\":[],\"field\":[]},\"parameterList\":[]}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"534562be-d35e-481e-8ce4-4dea04eccbf9\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":null},\"diagram\":{\"nodes\":[{\"label\":\"Image\",\"borders\":{},\"padding\":{},\"width\":250,\"height\":210,\"name\":\"image\",\"renderKey\":\"image\",\"parentKey\":\"elements\",\"isLeaf\":true,\"repeat\":\"rt\",\"category\":\"image\",\"zIndex\":10,\"type\":\"defaultNodes\",\"link\":\"\",\"file\":\"download.image\",\"dir\":\"Mahesh\",\"id\":\"node-a37c881f-3700-4efc-a9b5-03e0977e0bbf\",\"x\":100,\"y\":50,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":null,\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null,\"imagePath\":\"Mahesh/download.image\",\"imageResourceId\":1214},{\"label\":\"Image\",\"borders\":{},\"padding\":{},\"width\":250,\"height\":210,\"name\":\"image\",\"renderKey\":\"image\",\"parentKey\":\"elements\",\"isLeaf\":true,\"repeat\":\"rt\",\"category\":\"image\",\"zIndex\":10,\"type\":\"defaultNodes\",\"link\":\"\",\"file\":\"download.image\",\"dir\":\"Mahesh\",\"x\":100,\"y\":246,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":null,\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null,\"imagePath\":\"Mahesh/download.image\",\"imageResourceId\":1214,\"groupChildren\":null,\"groupCollapsedSize\":null,\"originalId\":\"node-a37c881f-3700-4efc-a9b5-03e0977e0bbf\",\"isCollapsed\":false,\"id\":\"node-787c32d9-a9ce-4af0-9c76-7feab96be9a6\"},{\"label\":\"Image\",\"borders\":{},\"padding\":{},\"width\":350,\"height\":180,\"name\":\"image\",\"renderKey\":\"image\",\"parentKey\":\"elements\",\"isLeaf\":true,\"repeat\":\"rt\",\"category\":\"image\",\"zIndex\":10,\"type\":\"defaultNodes\",\"link\":\"\",\"file\":\"headerTravel.image\",\"dir\":\"5_221942GA\",\"id\":\"node-fcb012ad-72a9-4304-a1b0-911742120688\",\"x\":69.99999999999986,\"y\":456.00000000000045,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"imagePath\":\"5_221942GA/headerTravel.image\",\"imageResourceId\":636,\"rt\":null,\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null}]},\"name\":\"subreport\",\"dir\":\"HCR_CROSSTAB\",\"previewFormData\":{\"connectionDetails\":{\"map_id\":\"1\",\"temp_uuid\":\""+tempEfwdUUID+"\"},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"designerProperties\":{\"columnCount\":1,\"designerStyle\":[],\"fields\":[],\"groups\":[],\"orientation\":\"Portrait\",\"pageHeight\":1500,\"pageWidth\":3000,\"parameters\":[{\"name\":\"MAIN_DATA\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}},{\"name\":\"MAIN_DATASET\",\"className\":\"java.sql.Connection\",\"connectionDetails\":{\"temp_uuid\":\""+tableDatasourceId+"\",\"map_id\":1}}],\"reportName\":\"Untitled 1\",\"summary\":{\"bandHeight\":900,\"break\":[],\"crosstab\":[],\"table\":[],\"image\":[],\"isImageAttached\":false,\"lines\":[],\"staticText\":[],\"textField\":[],\"chart\":[],\"subReport\":[{\"runToBottom\":true,\"usingCache\":false,\"dataSetExpression\":\"$P{MAIN_DATA}\",\"subReportExpression\":\"2\",\"overFlowType\":\"STRETCH\",\"componentElementProperties\":{\"X\":0,\"Y\":0,\"height\":700,\"width\":1200,\"stretchType\":\"NoStretch\",\"positionType\":\"FixRelativeToTop\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"parameters\":[{\"name\":\"MAIN_DATA\",\"expression\":\"$P{MAIN_DATA}\"}]},{\"runToBottom\":true,\"usingCache\":false,\"dataSetExpression\":\"$P{MAIN_DATASET}\",\"subReportExpression\":\"3\",\"overFlowType\":\"STRETCH\",\"componentElementProperties\":{\"X\":1500,\"Y\":0,\"height\":700,\"width\":1200,\"stretchType\":\"NoStretch\",\"positionType\":\"FixRelativeToTop\",\"mode\":\"Transparent\",\"foreColor\":\"#000000\",\"backColor\":\"#ffffff\"},\"parameters\":[{\"name\":\"MAIN_DATASET\",\"expression\":\"$P{MAIN_DATASET}\"}]}]},\"variables\":[]},\"format\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"page\":\"0\",\"reportName\":\"Untitled 1\",\"type\":\"pdf\"},\"uuid\":\"subreport.hcr\"}";
		String response = integrationTestUtility.saveHCRReportState(formData2);
		JsonObject rawNode = GsonUtility.parseString(response, JsonObject.class);
		JsonObject responseNode = GsonUtility.optJsonObject(rawNode, "response");
		Assert.assertEquals("Report saved successfully", GsonUtility.optString(responseNode, "message"));
	}

	static String fileName = "";
	
	@Test
	public void hcr_subreport_a9_exportSubReport() throws Exception {
		String formData = "{\"dir\": \"HCR_CROSSTAB\",\"file\" : \"subreport.hcr\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = integrationTestUtility.exportResource(formData, TESTURL);
	}


}
