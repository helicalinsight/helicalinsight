# MongoDB Connectivity for Helical Insight

This document explains the changes made to add MongoDB as a configurable database
connection in Helical Insight, and how to use it.

## Background

Helical Insight already ships with most of the scaffolding needed for a MongoDB
connection type, but it was incomplete and disabled by default:

- A `DSTypeNoSQL` JPA entity, `NoSqlProperties` JAXB model, and `NoSqlDataSourceProperties`
  write path already exist specifically to persist Mongo-style connections
  (host/port/username/password/database/**collection**).
- An abstract `NoSQLLoader` extension point (`server/core/.../datasource/nosql/NoSQLLoader.java`)
  already exists for "test connection" / "save connection" behaviour per NoSQL sub-type,
  resolved by Spring bean name (the `driverName` submitted by the UI).
- A "Mongodb" tile already existed in
  `server/hi-repository/System/Admin/Static/DataSourcesList.groovy`, complete with a
  "Collection" field and a Mongo icon in the client — but it only appeared in the UI when
  Apache Drill was enabled (`drillConfig.xml` → `<enabled>true</enabled>`), and its backing
  implementation, `MongoDrillLoader` (`server/adhoc/.../services/MongoDrillLoader.java`), is
  `@Deprecated` and requires registering Mongo as an Apache Drill storage plugin over REST.

So MongoDB support existed, but only as an opt-in, deprecated, Drill-dependent feature.

## What was changed

### 1. New file: `server/adhoc/src/main/java/com/helicalinsight/adhoc/services/MongoNativeLoader.java`

A new `NoSQLLoader` implementation, registered as Spring bean
`com.helicalinsight.nosql.mongo.native`, that talks to MongoDB **directly** using the
official MongoDB Java driver (`org.mongodb:mongo-java-driver`, already a project
dependency in `server/pom.xml` — no new Maven dependency was added):

- `testConnection(formData)` opens a real `MongoClient` (with/without credentials, or from
  a full connection URI) and calls `listDatabaseNames().first()` to force a round trip to
  the server, so "Test Connection" in the UI reports a genuine success/failure against a
  live MongoDB instance. A 5-second connect/server-selection timeout is set so a bad
  host/port fails fast instead of hanging on the default driver timeout.
- `loadToMiddleWare(formData)` just validates that a host or connection URL was supplied.
  Unlike the Drill-based loader, it does **not** need to register anything with an external
  service — the connection details are already persisted generically by the existing
  `NoSqlDataSourceProperties` / `DSTypeNoSQL` write path.

This class does not touch or replace `MongoDrillLoader` / `MongoConnectionFactory` — both
are left exactly as they were, so the existing (opt-in) Drill-based Mongo path keeps
working unchanged for anyone already relying on it.

### 2. Modified: `server/hi-repository/System/Admin/Static/DataSourcesList.groovy`

Added a second "Mongodb" datasource tile (same shape/category/icon as the existing one)
whose `driver` points at the new `com.helicalinsight.nosql.mongo.native` bean, and added it
to the datasource list **unconditionally**, instead of only inside the
`if (drillEnabledTypes)` block. This is what makes "Mongodb" show up under
**No SQL & Big Data** in the "New Connection" screen out of the box, with no Drill setup
required.

No other server config (`setting.xml`, `components.xml`, `databaseDrivers.properties`,
`driverDefaultQuery.properties`) or any client-side code needed to change — the "Collection"
field, the Mongo icon, and the generic save/test/update REST endpoints are all pre-existing
and already generic enough to support this.

## How to configure a MongoDB connection

1. In Helical Insight, go to **Data Sources → New Connection**.
2. Under the **No SQL & Big Data** category, pick the **Mongodb** tile.
3. Fill in:
   - **Host** / **Port** (default `27017`)
   - **Database** — also used as the authentication database when a username/password is
     given
   - **Collection** (optional, informational)
   - **Username** / **Password** (leave blank for an unauthenticated MongoDB instance)
4. Click **Test Connection** — this opens a real connection to the MongoDB server and
   reports success/failure.
5. Click **Save** to persist the connection.

## Verification performed

Since no MongoDB server was available in the target environment, a local MongoDB 6.0
instance was started and `MongoNativeLoader` was exercised directly (compiled against the
real `mongo-java-driver:3.12.10` jar, outside the full Maven build which isn't runnable in
this environment) to confirm real, functional connectivity:

- Successful connection to a running MongoDB instance → `testConnection` returns `true`.
- Unreachable host/port → fails in ~5s (not the driver's 30s default) and returns `false`.
- Correct username/password/authentication-database → `true`; wrong password → `false`.
- Missing host and URL → `loadToMiddleWare` throws a clear validation error instead of
  silently accepting an unusable connection.

No existing Java class was modified, and no existing datasource type's configuration was
touched, so existing MySQL/Postgres/Oracle/etc. connections and the legacy Drill-based Mongo
option are unaffected.

## Known limitation (pre-existing, not introduced by this change)

Helical Insight's ad-hoc report *query execution* for NoSQL sources (`db.noSql` connection
type) is wired in `setting.xml` to `SparkConnectionFactory` / `SparkMetadataProducer`, which
in turn depend on `com.helicalinsight.spark.service.NoSqlFileProvider`,
`UpdateNoSqlConfig`, and `QueryFetchFromSpark` — classes referenced in `components.xml` that
have **no source in this open-source repository** (they appear to belong to a
Spark-based enterprise module not shipped here), and there is also no `noSqlConfig.xml`
present. This is a pre-existing gap in this codebase, unrelated to MongoDB specifically —
it applies equally to the already-existing Drill-based Mongo option and to any other NoSQL
source, since the codebase has never included that execution engine in this checkout.

This change focuses on what "database driver/connectivity support" means in this app's own
architecture: making MongoDB a first-class, configurable, testable connection type in the
same way MySQL/Postgres/etc. are — not building the missing Spark NoSQL query engine, which
would be a much larger undertaking outside the scope of "add a database driver".
