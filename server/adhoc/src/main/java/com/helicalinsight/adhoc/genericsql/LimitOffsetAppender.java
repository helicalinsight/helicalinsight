package com.helicalinsight.adhoc.genericsql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Utility class to append limit and offset to SQL queries using a JavaScript function.
 *
 * Created on 10/9/2015.
 * @author Rajasekhar
 * @author Somen
 */
final class LimitOffsetAppender {
    private static final Logger logger = LoggerFactory.getLogger(LimitOffsetAppender.class);
    private final String driverClassName;
    private final String offset;
    private final String limit;
    private final String jsFileName;
    private String query;
    private SqlQueryContext context;

    public LimitOffsetAppender(String query, SqlQueryContext context) {
        this.context = context;
        this.driverClassName = context.getDriverClassName();
        this.query = query;
        this.offset = context.getQueryOffset();
        this.limit = context.getQueryLimit();
        this.jsFileName = context.getReferenceFile();
    }
    /**
     * Appends limit and offset to the SQL query using a JavaScript function.
     * @return The SQL query with limit and offset appended.
     */
    public String setLimitAndOffSet() {
        if (this.jsFileName == null) {
            log();
            return this.query;
        }
        String path = JsonUtils.sqlFunctionsLocation();
        String jsFilePath = path + File.separator + this.jsFileName + ".js";

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(jsFilePath), ApplicationUtilities.getEncoding()));
            Context context = Context.enter();
            ScriptableObject scope = context.initStandardObjects();
            context.evaluateReader(scope, bufferedReader, "script", 1, null);
            Function function = (Function) scope.get("setQueryOffsetLimit", scope);
            // String databaseName = this.context.doApplyQuotes(this.context.getMetadata().getDatabase().getName());

            JsonObject contextObject = prepareContext();
            Object result = function.call(context, scope, scope, new Object[]{this.query, this.offset, this.limit, contextObject.toString()});
            //logger.error("result  :"+(JSONObject) result);
            this.query = (String) Context.jsToJava(result, String.class);
        } catch (FileNotFoundException ignore) {
            log();
            return this.query;
        } catch (IOException ioe) {
            throw new EfwServiceException("The respective file was not found");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignore) {
                }
            }
            Context.exit();
        }
        return query;
    }
    /**
     * Logs a warning message when the JavaScript file is not found.
     */
    private void log() {
        logger.warn("The script file for " + (this.driverClassName != null ? this.driverClassName : " a driver") + "" +
                " is not found. Configure javascript to paginate the queries.");
    }
    /**
     * Prepares the context object containing metadata and other information.
     * @return  JSON object containing the context information.
     */
    private JsonObject prepareContext() {

        JsonObject contextJSONObject = new JsonObject();
        Gson gson = new Gson();
        try {
            contextJSONObject.add("nameAndFullNameMap", gson.toJsonTree(context.getNameAndFullNameMap()).getAsJsonObject());
            contextJSONObject.add("columnsMap", gson.toJsonTree(context.getColumnsMap()).getAsJsonObject());
            
            List<String> requestedTables = context.getRequestedTables();
            JsonArray array = new JsonArray();
            for (String item : requestedTables) {
            	array.add(item);
            }
            contextJSONObject.add("requestedTables", array);
            contextJSONObject.add("graphNodes", gson.toJsonTree(context.getGraphNodes()).getAsJsonArray());
            contextJSONObject.add("tables", gson.toJsonTree(context.getTables()).getAsJsonArray());
            contextJSONObject.add("tablesAlias", gson.toJsonTree(context.getTableAliasMap()).getAsJsonObject());
            contextJSONObject.add("derivedTableNames", gson.toJsonTree(context.getDerivedTableNames()).getAsJsonArray());
            contextJSONObject.add("derivedTableColumns", gson.toJsonTree(context.getDerivedTableColumns()).getAsJsonArray());
            contextJSONObject.addProperty("databaseName", context.getDatabaseName());
            contextJSONObject.add("formData", context.getFormData());
            contextJSONObject.addProperty("distinctResults", context.isDistinctResults());
            contextJSONObject.addProperty("applyWhere", context.isApplyWhere());
            contextJSONObject.addProperty("queryOffset", context.getQueryOffset());
            Metadata metadata = context.getMetadata();
            String metadataJson = gson.toJson(metadata);
            contextJSONObject.add("metadata", JsonParser.parseString(metadataJson).getAsJsonObject());
            contextJSONObject.addProperty("isLimitRequested", context.isLimitRequested());
            contextJSONObject.addProperty("applyAggregation", context.isApplyAggregation());
            contextJSONObject.addProperty("applyGroupBy", context.isApplyGroupBy());
            contextJSONObject.addProperty("applyOrderBy", context.isApplyOrderBy());
            contextJSONObject.addProperty("applyHaving", context.isApplyHaving());
            contextJSONObject.addProperty("queryLimit", context.getQueryLimit());
            contextJSONObject.addProperty("openQuote", context.getOpenQuote());
            contextJSONObject.addProperty("closeQuote", context.getCloseQuote());
            contextJSONObject.addProperty("driverClassName", context.getDriverClassName());
            String username = AuthenticationUtils.getUserDetails().getLoggedInUser().getUsername();
            contextJSONObject.addProperty("userName", username);
            String organization = AuthenticationUtils.getOrganization();
            contextJSONObject.addProperty("orgName", organization==null? ApplicationProperties.getInstance().getNullValue():organization);
        } catch (Exception ex) {
            logger.error("Exception occurred while prepared JSONObject context" + ex);
            ex.printStackTrace();
        }

        return contextJSONObject;
    }
}
