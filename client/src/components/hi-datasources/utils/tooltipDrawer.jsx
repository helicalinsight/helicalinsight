import { CheckOutlined, CopyOutlined, QuestionCircleOutlined } from "@ant-design/icons";
import { Drawer, Tooltip } from "antd";
import React, { useState } from "react";
import { useSelector } from "react-redux";

import { Typography, Collapse, Table } from 'antd';
import { getExampleConf, getTableConfData } from "./table-conf";
import { copyDefaultCode } from "./getSampleCode";

const { Title, Paragraph, Text } = Typography;

export const CopyText = ({ databaseDialect, name, type}) => {
  const [copied, setCopied] = useState(false);

  const copyToClipboard = (text) => {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.left = '-9999px';
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
  };

  
  return copied ? (
    <Paragraph>
      <Tooltip title="Copied to clipboard">
        <CheckOutlined style={{ color: "green" }} />
      </Tooltip>
      <Text style={{ marginLeft: "5px" }} strong>
        Copied
      </Text>
    </Paragraph>
  ) : (
    <Paragraph>
      <Tooltip title="Click to copy the default code">
        {/* <CopyOutlined
      onClick={() => {
        if (navigator.clipboard) {
          navigator.clipboard.writeText(
            copyDefaultCode(type, databaseDialect, name)
          );
          setCopied(true);
          setTimeout(setCopied, 3000, false);
        }
      }}
    /> */}
        <CopyOutlined
          onClick={(e) => {
            copyToClipboard(copyDefaultCode(type, databaseDialect, name));
            setCopied(true);
            e.stopPropagation();
            setTimeout(() => setCopied(false), 3000);
          }}
        />
      </Tooltip>
      <Text style={{ marginLeft: "5px" }} strong>
        Copy to clipboard
      </Text>
    </Paragraph>
  );
}

const FlatfileHowToUse = ({ databaseDialect, name }) => {
  return (
    <div style={{ padding: '20px' }}>
      <Title level={3}>How to use?</Title>
      <CopyText databaseDialect={databaseDialect} name={name} type="config"/>

      <Collapse>
          <pre>
            {`{
"tableName": "mydata",
"strategy": "in-memory",
"persistentLocation": "",
"extensions": [
    "excel",
    "spatial"
    ],
"config": {
     // check specific file configuration in the configuration section
    }
}`}
          </pre>
      </Collapse>

      <Paragraph>
        <Text strong>1. tableName:</Text> name of the table
      </Paragraph>

      <Paragraph>
        <Text strong>2. strategy:</Text> strategy can be <Text code>in-memory</Text> or <Text code>persistent</Text>.
      </Paragraph>

        <Paragraph>2.1. <Text code>in-memory</Text>: data will be stored in-memory (heap) (recommended)</Paragraph>
        <Paragraph>2.2. <Text code>persistent</Text>: a physical DB file will be created.</Paragraph>

      <Paragraph>
        <Text strong>3. persistentLocation:</Text> if strategy is persistent, the path of the DB file should be provided here (e.g., <Text code>C:\\dbs\\test.duckdb</Text>)
      </Paragraph>

      <Paragraph>
        <Text strong>4. Config:</Text> configuration of the specific file type.
      </Paragraph>

      <Paragraph>
        <Text strong>5. JDBC Url format:</Text>
        <Text code>{"jdbc:flatfile:<file-path|spreadsheet-url|s3-url>?configPath=<filename>.json?tableName=<TableName>&strategy=<STRATEGY=[IN-MEMORY | PERSISTENT]>&persistentLocation=<PERSISTENT-LOCATION=[C:\\dbs\\test.duckdb]>&extension=[csv|json|xlsx|xls|parquet]"}</Text>
      </Paragraph>

      <Paragraph>
        <Text strong>NOTE:</Text> URL parameters typically take precedence over the parameters mentioned in the configuration file.
      </Paragraph>

      <Paragraph>
        <Text strong>6. Wildcard entries in jdbc url</Text>
        <Text code>{"jdbc:flatfile:D:/FlatFileJDBCDriver/**/*.parquet?configPath=config.json"}</Text>
      </Paragraph>

      <Paragraph>
        6.1. The wildcard URL reads all the parquet files available in subfolders of <Text code>D:/FlatFileJDBCDriver</Text>
      </Paragraph>
    </div>
  );
};

