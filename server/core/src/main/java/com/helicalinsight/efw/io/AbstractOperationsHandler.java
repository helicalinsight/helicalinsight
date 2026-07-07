package com.helicalinsight.efw.io;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract class which holds the methods for use by the file operations module.
 * <p/>
 * Created by author on 11-Oct-14.
 *
 * @author Rajasekhar
 * @version 1.0
 * @since 1.1
 */
public abstract class AbstractOperationsHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractOperationsHandler.class);
    /**
     * <p>
     * Returns the list of extensions for which setting xml has configuration.
     * For example efw, efwsr, efwFav, efwFolder etc.
     * </p>
     *
     * @return List of rule attribute.
     */
    public List<String> getListOfExtensionsFromSettings() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        JsonObject xmlContent = processor.getJsonObject(applicationProperties.getSettingPath(), false);
        JsonObject jsonObject = null;
        try {
            jsonObject = ControllerUtils.getJSONOfVisibleExtensionTags(xmlContent.getAsJsonObject("Extentions"));
        } catch (JsonIOException ex) {
            logger.error("JSONException ", ex);
        }

        Iterator<?> iterator;
        if (jsonObject != null) {
            iterator = jsonObject.keySet().iterator();
        } else {
            throw new RuntimeException("Visible extensions is null");
        }
        List<String> listOfExtensions = new ArrayList<>();
        if (iterator != null) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
					if (jsonObject.get(key).isJsonObject()) {
						JsonObject object = jsonObject.getAsJsonObject(key);
						if (object.get("visible") != null && object.get("visible").getAsString().equals("true")) {
							listOfExtensions.add(key);
						}
					}
                } catch (JsonIOException ex) {
                    logger.info("No rule or text value for the key " + key + " is provided.");
                }
            }
        }
        logger.debug("The list of extensions for which setting xml has configuration : " + listOfExtensions);
        return listOfExtensions;
    }
}
