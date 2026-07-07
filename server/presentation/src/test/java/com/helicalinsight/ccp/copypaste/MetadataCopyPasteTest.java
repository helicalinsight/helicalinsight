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
import com.helicalinsight.admin.model.HIMetadataView;
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
public class MetadataCopyPasteTest {

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

	private static String view_id_1="",view_id_2="";
	private static final String FOLDER1="MetadataCopyPasteTest";
	private static final String FOLDER2="MetadataCopyPasteTest2";
	
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
		String res=createView(formData);
		JSONObject obj=JSONObject.fromObject(res);
		view_id_1=obj.getJSONObject("response").getString("viewId");
	}
	
	@Test
	public void copypaste_a3_createView2() throws Exception {
		String formData ="{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select *from \\\"HIUSER\\\".\\\"travel_details\\\"\",\"viewName\":\"View 2\",\"labels\":[{\"name\":\"travel_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"travel_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"travel_type\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_medium\",\"type\":\"text\",\"checked\":true},{\"name\":\"source_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"source\",\"type\":\"text\",\"checked\":true},{\"name\":\"destination_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"destination\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_cost\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"mode_of_payment\",\"type\":\"text\",\"checked\":true},{\"name\":\"booking_platform\",\"type\":\"text\",\"checked\":true},{\"name\":\"travelled_by\",\"type\":\"numeric\",\"checked\":true}]}";
		String res=createView(formData);
		JSONObject obj=JSONObject.fromObject(res);
		view_id_2=obj.getJSONObject("response").getString("viewId");
	}
	
	@Test
	public void copypaste_a4_createMetadata() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		String formData ="{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"9tf26\",\"dbId\":\"9tf26\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[\""+view_id_1+"\",\""+view_id_2+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\""+FOLDER1+"\",\"metadataReload\":true}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
	}
	
	@Test
	public void copypaste_a5_copyMetadataWhichContainsViews() throws Exception {
		String formData="{\"sourceUrl\":\"MetadataCopyPasteTest/Metadata_1.metadata\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":5}";
		ccpTestUtility.ccpOperation("copy",formData,ccpTestUtility.getSuccessMessage(),1);
	}
	
	@Test
	public void copypaste_a6_verifyCopiedMetadata() throws Exception {
		HIResource metadata1=serviceDb.getResourceByUrl("MetadataCopyPasteTest/Metadata_1.metadata");
		HIResource metadata2=serviceDb.getResourceByUrl("MetadataCopyPasteTest2/Metadata_1.metadata");
		Assert.assertNotNull(metadata2);
		HIResourceMetadata hiResourceMetadata1=hiMetadataResourceServiceDB.giveHIResourceMetadataByResourceId(metadata1.getResourceId());
		HIResourceMetadata hiResourceMetadata2=hiMetadataResourceServiceDB.giveHIResourceMetadataByResourceId(metadata2.getResourceId());
		Assert.assertNotNull(hiResourceMetadata1);
		Assert.assertNotNull(hiResourceMetadata2);
		List<HIMetadataView> metadata_1Views=hiMetadataResourceServiceDB.getMetadataViewList(hiResourceMetadata1.getId());
		List<HIMetadataView> metadata_2Views=hiMetadataResourceServiceDB.getMetadataViewList(hiResourceMetadata2.getId());
		Assert.assertNotNull(metadata_1Views);
		Assert.assertNotNull(metadata_2Views);
		Assert.assertTrue(metadata_1Views.size()==metadata_2Views.size());
		for(int i=0;i<metadata_1Views.size();i++) {
			HIMetadataView v1=metadata_1Views.get(i);
			HIMetadataView v2=metadata_2Views.get(i);
			Assert.assertEquals(v1.getViewAlias(), v2.getViewAlias());
			Assert.assertEquals(v1.getViewName(), v2.getViewName());
			Assert.assertEquals(v1.getViewType(), v2.getViewType());
			Assert.assertNotEquals(v1.getHiResourceMetadata().getId(), v2.getHiResourceMetadata().getId());
			Assert.assertNotEquals(v1.getHiMetadataDatabases().getId(), v2.getHiMetadataDatabases().getId());
			Assert.assertNotEquals(v1.getViewId(), v2.getViewId());
		}
	}
	
	@Test
	public void copypaste_a7_clearRB() throws Exception {
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	private String createView(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "saveView");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
	}

}
