package com.helicalinsight.admin.management;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * @author Somen
 *         Created  on 17/4/2017.
 */
@SuppressWarnings("unused")
public class DiskSpaceProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        String efwExtension = JsonUtils.getEfwExtension();
        JSONObject responseJson = new JSONObject();
        File file = new File(ApplicationProperties.getInstance().getSolutionDirectory());
        long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();

        responseJson.put("totalDiskSize", totalSpace / 1024 / 1024);
        responseJson.put("usedSpace", (totalSpace- freeSpace)/ 1024 / 1024);
        responseJson.put("freeSpace", freeSpace / 1024 / 1024);

        return responseJson.toString();
    }


}
