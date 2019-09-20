/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
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
        JSONObject xmlContent = processor.getJSONObject(applicationProperties.getSettingPath(), false);
        BaseLoader baseLoader = new BaseLoader(applicationProperties);
        JSONObject jsonObject = null;
        try {
            jsonObject = baseLoader.getJSONOfVisibleExtensionTags(xmlContent.getJSONObject("Extentions"));
        } catch (JSONException ex) {
            logger.error("JSONException ", ex);
        }

        Iterator<?> iterator;
        if (jsonObject != null) {
            iterator = jsonObject.keys();
        } else {
            throw new RuntimeException("Visible extensions is null");
        }
        List<String> listOfExtensions = new ArrayList<>();
        if (iterator != null) {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                    JSONObject object = jsonObject.getJSONObject(key);
                    if (object.getString("@rule") != null) {
                        listOfExtensions.add(object.getString("#text"));
                    }
                } catch (JSONException ex) {
                    logger.info("No rule or text value for the key " + key + " is provided.");
                }
            }
        }
        logger.debug("The list of extensions for which setting xml has configuration : " + listOfExtensions);
        return listOfExtensions;
    }
}
