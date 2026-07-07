	import net.sf.json.JSONObject;
	import groovy.json.*;

	def responseJson = [:];

	responseJson.query = [];
	JSONObject request = JSONObject.fromObject(requestJson);
	def dbName = request.databaseName ? request.databaseName : 'default';
	responseJson.query += "create database if not exists $dbName";
	responseJson.query += "use $dbName";

	request.tables.each {
	 it ->
	 
	 if(request.subType != "mongoDb"){
		responseJson.query += "CREATE TABLE IF NOT EXISTS ${dbName}.$it	USING org.apache.spark.sql.jdbc 	OPTIONS ( 	url \"$request.url\", 	dbtable \"$it\", 	user \'$request.username\', 	password \'$request.password\' 	)";
	  }
	if(request.subType == "mongoDb"){
			
		responseJson.query += "CREATE TABLE IF NOT EXISTS ${dbName}.${dbName}_${request.collection} USING com.mongodb.spark.sql OPTIONS (db '$dbName', collection '$request.collection')";
	}	  
	 if (request.formData.cache) {
	  responseJson.query += "cache TABLE ${dbName}.$it"
	 }else{
		responseJson.query += "uncache TABLE ${dbName}.$it"
	 }

	}
	responseJson.query += "use $dbName";
	responseJson.result = true;
	return new JsonBuilder(responseJson);