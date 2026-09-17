# MongoDB database connectivity

Helical Insight can use MongoDB through the MongoDB Java driver for MongoDB-specific connectivity and metadata discovery.

## Connection values

Configure a MongoDB connection with these values:

- **URI:** `mongodb://localhost:27017`
- **Database:** the MongoDB database name, for example `analytics`
- **Collection:** the collection used for metadata/data discovery, for example `customers`
- **Username/password:** include credentials in the URI when authentication is enabled, for example `mongodb://user:password@localhost:27017/analytics?authSource=admin`

For MongoDB Atlas use the SRV URI supplied by Atlas, for example:

`mongodb+srv://user:password@cluster.example.mongodb.net/analytics`

## Programmatic connectivity

`com.helicalinsight.datasource.nosql.MongoDbLoader` provides two small operations used by integration code:

- `testConnection(uri, database, collection)` verifies that the server, database and collection are reachable.
- `sample(uri, database, collection, limit)` reads a bounded sample of documents for metadata/discovery.

The loader closes the MongoDB client after every operation, so connections are not leaked.

## Notes

Do not commit production credentials to XML files, source code, or Git. Use the application's existing secure configuration mechanism/environment-specific repository settings for credentials.

Existing relational JDBC connections remain unchanged; MongoDB-specific access is isolated in the NoSQL loader.
