package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajesh
 *         This compoonent is used to handle the following service request.
 *         type:core
 *         serviceType:dataSource
 *         service:drillCsvDatasource
 *         formData:{"storageName":"sampleCSV"}
 */
public class DrillCsvDataSourceCreator implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(DrillCsvDataSourceCreator.class);

    @Override
    public String executeComponent(String jsonFormData) {

        JsonObject jsonFromData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String dbName = jsonFromData.get("storageName").getAsString();


        JsonObject formData = prepareFormDataForStorage(dbName);

        String drillStorageUrl = getUrlOfDrill();

        String resourceUrl = drillStorageUrl + "/storage.json";


        String result = drillRestApiCall(resourceUrl, "POST", formData.toString());
        JsonObject resultJSON = new JsonObject();
        resultJSON.addProperty("output", result);
        return resultJSON.toString();
    }

    private JsonObject prepareFormDataForStorage(String storageFileName) {
        JsonObject drillJSONObject = getDrillFileAsJson();
        JsonObject enabledTypesJSON = drillJSONObject.getAsJsonObject("enabledTypes");

        JsonObject csvJSONObject = enabledTypesJSON.getAsJsonObject("csv");

        JsonObject configJsonObject = csvJSONObject.getAsJsonObject("config");

        //JSONObject rootJsonObject = configJsonObject.getJSONObject("workspaces").getJSONObject("root");

        JsonObject urlConfigJSONObject = new JsonObject();

        urlConfigJSONObject.addProperty("name", storageFileName);
        JsonObject configJSON = new JsonObject();
        configJSON.addProperty("type", configJsonObject.get("type").getAsString());
        configJSON.addProperty("enabled", configJsonObject.get("enabled").getAsBoolean());
        configJSON.addProperty("connection", configJsonObject.get("connection").getAsString());
        urlConfigJSONObject.add("config", configJSON);
        return urlConfigJSONObject;
    }

    private static JsonObject getDrillFileAsJson() {
        String drillConfigPath = JsonUtils.getDrillConfigPath();
        JsonObject drillConfig = JsonUtils.newGetXmlAsJson(drillConfigPath);
        drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
        return drillConfig;
    }


    private static JsonObject getDrillWebConfig() {
        return getDrillFileAsJson().getAsJsonObject("urlConfig");
    }


    private static String doLogin(HttpClient client) throws IOException {
        JsonObject securityDetailsJSON = checkSecurityForDrill();
        if (securityDetailsJSON != null) {
            String requestURL = getUrlOfDrill();
            String username = securityDetailsJSON.get("username").getAsString();
            String password = securityDetailsJSON.get("password").getAsString();
            String securityCheckType = securityDetailsJSON.get("securityCheckType").getAsString();
            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("username", username));
            parameters.add(new BasicNameValuePair("password", password));
            return doPost(client, requestURL + "/" + securityCheckType, parameters);

        }
        return null;

    }

    private static JsonObject checkSecurityForDrill() {
        JsonObject drillWebConfigJSON = getDrillWebConfig();
        boolean securityEnabled = drillWebConfigJSON.get("securityEnabled").getAsBoolean();
        if (securityEnabled) {
            return drillWebConfigJSON;
        }
        return null;
    }

    private static String doPost(HttpClient client, String postUrl, List<NameValuePair> parameters) throws IOException {

        HttpPost postRequest = new HttpPost(postUrl);

        postRequest.setEntity(new UrlEncodedFormEntity(parameters));
        HttpResponse response = client.execute(postRequest);
        HttpEntity responseEntity = response.getEntity();
        return EntityUtils.toString(responseEntity);
    }

    private static String doGet(HttpClient client, String getUrl) throws IOException {
        HttpGet httpGet = new HttpGet(getUrl);
        HttpResponse response = client.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        return EntityUtils.toString(responseEntity);
    }

    public static String drillRestApiCall(String resourceUrl, String httpMethod, String requestQuery) {

        String result = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {

            result = doLogin(client);

            if ("POST".equalsIgnoreCase(httpMethod)) {
                HttpPost postRequest = new HttpPost(resourceUrl);
                postRequest.addHeader("Content-Type", "application/json");
                postRequest.setEntity(new ByteArrayEntity(requestQuery.getBytes(StandardCharsets.UTF_8.name())));
                HttpResponse response = client.execute(postRequest);

                HttpEntity responseEntity = response.getEntity();
                result = EntityUtils.toString(responseEntity);
            } else if ("GET".equalsIgnoreCase(httpMethod)) {
                result = doGet(client, resourceUrl);
            }
            if (result != null && result.contains("Login using FORM AUTHENTICATION")) {
                throw new EfwServiceException("Error while connecting drill. Please check the credentials and make Security Mode on");
            }
            return result;

        } catch (IOException e) {
            logger.error("There was an exception while calling drill api. ", e);
        }

        return result;



    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    public static String getUrlOfDrill() {
        JsonObject drillFileAsJson = getDrillFileAsJson();
        JsonObject urlConfig = drillFileAsJson.getAsJsonObject("urlConfig");

        String host = urlConfig.get("host").getAsString();
        String port = urlConfig.get("port").getAsString();
        String https = urlConfig.has("https") ? urlConfig.get("https").getAsString() : "http";
        return https + "://" + host + ":" + port;
    }

}
