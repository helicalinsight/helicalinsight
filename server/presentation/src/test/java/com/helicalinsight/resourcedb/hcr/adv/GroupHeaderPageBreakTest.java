package com.helicalinsight.resourcedb.hcr.adv;

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
public class GroupHeaderPageBreakTest {
	
	
	private  MockMvc efwMock;
	
	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	
	
	@Autowired
	private ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
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
	public void hcr_pageBreak_a1_save_datasource_state() throws Exception {
		String formData = "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"1\",\"type\":\"global.jdbc\",\"connDetails\":{\"globalId\":\"1\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"1\",\"type\":\"sql\",\"query\":\"select * from \\\"HIUSER\\\".\\\"travel_details\\\" order by \\\"travel_medium\\\" \",\"parameters\":[]}}]}}";
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
	public void hcr_pageBreak_a2_adv_generate() throws Exception {
		String formData = "{\"format\":\"html\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\""+tempEfwdUUID+"\",\"map_id\":1},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[{\"name\":\"group_travel_medium\",\"groupNumber\":1,\"expression\":\"$F{travel_medium}\",\"groupHeader\":{\"bandHeight\":30,\"textField\":[],\"lines\":[],\"image\":[],\"break\":[{\"X\":90,\"Y\":0,\"shapeId\":\"node-4922b457-7e57-44b0-bfd8-223f61f91e89\",\"breakHeight\":30,\"breakWidth\":100}],\"splitType\":\"Stretch\"},\"groupFooter\":{\"bandHeight\":0,\"textField\":[],\"lines\":[],\"image\":[],\"break\":[],\"splitType\":\"Stretch\"}}],\"fields\":[{\"name\":\"travel_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"travel_date\",\"clazz\":\"java.sql.Timestamp\"},{\"name\":\"travel_type\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_medium\",\"clazz\":\"java.lang.String\"},{\"name\":\"source_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"source\",\"clazz\":\"java.lang.String\"},{\"name\":\"destination_id\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"},{\"name\":\"mode_of_payment\",\"clazz\":\"java.lang.String\"},{\"name\":\"booking_platform\",\"clazz\":\"java.lang.String\"},{\"name\":\"travelled_by\",\"clazz\":\"java.lang.Integer\"}],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"pageHeader\":{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"$F{travel_medium}\",\"X\":90,\"Y\":0,\"shapeId\":\"node-0bfa73b7-e39a-499b-a6ef-3e8437aa37ad\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true}],\"image\":[],\"lines\":[],\"break\":[]},\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"$F{mode_of_payment}\",\"X\":90,\"Y\":0,\"shapeId\":\"node-15fa3e09-30fe-4e85-9ad7-51c5886c5869\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true},{\"textFieldExpression\":\"$F{travel_cost}\",\"X\":206.6864,\"Y\":0,\"shapeId\":\"node-abc373cd-3ea9-4ed5-afd9-d3ef0f9e0110\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true}],\"image\":[],\"lines\":[],\"break\":[]}]},\"type\":\"html\",\"isPreview\":true,\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp, JsonObject.class);
		int status = GsonUtility.optInt(jsonObject, "status");
		Assert.assertEquals(1, status);
		JsonObject reportPageInfo = jsonObject.getAsJsonObject("response").getAsJsonObject("reportPageInfo");
		
		// Note: Tested this manually and hard-coded the expected value
		Assert.assertEquals(43,reportPageInfo.get("totalPageCount").getAsInt());
	}

}
