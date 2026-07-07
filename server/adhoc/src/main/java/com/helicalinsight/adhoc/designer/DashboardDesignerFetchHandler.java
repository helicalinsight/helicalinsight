package com.helicalinsight.adhoc.designer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * This class implements the {@link IComponent} interface and is responsible for fetching data related to the dashboard designer.
 * Created by author on 30-01-2015.
 * @author Rajasekhar
 * @author Somen
 */
@SuppressWarnings("unused")
public class DashboardDesignerFetchHandler implements IComponent {

    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * Indicates whether the method is thread-safe for caching purposes.
     * @return {@code true}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * This method is used to fetch data related to the dashboard designer.
     * 
     * @param jsonFormData 				FormData providing parameters like directory and file.
     * @return A JSON string containing state and report name information of the dashboard designer
     * @throws IllegalArgumentException if there is no designer resource with the specified name
     */
    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonObject responseJson;
        responseJson = new JsonObject();
        if (formJson.get("file").isJsonNull()) {
            return null;
        }
        String file = formJson.get("file").getAsString();

        //file=file.substring(0, file.lastIndexOf('.'));
        String dir = formJson.get("dir").getAsString();
        //String ddExtension = JsonUtils.getDesignerExtension();

        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + dir;

        File designerFile = new File(solutionDirectory + File.separator + file);

        if (!designerFile.exists()) {
            throw new IllegalArgumentException("Aborting operation. There is no designer " + "resource with the " +
                    "specified name.");
        }

        EfwDashboardDesigner dashboardDesigner = JaxbUtils.unMarshal(EfwDashboardDesigner.class, designerFile);

        GsonUtility.accumulate(responseJson,"state", dashboardDesigner.getState());
        GsonUtility.accumulate(responseJson,"reportName", dashboardDesigner.getName());
        return responseJson.toString();
    }
}
