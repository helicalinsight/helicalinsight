package com.helicalinsight.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.metadata.jaxb.Views;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.processor.model.Security;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataWithViewsTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	IntegrationTestUtility testUtility;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	private HIResourceServiceDB resourceServiceDb;

	@Autowired
	private HIMetadataResourceServiceDB mdServiceDb;

	
	static String fileName = "";

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

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String TESTURL = "";
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	
	@Test
	public void exp_a1_init() throws Exception {
		testUtility.createFolder("MetadataWithViews");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");

	}
	
    @Test
    public void exp_a2_retrieve_view_label() throws Exception {
    	testUtility.retrievViewLabel("{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[]}");
    }
    static String viewId = "";
    @Test
    public void exp_a3_saveView() throws Exception {
    	String response = testUtility.saveView("{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}]}");
        JSONObject jsonObject = JSONObject.fromObject(response);
        JSONObject responseObject = jsonObject.getJSONObject("response");
        viewId = responseObject. getString("viewId");
    }
    
	@Test
	public void exp_a4_createMetadata() throws Exception {
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dimdate\",\"expressionType\":\"global\",\"accessType\":\"grant\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_HywgRinHc\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"filter\":\"\\\"dimdate\\\".\\\"dim_id\\\"=2\",\"action\":\"add\"}]}");
		String expressionId = JSONObject.fromObject(response).getJSONObject("response").getString("expressionId");
		testUtility.createMetadata("{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"dbId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[\""+viewId+"\"]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataWithViews\",\"fileName\":\"SaveSelectAll\",\"uniqueId\":true}");
	}
	@Test
	public void exp_a5_exportAFolderHavingMetadataWithViews() throws Exception {
		String request = "{\"dir\": \"MetadataWithViews\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}

	@Test
	public void exp_a6_cleanup() throws Exception {
		testUtility.deleteResource("MetadataWithViews");
		testUtility.clearRecycleBin();
	}
	@Test
	public void exp_a7_importAFolderHavingMetadata_noConflict() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		exp_a8_verifyImport();
	}
	
	
	public void exp_a8_verifyImport() throws Exception {
		
		HIResource resource =  resourceServiceDb.getResourceByUrl("MetadataWithViews/SaveSelectAll.metadata");
		Metadata metadata = mdServiceDb.getHIResourceMetadataByResourceId(resource.getResourceId());
		HIResourceMetadata resMetadata =  mdServiceDb.giveHIResourceMetadataByResourceId(resource.getResourceId());
		MetadataDatabases dbs =  resMetadata.getHiMetadataConnections().get(0).getMetadataDatabases().get(0);
		Map<String,Integer> viewMap = new HashMap<>();
		dbs.getMetadataTablesList().stream().filter(it -> it.getView()).forEach(it -> viewMap.put(it.getTableName(),it.getId()));
		Views views =  metadata.getDatabase().getViews();
		views.getViewList().forEach(it -> {
			int id = viewMap.get(it.getName());
			assertEquals(it.getId(), ""+id);
		});
		
		dbs.getMetadataRelationShipList().forEach(it -> {
			assertNotNull(it.getExternal());
			assertNotNull(it.getHiMetadataDatabases());
			assertNotNull(it.getHiResourceMetadata());
			assertNotNull(it.getJoinType());
			assertNotNull(it.getLeftMetadataColumns());
			assertNotNull(it.getRightMetadataColumns());
			assertNotNull(it.getId());
			
		});
		
		assertEquals(1,resMetadata.getMetadataSecurityList().size());
		resMetadata.getMetadataSecurityList().forEach(it -> {
			assertNotNull(it.getAccessType());
			assertNotNull(it.getExpressionCondition());
			assertNotNull(it.getExpressionFilter());
			assertNotNull(it.getExpressionId());
			assertNotNull(it.getExpressionName());
			assertNotNull(it.getExpressionOn());
			assertNotNull(it.getExpressionType());
			assertNotNull(it.getHiResourceMetadata());
			assertNotNull(it.getId());
			assertNotNull(it.getType());
		});
	}
	
	@Test
	public void exp_a9_importAFolderHavingMetadata_onConflict_update() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		exp_a8_verifyImport();
	}
	
	@Test
	public void exp_b1_create_metadata() throws Exception {
		testUtility.createFolder("MetadataWithJoins");
		String fetchCols = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6bj0t\",\"dbId\":\"6bj0t\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"aqs3-v4v2-jse9-20ss-w2/a8kg-cp33-qc2c-s6y3-za/00ii-vg0v-5xo4-8yor-g0\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
		String response = testUtility.fetchColumns(fetchCols);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"2au0-ky9x-sc3k-74wn-uz\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"6bj0t\",\"originalId\":\"5e46ac5f-fd58-45b9-b701-aec3007b496d\",\"id\":\"2sj9-tobq-vbba-kg4b-lh\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"6bj0t\",\"originalId\":\"4e3b8aeb-a418-40ce-adaa-fe77377f95e7\",\"id\":\"8vk3-gnzn-4jac-mtvh-m6\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"6bj0t\",\"originalId\":\"3973e26a-7e36-4660-9036-4f0ab018f2e8\",\"id\":\"rw1d-x6o7-vam6-wmaf-g0\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"6bj0t\",\"originalId\":\"48b8a64e-670b-4814-98e2-c000a88abf85\",\"id\":\"u4z4-ue5t-0tyk-9ke9-sz\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"6bj0t\",\"originalId\":\"65959040-e022-4f47-876b-bd3c05522d27\",\"id\":\"ouz0-fnlo-68oe-jdf9-9m\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"6bj0t\",\"originalId\":\"a8b90db5-238b-424b-8d22-768c7d704f4d\",\"id\":\"t9ym-ujv8-vo39-d2fq-5b\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"6bj0t\",\"originalId\":\"6c76b5ae-aa1b-4ac5-8844-c85f69acf925\",\"id\":\"m0v4-28x4-07ww-fzyh-5j\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"6bj0t\",\"originalId\":\"3e3ed3c1-0702-4fa2-8c64-88d91f6b1a8d\",\"id\":\"jgch-jmbr-h1nc-kul6-om\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"6bj0t\",\"originalId\":\"2b50928b-a43b-43a1-bb3a-6d606358b2ae\",\"id\":\"zz2q-afee-j0j8-m7s3-f4\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"6bj0t\",\"originalId\":\"18f41201-b9f9-4137-9aa7-94c287d7c0e0\",\"id\":\"8jbq-7n6u-gajx-81wy-8b\",\"name\":\"rating\"}],\"connId\":\"6bj0t\",\"originalName\":\"dimdate\",\"originalId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"name\":\"dimdate_1\"}],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"bss5-fciu-t9n2-u4fu-7w\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"2au0-ky9x-sc3k-74wn-uz\",\"dbId\":\"6bj0t\",\"table\":\"dimdate_1\"},\"right\":{\"column\":\"rating\",\"tableId\":\"2au0-ky9x-sc3k-74wn-uz\",\"dbId\":\"6bj0t\",\"table\":\"dimdate_1\"}},{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"z7p5-drt0-qdvj-e3p0-v7\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"6bj0t\",\"table\":\"dimdate\"},\"right\":{\"column\":\"rating\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"6bj0t\",\"table\":\"dimdate\"}}],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6bj0t\",\"dbId\":\"6bj0t\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_dup_tables\",\"location\":\"MetadataWithJoins\",\"metadataReload\":true}";
		String updatedFormData = testUtility.addDuplicateTableInFormData(response, formData, "dimdate");
		testUtility.createMetadata(updatedFormData);
	}
	
	@Test
	public void exp_b2_export() throws Exception {
		String request = "{\"dir\": \"MetadataWithJoins\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
		testUtility.deleteResource("MetadataWithJoins");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void exp_b3_importAFolderHavingMetadata_onConflict_update() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
	}
}

