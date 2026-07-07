export const getSampleCode = (clickedActiveDatabaseData = false, checkIfGroovyManaged = () => { }) => {
    if (clickedActiveDatabaseData && checkIfGroovyManaged(clickedActiveDatabaseData)) {
        return `import groovy.sql.Sql;
      import net.sf.json.JSONObject;
      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;
      public JSONObject evalCondition() {
        JSONObject responseJson = new JSONObject();
        String userName = GroovyUsersSession.getValue('${'${user}'}.name');
        userName = userName.replaceAll("'", "");
        if (userName.equals("hiadmin")) {
          responseJson.put("globalId", 1);
        }
      
        if (userName.equals("hiuser")) {
          responseJson.put("globalId", 3);
        }
      
        if (userName.equals("test")) {
          responseJson.put("globalId", 4);
        }
      
        responseJson.put("type", "global.jdbc");
      
        //throw new RuntimeException("This is a test exception" +responseJson);
        return responseJson;
      }`
    }
    return `import net.sf.json.JSONObject;
    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;
    public JSONObject evalCondition() {
        JSONObject responseJson = new JSONObject();
        String userName = GroovyUsersSession.getValue('${'${user}'}.name');
        userName = userName.replaceAll("'", "");
        responseJson.put("driver", "com.mysql.jdbc.Driver");
        responseJson.put("url", "jdbc:mysql://localhost:3306/" + userName);
        responseJson.put("user", "root");
        responseJson.put("pass", "root");
        return responseJson;
    }`
}


export const getDefaultCode = (editorType, driverType, name, opts={datasourceName: "" } ) => {

  if (editorType === "config" && driverType === "hihttp"){
    return ``
  }

  if (editorType === "groovy" && driverType === "hihttp"){
    return ``
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile csv"){
    return ``
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile excel"){
    return ``
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile json"){
    return ``
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile parquet"){
    return ``
  }

  if (editorType === "config" && driverType === "flatfile"){
    return ``
  }

  return ``

}

