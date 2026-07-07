package com.helicalinsight.datasource.managed;

import com.helicalinsight.datasource.GlobalJdbcType;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIHcrConnectionsEfwd;
import com.helicalinsight.admin.model.HIHcrConnectionsGlobal;
import com.helicalinsight.admin.model.HIHcrQueryParameters;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HiHcrQuery;
import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by author on 25-Dec-14.
 *
 * @author Rajasekhar
 */
public class JsonUtils {

    private static Map<String, List<String>> visibleExtensionsMap;

    @Nullable
    public static String extractPassword(String json) {
        String password = JsonUtils.getKeyFromJson(json, "password");
        if (password == null) {
            throw new MalformedJsonException("Can't obtain connection with null password");
        }
        return password;
    }

    @Nullable
    public static String extractOptionalPassword(String json) {
        String password = JsonUtils.getKeyFromJson(json, "password");
        if (password == null) {
            password = "";
        }
        return password;
    }

    @Nullable
    public static String getKeyFromJson(String json, String key) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement != null) {
            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() == 0) {
                return null;
            }
            return jsonElement.getAsString();
        } else {
            return null;
        }
    }

    @Nullable
    public static String extractUserName(String json) {
        String username = JsonUtils.getKeyFromJson(json, "username");
        if (username == null) {
            username = JsonUtils.getKeyFromJson(json, "userName");
            if (username == null) {
                throw new MalformedJsonException("Can't obtain connection with 'null' username");
            }
        }
        return username;
    }

    @Nullable
    public static String extractOptionalUserName(String json) {
        String username = JsonUtils.getKeyFromJson(json, "username");
        if (username == null) {
            username = JsonUtils.getKeyFromJson(json, "userName");
            if (username == null) {
                username = "";
            }
        }
        return username;
    }

    public static boolean isJndiLookUpRequested(String json) {
        String dataSourceProvider = JsonUtils.getKeyFromJson(json, "dataSourceProvider");
        return DataSourceProviders.JNDI.equalsIgnoreCase(dataSourceProvider);
    }

    public static boolean isNonPooled(String json) {
        String dataSourceProvider = JsonUtils.getKeyFromJson(json, "dataSourceProvider");
        return DataSourceProviders.NONE.equalsIgnoreCase(dataSourceProvider);
    }

    @Nullable
    public static String extractDriverName(String json) {
        String provider =getKeyFromJson(json,"dataSourceProvider");
        if(DataSourceProviders.JNDI.equalsIgnoreCase(provider)){
            return  null;
        }
        String driverName = com.helicalinsight.efw.utility.JsonUtils.extractDriverName(json);
        return driverName;
    }


    @Nullable
    public static String extractUrl(String json) {
        String jdbcUrl = JsonUtils.getKeyFromJson(json, "jdbcUrl");
        if (jdbcUrl == null) {
            jdbcUrl = JsonUtils.getKeyFromJson(json, "url");
            if (jdbcUrl == null) {
                throw new MalformedJsonException("The json parameters jdbcUrl or url is not " + "present in json");
            }
        }
        return jdbcUrl;
    }

    public static <T> List<List<T>> preparePaginatedList(Collection<T> c, Integer pageSize) {
        if (c == null)
            return Collections.emptyList();
        List<T> list = new ArrayList<T>(c);
        if (pageSize == null || pageSize <= 0 || pageSize > list.size())
            pageSize = list.size();
        int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
        List<List<T>> pages = new ArrayList<List<T>>(numPages);
        for (int pageNum = 0; pageNum < numPages; )
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        return pages;
    }

    public static Map<String, List<String>> prepareExtensionsMap(JsonObject visibleExtensions) {
        Assert.notNull(visibleExtensions, "Visible extension's JSON is null!");
        Iterator<?> iterator = (Iterator<?>) visibleExtensions.keySet().iterator();
        visibleExtensionsMap = new HashMap<>();
        while (iterator.hasNext()) {
            int counter = 0;
            String key = (String) iterator.next();
            try {
                iterate(visibleExtensions, counter, key);
            } catch (JsonSyntaxException ex) {
                // If the key is not a JSON object move to the next key
            }
        }
        return visibleExtensionsMap;
    }

    private static void iterate(JsonObject visibleExtensions, int counter, String key) {
        /*
         * visible attribute is provided. Hence no scope for
         * JSONException as it is a JSON object
         */
        JsonElement value = visibleExtensions.get(key);
        if (value instanceof JsonPrimitive) {
            String stringValue = value.getAsString();
            List<String> textAndRuleValuesList = new ArrayList<>();
            textAndRuleValuesList.add(stringValue);
            visibleExtensionsMap.put(key.toLowerCase(), textAndRuleValuesList);
        } else {
            JsonObject json = value.getAsJsonObject();
            try {
                List<String> textAndRuleValuesList = new ArrayList<>();
            /*
             * Look for the visible tag's rule implementation if it is
             * not null. If rule is not provided move to the catch block
             * as there will be an exception
             */
                try {
                    if (json.get("@rule").getAsString() != null) {
                    /*
                     * Whether or not rule attribute is provided text
                     * value must have been provided. Place the key's
                     * text value in the list
                     */
                        textAndRuleValuesList.add(counter, json.get("#text").getAsString());
                        // Increment the counter
                        counter = counter + 1;
                    /*
                     * Add the rule attribute value as the second index
                     * in the list
                     */
                        textAndRuleValuesList.add(counter, json.get("@rule").getAsString());
                        visibleExtensionsMap.put(key.toLowerCase(), textAndRuleValuesList);
                    }
                } catch (JsonSyntaxException ex) {
                    textAndRuleValuesList.add(counter, json.get("#text").getAsString());
                    counter = counter + 1;
                    // No rule attribute is provided. Add null to the list
                    textAndRuleValuesList.add(counter, null);
                    visibleExtensionsMap.put(key.toLowerCase(), textAndRuleValuesList);
                }
            } catch (JsonSyntaxException ignore) {
            }
        }
    }

    public static ResponseEntity<?> get500ErrorResponse(Exception e) {
        JsonObject status = new JsonObject();
        status.addProperty("cause", e.getCause().toString());
        status.addProperty("message", e.getMessage().toString());
        return ResponseEntity.status(500).body(status.toString());
    }
    
    public static JsonObject prepareEfwJsonByHcr(List<HIHcrConnections> hiHcrConnections) {
    	JsonObject efwd=new JsonObject();
    	JsonObject dataSources=new JsonObject();
    	JsonArray dataMaps=new JsonArray();
    	JsonArray connections=new JsonArray();
    	
    	hiHcrConnections.forEach(hcrCon->{
    		System.out.println(hcrCon.getId());
    		JsonObject connection=new JsonObject();
    		connection.addProperty("type", hcrCon.getConnectionType());
    		HIHcrConnectionsEfwd efwCon=hcrCon.getHiHcrConnectionsEfwd();
			JsonObject connDetails=new JsonObject();
    		Integer conId=null;
    		if(efwCon!=null) {
    			conId=efwCon.getHiEfwdConnection().getId();
    			connection.addProperty("id", conId);
    		}
    		else {
    			HIHcrConnectionsGlobal hcrGlobal=hcrCon.getHiHcrConnectionsGlobal();
    			if(hcrGlobal!=null) {
    				conId=hcrGlobal.getGlobalConnections().getId();
    				connection.addProperty("id", conId);
    				connDetails.add("globalId", connection.get("id"));
    			}
    		}
    		connection.add("connDetails", connDetails);
    		JsonObject json = new JsonObject();
    		json.add("connection",connection);
    		connections.add(json);
    		HiHcrQuery hiHcrQuery=hcrCon.getHiHcrQuery();
    		if(hiHcrQuery!=null) {
    			JsonObject dataMap=new JsonObject();
    			dataMap.addProperty("id", hiHcrQuery.getId());
    			dataMap.addProperty("name", hiHcrQuery.getHcrQueryName());
    			dataMap.addProperty("connection", conId);
    			dataMap.addProperty("type", hiHcrQuery.getHcrQueryType());
    			dataMap.addProperty("query", hiHcrQuery.getHcrQueryString());
    			JsonArray params=new JsonArray();
    			List<HIHcrQueryParameters> queryParams=hiHcrQuery.getHiHcrQueryParameters();
    			if(queryParams!=null) {
    				queryParams.forEach(p->{
        				JsonObject param=new JsonObject();
        				param.addProperty("default", p.getParamDefaultValue());
        				param.addProperty("name", p.getParameterName());
        				param.addProperty("type", p.getParameterType());
        				params.add(param);
    				});
    			}
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.add("dataMap", dataMap);
    			dataMaps.add(jsonObject);
    		}
    	});
    	dataSources.add("connections", connections);
    	efwd.add("dataSources", dataSources);
    	efwd.add("dataMaps", dataMaps);
    	JsonObject efwdObj = new JsonObject();
    	efwdObj.add("efwd", efwd);
    	return efwdObj;
    }

}
