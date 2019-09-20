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

package com.helicalinsight.efw.validator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * An instance of this class is used to validate the EFWD and EFWVF files
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Avi
 */
public class ResourceValidator {

    private static final Logger logger = LoggerFactory.getLogger(ResourceValidator.class);
    /**
     * The json of the file under concern
     */
    private final JSONObject jsonObject;

    /**
     * Constructs the object of the same type
     *
     * @param jsonObject The json of the file under concern
     */
    public ResourceValidator(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Returns false if the connection id is duplicate or if the dataMap id is
     * duplicated
     *
     * @return false if the connection id is duplicate or if the dataMap id is
     * duplicated
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
     * Returns false if the vf file consists of duplicate value for id
     *
     * @return Return false if there exists a duplicate id in the charts
     */
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
}
