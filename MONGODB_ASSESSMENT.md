# Helical Insight — MongoDB Driver Integration

## 1. Overview

This document describes the MongoDB data-source integration in this Helical Insight repository for the Helical IT Solutions Round 2 assessment. MongoDB is exposed through the existing NoSQL data-source workflow. The implementation uses the MongoDB Java driver to test connectivity and Apache Drill's Mongo storage plugin to make a saved source available to reporting and metadata workflows.

This is an external data-source integration. It does not migrate Helical Insight's internal application storage from Derby or PostgreSQL to MongoDB.

## 2. Objective

The assessment asks for MongoDB connectivity that can be configured in Helical Insight while preserving existing database support. This implementation updates the MongoDB loader already present in the repository and uses the existing NoSQL workflow; it leaves the application's existing relational database mechanisms in place.

## 3. Implementation

The repository already contains MongoDB in its NoSQL data-source list and has the Mongo Java driver dependency. The form supplies the connection URI and credentials to the existing global data-source APIs. The option is exposed when the Apache Drill configuration is enabled.

On the server, `MongoDrillLoader` implements the existing `NoSQLLoader` extension point. Its connection test creates a Mongo client, selects the configured database, and runs the MongoDB `ping` command. When a source is saved, the loader sends a Mongo storage-plugin configuration to the configured Drill REST endpoint at `/storage/{name}_{id}.json`.

The implementation uses `org.mongodb:mongo-java-driver` version `3.12.10` (the synchronous legacy Java driver API: `MongoClient` and `MongoClientURI`). It does not use Mongoose or Spring Data MongoDB.

MongoDB is a document database. This integration connects to an existing database and its collections; it does not create a relational schema or migrate Helical Insight's own tables to MongoDB.

## 4. Changes Made

- Updated the existing MongoDB NoSQL loader to support `mongodb://` and `mongodb+srv://` URIs, separate credentials, credential URL encoding, and a MongoDB `ping` connection check.
- Kept source registration on the existing Drill storage-plugin and NoSQL data-source paths.
- Updated local development paths in `setting.xml` and the sample Derby connection URL in `globalConnections.xml` for the current Windows workspace. These paths are environment-specific and need adjustment for another machine.

The MongoDB driver dependency (`org.mongodb:mongo-java-driver:3.12.10`) and MongoDB option in the data-source list were already present in the repository; they were not added in this change. Existing SQL drivers and application database configurations remain available.

## 5. Files and Modules

| File / module | Description |
| --- | --- |
| `server/adhoc/src/main/java/com/helicalinsight/adhoc/services/MongoDrillLoader.java` | Updated the existing loader's URI and credential handling, connectivity ping, and Drill storage-plugin configuration. |
| `server/hi-repository/System/Admin/setting.xml` | Changed the repository path and application base URL to local Windows workspace values. Update these for another environment. |
| `server/hi-repository/System/Admin/globalConnections.xml` | Changed the sample Derby URL to the current local Windows path. This remains an internal/sample relational connection, not the MongoDB source. |

The following existing files/configuration are used by the implementation but were not changed for this update: `server/core/src/main/java/com/helicalinsight/efw/components/GlobalConnectionsTester.java`, `server/hi-repository/System/Admin/Static/DataSourcesList.groovy`, `server/hi-repository/System/Admin/databaseDrivers.properties`, and the Mongo driver dependency in `server/pom.xml`.

## 6. MongoDB Configuration

The data-source form accepts these relevant values:

| Field | Purpose |
| --- | --- |
| `jdbcUrl` | MongoDB URI. Supports `mongodb://` and `mongodb+srv://`. |
| `userName`, `password` | Optional MongoDB credentials entered separately from the URI. If either is supplied, both are required. Credentials already embedded in the URI are rejected by the loader. |
| `database` | Database to ping; `databaseName` is also accepted as a fallback by the connection test. The URI database is used if neither form value supplies one. |
| `hostName`, `port` | Used to form a `mongodb://` URI when `jdbcUrl` is empty; default UI values are `localhost` and `27017`. |
| `collection` | Collection field retained by the existing data-source configuration workflow. |

Example Atlas URI (use placeholders only):

```text
mongodb+srv://<cluster-host>/<database>?retryWrites=true&w=majority
```

Enter the username and password in the separate fields. Add any required URI options, such as `authSource=admin`, to the URI. Characters requiring URL encoding in credentials are encoded by the server when credentials are added to the URI.

The Mongo Java driver dependency is declared in the existing server Maven build, and the resulting server artifact includes the dependency. No separate Mongoose package or JDBC MongoDB driver is used by this NoSQL loader.

## 7. Atlas Setup

