package com.helicalinsight.recyclebin;

import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RecycleBinDeleteTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	private EFWDConnectionService connectionService;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
		}
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}


	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	
	/**
	 *  1.  Create user with Organization 
	 *  2.  Create some resources (Folder and nested folders)
	 *  3.  Remove the parent folder
	 *  4.  Hard delete parent folder -> should throw exception.
	 *  5.  restore the folder
	 *  6.  Delete User
	 *  7.  Hard delete user -> should throw exception
	 *  8.  Create Datasource
	 *  9.  Create Metadata
	 *  10. Delete Datasource
	 *  11. Hard Delete Datasource. -> throw error message.
	 *  12.  Restore user
	 *  13.  Delete Organization
	 *  14. Hard Delete Organization -> should throw error
	 *  15. Delete User, nested folders , folder ( every resource should be marked as deleted)
	 *  16. Now Hard delete Organization -> everything should be deleted i.e recycleBin should be empty
	 * 
	 */
	
	static String orgName = "Org"+System.currentTimeMillis();
	static String userName = "User"+System.currentTimeMillis();
	static String userId="";
	static String orgId="";
	
	@Test
	public void rec_a1_createUserWithOrganization() throws Exception {
	
		testUtility.clearRecycleBin();
		String formData = "{\"name\":\""+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@helical.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userId = userResponseObj.getJSONObject("response").getString("id");
		Map<Integer,Map<String,Integer>> orgRoleMap = testUtility.getRoleMap();
		Map<String,Integer> roleNameIdMap = orgRoleMap.get(Integer.valueOf(orgId));
		int adminRoleId = roleNameIdMap.get("ROLE_ADMIN");
		int userRoleId = roleNameIdMap.get("ROLE_USER");
		String attachRole = "{\"id\":"+userId+",\"name\":\"alien18\",\"email\":\"alien18@helical.com\",\"enabled\":true,\"roleIds\":["+adminRoleId+","+userRoleId+"],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void rec_a2_createResources() throws Exception {
		testUtility.createFolder("RecycleBinDeleteTest", userName,"password",orgName);
		testUtility.createFolder("RecycleBinDeleteTest_Level1_1", List.of("RecycleBinDeleteTest"),userName,"password",orgName);
		testUtility.createFolder("RecycleBinDeleteTest_Level1_2", List.of("RecycleBinDeleteTest"),userName,"password",orgName);
	}
	
