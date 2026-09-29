MongoDB Driver Integration - Helical Insight
Overview
This implementation adds MongoDB database driver/connectivity support to the Helical Insight application. MongoDB is now natively integrated into the UI dropdown and backend configuration, allowing users to connect to MongoDB just like existing databases (MySQL, PostgreSQL, etc.).

Changes Made

1. Frontend (React)
   Added MongoDB to the dataSources array in the frontend constants/mock data.
   Configured the url template (mongodb://{{hostName}}:{{port}}/{{database}}) and parameters (port 27017, default host localhost) so the UI renders the correct input fields for MongoDB.
   Changed the category to "NoSQL" to properly group it.
2. Backend (Java Configuration)
   Verified the MongoDB JDBC driver mapping (com.helical.mongodb.MongoJdbcDriver) is active in the backend properties file and correctly formatted to generate mongodb:// connection URLs.
   How to Configure and Use MongoDB
   Ensure the backend server and frontend React app are running.
   Log in to Helical Insight as hiadmin.
   Navigate to Data Sources -> Add Data Source.
   Select MongoDB from the database type dropdown.
   Enter your MongoDB connection details:
   Host Name: localhost (or your remote host)
   Port: 27017
   Database: your_database_name
   Username/Password: (If applicable)
   Click Test Connection to verify, then Save the data source.
   Note on NoSQL
   Since MongoDB is a NoSQL database and Helical Insight uses SQL for ad-hoc reporting, a JDBC wrapper driver (com.helical.mongodb.MongoJdbcDriver) is utilized to translate SQL queries to MongoDB queries.