const ConfigEditorData = ({ databaseDialect, name}) => {
    return (
      <div style={{ padding: '20px' }}>
        <Title level={3}>Ex: Config</Title>
        <CopyText databaseDialect={databaseDialect} name={name} type="config"/>
        <Collapse>
            <pre>
              {`{
  "name" : "<tableName>",
  "url": "https://dummyjson.com/auth/me", // not using right now , may use in future , value of the url will be taken from the provided jdbc url
  "strategy" : "in-memory",  // Allowed values[in-memory,persistent]
  "persistentLocation" : "", 
  "preExecuteScript" : "<script-file-name>",
  "libPaths" : ["<Paths of the jar file folders>"],
  "authType": "jwt",  // Allowed values [basic,jwt,api-key] 
  "headers" : {
      "Authorization" : "<token>"
  },
  "queryParams": {},
  "params": [],
  "postBody": {},
  "dataPath": "",
  "method" : "", // Allowed values [GET,POST]
  "requireTail": false, // Allowed values [true,false]
  "username" : "",
  "password" : "",
  "connectionTimeOut" : 5000,
  "basicAuthScheme" : "BASIC",
  "requireTailCharacterMapping" : { "QM" : "?" , "AMP" : "&" },
  "sample": "<sample-size>", // Default sample size 10
  "schema": "<schema json strng>",
  "specialCharsInKey": {"regex":".","replace":"false"},
  "transformResponse":"false",
  "disableSSL": true //to enable disable ssl
}`}
            </pre>
        </Collapse>
  
        <Paragraph>
          <Text strong>1. name:</Text> name of the connection, useful to query the api. if not provided baseurl(unsupported characters will be excluded ex : dot(.) etc) will be considered as connection name
          Ex : <Text code>name: sample_connection</Text>
        </Paragraph>
  
        <Paragraph>
          <Text strong>2. url:</Text> base or full url of the rest api endpoint depends on the requireTail property , if requireTail is true , provide full path otherwise provide baseurl without tail ( query param )
          Ex: <Text code>jdbc:https://example.com</Text>
        </Paragraph>
  
        <Paragraph>
          <Text strong>3. strategy:</Text> strategy can be in-memory or persistent.
        </Paragraph>

        <Paragraph>i. <Text code>in-memory</Text>: data will be stored in-memory (heap) (recommended)</Paragraph>
          <Paragraph>ii. <Text code>persistent</Text>: a physical DB file will be created.</Paragraph>
          <Paragraph>
          <Text strong>NOTE:</Text> if selected persistent, please provide <Text code>persistentLocation</Text>
        </Paragraph>
  
        <Paragraph>
          <Text strong>4. persistentLocation:</Text> if strategy is persistent , the path of the db file should be provided here ( ex : <Text code>c:\\dbs\\test.db</Text>)
        </Paragraph>
  
        <Paragraph>
          <Text strong>5. preExecuteScript:</Text> <Text> name of the groovy script with extension. </Text>
           Ex: <Text code>preExecuteScript: token_generator.groovy </Text>
           Note: Set the authType to <Text strong>jwt</Text> when using the setToken method.   
        </Paragraph>
  
        <Paragraph>
          <Text strong>6. libPaths</Text> <Text> Directories of the dependency files referenced in the Groovy script.</Text>
           Ex: <Text code>libPaths : ["/home/helical/libs","/home/helical/jars"]</Text> 
        </Paragraph>
  
        <Paragraph>
          <Text strong>7. authType</Text> <Text> authentication type of endpoint. currently supporting , jwt , basic , api-key endpoints ,if auth not required leave it blank</Text>
          <Text strong>Allowed value</Text> [jwt,basic,api-key] 
        </Paragraph>

        <Paragraph>
          <Text strong>8. headers</Text><Text> Directories of the dependency files referenced in the Groovy script.</Text>
          </Paragraph>
           Ex:
          <Collapse>
            {/* <pre>
              {`{
    ....
    "Authorization" : "Bearer <token>",
        __or__
    "Authorization" : "<token>"
    ....
}`}
            </pre> */}
          <pre>
            {`{
  "name" : "sunrise",
  "url": "https://api.sunrise-sunset.org/json?", 
  "strategy" : "persistent",   
  "persistentLocation" : "/home/helical/Performance/hi/hi-repository/System/Admin/httpconfig/persistent/travel.duckdb",  
  "authType": "",   
  "headers" : { 
    "Authorization" : "Bearer <token>"
  }, 
  "queryParams": { 
    "lat": "36.7201600", 
    "lng ": " -4.4203400", 
    "date ": " today " 
  }, 
  "params": ["lat","lng","date"], 
}`}
            </pre>
        </Collapse>
        <Text strong>If Authorization mentioned in Headers , please setlect jwt as authType</Text>
       

        <Paragraph>
          <Text strong>9. queryParams</Text><Text> are useful when you are working with post data or any other http api methods which doesn’t support url embedding.</Text></Paragraph>
          <Paragraph>
            <Text>For example in case of http post method if you want to post the data in body then use queryParams. In case of get you can use the full url as well. </Text>
          </Paragraph>
           Ex:
          <Collapse>
          <pre>
        {`SELECT *
FROM sunrise
WHERE lat = 36.7201600
AND lng = -4.4203400
AND date = '2019-10-02'`}
            </pre>
          <pre>
          <Text>Below is the configuration required for http url with query parameters</Text><br />
            {`{
  "name": "sunrise",
  "url": "https://api.sunrise-sunset.org/json",
  "strategy": "in-memory",
  "persistentLocation": "",
  "authType": "",
  "headers" : {
  	"Authorization": ""
  },
  "queryParams" : {
  	"lat": "36.7201600",
  	"lng": " -4.4203400",
  	"date": " today "
  },
  "params": ["lat","lng","date"],
  "method": "GET"
}`}
          </pre>
        </Collapse>

        <Paragraph>
          <Text strong>10. params</Text><Text> is used to identify how http url will fetch the data dynamically. Either it will be updated to the url or created. From the sql these configured params are extracted at runtime to get the actual value and accordingly the fresh data would be fetched.</Text>
        </Paragraph>
        Ex.
        <Collapse>
          <pre>
            {`The params configuration is a json array, They are part of the http url

Say we have url
https://dummyjson.com/c/65e2-4152-44f9-9e2d/?id=5&date=today


The above url we have two params id and date

all the above params can be configured in the json array.

"params":["id","date"]


During sql query execution
These params are referred and cross verified with the where clause filters.

If the where clause  filter  have the parameter, then the  mapping are converted to key-value and appended in the http url endpoint.

say the query is

select id, date from posts where id=6 and date='12-10-2025'

then the http url is dynamically changed to

https://dummyjson.com/c/65e2-4152-44f9-9e2d/?id=6&date='12-10-2025'`}
          </pre>
        </Collapse>
        

        <Paragraph>
          <Text strong>11. postBody</Text><Text> Some http post requires a json body to be part of the http request body.  
The request body can be configured using this.
 
This is required if we set the method to POST. And we have any json data as body of the http request.</Text> </Paragraph>
          <Text strong>Note: Currently supporting json body only. </Text>
          Ex:
          <Collapse>
            <pre>
            {`{ 
  "name": "postBodyTest", 
  "url": "https://dummyjson.com/auth/login", 
  "strategy": "in-memory", 
  "postBody": { 
    "username": "michaelw", 
    "password": "michaelwpass",
    ...other parameters in JSON format 
  }, 
  "method": "POST" 
}`}
            </pre>
        </Collapse>
       

        <Paragraph>
          <Text strong>12. dataPath</Text><Text> if there are many objects in json data , provide a object name to consider it to query</Text></Paragraph>
          Ex : There are 3 objects in json data , to query on results data only
          <Collapse>
            <pre>
            {`{
  "results": {
    "sunrise": "6:12:17 AM",
    "sunset": "6:01:54 PM",
    "solar_noon": "12:07:06 PM",
    "day_length": "11:49:37",
    "civil_twilight_begin": "5:47:49 AM",
    "civil_twilight_end": "6:26:22 PM",
    "nautical_twilight_begin": "5:17:51 AM",
    "nautical_twilight_end": "6:56:21 PM",
    "astronomical_twilight_begin": "4:47:41 AM",
    "astronomical_twilight_end": "7:26:31 PM",
    "recorded_time":["6:12:14 AM","6:12:15 AM","6:12:16 AM"],
    },
  "status": "OK",
  "tzid": "UTC"
}`}
            </pre>
        </Collapse>
        
        <Paragraph>
        <Text strong>dataPath should be</Text> <Text code >$ for root element</Text>
        </Paragraph>
        <Paragraph>
        <Text strong>dataPath should be</Text> <Text code >$.results for results json</Text>
        </Paragraph>
        <Paragraph>
        <Text strong>dataPath should be</Text> <Text code >$.results.sunrise for sunrise present inside results json</Text>
        </Paragraph>
        <Paragraph>
        <Text strong>dataPath should be</Text> <Text code >$.results.recorded_time for recorded_time array present inside results json</Text>
        </Paragraph>
        <Paragraph>
        <Text strong>dataPath should be</Text> <Text code >$.results.recorded_time[0] for recorded_time array first element present inside results json</Text>
        </Paragraph>
        <Paragraph>
        <Text strong>dataPath should be</Text> <Text code >$.results.recorded_time[*] for all elements of recorded_time present inside results json</Text>
        </Paragraph>
        <Paragraph>
        <Text strong>Note:</Text> <Text>It support nested objects too.</Text>
        </Paragraph>
        
        <Paragraph>
          <Text strong>13. method:</Text> currently supporting GET and POST methods
        </Paragraph>

        <Paragraph>
          <Text strong>14. requireTail:</Text> [true,false]
          <Text strong>if true provide the tail part in query otherwise provide in url</Text>
        </Paragraph>
        Ex : Query 
        <Collapse>
            <pre>
        {`SELECT sunrise, sunset 
FROM sunrise.\`?lat=36.7201600&lng=-4.4203400&date=2019-10-02\` 
WHERE sunrise='6:12:17 AM'`}
            </pre>
        </Collapse>
        <Text strong>if true provide the tail part in query otherwise provide in url</Text>

        <Paragraph>
          <Text strong>15. username:</Text> username to authenticate <Text strong>{`// only for basic auth endpoint`}</Text>
        </Paragraph>

        <Paragraph>
          <Text strong>16. password:</Text>  password to authenticate
        </Paragraph>

        <Paragraph>
          <Text strong>17. timeout:</Text>  maximum time allowed to send the request to an endpoint
        </Paragraph>

        <Paragraph>
          <Text strong>18. requireTailCharacterMapping:</Text>  characters to replace special charcaters in query.
          <Text code>{`requireTailCharacterMapping:{"QM" : "?"`}</Text>

          <Text strong>Note: You can choose any name for the key.</Text>
          <Text strong>Ex:</Text> Some Databases/Dialects do not support some special characters in query or some dialects replace them.
          </Paragraph>
        <Collapse>
        <pre>
    {`SELECT  * FROM 
sunrise.\`?lat=36.7201600&lng=-4.4203400&date=2019-10-02\``}
        </pre>
        </Collapse>
<Text>In such cases the query can be re-written as</Text>
<Collapse>
        <pre>
    {`SELECT  * FROM 
sunrise.\`QMlat=36.7201600&lng=-4.4203400&date=2019-10-02\``}
        </pre>
        </Collapse>
    <Text>   here <Text code>QM</Text> will be replaced with <Text code>?</Text>  during the execution at Database level</Text> 
        
    <Paragraph>
          <Text strong>19. sample:</Text> The size of the sample
        </Paragraph>
          <Text>This property is useful to decide the schema.</Text>
          <Collapse>
    <pre>
    {`{
  ...
  "sample" : "100"
  ...
}`}
    </pre>
        </Collapse>

        <Paragraph>
          <Text strong>20. schema:</Text> user defined schema
        </Paragraph>
          <Text >This property is useful to set the schema.</Text>
          <Collapse>
    <pre>
    {`{
  ...
  "schema" : "{\"booking_platform\":\"TEXT\",\"travel_id\":\"INTEGER\",\"travel_date\":\"DATETIME\"}"
  ...
}`}
    </pre>
        </Collapse>

        <Paragraph>
          <Text strong>21. specialCharsInKey:</Text> 
          <Text > This is a json object with two keys replace and regex. The replace value should  be true whenever the response json has special key in the json. Default is fals value. We must set it to  false and regex is the characters that needs to be replaced 
          with underscore(_).</Text>
        </Paragraph>
          Consider this below JSON
          <Collapse>
			<pre>
      {`{
  "com.apx.planahead.nextDate": "2024-06-02"
}`}
			</pre>
        </Collapse>
		
		  <Paragraph>
          <Text strong>22. transformResponse:</Text> 
          <Text >  The transformResponse value should  be true/false. By default the value is set to false. If we set to true wee need to write a groovy code see example bewlow. Using the script we can update or remove any key or change the data to something else. </Text>
        </Paragraph>
        
        

    <Paragraph>
      <Text strong>What is difference between params, queryParams, postBody and requiredTail? </Text>
      <Paragraph><Text strong>A.</Text> params are required when your sql query has some filter which are part of http url queryParams and adding the same in http url will reduce the data of httpresponse. 
Say we have url http://www.example.com/loaddata and on hitting this url it gives some 1000 json items.  
And we are interested in a specific data say id=9 which will result in 1 data of http response  
So we can configure the id or other params that is supported in the api to filter the data.  
This will enhance the performance of the HRreport being executed. As it does not load all data and perform filter operation. Rather it just load a specific data.  </Paragraph>
      <Paragraph><Text strong>B.</Text> queryPrams: This should be used when we have a http POST query parameters. We need to configure the values that are required in the post call.  </Paragraph>
      <Paragraph><Text strong>C.</Text> postBody:  This is similar to queryParams but the baisic difference is instead of key value pair, we have a Json payload as part of post request.  </Paragraph>
      <Paragraph><Text strong>D.</Text> requireTail: These is required when we have custom sql query and we want to filter the api respose. The custom sql query doesnot support some special charactres like ? & etc and those special  characters can be mapped. During query execution these part is removed from sql query and appended to the http url . As a result the data is filtered.   </Paragraph>
    </Paragraph>
      </div>
    );
  };

