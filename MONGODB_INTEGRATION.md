**# MongoDB JDBC Integration - Helical Insight**



**## Overview**



**This implementation adds MongoDB JDBC connectivity support to Helical Insight using the existing datasource architecture.**



**## Changes Made**



**- Added MongoDB JDBC Driver dependency.**

**- Added MongoDB JDBC dependency to the hi-core module.**

**- Integrated MongoDB JDBC driver:**

&#x20; **`com.mongodb.jdbc.MongoDriver`**

**- Added MongoDB datasource configuration.**

**- Added MongoDB JDBC URL support.**

**- Added default MongoDB port 27017.**

**- Reused Helical Insight's existing JDBC connection infrastructure.**



**## Driver Configuration**



**Driver Class:**



**`com.mongodb.jdbc.MongoDriver`**



**MongoDB JDBC dependency:**



**`org.mongodb:mongodb-jdbc:2.2.3`**



**Example JDBC URL:**



**`jdbc:mongodb://localhost:27017/database`**



**Default MongoDB Port:**



**`27017`**



**## Configuration**



**To configure MongoDB:**



**1. Open the datasource configuration in Helical Insight.**

**2. Select MongoDB JDBC.**

**3. Enter the MongoDB hostname.**

**4. Enter port 27017 or the configured MongoDB port.**

**5. Enter the database name.**

**6. Provide authentication information if required.**

**7. Test the connection.**

**8. Save the datasource.**



**## Verification**



**The MongoDB JDBC dependency can be verified using:**



**mvn -f server/core/pom.xml dependency:tree -Dincludes=org.mongodb:mongodb-jdbc**



**The hi-core module can be validated using:**



**mvn -f server/core/pom.xml validate**



**Both Maven dependency resolution and project validation completed successfully.**



**## Compatibility**



**The implementation uses Helical Insight's existing JDBC connection infrastructure and preserves the existing MongoDB-related implementation.**

