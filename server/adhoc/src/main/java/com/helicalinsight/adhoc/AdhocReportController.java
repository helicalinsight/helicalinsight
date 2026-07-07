package com.helicalinsight.adhoc;

import com.helicalinsight.adhoc.report.ReportOpenHelper;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.adhoc.report.AdhocReport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * The {@code AdhocReportController} class is a Spring MVC controller that handles requests related to ad-hoc reports.
 * It provides a method to open an ad-hoc report and retrieve necessary information.
 * 
 * @author Rajasekhar
 * @since 3/14/2017
 */
@Controller
public class AdhocReportController {

	/**
     * Controller method to handle requests to open an ad-hoc report and retrieves necessary information.
     *
     * @param request 		 	HTTP servlet request containing parameters such as "dir" and "file".
     * @return A {@code ModelAndView} object representing the view and data to be displayed.
     * @throws IOException 		If an I/O exception occurs during the process.
     */
    @RequestMapping(value = "/adhocReport", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView openReport(HttpServletRequest request) throws IOException {
        String dir = request.getParameter("dir");
        String fileName = request.getParameter("file");
        AdhocReport adhocReport = (AdhocReport) ReportOpenHelper.getAdhocReport(dir, fileName);

        ModelAndView openReport = new ModelAndView();
        ControllerUtils.addUrlParameters(request, openReport);

        if (adhocReport != null) {
            openReport.addObject("reportParameters", ReportOpenHelper.reportContentAsJson(adhocReport));
        }

        openReport.setViewName("adhocReport");
        return openReport;
    }
}
