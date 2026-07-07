package com.helicalinsight.ccp.copypaste;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataSecurity;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.ccp.CCPTestUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataSecurityCopyPasteTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	
	@Autowired
	FilterChainProxy filterChainProxy;
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility testUtility;
	
	@Autowired
	private CCPTestUtility ccpTestUtility;
	
	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	private HIResourceServiceDB serviceDb;
	
	@Autowired
	private HIMetadataResourceServiceDB hiMetadataResourceServiceDB;

	private static String view_id_1="",securityId1="",securityId2="",securityId3="";
	private static final String FOLDER1="MetadataSecurityCopyPasteTest";
	private static final String FOLDER2="MetadataSecurityCopyPasteTest2";
	
	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}
	
	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}
	
	@Test
	public void copypaste_a1_init() throws Exception {
		ccpTestUtility.init();
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
	}
	
	@Test
	public void copypaste_a2_createView1() throws Exception {
		String formData ="{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select *from \\\"HIUSER\\\".\\\"employee_details\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"employee_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"employee_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"age\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"address\",\"type\":\"text\",\"checked\":true}]}";
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "saveView");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		String res=result.getResponse().getContentAsString();
		JSONObject obj=JSONObject.fromObject(res);
		view_id_1=obj.getJSONObject("response").getString("viewId");
	}
	
	@Test
	public void copypaste_a3_createSecurityMapForDupDimdate() throws Exception {
		String formData="{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide dimdate duplicate\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"emvj-9xtl-t918-c2dr-xc_qddpw\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
		String res=createSecurityMap(formData);
		JSONObject jsonObject=JSONObject.fromObject(res);
		securityId1=jsonObject.getJSONObject("response").getString("expressionId");
	}
	
	@Test
	public void copypaste_a4_createSecurityMapOriginalDimdate() throws Exception {
		String formData="{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide dimdate duplicate\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"emvj-9xtl-t918-c2dr-xc_qddpw\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}\r\n"
				+ "";
		String res=createSecurityMap(formData);
		JSONObject jsonObject=JSONObject.fromObject(res);
		securityId2=jsonObject.getJSONObject("response").getString("expressionId");
	}
	
	@Test
	public void copypaste_a5_createSecurityMapForEmpView() throws Exception {
		String formData="{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide address of view1\",\"expressionType\":\"column\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"b2a519f9-b078-41f4-9eb9-e8fbad2e62ec_qddpw\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
		String res=createSecurityMap(formData);
		JSONObject jsonObject=JSONObject.fromObject(res);
		securityId3=jsonObject.getJSONObject("response").getString("expressionId");
	}
	
	@Test
	public void copypaste_a6_createMetadata() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		String formData ="{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"emvj-9xtl-t918-c2dr-xc\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"qddpw\",\"originalId\":\"22478325-7dab-466f-8b2f-fca1b1c6d828\",\"id\":\"u03p-ndic-5ku6-mj0j-n8\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"qddpw\",\"originalId\":\"6ce0c6d1-0a93-4876-8e7a-281ad6f4e870\",\"id\":\"186h-sl75-irmi-nwxs-13\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"qddpw\",\"originalId\":\"62bcdc31-65c7-4c58-96a2-0e00acfc62ce\",\"id\":\"v8uh-eaff-sz4k-lgzo-kx\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"qddpw\",\"originalId\":\"cadd79a7-957b-4605-b494-209d2b32e86d\",\"id\":\"18nq-1dte-23yj-sj3y-dy\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"qddpw\",\"originalId\":\"79f0703d-681b-44b8-b0d7-066dbfd4c583\",\"id\":\"hrvc-c6ld-e9dx-7piu-b6\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"qddpw\",\"originalId\":\"94434ec2-a86d-48c8-901b-5c02c133d1f8\",\"id\":\"pu8f-wou1-o0yy-newx-h3\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"qddpw\",\"originalId\":\"6dad02ee-e8d9-499d-9e2f-2ceaeccbaa0d\",\"id\":\"6sqx-ldm9-i47h-ypd4-gb\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"qddpw\",\"originalId\":\"6aff6317-3178-4100-9983-929f9c7d367c\",\"id\":\"dhxq-lbz1-efek-w132-wu\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"qddpw\",\"originalId\":\"d0e8f0d8-ab32-47f4-89ad-d1bd20bf9dd6\",\"id\":\"tiw0-z93b-ijoy-rh2o-01\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"qddpw\",\"originalId\":\"9d7280d1-040f-4a10-9ee7-be2a6865e137\",\"id\":\"1l6k-id75-9ldp-joth-ol\",\"name\":\"rating\"}],\"connId\":\"qddpw\",\"originalName\":\"dimdate\",\"originalId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"name\":\"dimdate_1\"}],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"qddpw\",\"dbId\":\"qddpw\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[\""+view_id_1+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"access\":{\"expression\":[{\"expressionId\":\""+securityId1+"\",\"action\":\"add\"},{\"expressionId\":\""+securityId2+"\",\"action\":\"add\"},{\"expressionId\":\""+securityId3+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_1\",\"location\":\""+FOLDER1+"\",\"metadataReload\":true}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
	}
	
	@Test
	public void copypaste_a7_verifyMetadataSecurityCopy() throws Exception {
		String formData="{\"sourceUrl\":\"MetadataSecurityCopyPasteTest/Metadata_1.metadata\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":5}";
		ccpTestUtility.ccpOperation("copy",formData,ccpTestUtility.getSuccessMessage(),1);
	}
	
	@Test
	public void copypaste_a8_verifyMetadataSecurityCopy() throws Exception {
		HIResource metadata1=serviceDb.getResourceByUrl("MetadataSecurityCopyPasteTest/Metadata_1.metadata");
		HIResource metadata2=serviceDb.getResourceByUrl("MetadataSecurityCopyPasteTest2/Metadata_1.metadata");
		Assert.assertNotNull(metadata2);
		HIResourceMetadata hiResourceMetadata1=hiMetadataResourceServiceDB.giveHIResourceMetadataByResourceId(metadata1.getResourceId());
		HIResourceMetadata hiResourceMetadata2=hiMetadataResourceServiceDB.giveHIResourceMetadataByResourceId(metadata2.getResourceId());
		Assert.assertNotNull(hiResourceMetadata1);
		Assert.assertNotNull(hiResourceMetadata2);
		List<HIMetadataSecurity> metadata1Securities=hiMetadataResourceServiceDB.getMetaSecurity(hiResourceMetadata1.getId());
		List<HIMetadataSecurity> metadata2Securities=hiMetadataResourceServiceDB.getMetaSecurity(hiResourceMetadata2.getId());
		Assert.assertNotNull(metadata1Securities);
		Assert.assertNotNull(metadata2Securities);
		Assert.assertTrue(metadata1Securities.size()==metadata2Securities.size());
		for(int i=0;i<metadata1Securities.size();i++) {
			HIMetadataSecurity s1=metadata1Securities.get(i);
			HIMetadataSecurity s2=metadata2Securities.get(i);
			Assert.assertEquals(s1.getExpressionName(), s2.getExpressionName());
			Assert.assertEquals(s1.getExpressionType(), s2.getExpressionType());
			Assert.assertNotEquals(s1.getHiResourceMetadata().getId(), s2.getHiResourceMetadata().getId());
			Assert.assertNotEquals(s1.getExpressionOn(), s2.getExpressionOn());
		}
	}
	
	@Test
	public void copypaste_a9_clearRB() throws Exception {
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	private String createSecurityMap(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
	}

}
