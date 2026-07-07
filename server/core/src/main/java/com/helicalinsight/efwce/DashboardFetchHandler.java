package com.helicalinsight.efwce;

import com.helicalinsight.datasource.managed.jaxb.EFWCE;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * @author Rajesh
 *         Created by author on 4/22/2019.
 */
public class DashboardFetchHandler implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public String executeComponent(String jsonFormData) {

        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        JSONObject responseJson;
        responseJson = new JSONObject();
        String file = formJson.getString("file");

        if (file == null) {
            return null;
        }
        //file=file.substring(0, file.lastIndexOf('.'));
        String dir = formJson.getString("dir");
        //String ddExtension = JsonUtils.getDesignerExtension();

        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + dir;

        File designerFile = new File(solutionDirectory + File.separator + file);

        if (!designerFile.exists()) {
            throw new IllegalArgumentException("Aborting operation. There is no designer " + "resource with the " +
                    "specified name.");
        }

        EFWCE efwce = JaxbUtils.unMarshal(EFWCE.class, designerFile);

        responseJson.accumulate("state", efwce.getState());
        responseJson.accumulate("reportName", efwce.getName());
        return responseJson.toString();

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
