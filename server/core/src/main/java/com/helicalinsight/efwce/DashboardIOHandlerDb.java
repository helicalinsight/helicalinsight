/*package com.helicalinsight.efwce;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.model.HIResourceEFWCE;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.managed.jaxb.Chart;
import com.helicalinsight.datasource.managed.jaxb.CustomProp;
import com.helicalinsight.datasource.managed.jaxb.Efwvf;
import com.helicalinsight.datasource.managed.jaxb.GeneralProp;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

*
 * @author Rajesh


public class DashboardIOHandlerDb implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(DashboardIOHandler.class);

    @NotNull
    static ResourceEfwContents saveHtml(HIResource parentDirectory, String uuid, String appendedHtmlFile) {
        ResourceEfwContents efwContents = new ResourceEfwContents();
        efwContents.setResourceId(parentDirectory.getResourceId());
        efwContents.setContentType("html");
        efwContents.setFileName(uuid + ".html");

        efwContents.setContent(appendedHtmlFile.getBytes());
        return efwContents;

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
        } else if (completeCssString != null) {
            finalHtmlResult = htmlString + completeCssString;
        } else if (completeScriptString != null) {
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
        String uuid = null;
        Boolean isUpdate = false;
        if (formJson.has("uuid")) {
            uuid = formJson.optString("uuid");
            if (StringUtils.isBlank(uuid)) {
                throw new IllegalArgumentException("The parameter uuid is null or empty");
            }
            isUpdate = true;
        }
        //common parameters
        String dir = formJson.getString("dir");
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResource parentDirectory = serviceDB.getResourceByUrl(dir);


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
        String appendedHtmlFile = appendInFile(htmlString, cssString, configurationJsonObject, uuid);
        ResourceEfwContents efwContents = saveHtml(parentDirectory, uuid, appendedHtmlFile);
        serviceDB.addHIResourceEFWContents(efwContents);

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
            EFWCEUtils.saveEfwd(efwdJsonObject, dir, uuid, JsonUtils.getEfwdExtension());
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
        HIResourceEFWCE designer = designerObject(state, name, description, null);
        //saveDesignerFile(JsonUtils.getEFWCEExtension(), designer, uuid, dir);
        efwObject(icon, style, name, description, dir, efwFileWithUuid, uuid);
        responseJson.accumulate("uuid", uuid + "." + JsonUtils.getEFWCEExtension());

        if (!isUpdate)
            responseJson.accumulate("message", "Design is saved successfully");
        else if (isUpdate)
            responseJson.accumulate("message", "Design is updated successfully");
        return responseJson.toString();
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

    public HIResourceEFWCE designerObject(String state, String name, @Nullable String description,
                                          Integer efwResourceId) {
        HIResourceEFWCE designer = new HIResourceEFWCE();

        if (description != null) {
            designer.setDescription(description);
        } else {
            designer.setDescription("Efw Dashboard ");
        }
        designer.setFileName(name);
        designer.setEfw(efwResourceId);
        designer.setState(state);
        designer.setVisible(true);

        return designer;
    }


    public void efwObject(@Nullable String icon, @Nullable String style, String name, @Nullable String description,
                          String dir, String efwFileWithUuid, String uuid) {
        HIResourceEFW efw = new HIResourceEFW();

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
*/