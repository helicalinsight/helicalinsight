package com.helicalinsight.efw.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
public class EfwdReader implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(EfwdReader.class);
    
    public String evaluateEFWDCondition(String formData) {
        ObjectNode formDataJson = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formDataJson = mapper.readValue(formData, ObjectNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String type = formDataJson.required("type").asText();
        String id = formDataJson.required("id").asText();
        ObjectNode connection = EfwdDatasourceUtils.getEfwdConnection(id, type);
        return connection.toString();
    }
    
    public String executeComponent(String formData) {
        ObjectNode formDataJson = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formDataJson = mapper.readValue(formData, ObjectNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String type = formDataJson.required("type").asText();
        String id = formDataJson.required("id").asText();
        String dir = formDataJson.get("dir").asText();
        EfwdDatasourceUtils.validatePermission(dir);
        ObjectNode connection = EfwdDatasourceUtils.getContent(id, type);
        return connection.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
