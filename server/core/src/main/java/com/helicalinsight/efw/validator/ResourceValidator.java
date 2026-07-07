package com.helicalinsight.efw.validator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * An instance of this class is used to validate the EFWD and EFWVF files
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Avi
 * @since 1.0
 */
public class ResourceValidator {

    private static final Logger logger = LoggerFactory.getLogger(ResourceValidator.class);
    /**
     * The json of the file under concern
     */
    private  JSONObject jsonObject;

    private  JsonObject object;
    /**
     * Constructs the object of the same type
     *
     * @param jsonObject The json of the file under concern
     */
    public ResourceValidator(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public ResourceValidator(JsonObject object) {
        this.object = object;
    }
    /**
     * Returns false if the connection id is duplicate or if the dataMap id is
     * duplicated
     *
     * @return false if the connection id is duplicate or if the dataMap id is
     * duplicated
     */
    /**
     * validateEfwd
     * @deprecate
     * This method no longer used
     * <p> use {@link ResourceValidator#newValidateEfwd()} instead </p>
     * @return
     */
    public boolean validateEfwd() {
        JSONArray dataSources = jsonObject.getJSONArray("DataSources");
        JSONArray dataMaps = jsonObject.getJSONArray("DataMaps");
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (int connection = 0; connection < dataSources.size(); connection++) {
            int id = dataSources.getJSONObject(connection).getInt("@id");
            if (list.contains(id)) {
                return false;
            } else {
                list.add(id);
            }
        }

        for (int dataMap = 0; dataMap < dataMaps.size(); dataMap++) {
            int id = dataMaps.getJSONObject(dataMap).getInt("@id");
            if (arrayList.contains(id)) {
                return false;
            } else {
                arrayList.add(id);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The Efwd file is validated.");
        }
        return true;
    }
    /**
     * using gson
     * @return
     */
    public boolean newValidateEfwd() {
        JsonObject dataSources = object.getAsJsonObject("DataSources");
        JsonObject dataMaps = object.getAsJsonObject("DataMaps");
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> arrayList = new ArrayList<>();

        
            int id = dataSources.getAsJsonObject("Connection").get("id").getAsInt();
            if (list.contains(id)) {
                return false;
            } else {
                list.add(id);
            }
       

        
            int dataMapId = dataMaps.getAsJsonObject("DataMap").get("id").getAsInt();
            if (arrayList.contains(dataMapId)) {
                return false;
            } else {
                arrayList.add(dataMapId);
            }
        

        if (logger.isDebugEnabled()) {
            logger.debug("The Efwd file is validated.");
        }
        return true;
    }

    /**
     * Returns false if the vf file consists of duplicate value for id
     *
     * @return Return false if there exists a duplicate id in the charts
     */
    /**
     * validateVf()
     * This method is no longer acceptable 
     * <p> Use {@link ResourceValidator#newValidateVf()} instead.</p>
     * @return true if non same ids are there {@code false} id same id are repeating
     */
    @Deprecated
    public boolean validateVf() {
        JSONArray charts = jsonObject.getJSONArray("Charts");
        ArrayList<Integer> list = new ArrayList<>();
        for (int chart = 0; chart < charts.size(); chart++) {
            int id = charts.getJSONObject(chart).getInt("@id");
            if (list.contains(id)) {
                return false;
            } else {
                list.add(id);
            }
        }
        logger.info("VF file is validated.");
        return true;
    }
    /**
	 * newValidateVf() using gson
	 * @return boolean 
	 */
    public boolean newValidateVf() {
        JsonArray charts = object.getAsJsonArray("Charts");
        ArrayList<Integer> list = new ArrayList<>();
        for (int chart = 0; chart < charts.size(); chart++) {
            int id = charts.get(chart).getAsJsonObject().get("@id").getAsInt();
            if (list.contains(id)) {
                return false;
            } else {
                list.add(id);
            }
        }
        logger.info("VF file is validated.");
        return true;
    }
    
}
