package com.helicalinsight.datasource.nosql;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;

import org.bson.Document;

import com.google.gson.JsonObject;

public class MongoDbLoader extends NoSQLLoader{

	
	@Override
	public boolean loadToMiddleWare(JsonObject formData) {
	    String host = formData.get("host").getAsString();
	    int port = formData.get("port").getAsInt();
	    String dbName = formData.get("database").getAsString();
	    String collectionName = formData.get("collection").getAsString();
	    
	    String connectionString = "mongodb://" + host + ":" + port;
	    
	    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
	        MongoDatabase database = mongoClient.getDatabase(dbName);
	        MongoCollection<Document> collection = database.getCollection(collectionName);
	        
	        // Iterate over documents and process them into your middleware
	        for (Document doc : collection.find()) {
	            // Add your transformation / middleware loading logic here
	        }
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	@Override
	public boolean testConnection(JsonObject formData) {
	    String host = formData.get("host").getAsString();
	    int port = formData.get("port").getAsInt();
	    
	    // Construct the connection string
	    String connectionString = "mongodb://" + host + ":" + port;
	    
	    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
	        // Ping the database to verify the connection
	        mongoClient.getDatabase("admin").runCommand(new org.bson.Document("ping", 1));
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
    
    
}
