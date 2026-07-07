package com.helicalinsight.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * RunningQueryDetails
 * This class represents details of a running query, including its associated statement, database details, and thread.
 * @author Somen
 * Created on 03.02.2017
 */
public class RunningQueryDetails {
    private static final Logger logger = LoggerFactory.getLogger(RunningQueryDetails.class);
    private Long startTime;
    private Statement activeStatement;
    private PreparedStatement activePreparedStatement;
    private JsonObject databaseDetails;
    private Thread activeThread;
    
    public RunningQueryDetails(PreparedStatement preparedStatement, JsonObject databaseDetails, Thread thread) {
        this.activePreparedStatement = preparedStatement;
        setDatabaseDetails(databaseDetails, thread);
    }

    public RunningQueryDetails(Statement statement, JsonObject databaseDetails, Thread thread) {
        this.activeStatement = statement;
        setDatabaseDetails(databaseDetails, thread);
    }
    /**
     * setDatabaseDetails(JSONObject databaseDetails, Thread thread)
     * Sets database-related details for a running query.
     * @param databaseDetails             query details
     * @param thread                      thread object
     */
    private void setDatabaseDetails(JsonObject databaseDetails, Thread thread) {
        this.activeThread = thread;
        this.databaseDetails = databaseDetails;
        this.startTimer();
    }

    /**
     * startTimer()
     * it sets current time for thread
     */
    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * getDatabaseDetails()
     * @return query details in jsonObject form
     */
    public JsonObject getDatabaseDetails() {
        return databaseDetails;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RunningQueryDetails that = (RunningQueryDetails) o;

        if (activePreparedStatement != null ? !activePreparedStatement.equals(that.activePreparedStatement) : that
                .activePreparedStatement != null)
            return false;
        if (activeStatement != null ? !activeStatement.equals(that.activeStatement) : that.activeStatement != null)
            return false;
        if (activeThread != null ? !activeThread.equals(that.activeThread) : that.activeThread != null) return false;
        if (databaseDetails != null ? !databaseDetails.equals(that.databaseDetails) : that.databaseDetails != null)
            return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (activeStatement != null ? activeStatement.hashCode() : 0);
        result = 31 * result + (activePreparedStatement != null ? activePreparedStatement.hashCode() : 0);
        result = 31 * result + (databaseDetails != null ? databaseDetails.hashCode() : 0);
        result = 31 * result + (activeThread != null ? activeThread.hashCode() : 0);
        return result;
    }
    /**
     * cancelQuery()
     * this method is responsible for query cancellation.
     */
    public void cancelQuery() {
        try {
            if (this.activeStatement != null) {
                this.activeStatement.cancel();
                logger.info("The active statement has been cancelled");
            }
            if (this.activePreparedStatement != null) {

                this.activePreparedStatement.cancel();
                logger.info("The active prepared statement has been cancelled");
            }
            this.activeThread.interrupt();
        } catch (SQLException ex) {
            logger.error("There was an error ", ex);
        }
    }
}