export const copyDefaultCode = (editorType, driverType, name ) => {

  if (editorType === "config" && driverType === "hihttp"){
    return `{
    "name" : "",
    "url": "", 
    "strategy" : "in-memory",  
    "persistentLocation" : "", 
    "preExecuteScript" : "",   
    "libPaths" : [""],
    "authType": "", 
    "headers" : {
        "Authorization" : ""
    },
    "queryParams": {},
    "params": [],
    "postBody": {},
    "dataPath": "",
    "method" : "", 
    "requireTail": false,
    "username" : "",
    "password" : "",
    "connectionTimeOut" : 5000,
    "basicAuthScheme" : "BASIC",
    "requireTailCharacterMapping" : {},
    "sample": "", 
    "schema": "",
    "specialCharsInKey": {"regex":".","replace":"false"},
    "transformResponse": "false",
    "disableSSL": true
}`
  }

  if (editorType === "groovy" && driverType === "hihttp"){
    return `import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.helicalinsight.datasource.service.GlobalConnectionService;
// import com.helicalinsight.datasource.model.GlobalConnections;
import java.util.*;



/**
§. Comment the unnecessary methods
§. Precedence of the return values high while setting up the connection configuration
**/


// String getDS () {
//  GlobalConnectionService dsService =   ApplicationContextAccessor.getBean(GlobalConnectionService.class);
//  GlobalConnections connection =  dsService.findGlobalConnectionById(1);
//  System.out.println(connection.);
// }



/**
@return  token String
Ex: return "bWFzdGhfc2hhZGVzX3VubmF5X25lZWVsb29v";
**/

String setToken() {
def post = new URL("http://localhost:9091/hi-ee/rest/authToken").openConnection();
def body = '{"username":"hiadmin","password":"hiadmin"}';
post.setRequestMethod("POST")
post.setDoOutput(true)
post.setRequestProperty("Content-Type", "application/json")
post.getOutputStream().write(body.getBytes("UTF-8"));
def postRC = post.getResponseCode();
if (postRC.equals(200)) {
String response = post.getInputStream().getText();
ObjectMapper mapper = new ObjectMapper();
ObjectNode node = mapper.readValue(response,ObjectNode.class);
return node.get("token").asText();
}
else {
return "";
}
}

/**
@return json string
Ex: return "{"key":"value","key1":"value1"}";
**/

/**
String setPostBody() {
return "";
}
**/



/**
@return username string;
**/

/**
String setUserName() {
return "";
}
**/




/**
@retrun password string
**/

/**

String setPassword() {
return "";
}

**/



/**
@return strategy string 
**/

/**
String setStrategy() {
return "";
}
**/



/**
@return tablename 
**/

/**
String setTableName() {
return "";
}

**/



/**

@return headers map

Ex:  Map<String,String> map = new HashMap<>();
map.put('Content-Type','application/json')

return map;
**/

/**

Map<String,String> setHeaders() {
return Map.of();
}

**/

/**

@return Query parameter map

Ex:  Map<String,String> queryParams = new HashMap<>();
map.put('page','1');
map.put('pazeSize','10');
return map;
**/


/**
Map<String,String> setQueryParams() {
return Map.of();
}

**/



/**

@return params list

Ex:  List<String> params = new ArrayList<>();
params.add("lng");
params.add("lat"),
return params;
**/

/**

List<String> setParams() {
return List.of();
}

**/

/**
@return datapath string 

**/

/**
String setDataPath() {
return "";
}
**/

/**
@return method
Ex: return "GET";

**/

/**
String setMethod() {
return "";
}

**/

/**
@return requireTail string 
Ex: return "false";
**/


/**
String setRequireTail() {
return "";
}

**/

/**
@return  sample size 
Ex: return "100";
**/

/**
String setSample() {
return "";
}

**/


/**
@return  schema json string 
Ex: return "";
**/

/**
String setSchema() {
return "";
}

**/

/**
import com.fasterxml.jackson.databind.JsonNode
        import com.fasterxml.jackson.databind.node.ObjectNode

        JsonNode transformResponse(JsonNode data) {
            // Check if data is a JSON object
            if (data.isObject()) {
                ((ObjectNode) data).put("currentDate", "2025-10-10");
            } else if (data.isArray()) {
                // Handle JSON array if needed
                for (JsonNode element : data) {
                    if (element.isObject()) {
                        ((ObjectNode) element).put("currentDate", "2025-10-10");
                    }
                }
            }
            return data;
        }


**/`
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile csv"){
    return `{
    "tableName" : "",
    "strategy" : "in-memory",
    "persistentLocation" : "",
    "extensions": ["spatial"],
    "config": {    
    "auto_detect" : true,
    "parallel" : true
}
}`
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile excel"){
    return `{
    "tableName" : "",
    "strategy" : "in-memory",
    "persistentLocation" : "",
    "extensions": ["spatial"],
    "config": {    
    "layer" : ["sheet1","sheet2"],
    "open_options" : ["HEADERS=FORCE", "FIELD_TYPES=AUTO"]
	}
}`
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile json"){
    return `{
    "tableName" : "",
    "strategy" : "in-memory",
    "persistentLocation" : "",
    "extensions": ["spatial"],
    "config": {    
    "auto_detect" : true
	}
}`
  }

  if (editorType === "config" && driverType === "flatfile" && name === "Flatfile parquet"){
    return `{
    "tableName" : "",
    "strategy" : "in-memory",
    "persistentLocation" : "",
    "extensions": ["spatial"],
    "config": {    
    "binary_as_string" : true,
    "file_row_number" : true
	}
}`
  }

  if (editorType === "config" && driverType === "flatfile"){
    return `{
    "tableName" : "",
    "strategy" : "in-memory",
    "persistentLocation" : "",
    "extensions": ["spatial"],
    "config": {    
	}
}`
  }

  if (editorType === "groovy" && driverType === "groovyCopyCode" && name === "Groovy Copy Code") {
    return `import com.helicalinsight.efw.utility.GroovyUsersSession;
public String evalCondition() {

    String userName = GroovyUsersSession.getValue('\${user}.name'); 
    userName = userName.replaceAll("'", "");

    String responseJson;
    String whereClause;

    String selectClause = """select ("destination") as "destination","travel_cost" as "travel_cost" 
    from "travel_details" """;

    if (userName.equals("hiadmin")) {
        whereClause = """where ("destination"='Ambala')""";
    } else {
        whereClause = """where ("destination"='Paris')""";
    }

    responseJson = selectClause + " " + whereClause;

    return responseJson;  
}
 `
  }

}