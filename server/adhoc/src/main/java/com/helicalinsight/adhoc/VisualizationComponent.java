package com.helicalinsight.adhoc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.validator.ResourceValidator;
import com.helicalinsight.efw.vf.ChartResource;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Component for executing visualization based on provided Visualize.js parameters.
 * This component handles the execution of visualization by processing Visualize.js parameters
 * and retrieving chart properties from the specified Visualization File (VF).
 * 
 * Created by author on 30-01-2015.
 * @author Rajasekhar
 * @author Somen
 */
public class VisualizationComponent implements IComponent {
	/**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} the component is thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the VisualizationComponent based on the provided JSON form data.
     *
     * @param jsonFormData 			  form data containing visualization parameters.
     * @return String       		  A JSON string containing the result of the visualization execution.
     * @throws EfwServiceException    If there is an error during the execution of the visualization component.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String dir;
        String vfFile;
        int vfId;
        try {
            Object vf;
            if (formData.has("vf")) {
                vf = formData.get("vf");
                if (!(vf instanceof JsonObject)) {
                    throw new IllegalArgumentException("The parameter vf is not an object. Incorrect type of " +
                            "parameters.");
                }
            } else {
                throw new IncompleteFormDataException("Parameter vf is missing.");
            }
            JsonObject object = (JsonObject) vf;
            dir = object.get("dir").getAsString();
            vfFile = object.get("vf_file").getAsString();
            vfId = Integer.parseInt(object.get("vf_id").getAsString());
        } catch (Exception ex) {
            throw new EfwServiceException("The request parameters are incorrect.", ex);
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", dir);
        parameters.put("vf_file", vfFile);
        parameters.put("vf_id", vfId + "");

        //Validate
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        ApplicationProperties instance = ApplicationProperties.getInstance();
        String settingPath = instance.getSettingPath();

        File file = new File(instance.getSolutionDirectory() + File.separator + dir + File.separator + vfFile);

        if (!file.exists()) {
            throw new EfwServiceException("The vf file requested does not exists");
        }

        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject fileAsJson = processor.getJsonObject(file.getAbsolutePath(), true);
        ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
        if (!resourceValidator.newValidateVf()) {
            throw new EfwServiceException("The vf file requested has duplicate chart id(s)");
        }

        JsonArray charts = fileAsJson.getAsJsonArray("Charts");
        JsonObject chartData = null;

        for (int chartNumber = 0; chartNumber < charts.size(); chartNumber++) {
            JsonObject chart = charts.get(chartNumber).getAsJsonObject();
            int id = chart.get("id").getAsInt();
            if (vfId == id) {
                chartData = chart.getAsJsonObject("prop");
            }
        }

        if (chartData == null) {
            throw new EfwServiceException("The vf file has invalid id. The chart properties " +
                    "could" + " not be retrieved.");
        }

        JsonObject result;
        JsonArray array = new JsonArray();
        JsonPrimitive element = new JsonPrimitive("${data}");
        array.add(element);

        ChartResource chartResource = new ChartResource(settingPath, array, chartData,String.valueOf(vfId));

        String script = chartResource.getScript();
        result = new JsonObject();
        GsonUtility.accumulate(result,"script", script);

        return result.toString();
    }
}
