# MongoDB Integration in Helical Insight

## 1. Overview
This document describes the native MongoDB database driver and connectivity support implemented in the Helical Insight open-source Business Intelligence application.

The implementation is integrated directly into the existing Helical Insight codebase on the `feature/mongodb-driver` branch. It is **not** a standalone application or external microservice; rather, it extends Helical Insight's existing NoSQL data source architecture while leaving all relational JDBC database providers completely untouched.

### Architecture Components Reused
The integration leverages the following existing core components:
- `NoSQLLoader`: Abstract contract for NoSQL data source testing (`testConnection`) and middleware loading (`loadToMiddleWare`).
- `DSTypeNoSQL`: JPA Entity persisting NoSQL data source connection parameters to the `ds_type_nosql` database table.
- `NoSqlDataSourcePropertiesDB`: Data source manager bean (`noSqlDsManager`) providing CRUD lifecycle management for NoSQL configurations.
- `DataSourceUtils`: Utility routing test requests (`testNosqlDS`) to the appropriate NoSQL provider implementation.
- `GlobalConnectionsTester`: Top-level tester routing requests with `dataSourceProvider: "noSql"` to `DataSourceUtils`.
- `GlobalDatabaseConnectionWriter`: Writer persisting global connections into the repository database.
- `MongoDrillLoader`: Spring prototype bean registered as `@Component("com.helicalinsight.nosql.mongo")` implementing the active connection testing and middleware registration.
- `MongoConnectionFactory`: Factory class resolving MongoDB driver connections for metadata generation.
- `WorkflowMongoTemplate`: Template handling MongoDB metadata discovery for adhoc reporting workflows.

### Key Capabilities Implemented
- **Active Validation:** Replaces passive connection instantiation with an active command (`db.runCommand(new Document("ping", 1))`) to ensure network reachability and authentication validity.
- **Bounded Timeouts:** Enforces connection, socket, and server-selection timeouts (default: `5000 ms`), preventing thread starvation and UI hangs on unreachable hosts.
- **Safe Credential Handling:** Properly handles unauthenticated setups (treating blank strings as unauthenticated), custom auth mechanisms (`SCRAM-SHA-256`, `SCRAM-SHA-1`, `MongoCR`, `Plain`), and never logs credentials.
- **URI & SRV Support:** Supports standard `mongodb://` and DNS seedlist `mongodb+srv://` connection strings with protocol validation.
- **Standalone Operation:** Allows MongoDB data sources to be saved and tested independently of an external Apache Drill cluster.

---

## 2. Prerequisites / Environment Setup

To build and run Helical Insight with MongoDB support, ensure the following tools are available:
- **Java Development Kit (JDK):** OpenJDK 25 (LTS)
- **Build Tool:** Apache Maven 3.9+
- **Container Runtime:** Docker Engine / Docker Desktop (version 20.10+ / 28+)
- **Version Control:** Git
- **Source Repository:** Helical Insight repository (`feature/mongodb-driver` branch)
- **MongoDB Image:** Official `mongo:6.0` image

---

## 3. MongoDB Test Environment (Docker)

A reproducible local MongoDB test environment can be created using Docker.

### 1. Start MongoDB Container
```bash
docker run -d --name helical-mongo-test -p 27017:27017 mongo:6.0
```

### 2. Verify Container Status
```bash
docker ps --filter "name=helical-mongo-test"
```

### 3. Seed Sample Database and Collection
Create the `helical_test` database and populate the `employees` collection with sample records:
```bash
docker exec helical-mongo-test mongosh helical_test --eval "db.employees.insertMany([
  { employeeId: 1, name: 'Alice', department: 'Engineering', salary: 75000 },
  { employeeId: 2, name: 'Bob', department: 'Analytics', salary: 68000 },
  { employeeId: 3, name: 'Charlie', department: 'Engineering', salary: 82000 }
]);"
```

### 4. Verify Sample Data
```bash
docker exec helical-mongo-test mongosh helical_test --eval "db.employees.find();"
```

---

## 4. Application Configuration

### Helical Insight UI Workflow
1. Open and log in to Helical Insight.
2. Navigate to **Data Sources** → **Create**.
3. Select **MongoDB** under **No SQL & Big Data** (or from the **Supported** catalog).
4. Fill in the connection form:
   - **Host Name:** `localhost`
   - **Port:** `27017`
   - **Database Name:** `helical_test`
   - **User Name:** *(leave empty for unauthenticated test setup, or provide username)*
   - **Password:** *(leave empty for unauthenticated test setup, or provide password)*
   - **Collection:** `employees`
   - **URL / URI (Optional):** `mongodb://localhost:27017/helical_test`
5. Click **Test Connection** (returns `"The connection test is successful."`).
6. Click **Save** to persist the data source.