const GroovyEditorData = ({ databaseDialect, name }) => {
return (
    <div style={{ padding: '20px' }}>
    <Title level={3}>Groovy script as configuration</Title>
        <CopyText databaseDialect={databaseDialect} name={name} type="groovy"/>
    <Paragraph>
        <Text strong>NOTE : use below code format to set the properties dynamically.</Text>
        <Text>Ex: Script to set the Authroization token dynamically in Headers.</Text>
    </Paragraph>

    <Paragraph>
        <Text strong>Supported methods</Text> 
    </Paragraph>
    <Text code >{`String setMethod()`}</Text> < br />
    <Text code >{`String setDataPath()`}</Text> < br />
    <Text code >{`Map<String,String> setHeaders()`}</Text> < br />
    <Text code >{`String setStrategy()`}</Text> < br />
    <Text code >{`String setRequireTail()`}</Text> < br />
    <Text code >{`List<String> setParams()`}</Text> < br />
    <Text code >{`Map<String,String> setQueryParams()`}</Text> < br />
    <Text code >{`String setPassword()`}</Text> < br />
    <Text code >{`String setPostBody()`}</Text> < br />
    <Text code >{`String setTableName()`}</Text> < br />
    <Text code >{`String setUserName()`}</Text> < br />
    <Text code >{`String setToken()`}</Text> < br />
    <Text code >{`String setSample()`}</Text> < br />
    <Text code >{`String setSchema()`}</Text> < br />
    < br />

    <Collapse>
<pre>
{`import com.fasterxml.jackson.databind.ObjectMapper;
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
Ex: return "{\"key\":\"value\",\"key1\":\"value1\"}";
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
params.add("lat=<some_value>"),
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


**/

`}
</pre>
    </Collapse>


    </div>
);
};

