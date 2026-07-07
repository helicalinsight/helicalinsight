package com.helicalinsight.core.datasource;

import java.io.File;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.controller.DataSourceController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DSChangeViewQueryExecutionTest {
	
    MockMvc efwMock;
    MockMvc mockMvc;
    MockMvc dsMock;
    
    @Autowired
    private FilterChainProxy filterChainProxy;
    
	@Autowired
	private DataSourceController dataSourceController;

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private EFWDConnectionService efwdService;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;


    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }
        

    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
        this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();
    }

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
    @Autowired
    private IntegrationTestUtility testUtility;
    
    private static String dbName = "";
	private static String jdbcUrl = "";
	private static String plainJdbcConnectionId = "";
	private static String groovyPlainConnectionId = "";
	private static String groovyManagedConnectionId = "";
	
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

    @Test
    public void dsChangeQueryExecution_a1_folderCreation() throws Exception {
    	testUtility.clearRecycleBin();
    	testUtility.createFolder("DSChangeViewQueryExecutionTest");
    }
    
    @Test
    public void dsChangeQueryExecution_a2_expandScheam() throws Exception {
        String schema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
        String tables =  "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(schema);
    	testUtility.expand(tables);
    }
    
    @Test
    public void dsChangeQueryExecution_a3_createMetadata() throws Exception {
        String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"dbId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"DSChangeViewQueryExecutionTest\",\"fileName\":\"SaveSelectAll\",\"uniqueId\":true}";
        testUtility.createMetadata(formData);
    }
    
    @Test
    public void dsChangeQueryExecution_a4_createPlainJdbcConn() throws Exception{
    	String formData="{\"classifier\":\"efwd\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample-plain\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"directory\":\"DSChangeViewQueryExecutionTest\",\"type\":\"sql.jdbc\"}";
    	String output=testUtility.createPlainDatasource(formData);
    	JSONObject jsonObject = JSONObject.fromObject(output);
    	plainJdbcConnectionId = jsonObject.getJSONObject("response").getString("dataSourceId");
    }
    
    @Test
    public void dsChangeQueryExecution_a5_createGroovyPlainJdbcConn() throws Exception{
    	String formData="{\"classifier\":\"efwd\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample-groovy-plain\",\"userName\":\"\",\"password\":\"\",\"database\":\"+dbName+\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"DSChangeViewQueryExecutionTest\",\"type\":\"sql.jdbc.groovy\"}";
    	String output=testUtility.createPlainDatasource(formData);
    	JSONObject jsonObject = JSONObject.fromObject(output);
    	groovyPlainConnectionId =  jsonObject.getJSONObject("response").getString("dataSourceId");
    }
    
    @Test
    public void dsChangeQueryExecution_a6_createGroovyManagedConn() throws Exception{
    	String formData="{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"sample-groovy-managed\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"DSChangeViewQueryExecutionTest\"}";
    	String output=testUtility.createPlainDatasource(formData);
    	JSONObject jsonObject = JSONObject.fromObject(output);
    	groovyManagedConnectionId =  jsonObject.getJSONObject("response").getString("dataSourceId");
    }
	
	@Test
	public void dsChangeQueryExecution_a7_managedToPlain() throws Exception {
		String formData="{\"catSchemaPredicted\":false,\"baseType\":\"sql.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"sql.jdbc\",\"id\":\""+plainJdbcConnectionId+"\",\"sync\":false,\"classifier\":\"efwd\",\"dir\":\"DSChangeViewQueryExecutionTest\",\"queryType\":\"conditionIf\",\"query\":\"select *from HIUSER.\\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[]}";
		testUtility.viewQueryExecution(formData);
	}

	@Test
	public void dsChangeQueryExecution_a9_managedToGroovyManaged() throws Exception {
		String formData="{\"catSchemaPredicted\":false,\"baseType\":\"sql.jdbc.groovy.managed\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+groovyManagedConnectionId+"\",\"sync\":false,\"classifier\":\"efwd\",\"dir\":\"DSChangeViewQueryExecutionTest\",\"queryType\":\"conditionIf\",\"query\":\"select *from HIUSER.\\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}]}";
		testUtility.viewQueryExecution(formData);
	}
	
    @Test
    public void dsChangeQueryExecution_b1_deleteAllPlainConns() throws Exception {
    	String formData1="{\"classifier\":\"efwd\",\"id\":"+plainJdbcConnectionId+",\"type\":\"simple\",\"directory\":\"DSChangeViewQueryExecutionTest\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\"}";
    	String formData2="{\"classifier\":\"efwd\",\"id\":"+groovyPlainConnectionId+",\"type\":\"simple\",\"directory\":\"DSChangeViewQueryExecutionTest\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\"}";
    	String formData3="{\"classifier\":\"efwd\",\"id\":"+groovyManagedConnectionId+",\"type\":\"simple\",\"directory\":\"DSChangeViewQueryExecutionTest\",\"driver\":null}";
    	testUtility.deleteDatasource(formData1);
    	testUtility.deleteDatasource(formData2);
    	testUtility.deleteDatasource(formData3);
    }
    
	@Test
	public void dsChangeQueryExecution_b2_rootFolder() throws Exception {
		testUtility.deleteResource("DSChangeViewQueryExecutionTest");
	}
    
    @Test
	public void dsChangeQueryExecution_b3_clearRB() throws Exception{
		testUtility.clearRecycleBin();
	}

}
