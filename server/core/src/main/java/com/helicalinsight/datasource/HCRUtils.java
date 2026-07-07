package com.helicalinsight.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIHcrQueryParameters;
import com.helicalinsight.admin.model.HiHcrQuery;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SplitterUtils;
import com.lowagie.text.FontFactory;

import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDatasetParameter;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


public class HCRUtils {
    private static final Map<String, JsonObject> hcrDefaultComponentMap = new HashMap<>();
    private static final Set<String> REQUIRED_BANDS = Set.of("columnFooter", "columnHeader", "lastPageFooter", "noData", "summary", "pageFooter", "details", "pageHeader", "title", "groups", "groupHeader", "groupFooter");
    private static final Set<String> ARRAY_BANDS = Set.of("details", "groups");
    public static final String BEAN_DATASOURCE = "bean-datasource";


    private static final Set<String> IGNORE_MISSING = Set.of("textField", "subReport", "customVisualization", "groups");
    private static final Set<String> ignoreExpression = Set.of("$P{REPORT_CONNECTION}", "new net.sf.jasperreports.engine.JREmptyDataSource()");

    private static final String LAZY_SUB_DATASET_FACTORY_CLASS = "com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory";

    private static final PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
    private static final Map<String, String> propertiesMapReplace = propertiesFileReader.read("Admin", "hcrConfigProperties.properties", "replace.strategy");


    public static final String PROPERTY_FORE_COLOR = "foreColor";
    public static final String PROPERTY_BACK_COLOR = "backColor";
    public static final String PROPERTY_BORDER = "border";
    public static final String PROPERTY_PADDING = "padding";
    public static final String PROPERTY_COMPONENT_ELEMENT_PROPS = "componentElementProperties";
    public static final String HCR_SCRIPTS_PATH = "Admin/Static/HCRScripts";

    private static final String PROP_FONT_DIR = "fontDirectories";
    private static String fontDirHash = "";

    private static final Map<String, Byte> chartTypeMap = new HashMap<>();


    private static final Logger logger = LoggerFactory.getLogger(HCRUtils.class);

    private static Map<String, Byte> createChartTypeMap() {
        chartTypeMap.put("AREA", JRChart.CHART_TYPE_AREA);
        chartTypeMap.put("BAR", JRChart.CHART_TYPE_BAR);
        chartTypeMap.put("BAR3D", JRChart.CHART_TYPE_BAR3D);
        chartTypeMap.put("BUBBLE", JRChart.CHART_TYPE_BUBBLE);
        chartTypeMap.put("CANDLESTICK", JRChart.CHART_TYPE_CANDLESTICK);
        chartTypeMap.put("HIGHLOW", JRChart.CHART_TYPE_HIGHLOW);
        chartTypeMap.put("LINE", JRChart.CHART_TYPE_LINE);
        chartTypeMap.put("METER", JRChart.CHART_TYPE_METER);
        chartTypeMap.put("MULTI_AXIS", JRChart.CHART_TYPE_MULTI_AXIS);
        chartTypeMap.put("PIE", JRChart.CHART_TYPE_PIE);
        chartTypeMap.put("PIE3D", JRChart.CHART_TYPE_PIE3D);
        chartTypeMap.put("SCATTER", JRChart.CHART_TYPE_SCATTER);
        chartTypeMap.put("STACKEDBAR", JRChart.CHART_TYPE_STACKEDBAR);
        chartTypeMap.put("STACKEDBAR3D", JRChart.CHART_TYPE_STACKEDBAR3D);
        chartTypeMap.put("STACKEDAREA", JRChart.CHART_TYPE_STACKEDAREA);
        chartTypeMap.put("TIMESERIES", JRChart.CHART_TYPE_TIMESERIES);
        chartTypeMap.put("XYAREA", JRChart.CHART_TYPE_XYAREA);
        chartTypeMap.put("XYBAR", JRChart.CHART_TYPE_XYBAR);
        chartTypeMap.put("XYLINE", JRChart.CHART_TYPE_XYLINE);
        chartTypeMap.put("GANTT", JRChart.CHART_TYPE_GANTT);
        return chartTypeMap;
    }

