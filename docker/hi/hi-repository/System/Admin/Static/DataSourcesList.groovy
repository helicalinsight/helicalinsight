import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JdbcUrlFormatUtility;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import groovy.json.*;
import java.util.regex.Pattern;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory
import java.util.Map;

import java.util.stream.Collectors;

def staticDataSources = '''[ {
"name": "Mongodb",
"classifier": "global",
"categoryName": "No SQL & Big Data",
"categoryType": "nosql_bigdata",
"type": "global.jdbc",
"driver": "com.helicalinsight.nosql.mongo",
"url": "mongodb://{{hostName}}:{{port}}/{{database}}",
"parameters": {
"port": "27017",
"hostName": "localhost",
"database": "database",
"collection":"collection",
"sslPort":"3345"
},

"dataSourceProvider": "noSql"
}

]''';


def supportedArray = ["Oracle", "Mysql", "Apache Drill", "Microsoft Sqlserver",
                      "Postgresql", "IBM Db2", "Access", "Sqlite", "Teradata", "Mariadb", "Hive", "Informix", "Presto", "Derby", "Dremio",
                      "Snowflake","Elasticsearch","Trino","Google Bigquery","Amazon Dynamodb","Amazon Redshift","Celerdata","Yugabyte",
                      "Duckdb","Sap Db","Firebirdsql","API","Flatfile","Flatfile csv","Flatfile excel","Flatfile json","Flatfile aws","Flatfile Google spreadsheet","Flatfile parquet","Flatfile azure blobstorage" ,"Flatfile cloudfare r2","Flatfile GCS","Flatfile tsv","Athena","Ξ Add Driver Ξ"]

def virtualStaticDs = '''{
"name": "Virtual Datasource",
"classifier": "efwd",
"categoryName": "No SQL & Big Data",
"categoryType": "nosql_bigdata",
"type": "sql.calcite",
"driver": "org.apache.calcite.jdbc.Driver"

}''';


def supportedDs = [];

supportedArray.each {
    def itemMap = [:]
    itemMap['name'] = it
    itemMap['categoryType'] = "supported"
    itemMap['categoryName'] = "Supported"
    supportedDs.push(itemMap)
}



JSONArray jsonArray = JSONArray.fromObject(supportedDs);

def jsonSlurper = new JsonSlurper()
def staticMongoArray = jsonSlurper.parseText(staticDataSources)
def staticArray=[];
def virtualDsFlagObject = jsonSlurper.parseText(virtualStaticDs)
boolean virtualDsFlag = JsonUtils.getSettingsJson().getBoolean("hideVirtualDatasourceInAllCategory");
if (!virtualDsFlag) {
    staticArray[0] += virtualDsFlagObject
}

JSONObject dataSourcesList = SettingXmlUtility.getFilteredDataSourcesJson();

PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
Map<String, String> allProps = propertiesFileReader.read("Admin", "databaseDrivers.properties");
Map<String, String> filtered =
        allProps.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith("type"))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

JSONObject jsonOfDrivers = JdbcUrlFormatUtility.getJsonOfDrivers();
//return jsonOfDrivers.toString();
//return new JsonBuilder(jsonOfDrivers)
Map<String, String> dialectsInfo = JdbcUrlFormatUtility.getDialectInformation();


//JSONObject jsobj = JsonUtils.getXmlAsJson(JsonUtils.getDrillConfigPath()).getJSONObject("drill");
final IProcessor processor = ResourceProcessorFactory.getIProcessor();
JSONObject jsobj = processor.getJSONObject(JsonUtils.getDrillConfigPath(), false);

Boolean drillEnabledTypes = jsobj.getBoolean("enabled");
JSONObject jsonOfDrillDatasources = getFormatedDrillDatasources(jsobj)


def resultJSON = [:]
resultJSON."driversList" = []
if ((jsonOfDrillDatasources != null) && drillEnabledTypes) {
    resultJSON."driversList" += jsonOfDrillDatasources.optJSONArray("drillDatasources")
    resultJSON.driversList += ["driver": "com.helicalinsight.nosql.mongo", "available": "true", "url": "mongodb://{{hostName}}:{{port}}/{{database}}", "parameters": [
            "port"      : "27017",
            "hostName"  : "localhost",
            "database"  : "database",
            "collection": "collection",
            "sslPort"   : "3345"
    ]];

    staticArray+=staticMongoArray;
}
resultJSON.driversList += ["driver": "dynamicSwitch","showInDatasource":"false", "available": "true", "parameters": [

]];
resultJSON."driversList" += jsonOfDrivers.optJSONArray("drivers")

//Todo add the drivers which are not present in the driverlist but present in the static array



resultJSON."dataSourceTypes" = dataSourcesList.getJSONArray("dataSources").each {
    it.categoryName = "advanced"
    it.categoryType = "advanced"
    return it
}

//Supported Drivers
//resultJSON."supportedDrivers" = supportedDriversArray


