package com.helicalinsight.efw.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.managed.TestConnectionProvider;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 30-01-2015.
 *
 * @author Rajasekhar
 * @author Muqtar
 */
@Component
public class EfwdConnectionTester implements IComponent {

    @Nullable
    public String executeComponent(String formData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode formDataJson = null;
        try {
            formDataJson = mapper.readValue(formData, ObjectNode.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(null == formDataJson) {
            throw new EfwdServiceException("Something went wrong.");
        }
        String type = formDataJson.get("type").asText();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", type);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        if (formDataJson.has("directory")) {
            String directory = formDataJson.get("directory").asText();
            formDataJson.put("dir", directory);
        }

        if (formDataJson.has("driverName")) {
            String driverName = formDataJson.get("driverName").asText();
            if (driverName.contains("nosql") && type.equals("sql.jdbc")) {
                return DataSourceUtils.testNosqlDS(new Gson().fromJson(formDataJson.toString(),JsonObject.class));
            }
            if (driverName.startsWith(JsonUtils.getHiMiddleWareName()) && !driverName.contains("nosql")) {
                String minusJsonString = TestConnectionProvider.minusJson(null, new Gson().fromJson(formDataJson.toString(),JsonObject.class), driverName);
            }
        }
        if (formDataJson.has("id")) {
            formDataJson.put("access", DataSourceSecurityUtility.EXECUTE);
            //DataSourceSecurityUtility.isDataSourceAuthenticated(formDataJson);
        }

        EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);

        return handler.testDS(formDataJson).toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
