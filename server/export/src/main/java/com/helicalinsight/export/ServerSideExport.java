package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * The `ServerSideExport` class facilitates server-side export of reports by handling various parameters and generating
 * report locations. 
 * Created by user on 3/3/2016.
 * @author Rajasekhar
 */
public class ServerSideExport {
    private static final Logger logger = LoggerFactory.getLogger(ServerSideExport.class);

    private String format;
    private String reportName;
    private String reportParameters;
    private String reportType;
    private String dir;
    private String reportFileName;
    private JsonObject newSettings;
    /**
     * Constructor for the `ServerSideExport` class.
     *
     * @param format           The format of the report to be exported (e.g., PDF, PNG).
     * @param reportName       The name of the report to be exported.
     * @param reportParameters JSON-formatted parameters for the report.
     * @param reportType       The type of the report (e.g., Adhoc, Standard).
     * @param dir              The directory path of the report.
     * @param reportFileName   The filename of the report.
     * @param newSettings      settings for the export process.
     */
	public ServerSideExport(String format, String reportName, String reportParameters, String reportType, String dir,
            String reportFileName, JsonObject newSettings) {
		this.format = format;
		this.reportName = reportName;
		this.reportParameters = reportParameters;
		this.reportType = reportType;
		this.dir = dir;
		this.reportFileName = reportFileName;
		this.newSettings = newSettings;
	}
	
	/**
     * Generates a list of report locations based on the provided parameters.
     * @return List of report locations on the file system.
     */
    public List<String> listOfLocations() {
        ReportsProcessor reportsProcessor = new ReportsProcessor();
        List<String> locationsList;
        String parameters = "";
        if (this.reportParameters != null && !this.reportParameters.isEmpty()) {
            JsonObject parametersJson = JsonParser.parseString(this.reportParameters).getAsJsonObject();
            parameters = ControllerUtils.concatenateParameters(parametersJson);
            if (parameters.length() > 0) {
                parameters = parameters.substring(0, parameters.length() - 1);
            }
        }

        String baseUrl = ApplicationProperties.getInstance().getDomain();
        String reportExtension = JsonUtils.getReportExtension();
        if (reportExtension.equalsIgnoreCase(this.reportType)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/")) + "/" + ApplicationProperties.getInstance()
                    .getAdhocReportUrl();
        }

        String url = baseUrl + "?" + "file=" + this.reportFileName + "&dir=" + this.dir + "&" + parameters;
        String encodedData = null;
        try {
            encodedData = URLEncoder.encode(url, ApplicationUtilities.getEncoding());
        } catch (UnsupportedEncodingException ignore) {
            logger.error("Exception ", ignore);
        }
        GsonUtility.accumulateAll(newSettings,reportsProcessor.phantomCredentials());

        locationsList = reportsProcessor.generateReportFromURI(encodedData, this.format, this.reportName, newSettings);
        return locationsList;
    }
}
