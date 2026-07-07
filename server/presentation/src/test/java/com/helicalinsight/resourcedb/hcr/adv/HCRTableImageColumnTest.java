package com.helicalinsight.resourcedb.hcr.adv;


import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRTableImageColumnTest {
	
	
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
		JsonObject jsonObject = GsonUtility.parseString(result.getResponse().getContentAsString(), JsonObject.class);
		tempEfwdUUID = jsonObject.getAsJsonObject("response").get("temp_uuid").getAsString();
		Assert.assertNotEquals(tempEfwdUUID, "");
	}

	

	@Test
	public void hcr_a2_adv_generateTable() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[{\"name\":\"Query1\",\"className\":\"net.sf.jasperreports.engine.JRDataSource\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1}}],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"applyCustomSettings\":true,\"customSettings\":{\"export.xls.exclude.origin.keep.first.band.1\":\"pageHeader\",\"export.xls.exclude.origin.band.1\":\"title\",\"export.xls.exclude.origin.band.2\":\"pageFooter\",\"export.xls.exclude.origin.keep.first.band.2\":\"columnHeader\",\"export.xls.exclude.origin.keep.first.band.3\":\"columnHeader\",\"export.xls.remove.empty.space.between.rows\":\"true\",\"export.xls.remove.empty.space.between.columns\":\"true\",\"export.xlsx.exclude.origin.keep.first.band.1\":\"pageHeader\",\"export.xlsx.exclude.origin.band.1\":\"title\",\"export.xlsx.exclude.origin.band.2\":\"pageFooter\",\"export.xlsx.exclude.origin.keep.first.band.2\":\"columnHeader\",\"export.xlsx.exclude.origin.keep.first.band.3\":\"columnHeader\",\"export.xlsx.remove.empty.space.between.rows\":\"true\",\"export.xlsx.remove.empty.space.between.columns\":\"true\"},\"dataSets\":[{\"name\":\"Query1\",\"isMainDataset\":false,\"dataSetExpression\":\"$P{Query1}\",\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"fields\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}],\"parameters\":[]}],\"title\":{\"bandHeight\":310,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[],\"lines\":[],\"break\":[],\"table\":[{\"dataSetRun\":{\"dataSetName\":\"Query1\",\"dataSetExpression\":\"$P{Query1}\"},\"columns\":[{\"width\":308,\"tableHeader\":{\"enabled\":true,\"height\":25},\"columnHeaderOfTable\":{\"height\":175,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"Travel_id\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-34678000-f904-4e67-810a-6ae5610abb90\",\"textHeight\":175,\"textWidth\":308,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true}]},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_id}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-813c7c26-0c5d-4243-9053-7e92c692454c\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true,\"height\":25},\"tableFooter\":{\"enabled\":true,\"height\":25},\"tableGroupHeaders\":[],\"tableGroupFooters\":[]},{\"width\":350,\"tableHeader\":{\"enabled\":true,\"height\":25},\"columnHeaderOfTable\":{\"height\":175,\"rowSpan\":1,\"enabled\":true,\"image\":[{\"scaleImage\":\"FillFrame\",\"dir\":\"\",\"file\":\"\",\"link\":\"https://media.wired.com/photos/5926ffe47034dc5f91bed4e8/3:2/w_2560%2Cc_limit/google-logo.jpg\",\"X\":69.99999999999997,\"Y\":0,\"imageHeight\":121,\"imageWidth\":240}]},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_date}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-9044cbc9-351b-4272-a0ee-1bd3e2932f11\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true,\"height\":25},\"tableFooter\":{\"enabled\":true,\"height\":25},\"tableGroupHeaders\":[],\"tableGroupFooters\":[]},{\"width\":100,\"tableHeader\":{\"enabled\":true,\"height\":25},\"columnHeaderOfTable\":{\"height\":175,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"\\\"travel_type\\\"\",\"X\":0,\"Y\":0,\"shapeId\":\"node-7f3c7e18-bd9a-43a7-8344-3a304ef1c4ac\",\"textHeight\":175,\"textWidth\":100,\"textFontSize\":10}]},\"columnData\":{\"height\":25,\"rowSpan\":1,\"enabled\":true,\"textField\":[{\"textFieldExpression\":\"$F{travel_type}\",\"X\":0,\"Y\":0,\"shapeId\":\"node-abcfb2cf-fdf3-465d-bbbb-759ea0414c36\",\"textHeight\":25,\"textWidth\":100,\"textFontSize\":10}]},\"columnFooterOfTable\":{\"enabled\":true,\"height\":25},\"tableFooter\":{\"enabled\":true,\"height\":25},\"tableGroupHeaders\":[],\"tableGroupFooters\":[]}],\"componentElementProperties\":{\"X\":61.18639999999971,\"Y\":0,\"width\":480,\"height\":310,\"printRepeatedValues\":true,\"border\":{\"line\":{\"leftLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"rightLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"bottomLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"},\"topLine\":{\"lineWidth\":0,\"lineColor\":\"#000000\",\"lineStyle\":\"SOLID\"}}},\"padding\":{\"bottomPadding\":1,\"topPadding\":1,\"leftPadding\":1,\"rightPadding\":1}}}],\"crosstab\":[],\"chart\":[]}},\"type\":\"pdf\",\"generateXML\":true,\"isExport\":true,\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		Assert.assertTrue(reportPageInfo.get("totalPageCount").getAsInt() > 1);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String uuid = jsonObject.getAsJsonObject("response").getAsJsonObject("jrxmlData").get("uuid").getAsString();
		String filePath = String.join(File.separator, TempDirectoryCleaner.getTempDirectory().getAbsolutePath(),uuid);
		Assert.assertTrue(Path.of(filePath+".pdf").toFile().exists());
		Assert.assertTrue(Path.of(filePath+".jrxml").toFile().exists());
	}
}