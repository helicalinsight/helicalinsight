# MongoDB Support for Helical Insight

## What this does

Adds MongoDB as a database connection option, using the same JDBC-based
pipeline every other database in Helical Insight already uses (MySQL,
Postgres, etc.) instead of building a separate path just for Mongo.

The repo already had a half-finished hook for this in
`MongoConnectionFactory.java` — it recognized a Mongo driver name but always
returned a connection that was `null`. This fills that in with a real
connection.

## Driver used

`org.mongodb:mongodb-jdbc` v3.0.3 — MongoDB's own official JDBC driver,
Apache 2.0 licensed, free on Maven Central. Driver class:
`com.mongodb.jdbc.MongoDriver`.

One thing worth flagging: this driver talks to **MongoDB Atlas SQL**, not a
plain self-hosted `mongod`. For a fully local Mongo setup you'd need a
different driver — didn't have time to validate that path in the 24 hours.

Also worth noting — the codebase already had references to two *other*
Mongo driver names (`mongodb.jdbc.MongoDriver` and a proprietary
`com.helical.mongodb.MongoJdbcDriver`), but neither had an actual dependency
behind them. Went with the real MongoDB driver instead and kept backward
compatibility with the old driver name in code, just in case.

## Files changed

* `server/core/pom.xml` — added the mongodb-jdbc dependency (+ junit for
the test)
* `server/core/src/main/java/com/helicalinsight/datasource/MongoConnectionFactory.java` —
replaced the null-connection placeholder with a real `DriverManager`
connection, reading `jdbcUrl` / `userName` / `password` the same way the
rest of the app does
* `server/core/src/test/java/com/helicalinsight/datasource/MongoConnectionFactoryTest.java` —
small unit test, confirms the driver loads
* Three properties files under `hi-repository` (`databaseDrivers.properties`,
`sqlDialects.properties`, `driverDefaultQuery.properties`) — added the
same default-URL / dialect / test-query entries every other DB has, so
Mongo behaves like a first-class connection type

Nothing else was touched. The normal JDBC path for other databases is
untouched — the Mongo logic only kicks in when the driver name matches.

## How to set up a connection

Same connection JSON shape as any other JDBC data source:

```json
{
  "connectionJson": {
    "driverClassName": "com.mongodb.jdbc.MongoDriver",
    "jdbcUrl": "jdbc:mongodb://<HOST>:27017/<DATABASE>",
    "userName": "<MONGO\_USERNAME>",
    "password": "<MONGO\_PASSWORD>"
  }
}
```

## Testing

* `mvn clean compile` — builds fine
* Full app build from source via `docker compose -f docker-compose.dev.yml up --build` —
starts up clean
* Unit test passes (driver loads on classpath)
* Logged into the running app, clicked around, checked logs — no errors tied
to the Mongo changes. Only pre-existing, unrelated error is an SMTP
connection failure (no mail server configured locally, not related to this
work)
* Didn't get a chance to test against a real live MongoDB Atlas instance
within the time window — connection logic is verified to compile, load
the driver correctly, and fail cleanly (via `ConnectionException`) when
given a bad host, but not verified against a real successful connection

## 

