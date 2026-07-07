package com.helicalinsight.resourcedb;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
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
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
public class ApplicationSettingTest {

    private static boolean initialized = false;

    MockMvc efwMock;

    @Autowired
    private WebApplicationContext context;

    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }


    @Autowired
    FilterChainProxy filterChainProxy;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;


    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

    @Autowired
    HIResourceServiceDB hiResourceServiceDB;

    @Autowired
    private EfwServicesController efwServicesController;

    @InjectMocks
    private ServletContext servletContext;


    @Before
    @Transactional
    public void setup() throws ServletException {
        authenticationAndAuthorizationFilter.init(null);
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        servletContext.setAttribute("remainingDays", "10");

        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).
                addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();

    }


    @Test
    public void applicationSettingLicense() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .get("/applicationSettings").requestAttr("remainingDays", "5");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();

        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        Assert.assertTrue(jsonObject.has("license"));
        JSONObject license = jsonObject.getJSONObject("license");
        Assert.assertTrue(license.has("remainingDays"));
        Assert.assertTrue(license.has("remainingDayMessage"));
    }

    
    @Test
    public void applicationSettingStreamPropTest() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .get("/applicationSettings").requestAttr("remainingDays", "5");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        Assert.assertTrue(jsonObject.has("streamResponse"));
        Assert.assertFalse(jsonObject.getBoolean("streamResponse"));
    }


}