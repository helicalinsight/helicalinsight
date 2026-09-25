# MongoDB Connection

Helical Insight includes the MongoDB JDBC driver (`org.mongodb:mongodb-jdbc:2.0.2`) and exposes it through the normal JDBC datasource flow.

## Configure MongoDB

1. Prepare a MongoDB Atlas SQL endpoint for the target deployment. The JDBC driver connects to the Atlas SQL service, not directly to a standalone local `mongod` instance.
2. Build or start the server with the updated `server/pom.xml` so the JDBC driver is on the application classpath.
3. In the datasource screen, choose `com.mongodb.jdbc.MongoDriver`.
4. Use a JDBC URL such as `jdbc:mongodb://localhost:27017/my_database`. Set the Atlas SQL host, port, database, username, and password for the deployment.
5. Test and save the connection. MongoDB then appears alongside the other JDBC datasources for metadata discovery and SQL-backed reports.

The default driver entry is maintained in `server/hi-repository/System/Admin/databaseDrivers.properties`. Replace the host, port, and database in the connection form with the values from the Atlas SQL endpoint.

## Notes

- The JDBC driver supports the SQL surface exposed by Atlas SQL; MongoDB-specific aggregation syntax is not a JDBC SQL query.
- Existing native MongoDB/Drill datasource support remains available. The new JDBC driver is an additional connection option and does not change existing datasource types.