def driverListArray = resultJSON."driversList";
JSONArray clonedSupportedFilesObject = JSONArray.fromObject(jsonArray);
def notThere = []
List<Integer> indexes = new ArrayList<Integer>();
driverListArray.each {
    def foundOne = staticArray.findAll {
        item -> item.driver == it.driver
    }
    def modelJson = [:]
    if (foundOne.size() == 0) {

        modelJson.driver = it.driver
        modelJson.databaseDialect = dialectsInfo.get(it.driver) != null ? dialectsInfo.get(it.driver) : "";
        if ("org.apache.drill.jdbc.Driver".equals(it.driver)) {
            modelJson.enabledTypes = drillEnabledTypes;
            it.parameters.hostName="drillbit=localhost"
        }
        if ("com.dremio.jdbc.Driver".equals(it.driver)) {
            it.parameters.hostName="direct=localhost"
        }

        def findDbName = prepareDbName(it.driver)

        modelJson.name = findDbName
        if(it.driver=="com.helical.HttpDriver") {
            modelJson.name=findDbName="API"
        }
        if(it.driver=="com.helical.FlatFileDriver") {
            modelJson.name=findDbName="Flatfile"
        }


        for (int i = 0; i < clonedSupportedFilesObject.size(); i++) {
            JSONObject object = clonedSupportedFilesObject.getJSONObject(i);
            if (object.containsValue(findDbName) || object.containsValue("Mongodb")) {

                clonedSupportedFilesObject.remove(i);


            }
        }

        String middleWareName = JsonUtils.getHiMiddleWareName();
        def modifiedMiddlewareName = prepareDbName(middleWareName)
        if (findDbName.equals("Hive") || findDbName.equals("Apache Drill")) {
            modelJson.categoryName = "Big Data"
            modelJson.categoryType = "big_data"

        } else if (Pattern.compile(Pattern.quote(modifiedMiddlewareName), Pattern.CASE_INSENSITIVE).matcher(findDbName).find()) {
            modelJson.categoryName = "Flat Files"
            modelJson.categoryType = "flat_files"
            modelJson.type = "global.jdbc"
            modelJson.dataSourceProvider = "tomcat"
            modelJson.fileUpload = true
            String replacedValue = findDbName.replace(modifiedMiddlewareName.substring(0, 1).toUpperCase() + modifiedMiddlewareName.substring(1), "").trim();
            modelJson.name = replacedValue.substring(0, 1).toUpperCase() + replacedValue.substring(1)

        } else {
            modelJson.categoryName = "RDBMS"
            modelJson.categoryType = "rdbms"
            modelJson.type = "global.jdbc"
            modelJson.dataSourceProvider = "tomcat";
        }

        modelJson.classifier = "global"

        //modelJson.imgUrl = "../images/data_sources/defaut_datasource.png"

//modelJson.type=modelJson.type!=null?"sql.jdbc":modelJson.type
//modelJson.dataSourceProvider=modelJson.dataSourceProvider!=null?"noSql":modelJson.dataSourceProvider;


        if (it.url) {
            modelJson.url = it.url
        }
        if (it.parameters) {
            modelJson.parameters = it.parameters
        }
        notThere += modelJson
    }
}

staticArray += notThere

resultJSON.dataSources = staticArray



resultJSON.dataSources += clonedSupportedFilesObject

def prepareDbName(driverName) {
    if (driverName.contains("cdata.jdbc.mongodb")) {
        driverName = driverName.replace("cdata.jdbc.mongodb", "cdata.jdbc.mongodb Cdata")
    }
    def driverNameSplit = driverName.split("\\.")
    def possibleName = []
    driverNameSplit.each {
        def lowerIt = it.toLowerCase();
//|| lowerIt=="helicalinsight"
        if (!(lowerIt == "org" || lowerIt == "apache"
                || lowerIt.contains("driver") || lowerIt == "com" || lowerIt == "jdbc"
                || lowerIt == "net" || lowerIt == "sf" || lowerIt == "jtds" || lowerIt == "jcc"
                || lowerIt == "facebook"||lowerIt=="cdata"||lowerIt=="amazondynomodb"||lowerIt=="simba"
                ||lowerIt=="jdbc42" ||lowerIt=="ru"  || lowerIt=="yandex" || lowerIt=="jdbc42" ||lowerIt=="xpack"
                || lowerIt=="sql" || lowerIt=="io" || lowerIt=="client" || lowerIt=="iapi") )
        {
            if (it.contains("ucan")) {
                possibleName += it.replace("ucanaccess", "Access")
            } else if (it.contains("sourceforge")) {
                possibleName += it.replace("sourceforge", "Microsoft Sqlserver(sourceforge)")
            } else if (it.contains("ibm")) {
                possibleName += it.replace("ibm", "IBM")
            } else if (it.contains("drill")) {
                possibleName += it.replace("drill", "Apache Drill")
            } else if (it.contains("amazondynamodb")) {
                possibleName += it.replace("amazondynamodb", "Amazon DynamoDb")
            } else if (it.contains("googlebigquery")) {
                possibleName += it.replace("googlebigquery", "Google Bigquery")
            } else if (it.contains("clickhouse")) {
                possibleName += it.replace("clickhouse", "Clickhouse")
            }else if (it.contains("elasticsearch")) {
                possibleName += it.replace("elasticsearch", "Elasticsearch")
            } else if (it.contains("httpdriver")) {
                possibleName += it.replace("httpdriver", "API")
            } else {
//possibleName += it
                possibleName += it.substring(0, 1).toUpperCase() + it.substring(1)
            }
        }

    }
    return possibleName.join(" ")

}

