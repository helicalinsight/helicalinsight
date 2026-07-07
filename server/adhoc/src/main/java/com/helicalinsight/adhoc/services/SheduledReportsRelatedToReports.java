package com.helicalinsight.adhoc.services;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;


/**
 * A component for extracting scheduled reports related to specific adhoc reports.
 */
public class SheduledReportsRelatedToReports implements IComponent {

	private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	private static String solutionDirectory = applicationProperties.getSolutionDirectory();

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
	/**
     * Executes the component to extract scheduled reports related to a specific ad hoc report.
     * 
     * @param jsonFormData 	  form data provides report name
     * @return A JSON string containing the extracted scheduled reports.
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
		requiredResult.add("sheduledReport", requiredReportsArray);
		return requiredResult.toString();
	}
	/**
     * Extracts scheduled reports based on the specified ad hoc report file name.
     * 
     * @param adhocReportFileName 		 		name of the adhoc report.
     * @param allFilesAvailableToLoggedInUser 	A JSON array containing information about all available files.
     * @return A JSON array containing the extracted scheduled reports.
     */
	public static JsonArray extractFilesBasedOnReportFileName(String adhocReportFileName,
			JsonArray allFilesAvailableToLoggedInUser) {
		JsonArray requiredReportJsonArray = new JsonArray();
		JsonArray sheduledReportJsonArray = AdhocServiceUtils.getSpecificExtension(allFilesAvailableToLoggedInUser,
				JsonUtils.getEFWSRExtension());
		for (int index = 0; index < sheduledReportJsonArray.size(); index++) {
			JsonObject childrenObject = sheduledReportJsonArray.get(index).getAsJsonObject();

			String filePath = solutionDirectory + "" + File.separator + ""
					+ childrenObject.get("reportPath").getAsString();
			JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
			JAXBContext jaxbContext = jaxbContexts.getContextForClass(Efwsr.class);
			try {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				File xml = new File(filePath);

				Efwsr efwsrFileObj = (Efwsr) unmarshaller.unmarshal(xml);
				
				String reportFileName = efwsrFileObj.getReportFile();
				
				if (adhocReportFileName.equals(reportFileName)) {

					extractSheduledReport(adhocReportFileName, requiredReportJsonArray, efwsrFileObj, childrenObject);

				}

			} catch (JAXBException e) {
				e.printStackTrace();
			}

		}

		return requiredReportJsonArray;
	}
	/**
     * Extracts details of a scheduled report.
     * 
     * @param adhocReportFileName 		 name of the adhoc report.
     * @param dataSourceArray 			 A JSON array to store the extracted scheduled reports.
     * @param efwsrFileObj 				 An object representing the EFWSR file.
     * @param childrenObject A JSON object containing details of the scheduled report.
     */
	public static void extractSheduledReport(String adhocReportFileName, JsonArray dataSourceArray, Efwsr efwsrFileObj,
			JsonObject childrenObject) {
		JsonObject singleSheduledObject = new JsonObject();
		singleSheduledObject.addProperty("sheduledReportName", childrenObject.get("reportPath").getAsString());
		singleSheduledObject.addProperty("reportFileName", adhocReportFileName);
		singleSheduledObject.addProperty("reportDirectory", efwsrFileObj.getReportDirectory());
		singleSheduledObject.addProperty("sheduledReportFileName", efwsrFileObj.getReportName());
		dataSourceArray.add(singleSheduledObject);

	}
}
