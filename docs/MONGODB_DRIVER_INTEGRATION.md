# MongoDB Driver Integration Guide - Helical Insight

## Overview
This document outlines the implementation of MongoDB database driver support in the Helical Insight open-source platform. The integration adheres strictly to the existing Helical Insight architecture, allowing MongoDB to be discovered, configured, tested, and utilized as a first-class data source alongside standard RDBMS and Big Data sources.

---

## 1. Summary of Changes Made

### A. Frontend (React UI)
- **`client/src/components/common/custom-icons/CustomIcon.jsx`**:
  - Added matching cases for `"Mongodb"`, `"MongoDB"`, and `"MongoDb"` to render the official MongoDB SVG icon (`HelicalMongodbIcon`).
  - Ensures consistent visual branding on database selection cards.

### B. Server Repository & Configuration (`server/hi-repository/System/Admin/`)
- **`Static/DataSourcesList.groovy`**:
  - Added `"Mongodb"` to `supportedArray` so it is prominently displayed in the **Supported** and **All** datasource galleries even before driver installation.
  - Updated driver detection logic to dynamically assign `"Mongodb"` to the `"No SQL & Big Data"` category (`nosql_bigdata`) when detected.
  - Enhanced `prepareDbName(driverName)` to automatically map any MongoDB driver class (`com.helical.mongodb.MongoJdbcDriver`, `mongodb.jdbc.MongoDriver`, `com.mongodb.jdbc.MongoDriver`, etc.) to `"Mongodb"`.
  - Removed outdated Drill dependency for standalone MongoDB JDBC operation.

- **`databaseDrivers.properties`**:
  - Configured default connection templates and port mapping for MongoDB:
    ```properties
    mongoDb=mongodb://{{hostName}}:{{port}}/{{database}},27017
    mongodb.jdbc.MongoDriver=mongodb://{{hostName}}:{{port}}/{{database}},27017
    com.helical.mongodb.MongoJdbcDriver=mongodb://{{hostName}}:{{port}}/{{database}},27017
    com.mongodb.jdbc.MongoDriver=mongodb://{{hostName}}:{{port}}/{{database}},27017
    ```
  - Added support for MongoDB Atlas (`mongodb+srv://`) multi-URL templates:
    ```properties
    type.srv.com.helical.mongodb.MongoJdbcDriver=mongodb+srv://{{hostName}}/{{database}},27017
    type.srv.mongodb.jdbc.MongoDriver=mongodb+srv://{{hostName}}/{{database}},27017
    type.srv.com.mongodb.jdbc.MongoDriver=mongodb+srv://{{hostName}}/{{database}},27017
    ```

- **`driverDefaultQuery.properties`**:
  - Configured default validation queries for testing connection availability (`SELECT 1`).

- **`sqlDialects.properties`**:
  - Mapped MongoDB driver classes to SQL dialect handling (`MySQLDialect` / `PostgreSQLDialect`).

- **`sqlFunctionsXmlMapping.properties`**:
  - Mapped MongoDB driver classes to Helical Insight's built-in `himongo` / `mongo` function XML definitions (`SqlFunctions/himongo.xml`).

- **`Static/layout/configuration/databaseDrivers.ui.layout.json`**:
  - Added MongoDB configuration field to the Admin Settings layout UI.

---

## 2. Steps to Configure and Use MongoDB Connection

### Step 1: Add the MongoDB JDBC Driver JAR
Helical Insight uses JDBC drivers to query data sources and generate metadata:
1. Download a MongoDB JDBC driver (such as the UnityJDBC MongoDB driver `mongodb_unityjdbc.jar`, CData MongoDB JDBC driver, or MongoDB BI Connector driver).
2. Either:
   - **Via UI**: In the Helical Insight web interface, navigate to **Data Sources** → Click the **MongoDB** card (which shows a `+` icon when no driver is installed) → Click **Upload driver (.jar/.zip)**.
   - **Via Filesystem**: Place the `.jar` file directly into the server drivers directory:
     ```
     ../hi-repository/System/Drivers/
     ```
3. Restart or reload the server drivers. The MongoDB card in the UI will now display a **green checkmark**.

### Step 2: Create the MongoDB Data Source Connection
1. In Helical Insight, navigate to **Data Sources** from the main menu.
2. Select the **MongoDB** database card under **All** or **No SQL & Big Data**.
3. In the connection drawer:
   - **Datasource Name**: Enter a name for the connection (e.g., `Mongo_Production`).
   - **Host**: Enter the MongoDB host (e.g., `localhost` or remote host).
   - **Port**: Defaults to `27017`.
   - **Database**: Enter the target MongoDB database name.
   - **User / Password**: Enter authentication credentials (if enabled).
   - **URL Type**: Choose `default` (`mongodb://...`) or `srv` (`mongodb+srv://...` for MongoDB Atlas).
4. Click **Test Connection** to verify database connectivity.
5. Click **Save** to store the datasource connection.

### Step 3: Create Metadata and Reports
Once saved, click **Create Metadata** to map collections and fields, and begin building ad-hoc reports and dashboards.

---

## 3. Backward Compatibility
All existing database connectivity (MySQL, PostgreSQL, Oracle, SQL Server, Trino, Flat files, etc.) remains completely untouched and functional.