const GoogleSpreadsheetData = ({ databaseDialect, name}) => {
return (
    <div>
    <Title level={4}>Google Spreadsheet</Title>
        <CopyText databaseDialect={databaseDialect} name={name} type="config"/>
    <Paragraph>
        <Text>Convert the given Google Sheets URL into a download URL for exporting as a CSV file,</Text>
        <Text> we need to extract certain components and construct a new URL with the appropriate format.</Text>
    </Paragraph>

    <Text>Here's the breakdown of the URL components:</Text>

    <Paragraph>
        <Text strong>Base URL:</Text> The base URL is the common part of the Google Sheets URL, which is <Text code >https://docs.google.com/spreadsheets.</Text>
    </Paragraph>

    <Paragraph>
        <Text strong>Path:</Text>  We need to append /export to the base URL to indicate that we want to export the spreadsheet.
    </Paragraph>

    <Paragraph>
        <Text strong>ID:</Text> The ID of the spreadsheet is extracted from the original URL. It is the part between /d/ and /view in the URL.
    </Paragraph>

    <Paragraph>
        <Text strong>Format:</Text>  We need to append ?format=csv to the URL to specify that we want to export the spreadsheet in CSV format.
    </Paragraph>

    Here's how you can construct the new URL:

    <Collapse>
<pre>
{`Base URL: https://docs.google.com/spreadsheets
Path: /export
Format: ?format=csv
ID: 1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms

New URL: https://docs.google.com/spreadsheets/export?format=csv&id=1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms`}
</pre>
    </Collapse>

    <Text strong >JDBC URL :</Text>   <Text code >{`jdbc:flatfile:https://docs.google.com/spreadsheets/export?`}</Text> < br />
    <Text code >{`format=csv&id=1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms&extension=csv`}</Text> 
    </div>
);
};

