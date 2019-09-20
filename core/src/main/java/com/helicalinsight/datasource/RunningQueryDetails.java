/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Somen
 */
public class RunningQueryDetails {
    private static final Logger logger = LoggerFactory.getLogger(RunningQueryDetails.class);
    private Long startTime;
    private Statement activeStatement;
    private PreparedStatement activePreparedStatement;
    private JSONObject databaseDetails;
    private Thread activeThread;

    public RunningQueryDetails(PreparedStatement preparedStatement, JSONObject databaseDetails, Thread thread) {
        this.activePreparedStatement = preparedStatement;
        setDatabaseDetails(databaseDetails, thread);
    }

    public RunningQueryDetails(Statement statement, JSONObject databaseDetails, Thread thread) {
        this.activeStatement = statement;
        setDatabaseDetails(databaseDetails, thread);
    }

    private void setDatabaseDetails(JSONObject databaseDetails, Thread thread) {
        this.activeThread = thread;
        this.databaseDetails = databaseDetails;
        this.startTimer();
    }


    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }


    public JSONObject getDatabaseDetails() {
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
