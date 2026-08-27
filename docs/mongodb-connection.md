# MongoDB connection support

Helical Insight supports MongoDB through its existing NoSQL datasource and Drill/middleware integration. The MongoDB Java driver is already part of the server build; this implementation uses that dependency instead of introducing a second JDBC driver.

## Configure a MongoDB datasource

1. Start MongoDB and make sure the Helical Insight server can reach it.
2. Build and deploy Helical Insight normally.
3. In the datasource/connection UI, select **Mongodb** under **No SQL & Big Data**.
4. Use a connection URL such as:

   `mongodb://localhost:27017/mydatabase`

5. Provide the database name and, when authentication is enabled, the MongoDB username and password.
6. Test the connection. Helical Insight creates the MongoDB middleware storage through its existing Drill storage endpoint.

The datasource definition is registered as `com.helicalinsight.nosql.mongo` and uses the existing NoSQL loader path, so other datasource implementations are not changed.

## Authentication

MongoDB connection options can be supplied through the MongoDB URI. For example:

`mongodb://username:password@localhost:27017/mydatabase?authSource=admin`

For deployments using MongoDB's DNS SRV format, the connection test accepts a `mongodb+srv://...` URI. Middleware loading uses the host/port form required by the existing Drill Mongo storage integration.

## Build

From the `server` directory run the normal Maven build used by the project. The MongoDB driver version is inherited from the existing parent POM configuration.

## Implementation

`MongoDrillLoader` is the existing Helical Insight MongoDB integration. The implementation validates the MongoDB URI with `MongoClientURI`, performs a real `ping` against the selected database during connection testing, closes the client after testing, and reports invalid/missing connection information through Helical Insight's service exception mechanism.