const RemainingData = ({ databaseDialect, name }) => {
return (
    <div>
    <Title level={4}>S3 ,GCS, Cloudflare R2 , Azure blob Storage</Title>
        <CopyText databaseDialect={databaseDialect} name={name} type="config"/>

    <Paragraph>
        <Text>1.To access the secured files configure the secret object in config file.</Text>
    </Paragraph>

    
    <Collapse>
<pre>
{`    {
...

"secret": {
"name" : "aws_secret",
"type" : "s3",
"key_id" : "<secret-key-id>",
"secret" : "<secret-key>",
"region" : "<region : us-east-1 by default>",
"persist" : false -> make it true to store the secret in local file system
}
...

}
`}
</pre>
    </Collapse>

    <Paragraph>
        <Text>2. Use the file specific configuration.</Text>
    </Paragraph>

    <Paragraph>
        <Text>3. s3, gcp, r2 files use httpfs extension ( installation not required )</Text>
    </Paragraph>

    <Paragraph>
        <Text>4. For Azure Blob Storage files , install azure extension.</Text>
    </Paragraph>

    <Collapse>
<pre>
{`  ...

"extensions": [
    "azure"
]

...
`}
</pre>
    </Collapse>
    <Paragraph>
        <Text strong>Note :</Text> For public files secret is not required.

        <Text strong>JDBC URL :</Text> <Text code >jdbc:flatfile:s3://helical-k8-cluster-test-bucket/Department.csv?configPath=config.json</Text>
    </Paragraph>
    </div>
);
};

