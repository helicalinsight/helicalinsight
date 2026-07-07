package com.helicalinsight.admin.customauth;

import com.helicalinsight.admin.exception.AuthenticationException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Helical on 11/27/2020.
 */
public class TokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(TokenValidator.class);
    private String token;
    private Map<String, String> tokenMap;
    private Properties defaultProperties = new Properties();

    public Properties getDefaultProperties() {
        return defaultProperties;
    }

    public TokenValidator(String token) {
        this.token = token;
        tokenMap = new HashMap<>();
        try {
            defaultProperties.load(CustomUserDetailService.class.getResourceAsStream("/customAuthentication.properties"));
        } catch (IOException e) {
            logger.error("Problem loading the custom authentication properties file!");
        }
    }

    public Map<String,String> processToken() {
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("The Token is Invalid,Please provide a valid token");
        }
        String[] userDetailsParams = token.split(Pattern.quote("|"));
        List<String> tokenAsList = Arrays.asList(userDetailsParams);

        for (String itemInToken : tokenAsList) {
            prepareTokenMap(itemInToken);
        }
        if(tokenMap.containsKey("username")){
            return tokenMap;
        }else{
            throw new AuthenticationException("Mandatory parameter username is not present in the token!!!");
        }
    }

    private void prepareTokenMap(String itemInToken) {
        String[] token = itemInToken.split(Pattern.quote("="));
        try{
            switch (token[0].toLowerCase()) {
                case "username":
                    validateUserName(token);
                    break;
                case "company":
                    validateCompany(token);
                    break;
                case "email":
                    validateEmail(token);
                    break;
                case "role":
                    validateRole(token);
                    break;
                case "exptime":
                    validateExpiryTime(token);
                    break;
                default:
                   // System.out.println(token);
                    if(token.length==2){
                        if(!token[1].trim().isEmpty()){
                            tokenMap.put(token[0],token[1]);
                        }
                    }
            }
        }catch(Exception e){
            if(!e.getMessage().isEmpty()){
                throw new AuthenticationException(e.getMessage());
            }else{
                throw new AuthenticationException("Invalid Token!!!");
            }

        }
    }

    private void validateUserName(String[] token) {
        String userName = null;
        if (token.length == 2) {
            userName = token[1];
        }else{
            throw new AuthenticationException("Mandatory parameter username is not present in the token!!!");
        }
        tokenMap.put("username", userName.trim());
    }

    private void validateExpiryTime(String[] token) {
        String expTime = null;
        //System.out.println(token.length);
        if (token.length == 2) {
            if(!StringUtils.isBlank(token[1])){
                token[1]=token[1].trim();
                expTime = token[1];
                checkExpiry(expTime);
                tokenMap.put("expTime", expTime);
            }
        }
    }

    private void validateRole(String[] token) {
        String role = null;
        if (token.length == 2) {
            role = token[1];
        } else {
            role = defaultProperties.getProperty("defaultRole");
        }
        tokenMap.put("role", role);
    }

    private void validateEmail(String[] token) {
        String email = null;
        if (token.length == 2) {
            email = token[1];
            String[] emailArr = email.split(Pattern.quote(","));
            if(emailArr.length==2){
                throw new AuthenticationException("Multiple emails should not be assigned");
            }
            if(StringUtils.isBlank(email)){
                email = defaultProperties.getProperty("defaultEmail");
            }
        } else {
            email = defaultProperties.getProperty("defaultEmail");
            if (StringUtils.isBlank(email)) {
                throw new AuthenticationException("Email Should either be present in token or property file");
            }
        }
        tokenMap.put("email", email);
    }

    private void validateCompany(String[] token) {
        String companyName = null;
        if (token.length == 2) {
            companyName = token[1];
        } else {
            companyName = defaultProperties.getProperty("defaultCompany");
            if (StringUtils.isBlank(companyName)) {
                companyName = null;
            }
        }
        tokenMap.put("company", companyName);
    }

    public void checkExpiry(String expToken) {
        String defaultTimeZone = defaultProperties.getProperty("defaultTimezone");
        if (StringUtils.isEmpty(defaultTimeZone)) {
            throw new AuthenticationException("Timezone value is missing in the property file!!!");
        }
        String[] splittedTimeToken = expToken.split(" ");
        if (splittedTimeToken.length == 3) {
            defaultTimeZone = splittedTimeToken[2];
        }
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone(defaultTimeZone));
        expToken = splittedTimeToken[0] + " " + splittedTimeToken[1] + " " + defaultTimeZone;
        try {
            Date expiryDate = formatter.parse(expToken);
            Date todaysDate = new Date();
            if (expiryDate.before(todaysDate)) {
                throw new AuthenticationException("The Token is Expired or is invalid!");
            }
        } catch (ParseException e) {
            throw new AuthenticationException("Could not Parse the Token ExpiryTime!");
        }

    }

}