### Supported Configuration Fields
| Field | Description | Default |
|---|---|---|
| `hostName` / `host` | Target MongoDB hostname or IP address | `localhost` |
| `port` | MongoDB server port | `27017` |
| `database` / `databaseName` | Database name to verify and access | `admin` (or user-specified) |
| `collection` | Optional collection name for document verification | *empty* |
| `userName` / `username` | Authentication username (optional) | *empty* |
| `password` | Authentication password (optional) | *empty* |
| `authMechanism` | Auth mechanism (`SCRAM-SHA-256`, `SCRAM-SHA-1`, `MongoCR`, `Plain`) | Driver default |
| `jdbcUrl` / `url` | Connection URI (`mongodb://...` or `mongodb+srv://...`) | Generated from host/port/db |
| `timeOut` | Connection, socket, and server-selection timeout (ms) | `5000` |
| `ssl` | Enable SSL/TLS encryption (`true`/`false`) | `false` |

---

## 5. Architecture and Request Flow

```
┌─────────────────────────────────────────────────────────────┐
│                      React UI (Frontend)                    │
│    Data Sources -> Select MongoDB -> Enter Details -> Test  │
└─────────────────────────────┬───────────────────────────────┘
                              │ POST core/dataSource/test
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   GlobalConnectionsTester                   │
│             (Routes dataSourceProvider: "noSql")            │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     DataSourceUtils                         │
│            (Extracts subType & queries NoSqlUtils)          │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│             MongoDrillLoader (@Component)                   │
│   - Configures MongoClientOptions with bounded timeouts     │
│   - Supports Host/Port or mongodb:// and mongodb+srv:// URIs│
│   - Authenticates via SCRAM-SHA-1 / SCRAM-SHA-256 / MongoCR │
│   - Issues active command: db.runCommand(new Document("ping", 1)) │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MongoDB Server                         │
│             (Responds with { "ok": 1.0 })                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Connection Validation

- **Active Network Verification:** Rather than merely creating a client object, the loader explicitly executes `db.runCommand(new Document("ping", 1))` against the database.
- **Unauthenticated Handling:** Empty strings for username/password are handled cleanly without triggering authentication exceptions.
- **URI Support:** Directly parses `mongodb://` and `mongodb+srv://` URIs, rejecting invalid protocols (such as `invalid://`).
- **Resource Management:** Ensures every `MongoClient` is closed inside `finally` blocks.

---

## 7. Timeout and Error Handling

- **Bounded Timeouts:** Default timeout of `5000 ms` is applied to `connectTimeout`, `socketTimeout`, `serverSelectionTimeout`, and `maxWaitTime`.
- **Graceful Failure:** Intercepts `MongoTimeoutException`, `MongoSecurityException`, `MongoSocketOpenException`, `MongoCommandException`, and generic `MongoException`.
- **Security:** Plaintext passwords and credentials are never written to log files or error responses.

---

## 8. MongoDB Database Name Behavior

- **Lazy Creation Semantics:** In MongoDB, databases and collections are created lazily upon the first document write. Pinging an unused database name succeeds at the server level, confirming valid server connectivity and authentication.
- **Malformed Name Handling:** Database names containing illegal characters (such as null bytes `\0`) are caught and rejected cleanly without crashing the JVM.

---

## 9. Data Access Verification

- **Document Interaction:** Verified via `db.getCollection(collection).estimatedDocumentCount()` against `helical_test.employees` containing sample employee records.
- **Metadata Path:** Extended metadata generation routes through `MongoConnectionFactory` and `WorkflowMongoTemplate` for table/collection schema discovery.

---

## 10. Automated Testing

