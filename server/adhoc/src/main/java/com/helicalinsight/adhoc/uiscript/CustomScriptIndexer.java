package com.helicalinsight.adhoc.uiscript;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CustomScriptIndexer
 *
 * This class is responsible for indexing all custom scripts available in a specific folder.
 * It retrieves all custom scripts from the folder, unmarshals them using JAXB, and adds them
 * to a list. If any custom script is malformed, it logs an error message.
 *
 * Author: Somen
 * Created on: 9/23/2016
 */
public class CustomScriptIndexer {
    private static final Logger logger = LoggerFactory.getLogger(CustomScriptIndexer.class);
    /**
     * Retrieves all custom scripts available in the custom script folder.
     * @return A list of all custom scripts
     */
    public static List<CustomScript> getAllCustomScripts() {
        List<CustomScript> allCustomScripts = new ArrayList<>();
        String scriptFolder = CustomScriptSaveHandler.getCustomScriptPath();
        File folder = new File(scriptFolder);
        String[] extensionArray = {JsonUtils.getScriptExtension()};
        Collection<File> files = FileUtils.listFiles(folder, extensionArray, true);
        for (File scriptFiles : files) {
            try {
                CustomScript customScript = JaxbUtils.unMarshal(CustomScript.class, scriptFiles);
                allCustomScripts.add(customScript);
            }catch(ConfigurationException conf){
                logger.error("The CustomScript is malformed " + scriptFiles);
            }
        }
        return allCustomScripts;
    }
}
