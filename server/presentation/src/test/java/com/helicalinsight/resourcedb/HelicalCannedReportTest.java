
package com.helicalinsight.resourcedb;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

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

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelicalCannedReportTest {
	
    MockMvc efwMock;
    MockMvc mockMvc;
    @Autowired
    FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;
    


    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;


    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }
    
	@Autowired
	private IntegrationTestUtility testUtility;

    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
    }

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;

    @Test
    public void hcr_1_create_a_folder_to_save_hcr() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "HelicalCannedReportTest");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));
    }


    static String id = "";
    @Test
    public void hcr_2_create_groovy_plan_jdbc_datasource() throws Exception {
        MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "core");
        map.put("serviceType", "dataSource");
        map.put("service", "write");

        map.put("formData","{\"classifier\":\"efwd\",\"condition\":\"import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\\\");\\n        responseJson.put(\\\"user\\\", \\\"hiuser\\\");\\n        responseJson.put(\\\"pass\\\", \\\"hiuser\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"samplePlain\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\"SampleTravelData\",\"jdbcUrl\":\"jdbc:derby:SampleTravelData\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"HelicalCannedReportTest\",\"type\":\"sql.jdbc.groovy\"}");

        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);

        MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
        JSONObject response = jsonObject.getJSONObject("response");
        Assert.assertTrue(response.containsKey("dataSourceId"));
        id = response.getString("dataSourceId");
    }
    static String managedId = "";
    @Test
    public void hcr_3_create_groovy_managed_jdbc_datasource() throws Exception {
        MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "core");
        map.put("serviceType", "dataSource");
        map.put("service", "write");

        map.put("formData","{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"HelicalCannedReportTest\",\"name\":\"groovyManaged\"}");

        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);

        MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
        JSONObject response = jsonObject.getJSONObject("response");
        Assert.assertTrue(response.containsKey("dataSourceId"));
        managedId = response.getString("dataSourceId");
    }

