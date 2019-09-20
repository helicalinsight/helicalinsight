/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efwce;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efw;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author Rajesh
 *         Created by author on 4/9/2019.
 */
public class DashboardIOHandler implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(DashboardIOHandler.class);
    private boolean isUpdate = false;


    static File saveHtml(String dir, String uuid, String htmlString, String cssString, JSONObject configurationJsonObject) {
        File htmlFile = new File(applicationProperties.getSolutionDirectory() + File.separator +
                dir + File.separator + uuid + ".html");
        String appendedHtmlFile = appendInFile(htmlString, cssString, configurationJsonObject, uuid);
        if (ApplicationUtilities.createAFile(htmlFile, appendedHtmlFile)) {
            logger.debug(htmlFile + " is saved successfully.");
        } else {
            throw new EfwServiceException("Error in saving file. The html couldn't be saved.");
        }

        return htmlFile;
    }

    private static String appendInFile(String htmlString, String cssString, JSONObject configurationJsonObject, String uuid) {
        String cssId = UUID.randomUUID().toString();
        String scriptId = UUID.randomUUID().toString();
        String cssStart = "<style" + " id=\"" + cssId + "\" type=\"text/css\">";
        String cssEnd = "</style>";
        String scriptStart = "<script" + " id=\"" + scriptId + "\">";
        String scriptEnd = "</script>";
        List<String> varNames = new ArrayList<>();
        String scriptString = configurationJsonObject != null ? prepareConfiguration(configurationJsonObject, varNames) : null;
        if (scriptString != null && !scriptString.isEmpty()) {
            scriptString = replaceVfNameWithUUID(scriptString, uuid);
            scriptString = replaceEFWDNameWithUUID(scriptString, uuid);

        }

        String varComponentsString = prepareComponentsString(varNames);
        String finalHtmlResult;
        String completeCssString = null;
        String completeScriptString = null;
        if (cssString != null && !cssString.isEmpty()) {
            completeCssString = cssStart + cssString + cssEnd;
        }
        if (scriptString != null && !scriptString.isEmpty()) {
            completeScriptString = scriptStart + scriptString + varComponentsString + scriptEnd;
        }
        if (completeCssString != null && completeScriptString != null) {
            finalHtmlResult = htmlString + completeCssString + completeScriptString;
        } else if (completeCssString != null && completeScriptString == null) {
            finalHtmlResult = htmlString + completeCssString;
        } else if (completeCssString == null && completeScriptString != null) {
            finalHtmlResult = htmlString + completeScriptString;
        } else {
            finalHtmlResult = htmlString;
        }

        return finalHtmlResult;
    }

    private static String replaceEFWDNameWithUUID(String scriptString, String uuid) {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        return scriptString.replaceAll(settingsJson.getString("EfwdSampleName"), uuid + "." + JsonUtils.getEfwdExtension());
    }

    private static String prepareComponentsString(List<String> varNames) {

        String preparedArrayString = toJavascriptArray(varNames);

        String sampleString = "\nvar components = " + preparedArrayString + ";\n" +
                "dashboard.init(components);";

        return sampleString;
    }

    private static String toJavascriptArray(List<String> varNames) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < varNames.size(); i++) {
            // sb.append("\"").append(varNames.get(i)).append("\"");
            sb.append(varNames.get(i));
            if (i + 1 < varNames.size()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String replaceVfNameWithUUID(String scriptString, String uuid) {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        return scriptString.replaceAll(settingsJson.getString("EfwceSampleVfName"), uuid + "." + JsonUtils.getEfwvfExtension());
    }

    private static String prepareConfiguration(JSONObject configurationJsonObject, List<String> varNames) {
        StringBuffer scriptContent = new StringBuffer();

        JSONArray parametersJsonArray = configurationJsonObject.optJSONArray("parameters");
        JSONArray reportsJsonArray = configurationJsonObject.optJSONArray("reports");

        if (parametersJsonArray != null && !parametersJsonArray.isEmpty()) {
            retrieveElement(scriptContent, parametersJsonArray, "parameter", varNames);
        }
        if (reportsJsonArray != null && !reportsJsonArray.isEmpty()) {
            retrieveElement(scriptContent, reportsJsonArray, "report", varNames);
        }
        return scriptContent.toString();
    }

    private static void retrieveElement(StringBuffer scriptContent, JSONArray jsonArray, String key, List<String> varNames) {
        for (int index = 0; index < jsonArray.size(); index++) {
            JSONObject eachElement = jsonArray.getJSONObject(index);
            JSONObject innerJson = eachElement.getJSONObject(key);
            varNames.add(innerJson.getString("name"));
            scriptContent.append(innerJson.getString("value"));
        }
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String uuid;
        if (formJson.has("uuid")) {
            uuid = formJson.getString("uuid");
            if (uuid == null || "".equals(uuid) || "".equals(uuid.trim())) {
                throw new IllegalArgumentException("The parameter uuid is null or empty");
            }
            isUpdate = true;
        } else {
            uuid = UUID.randomUUID().toString();
            isUpdate = false;
        }
        //common parameters
        String dir = formJson.getString("dir");

        //efwce parameters
        String state = formJson.optString("state");
        JSONObject formData = new JSONObject();
        formData.put("formData", jsonFormData);
        if (state == null) {
            state = formData.toString();
        } else {
            state = state + "," + formData.toString();
        }
        String name = formJson.getString("file");
        //html,css,configuration parameters
        String htmlString = formJson.optString("htmlString");
        String cssString = formJson.optString("cssString");//append it in html styles tag.
        JSONObject configurationJsonObject = formJson.optJSONObject("configurations");//contains parameters and all append it in html script tag.
        //save html file
        saveHtml(dir, uuid, htmlString, cssString, configurationJsonObject);

        //designer parameters
        JSONArray visualisationJSONArray = formJson.optJSONArray("visualisation");
        //save designer File
        if (visualisationJSONArray != null && !visualisationJSONArray.isEmpty()) {
            saveVisualisation(visualisationJSONArray, dir, uuid, JsonUtils.getEfwvfExtension());
        }

        //efwd parameters
        JSONObject efwdJsonObject = formJson.optJSONObject("efwd");
        //save efwd file
        if (efwdJsonObject != null) {
            saveEfwd(efwdJsonObject, dir, uuid, JsonUtils.getEfwdExtension());
        }

        String description = null;
        if (formJson.has("description")) {
            description = formJson.getString("description");
        }

        String icon = null;
        if (formJson.has("icon")) {
            icon = formJson.getString("icon");
        }

        String style = null;
        if (formJson.has("style")) {
            style = formJson.getString("style");
        }

        JSONObject responseJson;
        responseJson = new JSONObject();

        String efwFileWithUuid = uuid + "." + JsonUtils.getEfwExtension();
        EFWCE designer = designerObject(state, name, description, efwFileWithUuid);
        saveDesignerFile(JsonUtils.getEFWCEExtension(), designer, uuid, dir);
        efwObject(icon, style, name, description, dir, efwFileWithUuid, uuid);
        responseJson.accumulate("uuid", uuid + "." + JsonUtils.getEFWCEExtension());
        if (!isUpdate)
            responseJson.accumulate("message", "Design is saved successfully");
        else if (isUpdate)
            responseJson.accumulate("message", "Design is updated successfully");
        return responseJson.toString();
    }

    public void saveEfwd(JSONObject efwdJsonObject, String dir, String uuid, String efwdExtension) {
        EfwdForCe efwdJaxb = prepareEfwd(efwdJsonObject);
        File efwdFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + efwdExtension);
        try {
            synchronized (this) {
                JaxbUtils.marshal(efwdJaxb, efwdFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }

    }

    public EfwdForCe prepareEfwd(JSONObject efwdJsonObject) {
        EfwdForCe efwdJaxb = ApplicationContextAccessor.getBean(EfwdForCe.class);
        efwdJaxb.setDataSources(prepareDataSource(efwdJsonObject));
        efwdJaxb.setDataMaps(prepareDataMaps(efwdJsonObject));
        return efwdJaxb;
    }

    public DataMaps prepareDataMaps(JSONObject efwdJsonObject) {
        JSONArray dataMapsJSONArray = efwdJsonObject.optJSONArray("dataMaps");
        DataMaps dataMapsJaxb = ApplicationContextAccessor.getBean(DataMaps.class);
        if (dataMapsJSONArray == null || dataMapsJSONArray.isEmpty()) {
            return null;
        }
        List<DataMap> listOfDataMaps = new ArrayList<>();
        for (int index = 0; index < dataMapsJSONArray.size(); index++) {
            JSONObject dataMap = dataMapsJSONArray.getJSONObject(index).getJSONObject("dataMap");
            DataMap dataMapJaxb = ApplicationContextAccessor.getBean(DataMap.class);
            dataMapJaxb.setId(dataMap.getString("id"));
            dataMapJaxb.setName(dataMap.getString("name"));
            dataMapJaxb.setType(dataMap.getString("type"));
            dataMapJaxb.setConnection(dataMap.getString("connection"));
            dataMapJaxb.setQuery(dataMap.getString("query"));
            Parameters parameters = prepareParameters(dataMap);
            if (parameters != null)
                dataMapJaxb.setParameters(parameters);
            listOfDataMaps.add(dataMapJaxb);
        }
        dataMapsJaxb.setDataMapList(listOfDataMaps);
        return dataMapsJaxb;
    }

    public Parameters prepareParameters(JSONObject dataMapJSON) {
        JSONArray parameters = dataMapJSON.optJSONArray("parameters");
        Parameters parametersJaxb = null;
        List<Parameter> listOfParameter = new ArrayList<>();
        if (parameters != null && !parameters.isEmpty()) {
            parametersJaxb = ApplicationContextAccessor.getBean(Parameters.class);
            for (int index = 0; index < parameters.size(); index++) {
                JSONObject parameterJSON = parameters.getJSONObject(index).getJSONObject("parameter");
                Parameter parameterJaxb = ApplicationContextAccessor.getBean(Parameter.class);
                parameterJaxb.setType(parameterJSON.getString("type"));
                parameterJaxb.setName(parameterJSON.getString("name"));
                parameterJaxb.setDefaultValue(parameterJSON.getString("default"));
                if (parameterJSON.has("openQuote") && parameterJSON.has("closeQuote")) {
                    parameterJaxb.setOpenQuote(parameterJSON.getString("openQuote"));
                    parameterJaxb.setCloseQuote(parameterJSON.getString("closeQuote"));
                }
                listOfParameter.add(parameterJaxb);
            }
            parametersJaxb.setParameter(listOfParameter);
        }
        return parametersJaxb;
    }

    public DataSourcesForCe prepareDataSource(JSONObject efwdJsonObject) {
        JSONObject dataSourcesJSON = efwdJsonObject.optJSONObject("dataSources");
        if (dataSourcesJSON == null) {
            return null;
        }
        DataSourcesForCe dataSourceJaxb = ApplicationContextAccessor.getBean(DataSourcesForCe.class);
        JSONArray connectionsJSONArray = dataSourcesJSON.getJSONArray("connections");
        List<ConnectionForCe> listOfConnection = new ArrayList<>();
        for (int index = 0; index < connectionsJSONArray.size(); index++) {
            ConnectionForCe connectionJaxb = ApplicationContextAccessor.getBean(ConnectionForCe.class);
            JSONObject connectionJSON = connectionsJSONArray.getJSONObject(index).getJSONObject("connection");
            JSONObject connDetailsJSON = connectionJSON.getJSONObject("connDetails");
            if (connDetailsJSON.has("globalId")) {
                connectionJaxb.setGlobalId(connDetailsJSON.getString("globalId"));
            } else if (connDetailsJSON.has("metadataFileName") && connDetailsJSON.has("location")) {
                connectionJaxb.setLocation(connDetailsJSON.getString("location"));
                connectionJaxb.setMetadataFileName(connDetailsJSON.getString("metadataFileName"));
            } else {
                connectionJaxb.setDriver(connDetailsJSON.getString("driver"));
                connectionJaxb.setUrl(connDetailsJSON.getString("url"));
                connectionJaxb.setUser(connDetailsJSON.getString("user"));
                connectionJaxb.setPass(connDetailsJSON.getString("pass"));
            }
            connectionJaxb.setId(connectionJSON.getString("id"));
            connectionJaxb.setType(connectionJSON.getString("type"));
            if (connectionJaxb.getSecurity() == null) {
                connectionJaxb.setSecurity(SecurityUtils.securityObject());
            }
            if (connDetailsJSON.has("condition"))
                connectionJaxb.setCondition(connDetailsJSON.getString("condition"));
            listOfConnection.add(connectionJaxb);
        }
        dataSourceJaxb.setConnectionList(listOfConnection);
        return dataSourceJaxb;
    }

    public void saveVisualisation(JSONArray visualisationJSONArray, String dir, String uuid, String vfExtension) {
        Efwvf efwvf = prepareVf(visualisationJSONArray);
        File vfFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + vfExtension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(efwvf, vfFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
    }

    public Efwvf prepareVf(JSONArray visualisationJSONArray) {
        Efwvf vfJaxb = ApplicationContextAccessor.getBean(Efwvf.class);
        List<Chart> listOfCharts = new ArrayList<>();
        for (int index = 0; index < visualisationJSONArray.size(); index++) {
            JSONObject eachChartJSON = visualisationJSONArray.getJSONObject(index);
            String chartTypeName = eachChartJSON.getString("type");
            Chart chartJaxb = ApplicationContextAccessor.getBean(Chart.class);
            if ("Custom".equalsIgnoreCase(chartTypeName)) {
                CustomProp customPropJaxb = ApplicationContextAccessor.getBean(CustomProp.class);
                customPropJaxb.setDataSource(Integer.parseInt(eachChartJSON.getString("dataSource")));
                customPropJaxb.setName(eachChartJSON.getString("name"));
                customPropJaxb.setType(chartTypeName);
                customPropJaxb.setScript(eachChartJSON.getJSONObject("settings").getString("script"));
                chartJaxb.setProperty(customPropJaxb);

            } else {
                GeneralProp generalProp = ApplicationContextAccessor.getBean(GeneralProp.class);
                DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
                Document document = documentBuilder.newDocument();
                Iterator<String> keys = eachChartJSON.keys();
                List<Element> elements = new ArrayList<>();

                while (keys.hasNext()) {
                    String key = keys.next();
                    Element elem = document.createElement(key);
                    if (!"id".equalsIgnoreCase(key)) {
                        elem.appendChild(document.createTextNode(eachChartJSON.getString(key)));
                        elements.add(elem);
                    }

                }
                generalProp.setOtherElements(elements);
                chartJaxb.setGeneralProp(generalProp);
            }
            chartJaxb.setId(eachChartJSON.getString("id"));

            listOfCharts.add(chartJaxb);
        }
        vfJaxb.setCharts(listOfCharts);
        return vfJaxb;
    }

    public EFWCE designerObject(String state, String name,  String description,
                                String efwLocation) {
        EFWCE designer = ApplicationContextAccessor.getBean(EFWCE.class);

        if (description != null) {
            designer.setDescription(description);
        } else {
            designer.setDescription("Efw Dashboard ");
        }

        designer.setName(name);
        designer.setEfw(efwLocation);
        designer.setState(state);
        designer.setVisible("true");
        Security security = designer.getSecurity();
        if (security == null) {
            designer.setSecurity(SecurityUtils.securityObject());
        }
        return designer;
    }

    public void saveDesignerFile(String ddExtension, EFWCE designer, String uuid, String dir) {
        File designerFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + ddExtension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(designer, designerFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The designer couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
    }

    public void efwObject(String icon, String style, String name, String description,
                          String dir, String efwFileWithUuid, String uuid) {
        File efwFile = new File(applicationProperties.getSolutionDirectory() + File.separator +
                dir + File.separator + efwFileWithUuid);

        Efw efw = ApplicationContextAccessor.getBean(Efw.class);

        efw.setTitle(name);
        efw.setAuthor(AuthenticationUtils.getUserName());
        if (description != null) {
            efw.setDescription(description);
        } else {
            efw.setDescription("Efw File");
        }

        if (icon != null) {
            efw.setIcon(icon);
        }

        efw.setTemplate(uuid + ".html");

        efw.setVisible("true");

        if (style != null) {
            efw.setStyle(style);
        }
        Security security = efw.getSecurity();
        if (security == null) {
            efw.setSecurity(SecurityUtils.securityObject());
        }
        try {
            synchronized (this) {
                JaxbUtils.marshal(efw, efwFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The efw file couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
