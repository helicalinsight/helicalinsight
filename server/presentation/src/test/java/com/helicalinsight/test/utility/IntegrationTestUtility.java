package com.helicalinsight.test.utility;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.externalauth.jwt.TokenProvider;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.validation.filter.ValidationFilter;

import net.lingala.zip4j.core.ZipFile;

@Component
public class IntegrationTestUtility {


    MockMvc efwMock;
    MockMvc mockMvc;
    @Autowired
    FilterChainProxy filterChainProxy;
    
    @Autowired
    ValidationFilter validationFilter;

    @Autowired
    private WebApplicationContext context;


    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
    
    @Bean
	public AdminController adminController() {
		return new AdminController();
	}
	
	@Autowired
	private AdminController adminController;



    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }

    @PostConstruct
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController,this.adminController).addFilters(filterChainProxy,validationFilter).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter,validationFilter).build();
    }


    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;

    
    
    public String createFolder(String folderName , String ...args) throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        mockHttpServletRequestBuilder.servletPath("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", folderName);
        map.put("sourceArray", sourceList.toString());
        String userName = "";
        String password = "";
        String orgName = "";
        if(args.length >= 2) {
        	userName = args[0];
        	password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	orgName = args[2];
        	map.put("j_organization", orgName);
        }
       if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
    	   map.put("Bearer", generateAuthToken(map.get("username"), map.get("password"), map.get("j_organization")));
       }
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
        return  result.getResponse().getContentAsString();
    }
    
    public String renameResource(String url, String newName, String ...args) throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        mockHttpServletRequestBuilder.servletPath("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("\""+url+"\"");
        sourceList.add("\""+newName+"\"");
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", List.of(sourceList).toString());
        if(args.length >= 2) {
        	String userName = args[0];
        	String password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	map.put("j_organization", args[2]);
        }
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful")).andReturn();
        return  result.getResponse().getContentAsString();
    }
    
    public String makeResourcePublic(String url, String newName, boolean isPublic, String ...args)  throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        mockHttpServletRequestBuilder.servletPath("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("\""+url+"\"");
        sourceList.add("\""+newName+"\"");
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("isPublic",String.valueOf(isPublic));
        map.put("sourceArray", List.of(sourceList).toString());
        if(args.length >= 2) {
        	String userName = args[0];
        	String password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	map.put("j_organization", args[2]);
        }
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful")).andReturn();
        return  result.getResponse().getContentAsString();
    }
	
	public String createFolder(String folderName,List<String> sourceList, String ...args) throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        mockHttpServletRequestBuilder.servletPath("/fileSystemOperations");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", folderName);
        map.put("sourceArray", sourceList.toString());
        String userName = "";
        String password = "";
        String orgName = "";
        if(args.length >= 2) {
        	userName = args[0];
        	password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	orgName = args[2];
        	map.put("j_organization", orgName);
        }
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
        	map.put("Bearer", generateAuthToken(userName, password, orgName));
        }
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
        return  result.getResponse().getContentAsString();
    }
    
	public String expand(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("username", "hiadmin");
		map.put("password", "hiadmin");
		formData = replaceCredentials(formData, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
	}
	
	
	public String createMetadata(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		formData = replaceCredentials(formData, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		String response = result.getResponse().getContentAsString();
		if(response!=null) {
			JsonObject responseObj = JsonParser.parseString(response).getAsJsonObject()
					.getAsJsonObject("response");
					
			String className = GsonUtility.optString(responseObj, "className");
			if(className!=null && className.equals("InconsistentStateException"))
				return className;
		}
		checkMetadataResponse(response);
		return  response;
	}
	public void checkMetadataResponse(String response) {
		JsonObject rawResponse = JsonParser.parseString(response).getAsJsonObject();
		JsonObject responseObject = rawResponse.getAsJsonObject("response");
		int status = rawResponse.get("status").getAsInt();
		String message = responseObject.get("message").getAsString();
		Assert.assertTrue(responseObject.has("data"));
		JsonArray array = responseObject.getAsJsonArray("data");
		for (int i = 0; i < array.size(); i++) {
			JsonObject data =  array.get(i).getAsJsonObject();
			Assert.assertTrue(data.has("lastModified"));
			Assert.assertTrue(data.has("options"));
			Assert.assertTrue(data.has("type"));
			Assert.assertTrue(data.has("extension"));
			Assert.assertTrue(data.has("path"));
			Assert.assertTrue(data.has("permissionLevel"));
			Assert.assertTrue(data.has("name"));
			Assert.assertTrue(data.has("title"));
		}
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
	}
	
	public String deleteResource(String ...resources) throws Exception {
		StringBuilder sb = new StringBuilder();
		if(resources.length != 0) {
			for(String resource : resources) {
				sb.append(resource+",");
			}
		}
		
		String input = sb.toString().substring(0,sb.length()-1);
		input = "[\""+input+"\"]";
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
		mockHttpServletRequestBuilder.servletPath("/fileSystemOperations");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray",input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful")).andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String fetchJoins(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchJoins");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return  result.getResponse().getContentAsString();
	}
	
	public String prepareNoChangeJoins(String formData) throws Exception {
		String rawResponse = fetchJoins(formData);
		JsonObject rawNode = JsonParser.parseString(rawResponse).getAsJsonObject();
		JsonArray joinsArray =  rawNode.getAsJsonObject("response").getAsJsonArray("joins");
		JsonArray joins = new JsonArray();
		for(JsonElement joinObjectElement : joinsArray) {
			JsonObject joinObject = joinObjectElement.getAsJsonObject();
			JsonObject eachJoin = new JsonObject();
			eachJoin.addProperty("id", joinObject.get("id").getAsString());
			eachJoin.addProperty("action","noChange");
			joins.add(eachJoin);
		}
		return joins.toString();
	}
	
	public JsonArray prepareNoChangeJoins(JsonArray joinsArray) {
		JsonArray noChangeJoins = new JsonArray();
		for (JsonElement object : joinsArray) {
			JsonObject eachJoin =  object.getAsJsonObject();
			JsonObject newJoins = new JsonObject();
			newJoins.addProperty("id", eachJoin.get("id").getAsString());
			newJoins.addProperty("action", "noChange");
			noChangeJoins.add(newJoins);
		}
		return noChangeJoins;
	}

	public JsonArray prepareTableIds(String tablesString) {
		JsonObject tables = JsonParser.parseString(tablesString).getAsJsonObject();
		JsonArray tableIds = new JsonArray();
		Set<String> tb1 = tables.keySet();
		for (String key : tb1) {
			JsonObject tableObj = tables.getAsJsonObject(key);
			String id = tableObj.get("id").getAsString();
			tableIds.add(id);
		}
		return tableIds;
	}
	
	public String prepareDeleteJoins(String formData) throws Exception {
		String rawResponse = fetchJoins(formData);
		JsonObject rawNode = JsonParser.parseString(rawResponse).getAsJsonObject();
		JsonArray joinsArray =  rawNode.getAsJsonObject("response").getAsJsonArray("joins");
		JsonArray joins = new JsonArray();
		for(JsonElement joinElement : joinsArray) {
			JsonObject joinObject = joinElement.getAsJsonObject();
			JsonObject eachJoin = new JsonObject();
			eachJoin.addProperty("id", joinObject.get("id").getAsString());
			eachJoin.addProperty("action","delete");
			joins.add(eachJoin);
		}
		return joins.toString();
	}
	public String getOuterDbId(String response) {
		JsonObject responseNode =  JsonParser.parseString(response).getAsJsonObject();
		return responseNode.getAsJsonObject("response")
				.getAsJsonObject("metadata")
				.getAsJsonObject("dataSource")
				.get("dbId").getAsString();
	}
	
	public String testDataSource(String formData) throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		formData = replaceCredentials(formData, map);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("A new Tomcat data source is created successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();

		return result.getResponse().getContentAsString();
	}
	
	public String createDatasource(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		formData = replaceCredentials(formData, map);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("A new Tomcat data source is created successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();

		return result.getResponse().getContentAsString();
	}
	
	public   String replaceCredentials(String formData , Map<String,String> map) {
		
		JsonObject formObj = JsonParser.parseString(formData).getAsJsonObject();
		String userName = GsonUtility.optString(formObj, "username");
		String password = GsonUtility.optString(formObj, "password");
		String organization = GsonUtility.optString(formObj,"j_organization");
		String bearer = GsonUtility.optString(formObj, "Bearer");
		if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			map.put("username", userName);
			map.put("password",password);
			formObj.remove("username");
			formObj.remove("password");
		}
		if(StringUtils.isNotBlank(organization)) {
			map.put("j_organization", organization);
			formObj.remove("j_organization");
		}
		
		if(StringUtils.isNotBlank(bearer)) {
			map.put("Bearer", bearer);
			formObj.remove("Bearer");
		}
		
		return formObj.toString();
	}
	
	
	public String fetchColumns(String formData)  throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchColumns");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();

		return result.getResponse().getContentAsString();
	}
	
	public String validateSecurity(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder =getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "evaluateSecurity");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Condition Test Success"))
				.andReturn();

		return result.getResponse().getContentAsString();
		
	}
	
	public String saveSecurity(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();

		return result.getResponse().getContentAsString();
	}
	
	public String getSecurity(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		
		String response = result.getResponse().getContentAsString();
		JsonObject getResponse = JsonParser.parseString(response).getAsJsonObject();
		JsonArray expressionArr = getResponse.getAsJsonObject("response").getAsJsonArray("expressions");
    	for(JsonElement object : expressionArr) {
    		JsonObject json =  object.getAsJsonObject();
    		JsonArray onArr =  json.getAsJsonArray("on");
    		for( JsonElement onObject : onArr) {
    			String eachOn =  onObject.getAsString();
    			Assert.assertNotNull(eachOn);
    			Assert.assertNotEquals("null", eachOn);
    		}
    	}

		return result.getResponse().getContentAsString();
	}
	
	public String addDuplicateColumnsInFormData(String columns , String metadata, String tableName) {
    	JsonObject metadataObj = JsonParser.parseString(metadata).getAsJsonObject();
    	JsonArray mdColumnsArray = (metadataObj.getAsJsonObject("duplicate").getAsJsonArray("table").get(0).getAsJsonObject()).getAsJsonArray("columns");
    	JsonObject fetchedColumns = JsonParser.parseString(columns).getAsJsonObject()
    									.getAsJsonObject("response")
    									.getAsJsonObject("metadata")
    									.getAsJsonObject("table")
    									.getAsJsonObject(tableName)
    									.getAsJsonObject("columns");
    	HashMap<String, JsonObject> columnMap =  new Gson().fromJson(fetchedColumns.toString(), new TypeToken<HashMap<String, JsonObject>>(){
			private static final long serialVersionUID = 1L;}.getType());
    	for(JsonElement it : mdColumnsArray) {
    		JsonObject eachColumn = it.getAsJsonObject();
    		JsonObject mapObj =  columnMap.get(eachColumn.get("originalName").getAsString());
    		eachColumn.addProperty("originalId", mapObj.get("id").getAsString());
    	}
    	(metadataObj.getAsJsonObject("duplicate").getAsJsonArray("table").get(0).getAsJsonObject()).add("columns", mdColumnsArray);
    	return metadataObj.toString();
    }
	
	public String addDuplicateTableInFormData(String columns , String metadata, String tableName) {
    	JsonObject metadataObj = JsonParser.parseString(metadata).getAsJsonObject();
    	JsonArray mdColumnsArray = (metadataObj.getAsJsonObject("duplicate").getAsJsonArray("table").get(0).getAsJsonObject()).getAsJsonArray("columns");
    	JsonObject fetchedColumns = JsonParser.parseString(columns).getAsJsonObject()
    									.getAsJsonObject("response")
    									.getAsJsonObject("metadata")
    									.getAsJsonObject("table")
    									.getAsJsonObject(tableName)
    									.getAsJsonObject("columns");
    	
    	HashMap<String, JsonObject> columnMap = new Gson().fromJson(
    		    fetchedColumns.toString(),
    		    new TypeToken<HashMap<String, JsonObject>>() {
					private static final long serialVersionUID = 1L;}.getType()
    		);
    	
    	for(JsonElement it : mdColumnsArray) {
    		JsonObject eachColumn =  it.getAsJsonObject();
    		JsonObject mapObj =  columnMap.get(eachColumn.get("alias").getAsString());
    		if( mapObj != null) {
    			eachColumn.addProperty("originalId", mapObj.get("id").getAsString());
    		}
    	}
    	(metadataObj.getAsJsonObject("duplicate").getAsJsonArray("table").get(0).getAsJsonObject()).add("columns", mdColumnsArray);
    	return metadataObj.toString();
    	
    }
	
	
	public String retrievViewLabel(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveViewLabels");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();

		return result.getResponse().getContentAsString();
	}
	
	public String saveView(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder =getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "saveView");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();

		return result.getResponse().getContentAsString();
	}
	public String retrieveView(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveView");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String fetchData(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "fetchData");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();

		return result.getResponse().getContentAsString();
	}
	
	public String saveReport(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		formData = replaceCredentials(formData, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
				.andReturn();
		String response = result.getResponse().getContentAsString();
		JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
		int status = jsonObject.get("status").getAsInt();
		JsonObject responseObject = jsonObject.getAsJsonObject("response");
		Assert.assertTrue(responseObject.has("data"));
		JsonArray array = responseObject.getAsJsonArray("data");
		Assert.assertEquals(1, status);
		for (int i = 0; i < array.size(); i++) {
			JsonObject data = array.get(i).getAsJsonObject();
			Assert.assertTrue(data.has("lastModified"));
			Assert.assertTrue(data.has("type"));
			Assert.assertTrue(data.has("options"));
			Assert.assertTrue(data.has("extension"));
			Assert.assertTrue(data.has("path"));
			Assert.assertTrue(data.has("permissionLevel"));
			Assert.assertTrue(data.has("name"));
			Assert.assertTrue(data.has("title"));
		}
		
		return response;
	}
	
	public String getMetadataForEdit(String formData) throws Exception {

		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getMetadataForEdit");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
		
	}
	
	public String getReportForEdit(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "getReportForEdit");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
		
	}
	
	
	public String getMetadataForEditNegativeCase(String formData) throws Exception {

		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getMetadataForEdit");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		return result.getResponse().getContentAsString();
		
	}
	
	
	public String getMetadata(String formData) throws Exception {

		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "get");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
		
	}
	
	public String generateQuery(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "generateQuery");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public JsonObject getColumns(JsonObject response , String tableName) {
		return response.getAsJsonObject("response")
				.getAsJsonObject("metadata")
				.getAsJsonObject("tables")
				.getAsJsonObject(tableName)
				.getAsJsonObject("columns");
	}
	
	public String createPlainDatasource(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		formData = replaceCredentials(formData, map);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();

		return result.getResponse().getContentAsString();
	}
	
	
	public String exportResource(String formData , String TESTURL) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
		Map<String, String> map = new HashMap<>();
		formData=replaceCredentials(formData, map);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		String header = result.getResponse().getHeader("Content-Disposition");
		String fileName = header.substring(header.indexOf("=")+1);
		fileName = fileName.substring(1,fileName.length()-1);
		
		byte[] bytes = result.getResponse().getContentAsByteArray();
		try (FileOutputStream outputStream = new FileOutputStream(String.join(File.separator, TESTURL, fileName))) {
			outputStream.write(bytes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ZipFile zipFile = new ZipFile(String.join("/", TESTURL,fileName));
		Assert.assertTrue(zipFile.isValidZipFile());
		Assert.assertNotNull(bytes);
		Assert.assertTrue(bytes.length > 0);
		return fileName;
	} 
	
	public String importResource(String formData , String TESTURL , String fileName) throws Exception{
		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data", fileStream);
		fileStream.close();
		Map<String, String> map = new HashMap<>();
		formData = replaceCredentials(formData, map);
		map.put("formData", formData);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart("/importResource").file(file);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"))
				.andReturn();
		return  result.getResponse().getContentAsString();
	}
	
	public String createCube(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "cube");
		map.put("service", "update");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		JsonObject jsonObject = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
		int status = jsonObject.get("status").getAsInt();
		JsonObject responseObject = jsonObject.getAsJsonObject("response");
		String message = responseObject.get("message").getAsString();
		Assert.assertEquals(1, status);
		Assert.assertEquals("Cube saved successfully", message);
		Assert.assertTrue(responseObject.has("data"));
		JsonArray array = responseObject.getAsJsonArray("data");
		for (int i = 0; i < array.size(); i++) {
			JsonObject data = array.get(i).getAsJsonObject();
			Assert.assertTrue(data.has("lastModified"));
			Assert.assertTrue(data.has("type"));
			Assert.assertTrue(data.has("options"));
			Assert.assertTrue(data.has("extension"));
			Assert.assertTrue(data.has("path"));
			Assert.assertTrue(data.has("permissionLevel"));
			Assert.assertTrue(data.has("name"));
			Assert.assertTrue(data.has("title"));
		}
		return result.getResponse().getContentAsString();
	}

	public String createDesign(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		String resp = result.getResponse().getContentAsString();
		JsonObject jsonObject = JsonParser.parseString(resp).getAsJsonObject();
		int status = jsonObject.get("status").getAsInt();
		JsonObject responseObject = jsonObject.getAsJsonObject("response");
		Assert.assertTrue(responseObject.has("data"));
		JsonArray array = responseObject.getAsJsonArray("data");
		for (int i = 0; i < array.size(); i++) {
			JsonObject data = array.get(i).getAsJsonObject();
			Assert.assertTrue(data.has("lastModified"));
			Assert.assertTrue(data.has("type"));
			Assert.assertTrue(data.has("options"));
			Assert.assertTrue(data.has("extension"));
			Assert.assertTrue(data.has("path"));
			Assert.assertTrue(data.has("permissionLevel"));
			Assert.assertTrue(data.has("name"));
			Assert.assertTrue(data.has("title"));
			String message = responseObject.get("message").getAsString();
			Assert.assertEquals(1, status);
			Assert.assertEquals("Design is saved successfully", message);
		}
		return resp;
	}
	
	private Map<String,Integer> tableNameIdMap = null;
	
	public Map<String,Integer> getSingleConnectionMetadataTableMap(String resp) {
		tableNameIdMap = new HashMap<>();
		
		JsonObject response = JsonParser.parseString(resp).getAsJsonObject().getAsJsonObject("response");
		JsonObject tables = response.getAsJsonObject("metadata").getAsJsonObject("tables");
		Set<String> tableKeys = tables.keySet();
		for(String tableKey : tableKeys) {
			tableNameIdMap.put(tableKey, Integer.valueOf(tables.getAsJsonObject(tableKey).get("id").getAsString()));
		}
		
		return tableNameIdMap;
	}
	public Map<String,Integer> getColumnMap(String resp) {
		Map<String,Integer> columnNameIdMap = new HashMap<>();
		JsonObject response = JsonParser.parseString(resp).getAsJsonObject().getAsJsonObject("response");
		JsonObject tables = response.getAsJsonObject("metadata").getAsJsonObject("tables");
		Set<String> set = tables.keySet();
		for(String tableName : set ) {
			JsonObject columns = response.getAsJsonObject("metadata")
					.getAsJsonObject("tables")
					.getAsJsonObject(tableName).getAsJsonObject("columns");
			
			Set<String> columnKeys = columns.keySet();
			for(String columnKey  : columnKeys) {
				columnNameIdMap.put(columnKey, Integer.valueOf(columns.getAsJsonObject(columnKey).get("id").getAsString()));
			}
		}
		
		return columnNameIdMap;
		
	}
	
	public String clearRecycleBin(String ...args) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		String formData = "{\"action\":\"clear\",\"force\":true}";
		String userName = "";
		String password = "";
		if(args.length >= 2) {
        	userName = args[0];
        	password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	map.put("j_organization", args[2]);
        }
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
     	   map.put("Bearer", generateAuthToken(map.get("username"), map.get("password"), map.get("j_organization")));
        }
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String clearRecycleBinNoForce(String ...args) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		String formData = "{\"action\":\"clear\"}";
		if(args.length >= 2) {
        	String userName = args[0];
        	String password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	map.put("j_organization", args[2]);
        }
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String createUser(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User created successfully."))
				.andReturn();
		return result.getResponse().getContentAsString();	
	}
	
	public String addProfile(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Profile added successfully."))
				.andReturn();
		return result.getResponse().getContentAsString();	
	}
	
	
	
	public String createOrg(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Organization added successfully"))
				.andReturn();
		return result.getResponse().getContentAsString();	
	}
	
	public String deleteOrg(String orgId) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("id", orgId);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Organization deleted successfully "))
				.andReturn();
		return result.getResponse().getContentAsString();	
	}
	
	public String deleteUser(String userId) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("id", userId);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User deleted successfully"))
				.andReturn();
		return result.getResponse().getContentAsString();	
	}
	
	public String attachRole(String formData,String userId) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("action", "update");
		map.put("id", userId);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User updated successfully. "))
				.andReturn();
		return result.getResponse().getContentAsString();	
	
	}
	
	public String changeOwner(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public void shareResource(String formdata) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("service", "update");
		map.put("serviceType", "share");
		map.put("type", "core");
		String formData = replaceCredentials(formdata, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	@Test
	public void shareDatasource(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	public JsonArray listRecycleBin() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		String formData = "{\"action\":\"list\"}";
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		map.put("formData", formData);

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JsonObject resultJson = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
		JsonArray data = resultJson.get("response").getAsJsonObject().getAsJsonArray("data");
		return data;
	}
	
	public JsonArray listRecycleBin(String ...args) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		String formData = "{\"action\":\"list\"}";
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		map.put("formData", formData);
		String userName = "";
		String password = "";
		if(args.length >= 2) {
        	userName = args[0];
        	password = args[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(args.length >=3 ) {
        	map.put("j_organization", args[2]);
        }
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
     	   map.put("Bearer", generateAuthToken(map.get("username"), map.get("password"), map.get("j_organization")));
        }
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JsonObject resultJson = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject();
		JsonArray data = resultJson.getAsJsonObject("response").getAsJsonArray("data");
		return data;
	}
	
	public String restore(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		formData  = replaceCredentials(formData, map);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String deletePermanently(String formData) throws Exception {
		return restore(formData);
	}

	public String recycleBinAction(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		return result.getResponse().getContentAsString();
	}
	
	
	public Map<Integer,Map<String,Integer>> getRoleMap() throws Exception {
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/roles");
		Map<String, String> map = new HashMap<>();
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		JsonArray roles = JsonParser.parseString(result.getResponse().getContentAsString()).getAsJsonObject().getAsJsonArray("roles");

		Map<Integer,Map<String,Integer>> orgRoleMap = new HashMap<>();
		for(Object roleObj : roles ) {
			JsonObject json = (JsonObject) roleObj;
			String roleName = json.get("name").getAsString();
			int roleId = json.get("id").getAsInt();
			int orgId = GsonUtility.optIntValue(json, "organisation", 0);
			Map<String,Integer> innerMap = new HashMap<>();
			innerMap.put(roleName, roleId);
			
			if(orgRoleMap.containsKey(orgId)) {
				orgRoleMap.get(orgId).putAll(innerMap);
			}
			else {
				orgRoleMap.put(orgId,innerMap);
			}
		}
		return orgRoleMap;
	}
	
	
	public String deleteDatasource(String formData) throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		formData = replaceCredentials(formData, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String deleteRBItemPermanently(String formData) throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder =getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	
	public String viewQueryExecution(String formData) throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveViewLabels");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String getRecycleBinIdByResourceName(String resourceName) throws Exception {
		JsonArray array =  listRecycleBin();
		for(JsonElement  object : array) {
			JsonObject json = object.getAsJsonObject();
			if(resourceName.equalsIgnoreCase(json.getAsJsonObject("data").get("name").getAsString())) {
				return json.get("recycleBinId").getAsString();
			}
		}
		return "";
	}
	public String getRecycleBinIdByResourcePath(String resourcePath) throws Exception {
		JsonArray array =  listRecycleBin();
		for(JsonElement object : array) {
			JsonObject json =  object.getAsJsonObject();
			if(resourcePath.equalsIgnoreCase(json.getAsJsonObject("data").get("path").getAsString())) {
				return json.get("recycleBinId").getAsString();
			}
		}
		return "";
	}

	public String generateAuthToken(String username, String password , String orgName) {
		List<GrantedAuthority> authorities =  List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),new SimpleGrantedAuthority("ROLE_USER"));
		Principal principal = new Principal(username, password, true, true, true, true, authorities, "", null, StringUtils.isNotBlank(orgName)?orgName:null, null);
		TokenProvider provider = ApplicationContextAccessor.getBean(TokenProvider.class);
		Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, password,authorities);
		return provider.generateToken(authentication);
	}
	
	public String addTokenInFormData(String formData) {
		JsonObject node =  JsonParser.parseString(formData).getAsJsonObject();
		String username = node.get("username").getAsString();
		String password = node.get("password").getAsString();
		String orgName =    GsonUtility.optString(node, "j_organization");
		String token = generateAuthToken(username, password, orgName);
		node.addProperty("Bearer", token);
		return node.toString();
	}
	
	public String cutPasteSourceToDestination(String formData) throws Exception{
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "cut");
        map.put("formData", formData);
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Resource(s) copied successfully.")).andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String retriveShareInfo(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "retrieveSharedInfo");
		formData = replaceCredentials(formData, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String fetchInfo(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "fetchInfo");
		formData = replaceCredentials(formData, map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public String fetchDashboard(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "fetch");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		String resp = result.getResponse().getContentAsString();
		return resp;
	}


	public String editDesign(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		String resp = result.getResponse().getContentAsString();
		JsonObject jsonObject = JsonParser.parseString(resp).getAsJsonObject();
		int status = jsonObject.get("status").getAsInt();
		JsonObject responseObject = jsonObject.getAsJsonObject("response");
		Assert.assertTrue(responseObject.has("data"));
		JsonArray array = responseObject.getAsJsonArray("data");
		for (int i = 0; i < array.size(); i++) {
			JsonObject data = array.get(i).getAsJsonObject();
			Assert.assertTrue(data.has("lastModified"));
			Assert.assertTrue(data.has("type"));
			Assert.assertTrue(data.has("options"));
			Assert.assertTrue(data.has("extension"));
			Assert.assertTrue(data.has("path"));
			Assert.assertTrue(data.has("permissionLevel"));
			Assert.assertTrue(data.has("name"));
			Assert.assertTrue(data.has("title"));
			String message = responseObject.get("message").getAsString();
			Assert.assertEquals(1, status);
			Assert.assertEquals("Design is edited successfully", message);
		}
		return resp;
	}
	
	public String generateHCRReport(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "generateReport");
		formData = replaceCredentials(formData,map);
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		return  result.getResponse().getContentAsString();
	}
	
	public String saveHCRReportState(String formData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getServiceRequestBuilder();
		Map<String, String> map = new HashMap<>();
		map.put("type", "hcr");
		map.put("serviceType", "report");
		map.put("service", "saveReportState");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		return  result.getResponse().getContentAsString();
	}
	
	private final MockHttpServletRequestBuilder getServiceRequestBuilder() {
		MockHttpSession session = new MockHttpSession();
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		mockHttpServletRequestBuilder.servletPath("/services")
		.session(session);
		return mockHttpServletRequestBuilder;
	}
	

}
    