static String tempUUid="";

    @Test
    public void hcr_2_save_hcr_with_plain_groovy() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "hcr");
        map.put("serviceType", "report");
        map.put("service", "saveReportState");
        map.put("formData", "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"3\",\"type\":\"sql.jdbc.groovy\",\"connDetails\":{\"efwdId\":\"3\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"3\",\"type\":\"sql.groovy\",\"query\":\"import com.helicalinsight.efw.utility.GroovyUsersSession;\\r\\npublic String evalCondition() {\\r\\n\\r\\nString userName = GroovyUsersSession.getValue('${user}.name');\\r\\nuserName = userName.replaceAll(\\\"'\\\",\\\"\\\");\\r\\n\\r\\nString responseJson\\r\\n\\r\\nString selectClause = \\\"\\\"\\\"select (\\\"destination\\\") as \\\"destination\\\",\\\"travel_cost\\\" as \\\"travel_cost\\\" \\r\\nfrom \\\"travel_details\\\" \\\"\\\"\\\";\\r\\n\\r\\nif(userName.equals(\\\"hiadmin\\\"))\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Ambala')\\\"\\\"\\\"\\r\\nelse\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Paris')\\\"\\\"\\\"\\r\\n\\r\\nresponseJson = selectClause+ \\\"\\\" +whereClause;\\r\\n\\r\\nreturn responseJson;\\r\\n}\",\"parameters\":[]}}]}}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
        JSONObject response = jsonObject.getJSONObject("response");
        Assert.assertTrue(response.containsKey("temp_uuid"));
        tempUUid = response.getString("temp_uuid");
    }

    @Test
    public void hcr_3_save_hcr_with_plain_groovy_savefile() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "hcr");
        map.put("serviceType", "report");
        map.put("service", "saveReportState");
        map.put("formData" , "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":" +
                "[{\"id\":1,\"name\":\"Query1\",\"config\":\"import com.helicalinsight.efw.utility.GroovyUsersSession;\\r\\npublic String evalCondition() {\\r\\n\\r\\nString userName = GroovyUsersSession.getValue('${user}.name');\\r\\nuserName = userName.replaceAll(\\\"'\\\",\\\"\\\");\\r\\n\\r\\nString responseJson\\r\\n\\r\\nString selectClause = \\\"\\\"\\\"select (\\\"destination\\\") as \\\"destination\\\",\\\"travel_cost\\\" as \\\"travel_cost\\\" \\r\\nfrom \\\"travel_details\\\" \\\"\\\"\\\";\\r\\n\\r\\nif(userName.equals(\\\"hiadmin\\\"))\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Ambala')\\\"\\\"\\\"\\r\\nelse\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Paris')\\\"\\\"\\\"\\r\\n\\r\\nresponseJson = selectClause+ \\\"\\\" +whereClause;\\r\\n\\r\\nreturn responseJson;\\r\\n}\\r\\n\",\"connectionDetails\":{\"id\":\"3\",\"baseType\":\"sql.jdbc.groovy\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"name\":\"samplePlain\",\"type\":\"sql.jdbc.groovy\"},\"executeQueryData\":{\"data\":[{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":300},{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":300},{\"destination\":\"Ambala\",\"travel_cost\":500}],\"field\":[{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"}]},\"parameterList\":[],\"temp_uuid\":\"" +tempUUid+
                "\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"60875de2-4c31-48e9-b1aa-934e9f99947b\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"destination\",\"width\":100,\"height\":30,\"label\":\"$F{destination}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rd\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-0a75758a-b245-4215-9231-f27d7ccf36f6\",\"x\":121.07200000000005,\"y\":36,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\"}]},\"name\":\"CannedReport_1\",\"dir\":\"HelicalCannedReportTest\",\"previewFormData\":{\"format\":\"html\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\"" +tempUUid+
                "\",\"map_id\":1},\"designerProperties\":{\"groups\":[],\"fields\":[{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"}],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"$F{destination}\",\"X\":121.07200000000005,\"Y\":0,\"shapeId\":\"node-0a75758a-b245-4215-9231-f27d7ccf36f6\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true}],\"image\":[],\"lines\":[],\"break\":[]}]},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"generateXML\":false}}");
        
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }



    @Test
    public void hcr_4_save_hcr_with_managed_groovy() throws Exception {
        tempUUid="";
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "hcr");
        map.put("serviceType", "report");
        map.put("service", "saveReportState");
        map.put("formData", "{\"name\":\"_temp_filename\",\"version\":5,\"efwd\":{\"dataSources\":{\"connections\":[{\"connection\":{\"id\":\"4\",\"type\":\"sql.jdbc.groovy.managed\",\"connDetails\":{\"efwdId\":\"4\"}}}]},\"dataMaps\":[{\"dataMap\":{\"name\":\"Query1\",\"id\":1,\"connection\":\"4\",\"type\":\"sql.groovy\",\"query\":\"import com.helicalinsight.efw.utility.GroovyUsersSession;\\r\\npublic String evalCondition() {\\r\\n\\r\\nString userName = GroovyUsersSession.getValue('${user}.name');\\r\\nuserName = userName.replaceAll(\\\"'\\\",\\\"\\\");\\r\\n\\r\\nString responseJson\\r\\n\\r\\nString selectClause = \\\"\\\"\\\"select (\\\"destination\\\") as \\\"destination\\\",\\\"travel_cost\\\" as \\\"travel_cost\\\" \\r\\nfrom \\\"travel_details\\\" \\\"\\\"\\\";\\r\\n\\r\\nif(userName.equals(\\\"hiadmin\\\"))\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Ambala')\\\"\\\"\\\"\\r\\nelse\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Paris')\\\"\\\"\\\"\\r\\n\\r\\nresponseJson = selectClause+ \\\"\\\" +whereClause;\\r\\n\\r\\nreturn responseJson;\\r\\n}\",\"parameters\":[]}}]}}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

        MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
        JSONObject response = jsonObject.getJSONObject("response");
        Assert.assertTrue(response.containsKey("temp_uuid"));
        tempUUid = response.getString("temp_uuid");
    }



    @Test
    public void hcr_5_save_hcr_with_managed_groovy_savefile() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "hcr");
        map.put("serviceType", "report");
        map.put("service", "saveReportState");
        map.put("formData", "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":" +
                "[{\"id\":1,\"name\":\"Query1\",\"config\":\"import com.helicalinsight.efw.utility.GroovyUsersSession;\\r\\npublic String evalCondition() {\\r\\n\\r\\nString userName = GroovyUsersSession.getValue('${user}.name');\\r\\nuserName = userName.replaceAll(\\\"'\\\",\\\"\\\");\\r\\n\\r\\nString responseJson\\r\\n\\r\\nString selectClause = \\\"\\\"\\\"select (\\\"destination\\\") as \\\"destination\\\",\\\"travel_cost\\\" as \\\"travel_cost\\\" \\r\\nfrom \\\"travel_details\\\" \\\"\\\"\\\";\\r\\n\\r\\nif(userName.equals(\\\"hiadmin\\\"))\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Ambala')\\\"\\\"\\\"\\r\\nelse\\r\\nwhereClause = \\\"\\\"\\\"where (\\\"destination\\\"='Paris')\\\"\\\"\\\"\\r\\n\\r\\nresponseJson = selectClause+ \\\"\\\" +whereClause;\\r\\n\\r\\nreturn responseJson;\\r\\n}\\r\\n\",\"connectionDetails\":{\"id\":\"3\",\"baseType\":\"sql.jdbc.groovy\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"name\":\"samplePlain\",\"type\":\"sql.jdbc.groovy\"},\"executeQueryData\":{\"data\":[{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":300},{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":500},{\"destination\":\"Ambala\",\"travel_cost\":300},{\"destination\":\"Ambala\",\"travel_cost\":500}],\"field\":[{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"}]},\"parameterList\":[],\"temp_uuid\":\"" +tempUUid+
                "\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"60875de2-4c31-48e9-b1aa-934e9f99947b\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"destination\",\"width\":100,\"height\":30,\"label\":\"$F{destination}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rd\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.String\",\"id\":\"node-0a75758a-b245-4215-9231-f27d7ccf36f6\",\"x\":121.07200000000005,\"y\":36,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\"}]},\"name\":\"CannedReport_1\",\"dir\":\"HelicalCannedReportTest\",\"previewFormData\":{\"format\":\"html\",\"page\":0,\"connectionDetails\":{\"temp_uuid\":\"" +tempUUid+
                "\",\"map_id\":1},\"designerProperties\":{\"groups\":[],\"fields\":[{\"name\":\"destination\",\"clazz\":\"java.lang.String\"},{\"name\":\"travel_cost\",\"clazz\":\"java.lang.Integer\"}],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"details\":[{\"bandHeight\":30,\"isImageAttached\":false,\"staticText\":[],\"textField\":[{\"textFieldExpression\":\"$F{destination}\",\"X\":121.07200000000005,\"Y\":0,\"shapeId\":\"node-0a75758a-b245-4215-9231-f27d7ccf36f6\",\"textHeight\":30,\"textWidth\":100,\"fontName\":\"Serif\",\"textFontSize\":10,\"printRepeatedValues\":true}],\"image\":[],\"lines\":[],\"break\":[]}]},\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"},\"generateXML\":false}}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }


}
