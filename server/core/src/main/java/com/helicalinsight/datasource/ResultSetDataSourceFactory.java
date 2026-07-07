package com.helicalinsight.datasource;

import java.sql.ResultSet;


import com.helicalinsight.concurrent.StreamedResultset;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import net.sf.jasperreports.engine.JRDataSource;

public class ResultSetDataSourceFactory {

    private  ResultSet resultSet;
    
    private boolean alreadyUsed = false;

    
    public ResultSetDataSourceFactory(ResultSet resultSet) {
    	this.resultSet = resultSet;
    }

    public JRDataSource create() {
    	
    	ResultSet currentRs = null;
    	
    	try {
            if ( resultSet instanceof StreamedResultset stream) {
            	if (stream.getCurrentResultSet() == null) {
            		currentRs=stream.init();
            	}
            	else {
            		if (alreadyUsed) {
            			currentRs = stream.init();
            			return new CustomJRResultSetDataSource(currentRs);
            		}
            		else if (resultSet != null) {
            			currentRs = stream.getCurrentResultSet();
            		}
            	}
            	 alreadyUsed = true;
            	 currentRs.beforeFirst();
            }
        } catch (Exception e) {
        	throw new EfwServiceException(e);
        }
        return new CustomJRResultSetDataSource(currentRs);
    }
}