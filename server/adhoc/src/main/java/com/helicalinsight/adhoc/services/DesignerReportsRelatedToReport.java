package com.helicalinsight.adhoc.services;

import java.io.File;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.designer.EfwDashboardDesigner;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * A component for extracting designer reports related to a specific adhoc report.
 */
public class DesignerReportsRelatedToReport implements IComponent {

	private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	private static String solutionDirectory = applicationProperties.getSolutionDirectory();

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
	/**
     * Executes the component to extract designer reports related to a specific ad hoc report.
     * 
     * @param jsonFormData 		 form data in report name.
     * @return A JSON string containing the extracted designer reports.
     */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
		String adhocReportFileName = formDataJson.get("adhocReportFileName").getAsString();

		IComponent reportStatisticsProviderComponent = FactoryMethodWrapper
				.getTypedInstance("com.helicalinsight.admin.management.ReportStatisticsProvider", IComponent.class);
		JsonObject formdataObject = AdhocServiceUtils.prepareFormData();
		String result = reportStatisticsProviderComponent.executeComponent(formdataObject.toString());
		JsonObject resultAsJson = JsonParser.parseString(result).getAsJsonObject();
		JsonArray allFilesAvailableToLoggedInUser = GsonUtility.optJsonArray(resultAsJson,"latestReports");
		JsonObject requiredResult = new JsonObject();

		JsonArray requiredReportsArray = extractFilesBasedOnReportFileName(adhocReportFileName,
				allFilesAvailableToLoggedInUser);
		requiredResult.add("designerReports", requiredReportsArray);
		return requiredResult.toString();
	}
	/**
     * Extracts designer reports based on the specified ad hoc report file name.
     * 
     * @param adhocReportFileName 					 name of the adhoc report.
     * @param allFilesAvailableToLoggedInUser 		 JSON array containing information about all available files.
     * @return A JSON array containing the extracted designer reports.
     */
	public static JsonArray extractFilesBasedOnReportFileName(String adhocReportFileName,
			JsonArray allFilesAvailableToLoggedInUser) {
		JsonArray requiredReportJsonArray = new JsonArray();
		JsonArray designerReportJsonArray = AdhocServiceUtils.getSpecificExtension(allFilesAvailableToLoggedInUser,
				JsonUtils.getDesignerExtension());
		for (int index = 0; index < designerReportJsonArray.size(); index++) {
			JsonObject childrenObject = designerReportJsonArray.get(index).getAsJsonObject();

			String filePath = solutionDirectory + "" + File.separator + "" + childrenObject.get("reportPath").getAsString();
			JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
			JAXBContext jaxbContext = jaxbContexts.getContextForClass(EfwDashboardDesigner.class);
			try {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				File xml = new File(filePath);

				EfwDashboardDesigner efwsrFileObj = (EfwDashboardDesigner) unmarshaller.unmarshal(xml);

				
				String state = efwsrFileObj.getState();
				JsonObject stateJSON = JsonParser.parseString(state).getAsJsonObject();
				JsonObject componentsJsonObject = stateJSON.getAsJsonObject("components");
				Iterator<String> keys = componentsJsonObject.keySet().iterator();
			
				String reportFile = null;
				while (keys.hasNext()) {
					String key = keys.next();
					//if (componentsJsonObject.get(key) instanceof JSONObject) {
						JsonObject singleJSON = componentsJsonObject.getAsJsonObject(key);
						if (singleJSON.has("options")) {
							JsonObject optionJSON = singleJSON.getAsJsonObject("options");
							reportFile = GsonUtility.optString(optionJSON,"file");
							if (adhocReportFileName.equals(reportFile)) {

								extractSheduledReport(adhocReportFileName, requiredReportJsonArray, efwsrFileObj, childrenObject,optionJSON);

							}
						}

					
				}

				

			} catch (JAXBException e) {
				e.printStackTrace();
			}

		}
		return requiredReportJsonArray;

	}
	/**
     * Extracts details of a designer report.
     * 
     * @param adhocReportFileName 		 name of the adhoc report.
     * @param dataSourceArray 			 JSON array to store the extracted designer reports.
     * @param efwsrFileObj 				 object representing the EfwDashboardDesigner file.
     * @param childrenObject 			 A JSON object containing details of the designer report.
     * @param optionJSON A JSON object containing options related to the designer report.
     */
	public static void extractSheduledReport(String adhocReportFileName, JsonArray dataSourceArray,
			EfwDashboardDesigner efwsrFileObj, JsonObject childrenObject,JsonObject optionJSON ) {
		JsonObject singleSheduledObject = new JsonObject();
		singleSheduledObject.addProperty("designerReportName", efwsrFileObj.getName());
		singleSheduledObject.addProperty("reportFileName", adhocReportFileName);
		singleSheduledObject.addProperty("reportDirectory", optionJSON.get("dir").getAsString());
		singleSheduledObject.addProperty("efwFileName", efwsrFileObj.getEfw());
		dataSourceArray.add(singleSheduledObject);

	}

}
