package com.helicalinsight.hwf.component;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.hwf.core.IJobProcess;
import net.sf.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class JDBCConnectionClass implements IJobProcess, Job {
    private static final Logger logger = LoggerFactory.getLogger(JDBCConnectionClass.class);

    @Override
    public JSONObject executionStatus() {
        return null;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        //JobKey key = context.getJobDetail().getKey();

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        JSONObject input = (JSONObject) dataMap.get("jobinput");
        // JSONObject input = JSONObject(jobSays);
        JSONObject jobprocessData = (JSONObject) dataMap.get("processdata");
        jobProcess(input, jobprocessData);
    }

    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject processData) {
        JSONObject rvalue = print(input);
        return rvalue;
    }

    public JSONObject print(JSONObject value) {
        String drivername = value.getString("drivername1");
        String password = value.getString("password1");
        String url = value.getString("url1");
        String username = value.getString("username1");
        JSONObject dbdata = new JSONObject();

        Connection conn = null;
        Statement stmt;
        try {
            //STEP 2: Register JDBC driver
            logger.info("Register JDBC driver");
            try {
                FactoryMethodWrapper.forName(drivername);
            } catch (ClassNotFoundException ex) {
                FactoryMethodWrapper.extendedForName(drivername);
            }

            //STEP 3: Open a connection
            logger.info("Connecting to a selected database...");
            conn = DriverManager.getConnection(url, username, password);
            logger.info("Connected database successfully...");

            //STEP 4: Execute a query
            logger.info("Creating statement...");
            stmt = conn.createStatement();

            String sql = "SELECT * FROM contacts";
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int id = rs.getInt("id");
                String name = rs.getString("firstname");
                dbdata.accumulate("ID", id);
                dbdata.accumulate("name", name);

                //Display values
                logger.debug("ID: " + id);
                logger.debug(", name: " + name);
            }
            rs.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            //handle error
        } catch (Exception e) {
            //Handle errors for Class.forName
            //handle error
        } finally {
            //finally block used to close resources
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                //handle error
            }//end finally try
        }//end try
        logger.info("Goodbye!");
        logger.debug("Return Value:  " + dbdata);
        return dbdata;
    }
}