//	@Test
	public void rec_a3_removeParentFolder() throws Exception {
		testUtility.deleteResource("RecycleBinDeleteTest");
		JsonObject binItem = testUtility.listRecycleBin().get(0).getAsJsonObject();
		String binId = binItem.get("recycleBinId").getAsString();
		String formData = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"],\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\"}";
		String response = testUtility.deletePermanently(formData);
		int size = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("recycleBin").getJSONArray("incomplete").size();
		Assert.assertEquals(1, size);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The resource could not be deleted, because some of the files linked to it are not in deleted state.", message);
		
		String formDataRestore = "{\"action\":\"restore\",\"recycleBinIds\":["+binId+"],\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\"}";
		testUtility.restore(formDataRestore);
	}
	
	static String dsId = "";
	
	@Test
	public void rec_a4_createDatasourceAndMetadata() throws Exception {
		
		
		String formData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
        formData = testUtility.addTokenInFormData(formData);
		dsId = JSONObject.fromObject(testUtility.createDatasource(formData)).getJSONObject("response").getString("dataSourceId");
		String catalog = testUtility.addTokenInFormData("{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"id\":\""+dsId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		String schema = testUtility.addTokenInFormData("{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"id\":\""+dsId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		testUtility.expand(catalog);
		testUtility.expand(schema);
        String metadata = testUtility.addTokenInFormData("{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\""+dsId+"\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"RecycleBinDeleteTest\",\"fileName\":\"Metadata_1\",\"uniqueId\":true}");
		String response = testUtility.createMetadata(metadata);
    	String deleteDatasource=testUtility.addTokenInFormData("{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"classifier\":\"global\",\"id\":\""+dsId+"\",\"type\":\"simple\",\"dataSourceProvider\":\"tomcat\"}");
		testUtility.deleteDatasource(deleteDatasource);
	}
	
	@Test
	public void rec_a5_deleteDsPermanently() throws Exception {
		JsonObject binItem = testUtility.listRecycleBin().get(0).getAsJsonObject();
		String binId = binItem.get("recycleBinId").getAsString();
		String formData = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"],\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\"}";
		formData = testUtility.addTokenInFormData(formData);
		String response = testUtility.deletePermanently(formData);
		
		int size = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("recycleBin").getJSONArray("incomplete").size();
		Assert.assertEquals(1, size);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The resource could not be deleted, because some of the files linked to it are not in deleted state.", message);
	
		String formDataRestore = "{\"action\":\"restore\",\"recycleBinIds\":["+binId+"],\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\"}";
		formDataRestore = testUtility.addTokenInFormData(formDataRestore);
		testUtility.restore(formDataRestore);
	}
	
	@Test
	public void rec_a6_deleteUser() throws Exception {
		testUtility.deleteUser(userId);
		JsonObject binItem =  testUtility.listRecycleBin().get(0).getAsJsonObject();
		String binId = binItem.get("recycleBinId").getAsString();
		String formData = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtility.deletePermanently(formData);
		int size = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("recycleBin").getJSONArray("incomplete").size();
		Assert.assertEquals(1, size);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The resource could not be deleted, because some of the files linked to it are not in deleted state.", message);
	
		String formDataRestore = "{\"action\":\"restore\",\"recycleBinIds\":["+binId+"]}";
		testUtility.restore(formDataRestore);
	}
	@Test
	public void rec_a7_deleteOrganization() throws Exception {
		testUtility.deleteOrg(orgId);
		JsonObject  binItem = testUtility.listRecycleBin().get(0).getAsJsonObject();
		String binId = binItem.get("recycleBinId").getAsString();
		String formData = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtility.deletePermanently(formData);
		int size = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("recycleBin").getJSONArray("incomplete").size();
		Assert.assertEquals(1, size);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The resource could not be deleted, because some of the files linked to it are not in deleted state.", message);
	
		String formDataRestore = "{\"action\":\"restore\",\"recycleBinIds\":["+binId+"]}";
		testUtility.restore(formDataRestore);
	}
	
//	@Test
	// TODO: Fix this 
	public void rec_a8_deleteAll() throws Exception {
		String token = testUtility.generateAuthToken(userName, "password", orgName);
		testUtility.deleteResource(token,"RecycleBinDeleteTest/RecycleBinDeleteTest_Level1_2");
		testUtility.deleteResource(token,"RecycleBinDeleteTest/RecycleBinDeleteTest_Level1_1");
		testUtility.deleteResource(token,"RecycleBinDeleteTest/Metadata_1.metadata");
		testUtility.deleteResource(token,"RecycleBinDeleteTest");
		
		String deleteDatasource="{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"classifier\":\"global\",\"id\":\""+dsId+"\",\"type\":\"simple\",\"dataSourceProvider\":\"tomcat\"}";
		deleteDatasource = testUtility.addTokenInFormData(deleteDatasource);
		testUtility.deleteDatasource(deleteDatasource);
		testUtility.deleteUser(userId);
		testUtility.deleteOrg(orgId);
		
		
		JsonArray array =  testUtility.listRecycleBin();
		String binId = "";
		for( Object obj : array ) {
			JSONObject json = (JSONObject) obj;
			if("Organizations".equalsIgnoreCase(json.getString("recycleBinType"))) {
				binId = json.getString("recycleBinId");
				break;
			}
		}
		String deleteBin = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtility.recycleBinAction(deleteBin);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", message);
		int size = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("recycleBin").getJSONArray("incomplete").size();
		Assert.assertEquals(0, size);
		int completed = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("recycleBin").getJSONArray("completed").size();
		Assert.assertTrue(completed > 0 );
		Assert.assertEquals(0, testUtility.listRecycleBin().size());
	}
}
