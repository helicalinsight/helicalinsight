package com.helicalinsight.efwce;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
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

/**
 * @author Rajesh
 *         Created by author on 4/9/2019.
 */
public class DashboardIOHandler implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(DashboardIOHandler.class);
    private boolean isUpdate = false;

    @NotNull
    static File saveHtml(String dir, String uuid, String htmlString, String cssString, JsonObject configurationJsonObject) {
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

    private static String appendInFile(String htmlString, String cssString, JsonObject configurationJsonObject, String uuid) {
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
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        return scriptString.replaceAll(settingsJson.get("EfwdSampleName").getAsString(), uuid + "." + JsonUtils.getEfwdExtension());
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
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        return scriptString.replaceAll(settingsJson.get("EfwceSampleVfName").getAsString(), uuid + "." + JsonUtils.getEfwvfExtension());
    }

    private static String prepareConfiguration(JsonObject configurationJsonObject, List<String> varNames) {
        StringBuffer scriptContent = new StringBuffer();

        JsonArray parametersJsonArray = GsonUtility.optJsonArray(configurationJsonObject,"parameters");
        JsonArray reportsJsonArray = GsonUtility.optJsonArray(configurationJsonObject,"reports");

        if (parametersJsonArray != null && !parametersJsonArray.isEmpty()) {
            retrieveElement(scriptContent, parametersJsonArray, "parameter", varNames);
        }
        if (reportsJsonArray != null && !reportsJsonArray.isEmpty()) {
            retrieveElement(scriptContent, reportsJsonArray, "report", varNames);
        }
        return scriptContent.toString();
    }

    private static void retrieveElement(StringBuffer scriptContent, JsonArray jsonArray, String key, List<String> varNames) {
        for (int index = 0; index < jsonArray.size(); index++) {
            JsonObject eachElement = jsonArray.get(index).getAsJsonObject();
            JsonObject innerJson = eachElement.getAsJsonObject(key);
            varNames.add(innerJson.get("name").getAsString());
            scriptContent.append(innerJson.get("value").getAsString());
        }
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String uuid;
        if (formJson.has("uuid")) {
            uuid = formJson.get("uuid").getAsString();
            if (uuid == null || "".equals(uuid) || "".equals(uuid.trim())) {
                throw new IllegalArgumentException("The parameter uuid is null or empty");
            }
            isUpdate = true;
        } else {
            uuid = UUID.randomUUID().toString();
            isUpdate = false;
        }
        //common parameters
        String dir = formJson.get("dir").getAsString();

        //efwce parameters
        String state = GsonUtility.optString(formJson,"state");
        JsonObject formData = new JsonObject();
        formData.addProperty("formData", jsonFormData);
        if (state == null) {
            state = formData.toString();
        } else {
            state = state + "," + formData.toString();
        }
        String name = formJson.get("file").getAsString();
        //html,css,configuration parameters
        String htmlString = GsonUtility.optString(formJson,"htmlString");
        String cssString = GsonUtility.optString(formJson,"cssString");//append it in html styles tag.
        JsonObject configurationJsonObject = GsonUtility.optJsonObject(formJson,"configurations");//contains parameters and all append it in html script tag.
        //save html file
        saveHtml(dir, uuid, htmlString, cssString, configurationJsonObject);

        //designer parameters
        JsonArray visualisationJSONArray = GsonUtility.optJsonArray(formJson,"visualisation");
        //save designer File
        if (visualisationJSONArray != null && !visualisationJSONArray.isEmpty()) {
            saveVisualisation(visualisationJSONArray, dir, uuid, JsonUtils.getEfwvfExtension());
        }

        //efwd parameters
        JsonObject efwdJsonObject = GsonUtility.optJsonObject(formJson,"efwd");
        //save efwd file
        if (efwdJsonObject != null) {
            EFWCEUtils.saveEfwd(efwdJsonObject, dir, uuid, JsonUtils.getEfwdExtension());
        }

        String description = null;
        if (formJson.has("description")) {
            description = formJson.get("description").getAsString();
        }

        String icon = null;
        if (formJson.has("icon")) {
            icon = formJson.get("icon").getAsString();
        }

        String style = null;
        if (formJson.has("style")) {
            style = formJson.get("style").getAsString();
        }

        JsonObject responseJson;
        responseJson = new JsonObject();

        String efwFileWithUuid = uuid + "." + JsonUtils.getEfwExtension();
        EFWCE designer = designerObject(state, name, description, efwFileWithUuid);
        saveDesignerFile(JsonUtils.getEFWCEExtension(), designer, uuid, dir);
        efwObject(icon, style, name, description, dir, efwFileWithUuid, uuid);
        GsonUtility.accumulate(responseJson,"uuid", uuid + "." + JsonUtils.getEFWCEExtension());
        if (!isUpdate)
        	GsonUtility.accumulate(responseJson,"message", "Design is saved successfully");
        else if (isUpdate)
        	GsonUtility.accumulate(responseJson,"message", "Design is updated successfully");
        return responseJson.toString();
    }


    public void saveVisualisation(JsonArray visualisationJSONArray, String dir, String uuid, String vfExtension) {
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

    public Efwvf prepareVf(JsonArray visualisationJSONArray) {
        Efwvf vfJaxb = ApplicationContextAccessor.getBean(Efwvf.class);
        List<Chart> listOfCharts = new ArrayList<>();
        for (int index = 0; index < visualisationJSONArray.size(); index++) {
            JsonObject eachChartJSON = visualisationJSONArray.get(index).getAsJsonObject();
            String chartTypeName = eachChartJSON.get("type").getAsString();
            Chart chartJaxb = ApplicationContextAccessor.getBean(Chart.class);
            if ("Custom".equalsIgnoreCase(chartTypeName)) {
                CustomProp customPropJaxb = ApplicationContextAccessor.getBean(CustomProp.class);
                customPropJaxb.setDataSource(Integer.parseInt(eachChartJSON.get("dataSource").getAsString()));
                customPropJaxb.setName(eachChartJSON.get("name").getAsString());
                customPropJaxb.setType(chartTypeName);
                customPropJaxb.setScript(eachChartJSON.getAsJsonObject("settings").get("script").getAsString());
                chartJaxb.setProperty(customPropJaxb);

            } else {
                GeneralProp generalProp = ApplicationContextAccessor.getBean(GeneralProp.class);
                DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
                Document document = documentBuilder.newDocument();
                Iterator<String> keys = eachChartJSON.keySet().iterator();
                List<Element> elements = new ArrayList<>();

                while (keys.hasNext()) {
                    String key = keys.next();
                    Element elem = document.createElement(key);
                    if (!"id".equalsIgnoreCase(key)) {
                        elem.appendChild(document.createTextNode(eachChartJSON.get(key).getAsString()));
                        elements.add(elem);
                    }

                }
                generalProp.setOtherElements(elements);
                chartJaxb.setGeneralProp(generalProp);
            }
            chartJaxb.setId(eachChartJSON.get("id").getAsString());

            listOfCharts.add(chartJaxb);
        }
        vfJaxb.setCharts(listOfCharts);
        return vfJaxb;
    }

    public EFWCE designerObject(String state, String name, @Nullable String description,
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

    public void efwObject(@Nullable String icon, @Nullable String style, String name, @Nullable String description,
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
