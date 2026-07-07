package com.helicalinsight.efw.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.EfwException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 9/19/2019.
 *
 * @author Rajesh
 */
public class JRXMLUtils {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(JRXMLUtils.class);
    private static JsonObject data;

    public static byte[] executeReport() {
        byte[] export = null;
        String dir = data.get("dir").getAsString();
        String fileName = data.get("file").getAsString();
        String format = data.has("format") ? data.get("format").getAsString().toUpperCase() : "HTML";

        String reportFile = applicationProperties.getSolutionDirectory() + File.separator + dir + File.separator + fileName;
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(reportFile);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            // JasperCompileManager.writeReportToXmlFile(jasperReport , destFileName);
            List<JsonObject> requiredParameterMap = extractRequiredParametersNames(jasperReport);
            logger.info("report Parameters :" + requiredParameterMap);
            Map<String, Object> parameters = prepareParameters(requiredParameterMap);
            JasperPrint jasperPrint = null;
            String queryText = jasperReport.getQuery() != null ? jasperReport.getQuery().getText() : null;
            if (queryText != null && !queryText.isEmpty()) {
                Connection JDBCConnection = connectionProvider(queryText, false);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, JDBCConnection);
            } else {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            }
            Integer reportSize = jasperPrint.getPages().size();
            return export(jasperPrint, format, reportSize);
        } catch (JRException | ParseException ex) {
            ex.printStackTrace();
        }
        return export;
    }

    public static Connection connectionProvider(String queryText, boolean isTemp) {
        JsonObject connectionParameters = null;
        String dataSourceType = null;
        Connection connection = null;
        if (queryText != null) {
            connectionParameters = data.has("connectionDetails") ? data.getAsJsonObject("connectionDetails") : null;
            dataSourceType = connectionParameters.get("type").getAsString();
            DataSourceUtils.validate(dataSourceType);
            DataSourceUtils.addExtraDataForWorkflowProcess(connectionParameters, dataSourceType);
            connectionParameters.addProperty("access", DataSourceSecurityUtility.EXECUTE);
            if (isTemp) {
                connectionParameters.addProperty("isTemp", isTemp);
                DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnectionFromTemp(connectionParameters, dataSourceType);
                connection = driverConnection.getConnection();
            } else {
                DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(connectionParameters, dataSourceType);
                connection = driverConnection.getConnection();
            }
        }
        return connection;
    }


    public static Map<String, Object> prepareParameters(List<JsonObject> requiredParameterMap) throws ParseException {
        Map<String, Object> parameter = new HashMap<>();
        JsonObject requestParameters = data.getAsJsonObject("parameters");
        requiredParameterMap.forEach((eachJson) -> {
            String parameterName = eachJson.get("name").getAsString();
            String valueClassName = eachJson.get("valueClassName").getAsString();
            Object parameterValue = prepareGenericValue(valueClassName, requestParameters, parameterName);
            if (parameterValue != null)
                parameter.put(parameterName, parameterValue);
        });

        return parameter;
    }

    private static Object prepareSubReport(String parameterName) {
        Object parameterValue = null;
        try {
            JsonObject parameters = data.getAsJsonObject("parameters");
            String subReportLoc = parameters.get(parameterName).getAsString();
            String subReportFile = applicationProperties.getSolutionDirectory() + File.separator + subReportLoc;
            JasperDesign jasperSubDesign = JRXmlLoader.load(subReportFile);
            parameterValue = JasperCompileManager.compileReport(jasperSubDesign);
        } catch (JRException e) {
            e.printStackTrace();
        }
        return parameterValue;
    }

    private static Object prepareGenericValue(String valueClassName, JsonObject requestParameters, String parameterName) {
        Object value;
        Object parameterValue;
        if (!requestParameters.has(parameterName)) {
            return null;
        }
        switch (valueClassName) {
            case "java.lang.Integer":
                parameterValue = GsonUtility.optString(requestParameters, parameterName);
                if (parameterValue instanceof String)
                    value = Integer.parseInt((String) parameterValue);
                else
                    value = (Integer) parameterValue;
                break;
            case "java.lang.Double":
                parameterValue = GsonUtility.optString(requestParameters, parameterName);
                if (parameterValue instanceof String)
                    value = Double.parseDouble((String) parameterValue);
                else
                    value = (Double) parameterValue;
                break;
            case "java.lang.Float":
                parameterValue = GsonUtility.optString(requestParameters, parameterName);
                if (parameterValue instanceof String)
                    value = Float.parseFloat((String) parameterValue);
                else
                    value = (Float) parameterValue;
                break;

            case "net.sf.jasperreports.engine.JasperReport":
                value = prepareSubReport(parameterName);
                break;

            case "java.util.Date":
                parameterValue = GsonUtility.optString(requestParameters, parameterName);
                try {
                    value = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse((String) parameterValue);
                } catch (ParseException e) {
                    value = null;
                }
            case "java.util.Collection":
                parameterValue = GsonUtility.optString(requestParameters, parameterName);
                if (parameterValue instanceof JsonArray) {
                    value = new Gson().fromJson(parameterValue.toString(),JsonObject.class);
                } else if (parameterValue instanceof JsonObject) {
                    value = new Gson().fromJson(parameterValue.toString(),JsonObject.class);
                } else {
                    value = parameterValue;
                }
                break;
            default:
                parameterValue = GsonUtility.optString(requestParameters,parameterName);
                value = (String) parameterValue;
                break;

        }
        return value;
    }

    public static List<JsonObject> extractRequiredParametersNames(JasperReport jasperMasterReport) {
        JRParameter[] parametersArray = jasperMasterReport.getParameters();
        List<JsonObject> parametersList = new ArrayList<>();
        JsonObject eachParameter;
        for (JRParameter param : parametersArray) {
            if (!param.isSystemDefined() && param.isForPrompting()) {
                eachParameter = new JsonObject();
                eachParameter.addProperty("name", param.getName());
                eachParameter.addProperty("valueClassName", param.getValueClassName());
                eachParameter.addProperty("description", param.getDescription());
                eachParameter.addProperty("valueExpression", param.getDefaultValueExpression() != null ? param.getDefaultValueExpression().getText() : null);
                eachParameter.addProperty("nestedTypeName", param.getNestedTypeName());
                parametersList.add(eachParameter);
            }
        }
        return parametersList;
    }

    public static byte[] export(final JasperPrint print, String format, Integer reportSize) throws JRException {
        int page = GsonUtility.optIntValue(data, "page", 0);
        if (!validatePageNum(page, reportSize)) {
            throw new EfwException("Invalid page number : provided page no:" + page + " is exceeding the total report size:" + reportSize + " NOTE page index starts from 0");
        }
        final Exporter exporter;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean html = false;

        switch (format) {
            case "HTML":
                exporter = exportHtmlPageWise(out, page, reportSize);
                html = true;
                break;

            case "CSV":
                exporter = new JRCsvExporter();
                break;

            case "XML":
                exporter = new JRXmlExporter();
                break;

            case "XLSX":
                exporter = new JRXlsxExporter();
                break;

            case "PDF":
                exporter = new JRPdfExporter();
                break;

            default:
                throw new JRException("Unknown report format: " + format);
        }
        if (!html) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        }

        exporter.setExporterInput(new SimpleExporterInput(print));
        try {
            exporter.exportReport();
        } catch (JRRuntimeException e) {
            throw new EfwException("Unable to export the report cause: " + e.getMessage());
        }

        return out.toByteArray();
    }

    private static boolean validatePageNum(int page, Integer reportSize) {
        if (reportSize != null && reportSize > 0 && page >= 0 && page < reportSize) {
            return page <= reportSize;
        } else return false;
    }

    private static Exporter exportHtmlPageWise(ByteArrayOutputStream out, Integer page, Integer reportSize) {
        Exporter htmlExporter = new HtmlExporter();
        SimpleHtmlExporterOutput htmlExporterOutput = new SimpleHtmlExporterOutput(out);
        htmlExporterOutput.setImageHandler(new FileHtmlResourceHandler(new File("E:\\cascadeDeleteTest\\9180748358050\\img"), "E:\\cascadeDeleteTest\\9180748358050\\img\\{0}"));
        htmlExporter.setExporterOutput(htmlExporterOutput);
        SimpleHtmlExporterConfiguration exportConfiguration = new SimpleHtmlExporterConfiguration();
        SimpleHtmlReportConfiguration reportConfiguration = new SimpleHtmlReportConfiguration();
        exportConfiguration.setBetweenPagesHtml(exportConfiguration.PROPERTY_BETWEEN_PAGES_HTML);

        reportConfiguration.setPageIndex(page);

        exportConfiguration.setHtmlHeader("");
        exportConfiguration.setHtmlFooter("");

        reportConfiguration.setWhitePageBackground(false);

        reportConfiguration.setUseBackgroundImageToAlign(false);

        reportConfiguration.setRemoveEmptySpaceBetweenRows(true);

        reportConfiguration.setWrapBreakWord(true);

        htmlExporter.setConfiguration(reportConfiguration);
        htmlExporter.setConfiguration(exportConfiguration);
        return htmlExporter;
    }

    public static void setData(String dataz) {
        data = new Gson().fromJson(dataz,JsonObject.class);
    }


}
