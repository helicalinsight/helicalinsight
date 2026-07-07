package com.helicalinsight.ccp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.annotation.*;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.json.JSONObject;

@Component
public class CCPTestUtility {
	
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
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}
	
	private String exceptionMessage1="The source or destination is wrong or missing";
	private String exceptionMessage2="You do not have sufficient privileges to do this action";
	private String exceptionMessage3="We could not copy the resource(s) successfully";
	private String successMessage="Resource(s) copied successfully.";
	private static final String FOLDER1="temp";
	private static final String FOLDER2="temp2";
	
    @PostConstruct
    @Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}
	
	public void init() throws Exception {
		String schema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(schema);
		String table = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(table);
	}
	
	public void createMetadata(String dir) throws Exception {
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g7cx1\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\""+dir+"\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
	}
	
	public void createReport(String metadataSaveDir,String dir) throws Exception {
		String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"metadata\":{\"location\":\""+metadataSaveDir+"\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\""+dir+"\"}";
		testUtility.saveReport(formData);
	}
	
	public void createDesignerReport(String reportDir,String efwddSaveDire) throws Exception {
		String formData = "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-UQTPj\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"i\":\"item-UQTPj\"},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"title\":\"report1\"}}],\"reportInfo\":{\"file\":{\"path\":\""+reportDir+"\",\"name\":\"report1.hr\",\"title\":\"report1\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"dd8f1f87-073d-4fd0-8d39-1438d6ff6f83\",\"lastModified\":1700464121833}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[],\"layout\":[],\"designerSettings\":[],\"parameterDrawerStatus\":false}},\"dir\":\""+efwddSaveDire+"\",\"fileName\":\"Dashboard_1\"}";
		testUtility.createDesign(formData);	
	}
	
	public String ccpOperation(String action,String formData,String expectedMessage,Integer status,String... userCreds) throws Exception{
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        map.put("formData", formData);
        map.put("sourceArray", sourceList.toString());
        String userName = null;
        String password = null;
        if(userCreds.length>1) {
        	userName = userCreds[0];
        	password = userCreds[1];
        	map.put("username", userName);
        	map.put("password",password);
        }
        if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
        	map.put("Bearer", testUtility.generateAuthToken(userName, password, ""));
        }
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result =  this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(status)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value(expectedMessage)).andReturn();
		return result.getResponse().getContentAsString();
	}
	
	public void clearDB(String... folder) throws Exception {
		for(int i=0;i<folder.length;i++)
			testUtility.deleteResource(folder[i]);
		testUtility.clearRecycleBin();
	}
	
	public void verifyCcpOperation(String prefix) {
		HIResource movedSource=serviceDb.getResourceByUrl(prefix);
		Assert.assertNotNull(movedSource);
		Assert.assertNotNull(movedSource.getParentId());
		checkForAllResources(movedSource,prefix);
	}

	public void checkForAllResources(HIResource movedSource,String prefix) {
		List<HIResource> contents = serviceDb.getResourceByParentId(movedSource.getResourceId());
		if (contents != null && !contents.isEmpty()) {
			contents.forEach(e -> {
				Assert.assertNotNull(e);
				Assert.assertTrue(e.getResourceURL().startsWith(prefix));
				checkForAllResources(e,prefix);
			});
		}
	}
	
	public void checkForSourceExistanceAfterCopyOperation(String sourceUrl) {
		HIResource hiResource=serviceDb.getResourceByUrl(sourceUrl);
		Assert.assertNotNull(hiResource);
	}
	
	public String createUser(String formData) throws Exception {
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		return responseObj.getJSONObject("response").getString("id");
	}
	
	public void ccpApiValidation(String action) throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		String formData="{\"sourceUrl\":\"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage1(),0);
		
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage1(),0);
		
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\"URL_WHICH_IS_NOT_EXISTED\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage1(),0);
		
		testUtility.deleteResource(FOLDER2);
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage1(),0);
		testUtility.clearRecycleBin();
		testUtility.createFolder(FOLDER2);
		
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"100\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage2(),0);
		
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"4\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage2(),0);
		
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"2\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage2(),0);
		
		formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"NOT_A_NUMBER\",\"destPermission\":\"5\"}";
		ccpOperation(action,formData,getExceptionMessage2(),0);
		
		clearDB(FOLDER1,FOLDER2);
	}
	
	public String getExceptionMessage1() {
		return exceptionMessage1;
	}

	public String getExceptionMessage2() {
		return exceptionMessage2;
	}

	public String getSuccessMessage() {
		return successMessage;
	}
	
	public String getExceptionMessage3() {
		return exceptionMessage3;
	}
}