def multiDatabaseMap=[Mysql:["Celerdata"],Postgresql:["Cockroach"],Flatfile:["Flatfile csv","Flatfile excel","Flatfile json","Flatfile aws","Flatfile Google spreadsheet","Flatfile parquet","Flatfile azure blobstorage" ,"Flatfile cloudfare r2","Flatfile GCS","Flatfile tsv"]];

def toAdd=[]
def toRemove=[]
resultJSON.dataSources = resultJSON.dataSources.toSet()
for(def itemD: resultJSON.dataSources){
    if(multiDatabaseMap.keySet().contains(itemD.name)){
        def supDbs = multiDatabaseMap.get(itemD.name);

        for(String ss: supDbs){
            def copiedMap = JSONObject.fromObject(itemD);
            copiedMap.name=ss;
            copiedMap.sendVendorName=true;
            toRemove.add(ss);
            toAdd.add(copiedMap);
        }
    }
}
toRemove.add("Flatfile");
if(toRemove){
    resultJSON.dataSources=resultJSON.dataSources.findAll { !(toRemove.contains(it.name) && it.categoryType=="supported") }.collect { it }
}


if(toAdd){
    resultJSON.dataSources.addAll(toAdd)
}

resultJSON.dataSources.each { obj ->
    if (obj.url instanceof String) {
        def myList = [ obj.url ]
        def types = [ "default" ]
        mymp =  getAllArrayUrl(obj.driver,filtered);
        if(!mymp.urls.isEmpty()) myList.addAll(mymp.urls)
        if(!mymp.types.isEmpty()) types.addAll(mymp.types)
        obj.labels=types;
        obj.url=myList

    }
}
resultJSON.driversList.each { obj ->
    if (obj.url instanceof String) {
        def myList = [ obj.url ]
        def types = [ "default" ]
        mymp =  getAllArrayUrl(obj.driver,filtered);
        if(!mymp.urls.isEmpty()) myList.addAll(mymp.urls)
        if(!mymp.types.isEmpty()) types.addAll(mymp.types)
        obj.labels=types;
        obj.url=myList
    }
}

return new JsonBuilder(resultJSON)






def Map<String, Object> getAllArrayUrl(String driverName, Map<String,String> filterMap){

    def result = []
    def types = []

    filterMap.each { key, value ->
        // Remove leading "type."
        def stripped = key.replaceFirst(/^type\./, "")   // ex: service.oracle.jdbc.OracleDriver
        def typePrefix = stripped.substring(0, stripped.indexOf('.'))

        // Extract everything after the first dot
        def actualDriver = stripped.substring(stripped.indexOf('.') + 1) // ex: oracle.jdbc.OracleDriver

        // Compare driver name
        if (actualDriver == driverName) {
            result << value .split(",")[0]     // add the URL string into array
            types << typePrefix
        }

    }

    return [
            urls : result,
            types: types
    ]


}





def getFormatedDrillDatasources(JSONObject drillJSONObejct) {


    JSONObject preparedJSONobject = new JSONObject();

    if (drillJSONObejct.has("enabledTypes")) {
        JSONObject enabledTypesJSON = drillJSONObejct.getJSONObject("enabledTypes");

        Set<String> enableTypeKeySet = enabledTypesJSON.keySet();

        List<JSONObject> listValue = new ArrayList<>();
        for (String eachKey : enableTypeKeySet) {

            JSONObject singleDatasource = new JSONObject();
            Object rawObject = enabledTypesJSON.get(eachKey);
            if (!(rawObject instanceof JSONObject)) {
                continue;
            }
            JSONObject configJSONobject = enabledTypesJSON.getJSONObject(eachKey).getJSONObject("config");
            if (!configJSONobject.has("extensions")) {
                configJSONobject.put("@extensions", "." + eachKey);
            }

            singleDatasource.put("driver", JsonUtils.getHiMiddleWareName() + "." + eachKey);
            singleDatasource.put("available", "true");
            singleDatasource.put("fileUpload", true);
            JSONObject parameters = new JSONObject();
            parameters.putAll(configJSONobject);
            singleDatasource.put("parameter", parameters);
            listValue.add(singleDatasource);
        }
        preparedJSONobject.put("drillDatasources", listValue);

    }
    return preparedJSONobject;
}