# MongoDB Connectivity

Helical Insight exposes MongoDB through its NoSQL data-source integration. The MongoDB entry uses Apache Drill's Mongo storage plugin for query and metadata access; it is not a standard `java.sql` MongoDB driver. The Mongo Java driver dependency is managed by the server parent Maven project.

## Prerequisites

- MongoDB must be reachable from the Helical Insight server.
- Apache Drill must be installed and enabled in the Helical Insight Drill configuration. The MongoDB entry is added to the data-source list when Drill data sources are enabled.
- Configure the Drill service and its storage directory as described in the existing Drill setup for this installation.

## Configure a Connection

1. Open **Administration > Data Sources** and create a connection in **No SQL & Big Data** named **Mongodb**.
2. Enter a MongoDB connection URI. For a local or self-hosted instance, use `mongodb://localhost:27017/analytics`. For an Atlas or other DNS SRV deployment, use `mongodb+srv://cluster.example.mongodb.net/analytics?retryWrites=true&w=majority`.
3. Enter the MongoDB username and password in the connection form when they are not embedded in the URI. The URI can also carry MongoDB options such as `authSource`, replica-set settings, and TLS parameters.
4. Set the collection when the form requests it, test the connection, and save.
5. Select the saved MongoDB connection when creating or editing metadata and reports.

The connection test executes a MongoDB `ping` against the selected database, so invalid credentials or an unreachable server are reported as a failed connection. Credentials supplied separately from the URI are URL-encoded before being added to the URI.

For a local development build, compile the server from `server/` with JDK 25 and Maven:

```powershell
mvn -pl adhoc -am -DskipTests compile
```