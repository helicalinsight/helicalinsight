# MongoDB connectivity

Helical Insight now includes the official MongoDB synchronous Java driver and exposes **Mongodb** under **No SQL & Big Data** in the datasource screen. It uses a native MongoDB `ping` command for the connection test; it does not pretend that MongoDB is a JDBC database or depend on an unavailable third-party JDBC driver.

## Configure a connection

1. Open **Data Sources**, select **Mongodb**, and enter a datasource name, host, database, optional collection, and credentials.
2. Use either a standard URL such as `mongodb://db.example.com:27017/analytics` or an Atlas SRV URL such as `mongodb+srv://cluster.example.mongodb.net/analytics`.
3. Click **Test connection**. A successful test proves that the server can authenticate and run `ping` against the selected database.
4. Save the datasource. Credentials are stored using Helical Insight's existing datasource encryption flow.

Credentials entered in the form are safely percent-encoded and added to a URL that does not already contain credentials. A URL that already has user information is left unchanged. TLS, replica-set, authentication-source, and other MongoDB options can be supplied as normal URL query options, for example `?tls=true&authSource=admin`.

## Reporting/query support

MongoDB is configured as a native NoSQL datasource. If Apache Drill is enabled in Helical Insight, saving the datasource also registers a Mongo storage plugin in Drill, which makes the datasource available to the existing SQL/reporting pipeline. If Drill is disabled, saving and testing the MongoDB connection still work; querying it through the SQL report pipeline requires enabling and configuring Drill's Mongo storage plugin.

The MongoDB server user must have permission to authenticate and execute `ping` on the selected database. For Atlas, allow the Helical Insight server's network address in the Atlas access list and use an SRV URL with TLS enabled.
