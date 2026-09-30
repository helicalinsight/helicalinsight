# MongoDB Driver Support

## Overview

This assessment adds native MongoDB JDBC connectivity to Helical Insight through the driver class `com.helical.mongodb.MongoJdbcDriver`.

The implementation bridges Helical Insight's existing JDBC datasource infrastructure to the MongoDB Java driver already used by the project. The adapter implements `java.sql.Driver`, registers itself with `DriverManager`, accepts MongoDB connection URLs, creates MongoDB client connections, and exposes a JDBC `Connection`/`Statement`/`ResultSet` surface for read-oriented reporting and metadata flows.

The existing datasource configuration already contains MongoDB-related driver mappings. The new JDBC adapter makes the configured `com.helical.mongodb.MongoJdbcDriver` class available to the normal JDBC connection path rather than requiring a separate third-party MongoDB JDBC jar.

## Changes Made

### 1. Native MongoDB JDBC driver

Added:

```text
server/core/src/main/java/com/helical/mongodb/MongoJdbcDriver.java
```

The driver:

- Implements `java.sql.Driver`.
- Registers itself with `DriverManager` during class initialization.
- Accepts `jdbc:mongodb:`, `mongodb://`, and `mongodb+srv://` URLs.
- Uses the MongoDB Java driver for the actual MongoDB connection.
- Supports username/password authentication.
- Creates JDBC connection and statement proxies required by the existing datasource layer.
- Supports read-oriented `SELECT <fields> FROM <collection> [LIMIT n]` queries.
- Exposes basic JDBC result-set and metadata operations used by reporting/metadata flows.
- Rejects write operations through `SQLFeatureNotSupportedException` because this adapter is intended for BI/reporting reads.

### 2. Existing Helical Insight JDBC configuration

The driver is already registered in the datasource configuration as:

```text
com.helical.mongodb.MongoJdbcDriver
```

The default MongoDB URL template is:

```text
mongodb://{{hostName}}:{{port}}/{{database}}
```

with the default MongoDB port:

```text
27017
```

The driver is also mapped to the existing MongoDB SQL/function configuration so it participates in the same datasource/SQL configuration mechanism as other supported drivers.

### 3. Existing MongoDB dependency

The server already defines the MongoDB Java driver dependency and version in `server/pom.xml`:

```text
org.mongodb:mongo-java-driver:3.12.10
```

No separate third-party JDBC MongoDB jar is required for this implementation.

## Configuration

### MongoDB server

A MongoDB server must be reachable from the Helical Insight backend.

Example local MongoDB connection:

```text
Host: localhost
Port: 27017
Database: analytics
Username: <username>
Password: <password>
```

For MongoDB without authentication, leave the username and password empty where the datasource UI permits it.

### Helical Insight datasource

Create a datasource from the Helical Insight datasource administration screen and select the MongoDB JDBC driver:

```text
Driver: com.helical.mongodb.MongoJdbcDriver
Host: localhost
Port: 27017
Database: analytics
Username: <username>
Password: <password>
```

The resulting connection URL follows the existing project template:

```text
mongodb://localhost:27017/analytics
```

For an authenticated connection, credentials can be supplied through the normal Helical Insight datasource username/password fields. The JDBC adapter passes those credentials to the MongoDB Java driver.

## Using MongoDB

1. Start the MongoDB server.
2. Start Helical Insight.
3. Open the datasource/connection administration screen.
4. Create a new database connection.
5. Select `com.helical.mongodb.MongoJdbcDriver`.
6. Enter the MongoDB host, port, database, username, and password.
7. Test the connection.
8. Save the datasource.
9. Use the datasource from the metadata/reporting workflow.

For a collection named `customers`, a supported read query has the following form:

```sql
SELECT * FROM customers
```

A projection and limit can also be used:

```sql
SELECT name,email FROM customers LIMIT 100
```

The adapter translates the supported SELECT operation into a MongoDB collection read.

## Connection Flow

The connection follows Helical Insight's existing JDBC architecture:

```text
Datasource configuration
        |
        v
com.helical.mongodb.MongoJdbcDriver
        |
        v
Helical Insight JDBC connection provider
        |
        v
java.sql.DriverManager
        |
        v
MongoDB Java Driver 3.12.10
        |
        v
MongoDB server
```

The existing connection provider can dynamically load/register a configured JDBC driver when it is not already registered. This allows the new driver to participate in the same driver-loading mechanism used by the application.

## Supported Scope

The current adapter is intentionally read-oriented for BI/reporting use cases.

Supported:

- MongoDB connection creation.
- Username/password authentication.
- MongoDB database selection from the connection URL.
- JDBC connection lifecycle operations.
- Basic JDBC metadata.
- `SELECT * FROM collection`.
- Field projections such as `SELECT name,email FROM collection`.
- `LIMIT` for result-size control.
- Basic JDBC result-set getters.

Not currently intended as a full SQL-to-MongoDB translation engine. Complex SQL joins, updates, inserts, deletes, transactions, and arbitrary SQL expressions are outside the adapter's supported scope.

## Build and Verification

Build the backend from the repository root with:

```bash
cd server
mvn clean package -DskipTests
```

Run the backend tests with:

```bash
cd server
mvn test
```

A live MongoDB instance is required to perform an end-to-end connection test. The implementation should be tested against the MongoDB version/environment used for deployment before production use.

## Files Relevant to the Integration

```text
server/core/src/main/java/com/helical/mongodb/MongoJdbcDriver.java
server/pom.xml
server/hi-repository/System/Admin/databaseDrivers.properties
server/hi-repository/System/Admin/driverDefaultQuery.properties
server/hi-repository/System/Admin/sqlDialects.properties
server/hi-repository/System/Admin/sqlFunctionsXmlMapping.properties
```