1. Create or select an Atlas project and create a MongoDB deployment.
2. Create a database user with the minimum permissions needed to read the target database and collections.
3. Add the machine running Helical Insight to the Atlas network access list. For a hosted Helical Insight deployment, allow the server's egress IP rather than a developer workstation's IP.
4. Copy the deployment's `mongodb+srv://` connection string and replace its database placeholder. Do not include the username or password in the URI for this form; enter them in their separate fields.
5. Ensure Apache Drill is enabled and its Mongo storage plugin is available to the Helical Insight server. Saved sources are registered through Drill's REST storage endpoint.
6. Configure the source in Helical Insight as described below and use **Test Connection**.

## 8. Configure and Use MongoDB

1. Sign in to Helical Insight with an account that can manage data sources.
2. Open the application's data-source management screen and create a global data source.
3. In the No SQL & Big Data category, choose **Mongodb**. This option is exposed when Drill is enabled.
4. Enter a connection name, the Atlas URI in the URL/`jdbcUrl` field, the database name, and the Atlas database username and password in their separate fields. Keep credentials out of the URI.
5. Select **Test Connection**. A successful test is a MongoDB `ping` response. A failed ping is returned as a data-source connection failure.
6. Save the source. The server registers its Mongo storage configuration with Drill using the source name and generated ID.
7. Use the saved source in the application's reporting or metadata workflow supported by the configured Drill Mongo plugin.

Atlas connectivity requires outbound network access from the Helical Insight server and a matching Atlas IP allow-list entry. Saving a source also requires the configured Drill REST service to be reachable.

## 9. Build and Run

From PowerShell, build the server:

```powershell
cd C:\HelicalIt\helicalinsight\server
mvn clean package -DskipTests
```

The WAR is produced at `presentation\target\hi-ee-7.0.0.war`. Deploy it to a locally installed Tomcat by setting `CATALINA_HOME` to the real Tomcat directory (do not use the example placeholder path):

```powershell
$env:CATALINA_HOME = "C:\path\to\your\apache-tomcat"
Copy-Item ".\presentation\target\hi-ee-7.0.0.war" `
  "$env:CATALINA_HOME\webapps\hi-ee.war" -Force
& "$env:CATALINA_HOME\bin\startup.bat"
```

The current development configuration points at `C:\HelicalIt\helicalinsight\server\hi-repository` and a local Derby database path. Change those local paths in `server/hi-repository/System/Admin/setting.xml` and `globalConnections.xml` before running the project from a different location. The sample Docker profile expects its configured PostgreSQL service; it is separate from the MongoDB reporting data source.

To start the client in a separate PowerShell window:

```powershell
cd C:\HelicalIt\helicalinsight\client
npm start
```

The development client expects the backend on `http://localhost:8080` through its configured proxy.

## 10. Testing and Verification

Checks actually performed for this workspace:

- `mvn clean package -DskipTests` completed and produced the server WAR. Tests were skipped by this command.
- The WAR started in local development with the Derby profile; the local backend was not started against PostgreSQL.
- `http://localhost:8080/hi-ee/applicationSettings` returned HTTP 200.
- A JSON request to the client development server at `http://localhost:3000/hi-ee/applicationSettings` returned HTTP 200, verifying the client proxy reached the backend.
- The client compiled with warnings; this was not a MongoDB integration test.

MongoDB Atlas connectivity, the MongoDB ping against Atlas, saving a source to a live Drill service, report queries against Mongo collections, invalid-credential behavior, and database regression tests were not verified in this run. A reviewer should perform those checks using an Atlas deployment and reachable Drill Mongo plugin before treating end-to-end MongoDB access as validated.

## 11. Existing Functionality

MongoDB follows the existing NoSQL loader and global data-source paths. The integration does not replace the existing JDBC drivers or internal Derby/PostgreSQL storage. The local `setting.xml` and `globalConnections.xml` edits are path adjustments for this workspace and are not MongoDB configuration.

## 12. Security

- Do not commit Atlas usernames, passwords, full credential-bearing URIs, or other secrets to GitHub.
- Use a least-privilege Atlas database user and restrict Atlas network access to the application server's egress IP.
- Enter credentials in the application's separate credential fields. Protect access to saved data-source configuration and the Drill storage configuration according to the deployment's security controls.
- Replace local absolute development paths before building or deploying from another machine.

## 13. Summary

The repository provides a MongoDB option in Helical Insight's existing NoSQL data-source workflow. The Java loader tests a MongoDB connection with `ping` and registers saved sources with Apache Drill's Mongo storage plugin. The server builds and starts locally, but a live Atlas-to-Drill-to-reporting flow still needs end-to-end verification.
