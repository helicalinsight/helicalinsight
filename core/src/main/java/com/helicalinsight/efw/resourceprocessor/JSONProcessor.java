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

package com.helicalinsight.efw.resourceprocessor;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.RuntimeIOException;
import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Used throughout the application for getting the json of an xml file. Used
 * heavily than any other class.
 * <p/>
 * Note: This class is supposed to be stateless. Adding state to this class
 * breaks the other modules.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Somen
 */
public class JSONProcessor implements IProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JSONProcessor.class);

    private static final IResourceManager resourceManager = ResourceManager.getInstance();

    private static boolean LOAD_TIME_CHECK = true;

    public static JSONObject prepareJsonFromXML(String resource, boolean flag) {
        //logger.debug("Preparing Json for the resource " + resource);
        JSONObject json = null;

        InputStream inputStream = null;
        File file = new File(resource);

        //Added on 20th July 2015 to check if the resource exists
        validate(resource, file);

        try {
            inputStream = new FileInputStream(file);
            String xml = IOUtils.toString(inputStream, Charset.defaultCharset());
            XMLSerializer xmlSerializer = new XMLSerializer();
            if (flag) {
                xmlSerializer.setForceTopLevelObject(true);
            }
            xmlSerializer.setTypeHintsCompatibility(false);
            xmlSerializer.setTypeHintsEnabled(false);
            json = (JSONObject) xmlSerializer.read(xml);
        } catch (JSONException ex) {
            logger.debug("JSONException ", ex);
        } catch (IOException ex) {
            logger.debug("IOException ", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
        return json;
    }

    private static void validate(String resource, File file) {
        if (!file.canRead()) {
            throw new RuntimeIOException("Can't convert to JSON. The resource requested " + resource + " doesn't " +
                    "exists. Or there are not enough permissions to read the file. Please check for file permissions.");
        }
    }

    /**
     * Returns the json of a file. The top level key of xml is excluded if the
     * boolean flag is false.
     *
     * @param resource The file under concern
     * @param flag     true or false for exclusion or inclusion of top level key
     * @return The json of the resource
     */
    public JSONObject getJSONObject(String resource, boolean flag) {
        if (!LOAD_TIME_CHECK) {
            final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            if ("true".equals(applicationProperties.isCacheEnabled())) {
                JSONObject cachedObject = resourceManager.getResource(resource, flag);
                if (cachedObject != null) {
                    return cachedObject;
                }
            }
        }
        if (LOAD_TIME_CHECK) {
            LOAD_TIME_CHECK = false;
        }
        return prepareJsonFromXML(resource, flag);
    }

    /**
     * Returns the json array of a file. The top level key of xml is excluded if
     * the boolean flag is false.
     *
     * @param resource The file under concern
     * @param flag     true or false for exclusion or inclusion of top level key
     * @return The json array of the resource
     */
    public JSONArray getJSONArray(String resource, boolean flag) {
        //logger.debug("Preparing Json array for the resource " + resource);
        JSONArray json = null;

        InputStream inputStream = null;
        File file = new File(resource);

        //Added on 20th July 2015 to check if the resource exists
        validate(resource, file);

        try {
            inputStream = new FileInputStream(file);
            String xml = IOUtils.toString(inputStream, Charset.defaultCharset());
            XMLSerializer xmlSerializer = new XMLSerializer();
            if (flag) {
                xmlSerializer.setForceTopLevelObject(true);
            }
            xmlSerializer.setTypeHintsCompatibility(false);
            xmlSerializer.setTypeHintsEnabled(false);
            json = (JSONArray) xmlSerializer.read(xml);
        } catch (JSONException ex) {
            logger.debug("JSONException ", ex);
        } catch (IOException ex) {
            logger.debug("IOException ", ex);
        } finally {
            ApplicationUtilities.closeResource(inputStream);
        }
        return json;
    }
}