const DateFormatData = () => {
  return (
    <div style={{paddingTop:"10px"}}>
      <Title level={4}>Date Format Functions</Title>

      <Paragraph>
        <Text>The <Text code>strftime</Text> and <Text code>strptime</Text> functions provide essential tools for converting between <Text code>DATE</Text> or <Text code>TIMESTAMP</Text> values and string representations.</Text>
      </Paragraph>

      <Paragraph>
        1.<Text code>strftime</Text> - <Text strong>Converting Timestamps or Dates to Strings</Text>
        </Paragraph>
        <Paragraph>
        <Text> • The <Text code>strftime(timestamp, format)</Text> function formats timestamps or dates into strings according to a specified pattern.</Text>
        </Paragraph>
          
      
      <Text strong>Examples :</Text>
      <Collapse>
<pre>
{`SELECT strftime(DATE '1992-03-02', '%d/%m/%Y');
-- Output: 02/03/1992

SELECT strftime(TIMESTAMP '1992-03-02 20:32:45', '%A, %-d %B %Y - %I:%M:%S %p');
-- Output: Monday, 2 March 1992 - 08:32:45 PM`}
</pre>
      </Collapse>

      <Paragraph>
        2.<Text code>strptime</Text> - <Text strong>Converting Strings to Timestamps</Text>
      </Paragraph>

      <Paragraph>
        <Text> • The <Text code>strptime(text, format)</Text> function parses strings into timestamps based on a specified pattern.</Text>
      </Paragraph>
      <Text strong>Examples :</Text>
      <Collapse>
<pre>
{`SELECT strptime('02/03/1992', '%d/%m/%Y');
-- Output: 1992-03-02 00:00:00

SELECT strptime('Monday, 2 March 1992 - 08:32:45 PM', '%A, %-d %B %Y - %I:%M:%S %p');
-- Output: 1992-03-02 20:32:45`}
</pre>
      </Collapse>

      <Paragraph>
        <Text strong>Error Handling :</Text>
      </Paragraph>
        
        <Paragraph>
        <Text>If <Text code>strptime</Text> encounters an invalid date, it will throw an <Text strong>error.</Text></Text>
        </Paragraph>

      <Text strong> For example:</Text>
      <Collapse>
<pre>
{`SELECT strptime('02/50/1992', '%d/%m/%Y') AS x;
-- Error: Invalid Input - "02/50/1992" is not a valid date (month out of range).`}
</pre>
      </Collapse>

      <Paragraph>
        <Text strong>To avoid errors and instead return <Text code>NULL</Text> for invalid inputs, use the <Text code>try_strptime</Text> function:</Text>
      </Paragraph>
      <Collapse>
<pre>
{`SELECT try_strptime('02/50/1992', '%d/%m/%Y');
-- Output: NULL`}
</pre>
      </Collapse>


    </div>
  );
};


