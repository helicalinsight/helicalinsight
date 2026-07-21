package com.helicalinsight.admin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.helicalinsight.admin.utils.DateUtils;
import com.helicalinsight.efw.ApplicationInformation;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.model.LicenseMetadata;
import com.helicalinsight.efw.utility.ApplicationUtilities;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 * This Endpoint is Deprecated and marked for removal infavor of /v2/getProductInformation
 * 
 */

@Deprecated(forRemoval = true)
@WebServlet(urlPatterns = "/getProductInformation")
public class ProductInformationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        final ServletContext servletContext = getServletContext();

        response.setContentType("application/json");
        
        LicenseMetadata licenseMetadata = ApplicationProperties.getInstance().getLicenseMetadata();

        JSONObject jsonObject = new JSONObject();
        ApplicationInformation productInfo = ApplicationInformation.getInstance();
        Map<String, String> details = new HashMap<>();

        details.put("Product Name", productInfo.getProductName());
        details.put("Product Type", productInfo.getProductType());
        details.put("Version", productInfo.getVersion());
        details.put("Build", productInfo.getBuild());

		synchronized (servletContext) {
			if (licenseMetadata != null) {
				String licenceKeyType = licenseMetadata.licenseKeyType();
				details.put("License Type", licenceKeyType);
				if (!"Unlimited".equalsIgnoreCase(licenceKeyType)) {
					details.put("Expiration", DateUtils.convertDateToString(licenseMetadata.lastDate(), "dd/MM/yyyy"));
				}
			}
			else {
				details.put("License Type", productInfo.getSourceCodeType());
				details.put("Expiration","NA");
			}
		}

        jsonObject.putAll(details);

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.print(jsonObject);
            printWriter.flush();
        } catch (IOException ignore) {
            throw new RuntimeException("Unable to retrieve product information", ignore);
        } finally {
            ApplicationUtilities.closeResource(printWriter);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        doGet(request, response);
    }
}