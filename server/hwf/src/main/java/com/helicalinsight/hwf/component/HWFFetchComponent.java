package com.helicalinsight.hwf.component;

import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.datasource.managed.jaxb.HWFD;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
public class HWFFetchComponent implements IComponent {
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
        if (!file.contains("." + JsonUtils.getHWFDExtension())) {
            file = file.concat("." + JsonUtils.getHWFDExtension());
        }
        //file=file.substring(0, file.lastIndexOf('.'));
        String dir = formJson.getString("dir");
        //String ddExtension = JsonUtils.getDesignerExtension();

        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + dir;

        File hwfdFile = new File(solutionDirectory + File.separator + file);

        if (!hwfdFile.exists()) {
            throw new IllegalArgumentException("Aborting operation. There is no HWFD " + "resource with the " +
                    "specified name.");
        }

        HWFD hwfd = JaxbUtils.unMarshal(HWFD.class, hwfdFile);

        responseJson.accumulate("state", hwfd.getState());
        responseJson.accumulate("diagramData", hwfd.getDiagramData());
        responseJson.accumulate("hwfFileName", hwfd.getHwfFile());
        responseJson.accumulate("hwfFileDir", hwfd.getHwfFileDir());
        return responseJson.toString();

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