const ConfigurationTable = ({ type, name}) => {
    const { title, columns, data } = getTableConfData(type, name);
    const exampleConf = getExampleConf(name);
    return <>
        <Title level={5}>{title}</Title>
        <Table columns={columns} dataSource={data} pagination={false} />
        <Title level={5}>{"Example "}{title}</Title>
        <Collapse><pre>{exampleConf}</pre></Collapse>
        <br />
    </>
}

const TooltipDrawer = ({ children, confType="config" }) => {
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.selectedDriverInfo
  );

  const { databaseDialect, name } = clickedActiveDatabaseData;
  const [visible, setVisible] = useState(false);
  
  const showDrawer = (e) => {
    e.stopPropagation();
    setVisible(true);
  };
  const onClose = (e) => {
    e.stopPropagation()
    setVisible(false);
  };

  const isAwsOrAzureOrCloudFareOrGCS = () => {
    return [
      "Flatfile aws",
      "Flatfile azure blobstorage",
      "Flatfile cloudfare r2",
      "Flatfile GCS",
    ].includes(clickedActiveDatabaseData?.name);
  }

  const isGoogleSpreadsheets = () => {
    return "Flatfile Google spreadsheet" === clickedActiveDatabaseData?.name;
  }

  return (
    <>
      <Tooltip title="Please click here for information">
        <QuestionCircleOutlined
          style={{ marginLeft: "5px", fontSize: "10px" }}
          onClick={showDrawer}
        />
      </Tooltip>
      <div onClick={e => e.stopPropagation()}>
      <Drawer
        title={clickedActiveDatabaseData.name + " Configuration"}
        placement="right"
        onClose={onClose}
        visible={visible}
        width={"60%"}
      >
        {databaseDialect === "flatfile" &&
          !isAwsOrAzureOrCloudFareOrGCS() &&
          !isGoogleSpreadsheets() && (
            <FlatfileHowToUse databaseDialect={databaseDialect} name={name} />
          )}
        {databaseDialect === "flatfile" && isAwsOrAzureOrCloudFareOrGCS() && (
          <RemainingData databaseDialect={databaseDialect} name={name} />
        )}
        {databaseDialect === "flatfile" && isGoogleSpreadsheets() && (
          <GoogleSpreadsheetData databaseDialect={databaseDialect} name={name} />
        )}

        {databaseDialect === "hihttp" && confType === "config" && (
          <ConfigEditorData databaseDialect={databaseDialect} name={name} />
        )}
        {databaseDialect === "hihttp" && confType === "groovy" && (
          <GroovyEditorData databaseDialect={databaseDialect} name={name} />
        )}
        {databaseDialect === "flatfile" &&
          name !== "Flatfile" &&
          !isAwsOrAzureOrCloudFareOrGCS() &&
          !isGoogleSpreadsheets() && (
            <ConfigurationTable type={databaseDialect} name={name} />
          )}
        {databaseDialect === "flatfile" &&
          name === "Flatfile" &&
          [
            "Flatfile csv",
            "Flatfile excel",
            "Flatfile json",
            "Flatfile parquet",
          ].map((name) => (
            <ConfigurationTable type={databaseDialect} name={name} />
          ))}

           {databaseDialect === "flatfile" &&
           name === "Flatfile" &&
          <RemainingData databaseDialect={databaseDialect} name={name} />}

          {databaseDialect === "flatfile" &&
           name === "Flatfile" &&
           <GoogleSpreadsheetData databaseDialect={databaseDialect} name={name} />}

          {databaseDialect === "flatfile" &&
          (name === "Flatfile json" || name === "Flatfile csv" || name === "Flatfile") &&
          <DateFormatData />}
      </Drawer>
      </div>
    </>
  );
};

export default TooltipDrawer;
