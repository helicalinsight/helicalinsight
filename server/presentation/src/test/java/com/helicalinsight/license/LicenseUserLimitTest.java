//package com.helicalinsight.license;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThrows;
//
//import java.lang.reflect.Field;
//
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.exception.LicenseViolationException;
//import com.helicalinsight.efw.ApplicationProperties;
//import com.helicalinsight.efw.model.LicenseMetadata;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.IntegrationTestUtility;
//
//import jakarta.servlet.ServletContext;
//import jakarta.servlet.ServletException;
//import jakarta.transaction.Transactional;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//        "classpath:spring-security.xml"})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class LicenseUserLimitTest {
//	
//	MockMvc efwMock;
//    MockMvc mockMvc;
//    
//    @Autowired
//    private FilterChainProxy filterChainProxy;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//    
//    @Autowired
//    IntegrationTestUtility testUtility;
//    
//    @Bean
//    public FileSystemOperationsController fileSystemOperationsController() {
//        return new FileSystemOperationsController();
//    }    
//    
//    @Before
//    @Transactional
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(fileSystemOperationsController()).addFilters(filterChainProxy).build();
//        ServletContext servletContext = context.getServletContext();
//        servletContext.setAttribute("filterStatus", "ok");
//        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//    }
//    
//    
//    private void setLicenseMetadata(LicenseMetadata licenseMetadata) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//    	
//    	ApplicationProperties appProp = ApplicationProperties.getInstance();
//        Field initField = ApplicationProperties.class.getDeclaredField("initialized");
//        initField.setAccessible(true);
//        initField.set(appProp, false);
//        
//        appProp.setLicenseMetadata(licenseMetadata);
//    }
//    
//    
//    
//	@Test
//	public void createFifthUser_should_throw_license_violation_Exception() throws Exception {
//		
//		String formData = "{\"id\":\"\",\"email\":\"theghost@helical.com\",\"name\":\"theghost\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
//		
//		LicenseMetadata metadata = new LicenseMetadata.Builder()	
//				.email("test@helical.com")
//				.userLimit(4)
//				.build();
//		
//		LicenseMetadata old =  ApplicationProperties.getInstance().getLicenseMetadata();
//		
//		setLicenseMetadata(metadata);
//		 
//		Throwable throwable =  assertThrows(ServletException.class, () ->  testUtility.createUser(formData));
//		 
//		 Throwable cause = throwable.getCause();
//		 
//		 Exception exception = null;
//		 
//		 while( cause != null  ) {
//			 if ( cause instanceof LicenseViolationException ) {
//				 exception = (Exception) cause;
//			 }
//			 cause = cause.getCause();
//		 }
//		
//		 setLicenseMetadata(old);
//		assertEquals("User limit exceeded.", exception.getMessage());
//	}
//}
