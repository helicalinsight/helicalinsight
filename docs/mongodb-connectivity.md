# MongoDB connectivity

## Existing integration

The backend already declares the MongoDB Java driver (`org.mongodb:mongo-java-driver`, version 3.12.10) in `server/pom.xml`. The existing MongoDB NoSQL loader is `server/adhoc/src/main/java/com/helicalinsight/adhoc/services/MongoDrillLoader.java`.

## Connectivity validation changes

The loader's connection test now:
- accepts a MongoDB connection URI or host/port configuration;
- supports credentials supplied in the URI or separately in the form;
- uses configurable connect and server-selection timeouts;
- requires a database name, from the form or URI;
- runs a `ping` command so the test verifies server selection and connectivity rather than relying on MongoClient's lazy initialization;
- closes the client on both success and failure; and
- reports a safe high-level failure message without putting the connection URI in the message.

## Manual test checklist

Use a disposable MongoDB instance and do not commit credentials.

1. Valid unauthenticated URI, e.g. `mongodb://localhost:27017/testdb` — connection test should pass.
2. Valid authenticated URI with credentials embedded in the URI — connection test should pass.
3. Valid host/port/URI with username and password supplied separately — connection test should pass when the credentials are authorized for the selected database.
4. Invalid credentials — connection test should fail with a connection error.
5. Stopped server or unreachable host — connection test should fail within the configured timeout.
6. Missing database — validation should report that a database is required.
7. Re-run existing SQL datasource connection tests to check for regressions; this change is limited to the MongoDB NoSQL loader.

## Build

From the repository root, run the project's normal Maven build from `server`, for example:

```powershell
cd server
mvn -pl adhoc -am -DskipTests package
```

Then run the relevant project test suite before submitting. A successful compile does not replace the live MongoDB checks above.

## Scope and limitations

This change improves the existing MongoDB NoSQL connection test path. It does not add a new JDBC implementation for MongoDB; the standard MongoDB Java driver is not itself a JDBC driver. Querying MongoDB through a JDBC-only path still requires a compatible JDBC bridge/driver and its packaging.
