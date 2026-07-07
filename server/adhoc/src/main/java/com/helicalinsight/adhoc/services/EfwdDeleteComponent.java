package com.helicalinsight.adhoc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.efw.components.SqlJdbcHandler;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;

public class EfwdDeleteComponent implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        ObjectNode formDataJson = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formDataJson = mapper.readValue(jsonFormData, ObjectNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String id = "";
        String type = "";
        String dir = "";

        if (null != formDataJson) {
            type = formDataJson.path("type").asText();
            id = formDataJson.required("id").asText();
            dir = formDataJson.get("directory").asText();
            EfwdDatasourceUtils.validatePermission(dir);
        }

        ObjectNode model = JsonNodeFactory.instance.objectNode();
        SqlJdbcHandler handler = new SqlJdbcHandler();
        handler.deleteDatasource(Integer.valueOf(id));

        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("id", id);
        model.put("message", "The data source has been deleted successfully.");
        model.set("data", data);
        return model.toString();

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
