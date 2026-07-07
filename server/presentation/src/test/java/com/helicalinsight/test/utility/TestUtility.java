package com.helicalinsight.test.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import java.util.HashMap;
import java.util.Map;

//TODO need to use jwt_token
public class TestUtility {


    private static String user = "hiadmin";

    private static String pwd = "hiadmin";
private static Map<String, String> map = new HashMap<String, String>();

    public static MockHttpServletRequestBuilder getMockHttpServletRequestBuilder(MockHttpServletRequestBuilder builder, Map<String, String> map) {
    	String isUserProvided= map.get("username");
		String isPwdProvided= map.get("password");
		String token = map.get("Bearer");
		if(StringUtils.isNotBlank(token)) {
			token = "Bearer "+token;
			builder.header("Authorization", token);
		}
		else {
			IntegrationTestUtility bean = ApplicationContextAccessor.getBean(IntegrationTestUtility.class);
			String generateAuthToken = bean.generateAuthToken(isUserProvided==null?user:isUserProvided, isPwdProvided==null?pwd:isPwdProvided, "");
			String token_1 = "Bearer "+generateAuthToken;
			builder.header("Authorization", token_1);
		}
		builder.param("username", isUserProvided==null?user:isUserProvided);
        builder.param("password", isPwdProvided==null?pwd:isPwdProvided);
        builder.param("requestId", "sample");
		map.remove("password");
		map.remove("username");
		for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.param(entry.getKey(), entry.getValue());
        }
        return builder;
    }


}