    static {
        String path = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator + "Static" + File.separator + "HCR";
        File folder = new File(path);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (File eachDefaultConfigFile : files) {
                JsonObject config = JsonParser.parseString(JsonUtils.getFileAsString(eachDefaultConfigFile.getAbsolutePath())).getAsJsonObject();
                hcrDefaultComponentMap.put(FileNameUtils.getBaseName(eachDefaultConfigFile.getName()), config);
            }
        }

        createChartTypeMap();
    }


    public static JsonObject prepareConnectionJson(String connectionId) {
        connectionId = connectionId.replace("hi_hcr_db", "");
        EFWDConnectionService efwdConnectionService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
        HIHcrConnections item = efwdConnectionService.fetchHIHcrConnectionsById(Integer.valueOf(connectionId));
        // Create the outer JsonObject
        JsonObject rootObject = new JsonObject();

        // Create DataSources JsonObject
        JsonObject dataSourcesObject = new JsonObject();
        JsonObject connectionObject = new JsonObject();
        // Populate the connection object
        connectionObject.addProperty("id", "1");
        String type = item != null ? item.getConnectionType() : "";
        connectionObject.addProperty("type", type);
        if (type.equals("sql.jdbc") || type.equals("sql.jdbc.groovy") || type.equals("sql.jdbc.groovy.managed")) {
            connectionObject.addProperty("efwdId", item.getHiHcrConnectionsEfwd().getHiEfwdConnection().getId());
        } else {
            connectionObject.addProperty("globalId", item != null ? item.getHiHcrConnectionsGlobal().getGlobalConnections().getGlobalId() : 0);
        }
        // Add connection to DataSources
        dataSourcesObject.add("Connection", connectionObject);
        rootObject.add("DataSources", dataSourcesObject);

        // Create DataMaps JsonObject
        JsonObject dataMapsObject = new JsonObject();
        JsonObject dataMapObject = new JsonObject();
        JsonObject parametersObject = new JsonObject();
        JsonArray parametersArray = new JsonArray();

        if (item != null) {
            HiHcrQuery hiHcrQuery = item.getHiHcrQuery();
            List<HIHcrQueryParameters> queryParameters = hiHcrQuery.getHiHcrQueryParameters();
            for (HIHcrQueryParameters queryParameter : queryParameters) {
                JsonObject parameterObject = new JsonObject();
                parameterObject.addProperty("name", queryParameter.getParameterName());
                parameterObject.addProperty("type", queryParameter.getParameterType());
                parameterObject.addProperty("default", queryParameter.getParamDefaultValue());
                parameterObject.addProperty("openQuote", queryParameter.getOpenQuotes());
                parameterObject.addProperty("closeQuote", queryParameter.getCloseQuotes());
                parametersArray.add(parameterObject);
            }

            // Populate the parameters object
            if (queryParameters.size() == 1) {
                parametersObject.add("Parameter", parametersArray.get(0));
            } else {
                parametersObject.add("Parameter", parametersArray);
            }


            // Populate the data map object
            dataMapObject.addProperty("id", "1");
            dataMapObject.addProperty("connection", 1);
            dataMapObject.addProperty("type", hiHcrQuery.getHcrQueryType());
            dataMapObject.addProperty("Name", hiHcrQuery.getHcrQueryName());
            dataMapObject.addProperty("Query", hiHcrQuery.getHcrQueryString());
            dataMapObject.add("Parameters", parametersObject);
        }
        // Add data map to DataMaps
        dataMapsObject.add("DataMap", dataMapObject);
        rootObject.add("DataMaps", dataMapsObject);

        // Print the resulting JsonObject
        return rootObject;
    }

    private static JsonObject loadDefaultConfig(String key) {
        return hcrDefaultComponentMap.get(key.equals("staticText") ? "textField" : key);
    }


    private static Boolean isRequiredKeyForJsonArray(String key) {
        return hcrDefaultComponentMap.containsKey(key) || key.equals("staticText");
    }


    public static JsonObject checkForDefaultValues(JsonObject formData) {
        JsonObject defaultJson = loadHcrPreviewStaticData("HcrPreviewFormdataConfig" + JsonUtils.JSON_EXTENSION);
        JsonObject designerProps = GsonUtility.optJsonObject(formData, "designerProperties");
        JsonObject designerProps2 = defaultJson.getAsJsonObject("formData").getAsJsonObject("designerProperties");
        if (designerProps == null) formData.add("designerProperties", designerProps2);
        else copyDefault(designerProps2, designerProps);
        return formData;
    }

    private static JsonObject loadHcrPreviewStaticData(String location) {
        JsonObject textField = null;
        String path = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator + "Static" + File.separator + location;
        File file = new File(path);
        if (file.exists()) {
            textField = JsonParser.parseString(JsonUtils.getFileAsString(path)).getAsJsonObject();
        }
        return textField;
    }


    private static JsonObject getSampleDefault(String key, JsonArray defaultArray) {
        if (isRequiredKeyForJsonArray(key)) {
            return loadDefaultConfig(key);
        }
        if (!defaultArray.isEmpty() && (!REQUIRED_BANDS.contains(key) || ARRAY_BANDS.contains(key))) {
            return defaultArray.get(0).getAsJsonObject();
        }
        return new JsonObject();
    }


    private static void addMissingKey(String key, JsonElement defaultElement, JsonObject formData) {

        if (REQUIRED_BANDS.contains(key)) return;

        if (isRequiredKeyForJsonArray(key) && !IGNORE_MISSING.contains(key)) {
            formData.add(key, loadDefaultConfig(key));
        } else {
            formData.add(key, defaultElement);
        }
    }


    private static void mergeExistingKey(String key, JsonElement defaultElement, JsonObject formData) {
        if (defaultElement.isJsonObject()) {
            mergeObject(defaultElement.getAsJsonObject(), formData.getAsJsonObject(key));
            return;
        }
        if (defaultElement.isJsonArray()) {

            mergeArray(key, defaultElement.getAsJsonArray(), formData.getAsJsonArray(key));
        }
    }

    private static void mergeObject(JsonObject defaultObject, JsonObject formObject) {
        copyDefault(defaultObject, formObject);
    }

    private static void mergeArray(String key, JsonArray defaultArray, JsonArray formArray) {
        JsonObject sampleDefault = getSampleDefault(key, defaultArray);

        if (sampleDefault == null || sampleDefault.keySet().isEmpty()) {
            return;
        }

        if (formArray.isEmpty() && !IGNORE_MISSING.contains(key)) {
            formArray.addAll(defaultArray);
            return;
        }

        for (int i = 0; i < formArray.size(); i++) {
            JsonObject formElement = getFromArrayElement(key, formArray, i);
            copyDefault(sampleDefault, formElement);
        }
    }

    private static JsonObject getFromArrayElement(String key, JsonArray formArray, int index) {
        JsonObject element = formArray.get(index).getAsJsonObject();
        if (hcrDefaultComponentMap.containsKey(key)) {
            JsonObject wrapped = new JsonObject();
            wrapped.add(key, element);
            return wrapped;
        }
        return element;
    }

    private static Map<String, JsonObject> indexStylesByName(JsonArray formStyles) {

        Map<String, JsonObject> map = new HashMap<>();

        for (JsonElement el : formStyles) {
            JsonObject style = el.getAsJsonObject();
            if (style.has("name")) {
                map.put(style.get("name").getAsString(), style);
            }
        }

        return map;
    }


    private static void mergeDesignerStyles(JsonArray defaultStyles, JsonArray formStyles) {

        if (defaultStyles == null || defaultStyles.isEmpty()) {
            return;
        }

        Map<String, JsonObject> formStyleByName = indexStylesByName(formStyles);

        for (JsonElement defaultEl : defaultStyles) {
            JsonObject defaultStyle = defaultEl.getAsJsonObject();
            String name = defaultStyle.get("name").getAsString();

            if (formStyleByName.containsKey(name)) {
                JsonObject existingStyle = formStyleByName.get(name);
                copyDefault(defaultStyle, existingStyle);
            } else {
                formStyles.add(defaultStyle.deepCopy());
            }
        }
    }


    public static void copyDefault(JsonObject defaultJson, JsonObject formData) {

        for (String key : defaultJson.keySet()) {

            if ("designerStyle".equals(key) && formData.has(key) && defaultJson.get(key).isJsonArray()) {
                mergeDesignerStyles(defaultJson.getAsJsonArray(key), formData.getAsJsonArray(key));
                continue;
            }

            JsonElement defaultElement = defaultJson.get(key);
            if (formData.has(key)) {
                mergeExistingKey(key, defaultElement, formData);
            } else {
                addMissingKey(key, defaultElement, formData);
            }
        }
    }

    public static byte getChartTypeByName(String chartType) {
        return chartTypeMap.get(chartType.toUpperCase());
    }


    public static Color getColorFromJson(JsonObject json, String key) {
        if (json.has(key)) {
            String colorString = json.get(key).getAsString();
            if (StringUtils.isNotBlank(colorString)) {
                return Color.decode(colorString);
            }
        }
        return null;
    }


    public static JRBaseFont configureFont(JsonObject fontJson) {
        JRBaseFont font = new JRBaseFont();
        font.setFontName(GsonUtility.optStringValue(fontJson, "fontName", "SansSerif"));
        font.setFontSize(Float.valueOf(GsonUtility.optStringValue(fontJson, "size", "10")));
        font.setBold(Boolean.valueOf(GsonUtility.optBooleanValue(fontJson, "isBold", false)));
        font.setItalic(Boolean.valueOf(GsonUtility.optBooleanValue(fontJson, "isItalic", false)));
        font.setUnderline(Boolean.valueOf(GsonUtility.optBooleanValue(fontJson, "isUnderline", false)));
        font.setStrikeThrough(Boolean.valueOf(GsonUtility.optBooleanValue(fontJson, "isStrikeThrough", false)));

        return font;

    }

    public static JRBaseFont getFontFromJson(JsonObject json, String key) {
        JsonObject fontObject = json.has(key) ? json.getAsJsonObject(key) : new JsonObject();
        return configureFont(fontObject);
    }

    public static JRDesignExpression getExpressionFromJson(JsonObject json, String key) {
        if (json.has(key)) {
            String expressionText = GsonUtility.optString(json, key);
            if (StringUtils.isNotBlank(expressionText)) {
                JRDesignExpression expression = new JRDesignExpression();
                expression.setText(expressionText);
                return expression;
            }
        }
        return null;
    }

    public static JRDesignExpression getDataSetExpressionFromJson(JsonObject json, String key) {
        if (json.has(key)) {
            String expressionText = GsonUtility.optString(json, key);
            if (StringUtils.isNotBlank(expressionText) && !ignoreExpression.contains(expressionText)) {
                JRDesignExpression expression = new JRDesignExpression();
                final String finalExpression = expressionText + ".create()";
                expression.setText(finalExpression);
                return expression;
            }
        }
        return null;
    }

    /**
     * Configures a {@link JRDesignDatasetRun} for any subdataset-bound component.
     * Always uses the lazy resolve pipeline. Main-to-subdataset parameter mapping
     * expressions ($F{}, $V{}, $P{}, etc.) are configured via
     * {@link #configureDatasetRunParameters(JRDesignDatasetRun, JsonObject)}.
     */
    public static JRDesignDatasetRun configureSubDatasetRun(JsonObject dataSetRunJson) {
        JRDesignDatasetRun datasetRun = new JRDesignDatasetRun();
        datasetRun.setDatasetName(dataSetRunJson.get("dataSetName").getAsString());
        configureDatasetRunParameters(datasetRun, dataSetRunJson);

        String dataSetExpression = GsonUtility.optStringValue(dataSetRunJson, "dataSetExpression", "");
        if (ignoreExpression.contains(dataSetExpression)) {
            JRDesignExpression legacyExpression = getDataSetExpressionFromJson(dataSetRunJson, "dataSetExpression");
            if (legacyExpression != null) {
                datasetRun.setDataSourceExpression(legacyExpression);
            }
            return datasetRun;
        }

        String factoryParamName = extractReportParameterName(dataSetExpression);
        if (StringUtils.isBlank(factoryParamName)) {
            throw new EfwServiceException("Subdataset dataSetRun requires dataSetExpression in the form $P{ReportParameterName}.");
        }

        java.util.List<String[]> mappedParameters = extractDatasetRunParameters(dataSetRunJson);
        datasetRun.setDataSourceExpression(buildLazyResolveExpression(factoryParamName, mappedParameters));
        return datasetRun;
    }

    public static String extractReportParameterName(String dataSetExpression) {
        if (StringUtils.isBlank(dataSetExpression)) {
            return null;
        }
        final String prefix = "$P{";
        if (dataSetExpression.startsWith(prefix) && dataSetExpression.endsWith("}")) {
            return dataSetExpression.substring(prefix.length(), dataSetExpression.length() - 1);
        }
        return null;
    }

    /**
     * Returns a list of [name, parentScopeExpression] pairs for each parameter
     * declared in the {@code parameters} array of the dataset-run JSON.
     * <p>
     * Each entry is a two-element array: {@code [name, expression]}. The
     * {@code expression} is the <em>parent-report</em> expression text (e.g.
     * {@code $F{mode_of_payment}}) taken directly from the JSON mapping. It is
     * intentionally kept as raw text so that {@link #buildLazyResolveExpression}
     * can embed it verbatim into the generated Jasper expression, letting Jasper
     * evaluate it in the parent scope.
     */
    public static java.util.List<String[]> extractDatasetRunParameters(JsonObject dataSetRunJson) {
        java.util.List<String[]> pairs = new java.util.ArrayList<>();
        JsonArray parameterArray = GsonUtility.optJsonArray(dataSetRunJson, "parameters");
        if (parameterArray == null) {
            return pairs;
        }
        for (JsonElement element : parameterArray) {
            JsonObject param = element.getAsJsonObject();
            String name = GsonUtility.optString(param, "name");
            String expression = GsonUtility.optString(param, "expression");
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(expression)) {
                pairs.add(new String[]{name, expression});
            }
        }
        return pairs;
    }

    /**
     * Builds the Jasper {@code dataSourceExpression} that defers subdataset query
     * execution to the lazy factory.
     * <p>
     * Each mapped parameter contributes a name literal and its parent-scope
     * expression to the varargs of {@code resolve()}. For example:
     * <pre>
     * LazySubDatasetDataSourceFactory.resolve($P{TravelData},
     *     "mode_of_payment", $F{mode_of_payment})
     * </pre>
     * The value expression ({@code $F{mode_of_payment}}) is evaluated by Jasper
     * <em>in the parent report scope</em>, so fields, variables, and parameters
     * of the parent report are all accessible.
     */
    public static JRDesignExpression buildLazyResolveExpression(String factoryParamName, java.util.List<String[]> mappedParameters) {
        StringBuilder expressionText = new StringBuilder(LAZY_SUB_DATASET_FACTORY_CLASS).append(".resolve($P{").append(factoryParamName).append("}");

        if (mappedParameters != null) {
            for (String[] pair : mappedParameters) {
                String name = pair[0];
                String valueExpression = pair[1]; // parent-scope expression, e.g. $F{mode_of_payment}
                expressionText.append(", \"").append(name).append("\", ").append(valueExpression);
            }
        }
        expressionText.append(")");

        JRDesignExpression expression = new JRDesignExpression();
        expression.setText(expressionText.toString());
        return expression;
    }


    public static JsonArray extractAllObjectsFromBands(String previewFormData, String key) {

        JsonArray array = new JsonArray();

        JsonElement jsonElement = JsonParser.parseString(previewFormData);

        if (!jsonElement.isJsonObject()) {
            return array;
        }

        JsonObject formData = jsonElement.getAsJsonObject().getAsJsonObject("designerProperties");

        for (String band : REQUIRED_BANDS) {

            JsonElement bandElement = formData.get(band);

            if (bandElement != null) {
                if (bandElement.isJsonObject()) {
                    JsonObject bandJson = bandElement.getAsJsonObject();
                    if (bandJson.has(key)) {
                        array.addAll(bandJson.get(key).getAsJsonArray());
                    }
                } else if (bandElement.isJsonArray()) {
                    for (JsonElement element : bandElement.getAsJsonArray()) {
                        JsonObject eachObject = element.getAsJsonObject();
                        if (eachObject.has(key)) {
                            array.addAll(eachObject.get(key).getAsJsonArray());
                        }
                    }
                }
            }
        }
        return array;
    }

    
    
	public static void applyStyleReference(JsonObject componentJson, Consumer<String> setter) {

		if (!componentJson.has("styleNameReference")) {
			return;
		}

		String styleNameReference = GsonUtility.optString(componentJson,"styleNameReference"); 
		if (StringUtils.isNotBlank(styleNameReference)) {
			setter.accept(styleNameReference);
		}
	}

    public static void configureComponentElementProperties(JRDesignElement component, JsonObject formData) {

        JsonObject componentElementProperties = GsonUtility.optJsonObject(formData, "componentElementProperties");

        if (componentElementProperties == null) return;

        component.setHeight(componentElementProperties.get("height").getAsInt());
        component.setWidth(componentElementProperties.get("width").getAsInt());
        component.setX(componentElementProperties.get("X").getAsInt());
        component.setY(componentElementProperties.get("Y").getAsInt());
        component.setPositionType(PositionTypeEnum.getByName(componentElementProperties.get("positionType").getAsString()));
        component.setStretchType(StretchTypeEnum.getByName(componentElementProperties.get("stretchType").getAsString()));
        component.setMode(ModeEnum.getByName(componentElementProperties.get("mode").getAsString()));
        component.setForecolor(HCRUtils.getColorFromJson(componentElementProperties, PROPERTY_FORE_COLOR));
        component.setBackcolor(HCRUtils.getColorFromJson(componentElementProperties, PROPERTY_BACK_COLOR));

        JRDesignExpression printWhenExpression = getExpressionFromJson(componentElementProperties, "printWhenExpression");
        if (printWhenExpression != null) {
            component.setPrintWhenExpression(printWhenExpression);
        }
        component.setPrintRepeatedValues(GsonUtility.optBooleanValue(componentElementProperties, "printRepeatedValues", false));
        component.setPrintInFirstWholeBand(GsonUtility.optBooleanValue(componentElementProperties, "printInFirstWholeBand", false));
        component.setRemoveLineWhenBlank(GsonUtility.optBooleanValue(componentElementProperties, "removeLineWhenBlank", false));
        component.setPrintWhenDetailOverflows(GsonUtility.optBooleanValue(componentElementProperties, "printWhenDetailOverflows", false));
        JsonObject borderJson = GsonUtility.optJsonObject(componentElementProperties, PROPERTY_BORDER);
        movePaddingFromBorder(borderJson, componentElementProperties);
        prepareBorder(component, borderJson);
        preparePadding(component, GsonUtility.optJsonObject(componentElementProperties, PROPERTY_PADDING));
        JsonObject customSettings = GsonUtility.optJsonObject(componentElementProperties, "customSettings");
        applyCustomProperties(component, customSettings);
    }


    public static void applyCustomProperties(JRPropertiesHolder component, JsonObject customSettings) {

        if (customSettings == null || component == null) return;

        Set<String> set = customSettings.keySet();
        String blankPrefix = propertiesMapReplace.get("replace.strategy.blank");
        for (String props : set) {
            for (Map.Entry<String, String> entry : propertiesMapReplace.entrySet()) {
                String key = entry.getKey();
                String result = key.substring("replace.strategy.".length());
                String value = entry.getValue();
                if (props.startsWith(result) && !"blank".equals(result)) {
                    String propNameJasperPrefix = value + props;
                    component.getPropertiesMap().setProperty(propNameJasperPrefix, GsonUtility.optString(customSettings, props));
                } else if (result.equals("blank")) {
                    if (!props.startsWith(blankPrefix)) {
                        String propNameJasperPrefix = blankPrefix + props;
                        component.getPropertiesMap().setProperty(propNameJasperPrefix, GsonUtility.optString(customSettings, props));
                    }
                }
            }

            if (propertiesMapReplace.isEmpty()) {
                component.getPropertiesMap().setProperty(props, GsonUtility.optString(customSettings, props));
            }

        }
    }


    /**
     * If component extends/implements both JRDesignElement & JRBoxContainer , add the casting explicitly
     * Fox ex: JRDesignImage , JRDesignTextField
     *
     * @param component
     * @param borderJson
     */
    public static void prepareBorder(JRDesignElement component, JsonObject borderJson) {
        if (component instanceof JRBoxContainer container) {
            prepareBorder(container, borderJson);
        }
    }

    public static void preparePadding(JRDesignElement component, JsonObject paddingJson) {
        if (component instanceof JRBoxContainer container) {
            preparePadding(container, paddingJson);
        }
    }

    public static void prepareBorder(JRBoxContainer component, JsonObject borderJson) {
        prepareLineBox(borderJson, component.getLineBox());
    }

    public static void preparePadding(JRBoxContainer component, JsonObject paddingJson) {
        preparePadding(paddingJson, component.getLineBox());
    }


    public static List<String> findAllDependencies() {
        String systemDir = ApplicationProperties.getInstance().getSystemDirectory();
        String dependenciesPath = Paths.get(systemDir, "Admin", "Static", "HCRScripts", "dependencies").toString();
        File dependenciesFolder = new File(dependenciesPath);
        List<String> deps = new LinkedList<String>();
        if (dependenciesFolder.exists()) {
            String[] arr = dependenciesFolder.list();
            for (String dep : arr) {
                deps.add(FilenameUtils.getBaseName(dep));
            }
        }
        return deps;
    }

    public static String getFileAsString(Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath), ControllerUtils.defaultCharSet());
        } catch (IOException ioe) {
            throw new RuntimeException("There was a problem in the operation" + " " + ioe.getMessage());
        }
    }


    public static void writeToFile(String fileData, String dir, String fileName) {

        String filePath = String.join(File.separator, dir, fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            logger.info("Writing file {} to {}", dir, fileName);
            byte[] bytes = fileData.getBytes(StandardCharsets.UTF_8);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
        } catch (IOException e) {
            logger.error("Error occurred while writing the files into directory {}", dir);
        }
    }


    public static void loadFontDirectories() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> propertiesMap = propertiesFileReader.read("Admin", "hcrConfigProperties.properties");
        String fontDirs = propertiesMap.getOrDefault(PROP_FONT_DIR, "");
        String hash = SplitterUtils.prepareServiceId(fontDirs);
        if (fontDirHash.equals(hash)) {
            return;
        }
        String[] dirs = fontDirs.split(",");
        for (String dir : dirs) {
            logger.debug("Loading font dir : {}", dir);
            FontFactory.registerDirectory(dir, true);
        }
        fontDirHash = hash;
    }

    public static Boolean fontExists(String fontName) {
        return FontFactory.isRegistered(fontName.toLowerCase());
    }


    public static boolean loadHcrPropertiesByPrefix(String prefix) {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> propertiesMap = propertiesFileReader.read("Admin", "hcrConfigProperties.properties", prefix);
        Set<String> keys = propertiesMap.keySet();
        JRPropertiesUtil jrPropUtil = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
        keys.forEach(key -> {
            jrPropUtil.setProperty(key, propertiesMap.get(key));
        });
        return true;
    }

    public static String textToImage(String text, int imageWidth, int imageHeight) {

        Font font = new Font("Arial", Font.PLAIN, 12);
        int padding = 20;

        int charWidth = 14;  // average width per character
        int width = Math.max(imageWidth, text.length() * charWidth / 2);
        int height = Math.max(imageHeight, (width / 4) + padding);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setFont(font);
        g2d.setColor(Color.RED);
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int x = (width - textWidth) / 2;
        int y = ((height - textHeight) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);

        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }


    public static void prepareLineBox(JsonObject borderJson, JRLineBox lineBox) {

        if (lineBox == null || borderJson == null || borderJson.keySet().isEmpty()) {
            return;
        }

        if (borderJson.has("line")) {
            JsonObject lineJson = borderJson.getAsJsonObject("line");
            // leftPen
            if (lineJson.has("leftLine")) {
                JsonObject leftLine = lineJson.getAsJsonObject("leftLine");
                JRBoxPen leftPen = lineBox.getLeftPen();
                prepareCommonPen(leftLine, leftPen);
            }
            // rightPen
            if (lineJson.has("rightLine")) {
                JsonObject rightLine = lineJson.getAsJsonObject("rightLine");
                JRBoxPen rightPen = lineBox.getRightPen();
                prepareCommonPen(rightLine, rightPen);
            }
            // bottomPen
            if (lineJson.has("bottomLine")) {
                JsonObject bottomLine = lineJson.getAsJsonObject("bottomLine");
                JRBoxPen bottomPen = lineBox.getBottomPen();
                prepareCommonPen(bottomLine, bottomPen);
            }
            // topPen
            if (lineJson.has("topLine")) {
                JsonObject topLine = lineJson.getAsJsonObject("topLine");
                JRBoxPen topPen = lineBox.getTopPen();
                prepareCommonPen(topLine, topPen);
            }
            if (lineJson.has("pen")) {
                JsonObject penLine = lineJson.getAsJsonObject("pen");
                JRBoxPen pen = lineBox.getPen();
                prepareCommonPen(penLine, pen);
            }
        }
    }

    private static void prepareCommonPen(JsonObject lineJson, JRBoxPen pen) {
        pen.setLineWidth(Float.valueOf(GsonUtility.optString(lineJson, "lineWidth")));
        if (lineJson.has("lineColor")) pen.setLineColor(Color.decode(lineJson.get("lineColor").getAsString()));
        if (lineJson.has("lineStyle"))
            pen.setLineStyle(LineStyleEnum.getByName(lineJson.get("lineStyle").getAsString()));

    }

    public static void preparePadding(JsonObject paddingJson, JRLineBox lineBox) {

        if (lineBox == null || paddingJson == null || paddingJson.keySet().isEmpty()) return;

        Function<String, Integer> getPaddingValue = key -> Integer.valueOf(GsonUtility.optIntValue(paddingJson, key, 0));

        if (paddingJson.has(PROPERTY_PADDING)) {
            lineBox.setPadding(getPaddingValue.apply(PROPERTY_PADDING));
            return;
        }

        lineBox.setBottomPadding(getPaddingValue.apply("bottomPadding"));
        lineBox.setTopPadding(getPaddingValue.apply("topPadding"));
        lineBox.setLeftPadding(getPaddingValue.apply("leftPadding"));
        lineBox.setRightPadding(getPaddingValue.apply("rightPadding"));
    }


    public static void movePaddingFromBorder(JsonObject borderJson, JsonObject parentJson) {
        if (borderJson != null && borderJson.has(PROPERTY_PADDING)) {
            JsonObject paddingJson = GsonUtility.optJsonObject(borderJson, PROPERTY_PADDING);
            parentJson.add(PROPERTY_PADDING, paddingJson);
            borderJson.remove(PROPERTY_PADDING);
        }
    }

    public static void configureDatasetRunParameters(JRDesignDatasetRun datasetRun, JsonObject dataSetRunJson) {
        JsonArray parameterArray = GsonUtility.optJsonArray(dataSetRunJson, "parameters");
        if (parameterArray == null || parameterArray.isEmpty()) {
            return;
        }
        for (JsonElement element : parameterArray) {
            JsonObject parameter = element.getAsJsonObject();
            JRDesignExpression parameterExpression = getExpressionFromJson(parameter, "expression");
            if (parameterExpression == null) {
                continue;
            }
            JRDesignDatasetParameter datasetParameter = new JRDesignDatasetParameter();
            datasetParameter.setName(GsonUtility.optString(parameter, "name"));
            datasetParameter.setExpression(parameterExpression);
            try {
                datasetRun.addParameter(datasetParameter);
            } catch (JRException e) {
                throw new EfwServiceException(e.getMessage());
            }
        }
    }
}