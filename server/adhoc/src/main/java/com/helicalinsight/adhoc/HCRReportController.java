package com.helicalinsight.adhoc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.jreport.HCRHelper;
import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * The {@code HCRReportController} class is a Spring MVC controller responsible for handling requests related to
 * (HCR) operations. It provides methods to open and edit HCR reports.
 *
 * @author Rajesh
 * @since 12/13/2019
 */
@Controller
public class HCRReportController {
	/**
     * Handles the request to open an HCR report. Retrieves the report parameters and configurations.
     *
     * @param request 			 HttpServletRequest object containing request parameters.
     * @return A ModelAndView object representing the view to be displayed.
     * @throws IOException 		if an I/O exception occurs.
     */
    @RequestMapping(value = "/hcr-report", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView openReport(HttpServletRequest request) throws IOException {

        ModelAndView openReport = new ModelAndView();
        openAndEdit(request, openReport);
        openReport.setViewName("hcr-report");
        return openReport;
    }
    /**
     * Opens and edits the HCR report based on the provided HttpServletRequest and ModelAndView.
     *
     * @param request     		 HttpServletRequest object containing request parameters.
     * @param openReport  		 ModelAndView object representing the view.
     */
    private void openAndEdit(HttpServletRequest request, ModelAndView openReport) {
        String dir = request.getParameter("dir");
        String fileName = request.getParameter("file");
        if (dir == null && fileName == null) {
            ControllerUtils.addAndGetUrlParameters(request, openReport);
            return;
        }
        HCReport hcrReport = (HCReport) ReportOpenHelper.getAdhocReport(dir, fileName);
        JsonObject urlParametersJson = ControllerUtils.addAndGetUrlParameters(request, openReport);
        if (hcrReport != null) {
            JsonObject hcrJsonData = ReportOpenHelper.newReportContentAsJson(hcrReport);
            if (urlParametersJson.size() > 3) {
                HCRHelper.prepareFormDataForHCRParameters(urlParametersJson, JsonParser.parseString(hcrJsonData.get("previewFormData").getAsString()).getAsJsonObject());
            }
            openReport.addObject("reportParameters", hcrJsonData);
        }
        String response = getExportFromStatic();
        openReport.addObject("hcrConfigurations", response);
    }
    /**
     * Handles the request to edit an HCR report. Retrieves the report parameters and configurations.
     *
     * @param request 		 	HttpServletRequest object containing request parameters.
     * @return A ModelAndView object representing the view to be displayed for editing.
     * @throws IOException 		if an I/O exception occurs.
     */
    @RequestMapping(value = "/hcr-report-edit", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView editReport(HttpServletRequest request) throws IOException {
        ModelAndView openReport = new ModelAndView();
        openAndEdit(request, openReport);
        openReport.setViewName("hcr-report-edit");
        return openReport;
    }
    /**
     * Retrieves the export configuration from the static resources.
     *
     * @return A String representing the export configurations.
     */
    private String getExportFromStatic() {
        JsonObject formData = new JsonObject();
        formData.addProperty("contentId", "hcrConfigurations");
        IComponent anySettingsGroovyProcessor = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.export.components.AnySettingsGroovyProcessor", IComponent.class);
        return anySettingsGroovyProcessor.executeComponent(formData.toString());
    }
}
