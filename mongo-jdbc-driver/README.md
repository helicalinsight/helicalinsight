# MongoDB JDBC Driver for Helical Insight

## Overview

This module adds MongoDB connectivity to Helical Insight through a JDBC-compatible driver.

The driver integrates with Helical Insight's existing JDBC datasource flow and provides:

- MongoDB JDBC connectivity
- MongoDB datasource configuration
- Database and collection metadata discovery
- JDBC `Connection`, `Statement`, and `ResultSet` support
- MongoDB query execution through the Helical Insight reporting flow
- Support for standard MongoDB connection strings and MongoDB SRV URLs

## Build

From the `mongo-jdbc-driver` directory:

```powershell
mvn clean package