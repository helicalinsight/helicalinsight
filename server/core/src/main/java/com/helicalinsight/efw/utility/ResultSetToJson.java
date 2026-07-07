package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.ResultSetHelper;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;


//move this class inside efw.utility.*
@Component
@Scope("prototype")
//TODO Instead of taking result set directly, cache layer can be implemented
public class ResultSetToJson {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetToJson.class);

    private ResultSet resultSet;

    public ResultSetToJson() {

    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }


    //TODO we need to check the performance with respective huge data, expected to iterate using batchsize
    public JsonObject resultSetToJson(boolean wrap, JsonObject formData) {

        ResultSetHelper resultSetHelper = new ResultSetHelper();


        JsonObject queryResult = new JsonObject();
        try {
            queryResult = resultSetHelper.resultSetToJsonConverter(this.resultSet, formData);
        } catch (Exception e) {
            logger.error("Exception", e);
            if (wrap) {
                return wrapError(e);

            }
        }
        return wrap ? wrapSuccess(queryResult) : queryResult;
    }


    private JsonObject wrapSuccess(JsonObject object) {
        JsonObject model = new JsonObject();
        model.addProperty("status", 1);
        model.add("response", object);
        return model;
    }

    private JsonObject wrapError(Exception exception) {
        JsonObject model = new JsonObject();
        model.addProperty("status", 0);
        logger.error("An exception has taken place. The stackTrace is ", exception);
        JsonObject response = new JsonObject();
        response.addProperty("message", "Error: " + ExceptionUtils.getRootCauseMessage(exception));
        model.add("response", response);
        return model;
    }
}