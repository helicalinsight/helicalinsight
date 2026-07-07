package com.helicalinsight.datasource.managed;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;

import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.exceptions.EfwdServiceException;


public class ResultSetJdbcQueryStreamer implements  Runnable  {
    private final Statement statement;
    private final String sql;
    private final int batchSize;
    private final CallBack<ResultSet> callBack;
    
    public ResultSetJdbcQueryStreamer(Statement statement, String sql, int batchSize, CallBack<ResultSet> callBack) {
        this.statement = statement;
        this.sql = sql;
        this.batchSize = batchSize;
        this.callBack = callBack;
    }

    public void executeSql()  {
    	
    	try(ResultSet  rs = this.statement.executeQuery(this.sql)) {
    		
    		CachedRowSet batch = null;
			
    		int rowCount = 0;
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			RowSetMetaData md = buildMetadata(rsmd);
			
			final int columnCount = rsmd.getColumnCount();
			boolean hasData = false;
			while(rs.next() && !Thread.currentThread().isInterrupted()) {
				hasData = true;
				if ( batch == null ) {
					batch = RowSetProvider.newFactory().createCachedRowSet();
					batch.setMetaData(md);
					rowCount = 0;
				}
				
				batch.moveToInsertRow();
				copyCurrentRow(rs, batch, columnCount);
				batch.insertRow();
				batch.moveToCurrentRow();
				batch.next();
				rowCount++;
				
				if ( rowCount == batchSize) {
					batch.beforeFirst();
					callBack.process(batch);
					batch = null;
				}
			}

			if (!hasData) {
				CachedRowSet empty = RowSetProvider.newFactory().createCachedRowSet();
				empty.setMetaData(md);
				empty.beforeFirst();
				callBack.process(empty);
				return;
			}
			
			if ( batch != null && rowCount > 0 )  {
				batch.beforeFirst();
				callBack.process(batch);
			}
        } catch (Exception ex) {
        	 Thread.currentThread().interrupt();
            throw new EfwdServiceException("Couldn't query the database", ex);
        } 
    }

    @Override
    public void run() {
        executeSql();
    }
    
    private static RowSetMetaData buildMetadata(ResultSetMetaData rsmd) throws SQLException {
		
		RowSetMetaDataImpl md = new RowSetMetaDataImpl();
		
		int cols = rsmd.getColumnCount();
		
		md.setColumnCount(cols);
		
		for(int index=1; index<=cols; index++) {
			md.setColumnLabel(index, rsmd.getColumnLabel(index));
			md.setColumnName(index, rsmd.getColumnName(index));
			md.setColumnType(index, rsmd.getColumnType(index));
			md.setNullable(index, rsmd.isNullable(index));
		    md.setPrecision(index, rsmd.getPrecision(index));
		    md.setScale(index, rsmd.getScale(index));
		}
		return md;
	}
    
    
    
    private void copyCurrentRow(ResultSet rs, CachedRowSet crs, int cols) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();

        for (int i = 1; i <= cols; i++) {
            Object value = rs.getObject(i);

            if (value == null) {
                crs.updateNull(i);
                continue;
            }

            int type = meta.getColumnType(i);

            try {
                crs.updateObject(i, value, type);
            } catch (SQLException e) {
                crs.updateObject(i, value);
            }
        }
    }
}