The automated test suite in [`MongoConnectionTest.java`](file:///server/presentation/src/test/java/com/helicalinsight/datasource/MongoConnectionTest.java) covers 11 comprehensive test cases:

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
```

### Positive Tests
1. `testPositiveMongoConnectionDirectHostPort`: Verifies active ping connectivity against `localhost:27017/helical_test`. *(PASS)*
2. `testPositiveMongoConnectionWithUri`: Verifies URI parsing and active ping using `mongodb://localhost:27017/helical_test`. *(PASS)*
3. `testPositiveMongoConnectionWithEmptyCredentials`: Verifies that empty string credentials pass safely as unauthenticated. *(PASS)*
4. `testLoadToMiddleWareStandalone`: Verifies data source saving without requiring an external Apache Drill cluster. *(PASS)*

### Negative Tests
5. `testNegativeMongoConnectionWithInvalidCredentials`: Verifies that invalid credentials fail authentication gracefully without hanging. *(PASS)*
6. `testNegativeMongoConnectionClosedPort`: Verifies graceful failure when connecting to closed port `59999`. *(PASS)*
7. `testNegativeMongoConnectionUnreachableHostWithBoundedTimeout`: Verifies that non-routable host `192.0.2.1` fails within bounded timeout (<6000ms). *(PASS)*
8. `testNegativeMongoConnectionMalformedUri`: Verifies that invalid URI protocols fail gracefully without throwing uncaught exceptions. *(PASS)*
9. `testMongoConnectionDatabaseLazyHandling`: Validates MongoDB lazy database creation semantics on non-existent database names. *(PASS)*
10. `testNegativeMongoConnectionInvalidDatabaseNameWithIllegalCharacters`: Verifies that illegal database characters (e.g. null bytes) fail gracefully. *(PASS)*

### Regression Test
11. `testExistingDerbyDatabaseConnectionRegression`: Establishes an in-memory Apache Derby JDBC connection, creates table `REGRESSION_CHECK`, inserts and retrieves records, confirming existing relational JDBC functionality is 100% operational. *(PASS)*

---

## 11. Test Execution Commands

### Run Automated Tests
```bash
cd server
mvn test "-Dtest=MongoConnectionTest" "-DfailIfNoSpecifiedTests=false" "-Dsurefire.failIfNoSpecifiedTests=false"
```

### Run Full Maven Package Build
```bash
cd server
mvn clean package -DskipTests
```

---

## 12. Build Verification

- **Java Version:** OpenJDK 25.0.4.1 LTS
- **Maven Version:** Apache Maven 3.9.9
- **Build Result:** `BUILD SUCCESS` across all 11 Maven reactor modules:
  `parent`, `hi-core`, `hi-cache`, `hi-adhoc`, `hi-instant`, `hi-validation`, `hi-export`, `hi-externalauth`, `hi-hwf`, `hi-scheduling`, `hi-ee`.
- **Target Artifact:** `server/presentation/target/hi-ee-7.0.0.war` (~231 MB).

---

## 13. Summary of Changes Made

| File | Type | Purpose |
|---|---|---|
| [`server/adhoc/src/main/java/com/helicalinsight/adhoc/services/MongoDrillLoader.java`](file:///server/adhoc/src/main/java/com/helicalinsight/adhoc/services/MongoDrillLoader.java) | Modified | Active ping verification, bounded timeouts, credential parsing, URI validation, standalone middleware saving. |
| [`server/core/src/main/java/com/helicalinsight/datasource/MongoConnectionFactory.java`](file:///server/core/src/main/java/com/helicalinsight/datasource/MongoConnectionFactory.java) | Modified | Extended driver class recognition for `com.helicalinsight.nosql.mongo`. |
| [`server/hi-repository/System/Admin/Static/DataSourcesList.groovy`](file:///server/hi-repository/System/Admin/Static/DataSourcesList.groovy) | Modified | Registered MongoDB unconditionally in `supportedArray`, `staticArray`, and `driversList`. |
| [`server/presentation/src/test/java/com/helicalinsight/datasource/MongoConnectionTest.java`](file:///server/presentation/src/test/java/com/helicalinsight/datasource/MongoConnectionTest.java) | New | 11 automated positive, negative, and JDBC regression test cases. |
| [`docs/mongodb-integration.md`](file:///docs/mongodb-integration.md) | New | Technical documentation and reproduction guide. |

---

## 14. Security Considerations

- **Zero Plaintext Secrets:** No passwords, API keys, private tokens, or secret `.env` files are committed.
- **Log Sanitation:** Plaintext passwords and credentials are never included in application logs or exception traces.
- **Bounded Resource Limits:** Bounded timeouts prevent socket exhaustion and denial-of-service from unresponsive external hosts.
- **Portability:** All documentation contains environment-neutral instructions with no local machine-specific file paths.

---

## 15. Setup Reproduction Guide for Developers

1. **Clone & Switch Branch:**
   ```bash
   git checkout feature/mongodb-driver
   ```
2. **Build Backend:**
   ```bash
   cd server
   mvn clean package -DskipTests
   ```
3. **Start Test MongoDB Container:**
   ```bash
   docker run -d --name helical-mongo-test -p 27017:27017 mongo:6.0
   ```
4. **Seed Sample Records:**
   ```bash
   docker exec helical-mongo-test mongosh helical_test --eval "db.employees.insertMany([
     { employeeId: 1, name: 'Alice', department: 'Engineering', salary: 75000 },
     { employeeId: 2, name: 'Bob', department: 'Analytics', salary: 68000 },
     { employeeId: 3, name: 'Charlie', department: 'Engineering', salary: 82000 }
   ]);"
   ```
5. **Run Test Suite:**
   ```bash
   mvn test "-Dtest=MongoConnectionTest" "-DfailIfNoSpecifiedTests=false" "-Dsurefire.failIfNoSpecifiedTests=false"
   ```
