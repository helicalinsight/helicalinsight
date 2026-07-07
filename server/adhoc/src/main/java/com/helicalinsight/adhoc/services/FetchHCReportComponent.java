package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import java.io.File;

/**
 * Created by author on 10/17/2019.
 *
 * @author Rajesh
 */
public class FetchHCReportComponent implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonObject responseJson;
        responseJson = new JsonObject();
        String file = formJson.get("file").getAsString();

        String uuid = file.replace("." + JsonUtils.getHCRExtension(), "");
        if (!file.contains("." + JsonUtils.getHCRExtension())) {
            uuid = file;
            file = file.concat("." + JsonUtils.getHCRExtension());
        }
        String dir = formJson.get("dir").getAsString();
        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + dir;

        File hcrFile = new File(solutionDirectory + File.separator + file);
        if (!hcrFile.exists()) {
            throw new ResourceNotFoundException("There is no HCR " + "resource with the " +
                    "specified name: " + file);
        }

        HCReport hcr = JaxbUtils.unMarshal(HCReport.class, hcrFile);
        JsonObject fileDetails = new JsonObject();
        fileDetails.addProperty("uuid", uuid);
        fileDetails.addProperty("name", hcr.getName());
        fileDetails.addProperty("dir", dir);
        GsonUtility.accumulate(responseJson,"file", fileDetails);
        GsonUtility.accumulate(responseJson,"state", hcr.getState());
        GsonUtility.accumulate(responseJson,"diagramData", hcr.getDiagramData());
        GsonUtility.accumulate(responseJson,"previewFormData", hcr.getFormData());
        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
