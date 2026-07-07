package com.helicalinsight.efw.resourceprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Used throughout the application for getting the json of an xml file. Used
 * heavily than any other class.
 * </p>
 * Note: This class is supposed to be stateless. Adding state to this class
 * breaks the other modules.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Somen
 * @version 1.1
 * @since 1.0
 */
//TODO: Marked for removal 
@Deprecated
public class JSONProcessor implements IProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JSONProcessor.class);

    private static final IResourceManager resourceManager = ResourceManager.getInstance();

    private static boolean LOAD_TIME_CHECK = true;

    /**
     * prepareJsonFromXML
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JSONProcessor#prepareJsonFromXML(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    @Deprecated
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
            logger.error("Resource requested was " + resource);
            logger.debug("JSONException ", ex);
        } catch (IOException ex) {
            logger.error("Resource requested was " + resource);
            logger.error("IOException ", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                    logger.debug("Could not close resource " + resource);
                }
            }
        }
        return json;
    }
/**
 * newPrepareJsonFromXML using gson
 * @param resource
 * @param flag
 * @return
 */
    public static JsonObject newPrepareJsonFromXML(String resource, boolean flag) {
        //logger.debug("Preparing Json for the resource " + resource);
        JsonObject json = null;

        InputStream inputStream = null;
        File file = new File(resource);

        //Added on 20th July 2015 to check if the resource exists
        validate(resource, file);

        try {
            inputStream = new FileInputStream(file);
            String xml = IOUtils.toString(inputStream, Charset.defaultCharset());
            ObjectMapper xmlMapper = new XmlMapper();
            ObjectMapper jsonMapper = new ObjectMapper();

            // Convert XML string to JsonNode
            JsonNode jsonNode = xmlMapper.readTree(xml.getBytes());
           
            // Convert JsonNode to JSON string
            String jsonString = jsonMapper.writeValueAsString(jsonNode);

           json =  new Gson().fromJson(jsonString, JsonObject.class);

//            XmlMapper xmlMapper = new XmlMapper();
//            JsonObject jsonObject = new Gson().fromJson(xmlMapper.readTree(xml).toString(), JsonObject.class);
//            json = jsonObject;
            
//            org.json.JSONObject jsonObject = new org.json.JSONObject();
//            try {
//				 jsonObject = XML.toJSONObject(xml);
//			} catch (org.json.JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            JsonObject object = new Gson().fromJson(jsonObject.toString(), JsonObject.class);
//            
//            json = object.getAsJsonObject("mappings");
//            XMLSerializer xmlSerializer = new XMLSerializer();
//            if (flag) {
//                xmlSerializer.setForceTopLevelObject(true);
//            }
//            xmlSerializer.setTypeHintsCompatibility(false);
//            xmlSerializer.setTypeHintsEnabled(false);
//            JSONObject jsonObj = (JSONObject) xmlSerializer.read(xml);
//            
//            json = new Gson().fromJson(jsonObj.toString(),JsonObject.class);
        } catch (JsonSyntaxException ex) {
            logger.error("Resource requested was " + resource);
            logger.debug("JSONException ", ex);
        } catch (IOException ex) {
            logger.error("Resource requested was " + resource);
            logger.error("IOException ", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                    logger.debug("Could not close resource " + resource);
                }
            }
        }
        return json;
    }

    private static void processArrays(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
        	
        }
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
    /**
     * getJsonObject
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JSONProcessor#getJsonObject(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    @Deprecated
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
     * getJsonObject using gson
     * @param String resource
     * @param boolean flag
     * @return JsonObject
     */
    public JsonObject getJsonObject(String resource, boolean flag) {
        if (!LOAD_TIME_CHECK) {
            final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            if ("true".equals(applicationProperties.isCacheEnabled())) {
                JsonObject cachedObject = resourceManager.newGetResource(resource, flag);
                if (cachedObject != null) {
                    return cachedObject;
                }
            }
        }
        if (LOAD_TIME_CHECK) {
            LOAD_TIME_CHECK = false;
        }
        return newPrepareJsonFromXML(resource, flag);
    }
    /**
     * Returns the json array of a file. The top level key of xml is excluded if
     * the boolean flag is false.
     *
     * @param resource The file under concern
     * @param flag     true or false for exclusion or inclusion of top level key
     * @return The json array of the resource
     */
    /**
     * getJsonArray
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JSONProcessor#getJsonArray(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonArray 
     */
    @Deprecated
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
    /**
     * getJsonArray using gson
     * @param String resource
     * @param boolean flag
     * @return
     */
    public JsonArray getJsonArray(String resource, boolean flag) {
        //logger.debug("Preparing Json array for the resource " + resource);
        JsonArray json = null;

        InputStream inputStream = null;
        File file = new File(resource);

        //Added on 20th July 2015 to check if the resource exists
        validate(resource, file);

        try {
            inputStream = new FileInputStream(file);
            String xml = IOUtils.toString(inputStream, Charset.defaultCharset());
            ObjectMapper xmlMapper = new XmlMapper();
            ObjectMapper jsonMapper = new ObjectMapper();

            // Convert XML string to JsonNode
            JsonNode jsonNode = xmlMapper.readTree(xml.getBytes());
           
            // Convert JsonNode to JSON string
            String jsonString = jsonMapper.writeValueAsString(jsonNode);


            json = new Gson().fromJson(jsonString,JsonArray.class);
        } catch (JsonSyntaxException ex) {
            logger.debug("JSONException ", ex);
        } catch (IOException ex) {
            logger.debug("IOException ", ex);
        } finally {
            ApplicationUtilities.closeResource(inputStream);
        }
        return json;
    }
}