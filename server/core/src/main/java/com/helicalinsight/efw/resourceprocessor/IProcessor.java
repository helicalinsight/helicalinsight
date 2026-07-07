package com.helicalinsight.efw.resourceprocessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * implementation class of this interface return the JSONObject
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @author Somen
 * @version 1.1
 * @since 1.0
 */
public interface IProcessor {

    /**
     * this method get the resource type and flag value
     *
     * @param resource type of resource
     * @param flag     flag value
     * @return JSONObject
     */

    /**
     * @getJSONObject,@getJSONArray will be deprecated in the future releases
     */
	/**
     * getJsonObject
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link IProcessor#getJsonObject(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonObject 
     */
    @Deprecated
    default JSONObject getJSONObject(String resource, boolean flag){return null;}
    /**
     * getJsonObject using gson
     * @param String resource
     * @param boolean flag
     * @return JsonObject
     */
    default JsonObject getJsonObject(String resource, boolean flag){return null;}
    /**
     * getJsonArray
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link IProcessor#getJsonArray(String resource, boolean flag)} instead.</p>
     *
     * @param String resource
     * @param boolean flag
     * @return JsonArray 
     */
    @Deprecated
    default JSONArray getJSONArray(String resource, boolean flag){return null;}

    /**
     * getJsonArray using gson
     * @param String resource
     * @param boolean flag
     * @return JsonArray
     */
    default JsonArray getJsonArray(String resource, boolean flag){return null;}

    
    default Map<String,Object> getContent(String resource){return null;}

}
