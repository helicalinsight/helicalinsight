package com.helicalinsight.datasource.managed;

import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

public class ResultSetJdbcQueryStreamer implements Runnable {
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

    public void executeSql() {
    	try (ResultSet rs = this.statement.executeQuery(this.sql)) {
			ResultSet cacheView = ResultSetCacheTypeConversion.wrapForWrite(rs);
			boolean first = true;
			while (!Thread.currentThread().isInterrupted()) {
				CachedRowSet batch = RowSetProvider.newFactory().createCachedRowSet();
				batch.populate(ResultSetCacheTypeConversion.batchLimited(cacheView, batchSize));
				int size = batch.size();
				if (size == 0) {
					if (first) {
						batch.beforeFirst();
						callBack.process(batch);
					}
					return;
				}
				first = false;
				batch.beforeFirst();
				callBack.process(batch);
				if (size < batchSize) {
					return;
				}
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
}
