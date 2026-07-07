package com.helicalinsight.concurrent;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import com.helicalinsight.datasource.ChunkIterator;
import com.helicalinsight.datasource.ChunkReader;
import com.helicalinsight.datasource.RowCacheSetChunkReader;


public class StreamedResultset  implements ResultSet {
	
	private ChunkIterator<ResultSet> iterator;
	
	private ResultSet currentRs;
    private ResultSetMetaData metaData;
    private String chunkDirectory;
	
	public StreamedResultset(String chunkDirectory) {
		this.chunkDirectory = chunkDirectory;
		this.currentRs = init();
	}
	
	public ResultSet getCurrentResultSet() {
		return this.currentRs;
	}
	
	
	public ResultSet init() {
		ChunkReader<ResultSet> chunkReader = new RowCacheSetChunkReader();
		ResultSet resultSet = null;
		this.iterator = new ChunkIterator<ResultSet>(new File(this.chunkDirectory), chunkReader);
		if (iterator.hasNext()) {
			resultSet= iterator.next();
			try {
				this.metaData = resultSet.getMetaData();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return resultSet;
	}
	
	@Override
	public boolean next() {
	    try {
	        while (true) {
	            if (currentRs == null) {
	                if (!iterator.hasNext()) {
	                    return false;
	                }
	                currentRs = iterator.next();
	                if (metaData == null) {
	                    metaData = currentRs.getMetaData();
	                }
	            }
	            if (currentRs.next()) {
	                return true;
	            }
	            closeCurrent();
	        }
	    } catch (Exception e) {
	    	return false;
	    }
	}

	
	private void closeCurrent() {
	    try {
	        if (currentRs != null) {
	            currentRs.close();
	            currentRs = null;
	        }
	    } catch (Exception ignored) {
	    } finally {
	        currentRs = null;
	    }
	}
	
	
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		if (currentRs != null) {
	        return currentRs.getMetaData();
	    }
	    return metaData;
	}
	
	
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return currentRs.unwrap(iface);
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return currentRs.isWrapperFor(iface);
		}

		public void close() throws SQLException {
			closeCurrent();
			while(iterator.hasNext()) {
				try {
					iterator.next().close();
				}
				catch (Exception ignored) {
					
				}
			}
		}

		public boolean wasNull() throws SQLException {
			return currentRs.wasNull();
		}

		public String getString(int columnIndex) throws SQLException {
			return currentRs.getString(columnIndex);
		}

		public boolean getBoolean(int columnIndex) throws SQLException {
			return currentRs.getBoolean(columnIndex);
		}

		public byte getByte(int columnIndex) throws SQLException {
			return currentRs.getByte(columnIndex);
		}

		public short getShort(int columnIndex) throws SQLException {
			return currentRs.getShort(columnIndex);
		}

		public int getInt(int columnIndex) throws SQLException {
			return currentRs.getInt(columnIndex);
		}

		public long getLong(int columnIndex) throws SQLException {
			return currentRs.getLong(columnIndex);
		}

		public float getFloat(int columnIndex) throws SQLException {
			return currentRs.getFloat(columnIndex);
		}

		public double getDouble(int columnIndex) throws SQLException {
			return currentRs.getDouble(columnIndex);
		}

		public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
			return currentRs.getBigDecimal(columnIndex, scale);
		}

		public byte[] getBytes(int columnIndex) throws SQLException {
			return currentRs.getBytes(columnIndex);
		}

		public Date getDate(int columnIndex) throws SQLException {
			return currentRs.getDate(columnIndex);
		}

		public Time getTime(int columnIndex) throws SQLException {
			return currentRs.getTime(columnIndex);
		}

		public Timestamp getTimestamp(int columnIndex) throws SQLException {
			return currentRs.getTimestamp(columnIndex);
		}

		public InputStream getAsciiStream(int columnIndex) throws SQLException {
			return currentRs.getAsciiStream(columnIndex);
		}

		public InputStream getUnicodeStream(int columnIndex) throws SQLException {
			return currentRs.getUnicodeStream(columnIndex);
		}

		public InputStream getBinaryStream(int columnIndex) throws SQLException {
			return currentRs.getBinaryStream(columnIndex);
		}

		public String getString(String columnLabel) throws SQLException {
			return currentRs.getString(columnLabel);
		}

		public boolean getBoolean(String columnLabel) throws SQLException {
			return currentRs.getBoolean(columnLabel);
		}

		public byte getByte(String columnLabel) throws SQLException {
			return currentRs.getByte(columnLabel);
		}

		public short getShort(String columnLabel) throws SQLException {
			return currentRs.getShort(columnLabel);
		}

		public int getInt(String columnLabel) throws SQLException {
			return currentRs.getInt(columnLabel);
		}

		public long getLong(String columnLabel) throws SQLException {
			return currentRs.getLong(columnLabel);
		}

		public float getFloat(String columnLabel) throws SQLException {
			return currentRs.getFloat(columnLabel);
		}

		public double getDouble(String columnLabel) throws SQLException {
			return currentRs.getDouble(columnLabel);
		}

		public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
			return currentRs.getBigDecimal(columnLabel, scale);
		}

		public byte[] getBytes(String columnLabel) throws SQLException {
			return currentRs.getBytes(columnLabel);
		}

		public Date getDate(String columnLabel) throws SQLException {
			return currentRs.getDate(columnLabel);
		}

		public Time getTime(String columnLabel) throws SQLException {
			return currentRs.getTime(columnLabel);
		}

		public Timestamp getTimestamp(String columnLabel) throws SQLException {
			return currentRs.getTimestamp(columnLabel);
		}

		public InputStream getAsciiStream(String columnLabel) throws SQLException {
			return currentRs.getAsciiStream(columnLabel);
		}

		public InputStream getUnicodeStream(String columnLabel) throws SQLException {
			return currentRs.getUnicodeStream(columnLabel);
		}

		public InputStream getBinaryStream(String columnLabel) throws SQLException {
			return currentRs.getBinaryStream(columnLabel);
		}

		public SQLWarning getWarnings() throws SQLException {
			return currentRs.getWarnings();
		}

		public void clearWarnings() throws SQLException {
			currentRs.clearWarnings();
		}

		public String getCursorName() throws SQLException {
			return currentRs.getCursorName();
		}


		public Object getObject(int columnIndex) throws SQLException {
			return currentRs.getObject(columnIndex);
		}

		public Object getObject(String columnLabel) throws SQLException {
			return currentRs.getObject(columnLabel);
		}

		public int findColumn(String columnLabel) throws SQLException {
			return currentRs.findColumn(columnLabel);
		}

		public Reader getCharacterStream(int columnIndex) throws SQLException {
			return currentRs.getCharacterStream(columnIndex);
		}

		public Reader getCharacterStream(String columnLabel) throws SQLException {
			return currentRs.getCharacterStream(columnLabel);
		}

		public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
			return currentRs.getBigDecimal(columnIndex);
		}

		public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
			return currentRs.getBigDecimal(columnLabel);
		}

		public boolean isBeforeFirst() throws SQLException {
			return currentRs.isBeforeFirst();
		}

		public boolean isAfterLast() throws SQLException {
			return currentRs.isAfterLast();
		}

		public boolean isFirst() throws SQLException {
			return currentRs.isFirst();
		}

		public boolean isLast() throws SQLException {
			return currentRs.isLast();
		}

		public void beforeFirst() throws SQLException {
			currentRs.beforeFirst();
		}

		public void afterLast() throws SQLException {
			currentRs.afterLast();
		}

		public boolean first() throws SQLException {
			return currentRs.first();
		}

		public boolean last() throws SQLException {
			return currentRs.last();
		}

		public int getRow() throws SQLException {
			return currentRs.getRow();
		}

		public boolean absolute(int row) throws SQLException {
			return currentRs.absolute(row);
		}

		public boolean relative(int rows) throws SQLException {
			return currentRs.relative(rows);
		}

		public boolean previous() throws SQLException {
			return currentRs.previous();
		}

		public void setFetchDirection(int direction) throws SQLException {
			currentRs.setFetchDirection(direction);
		}

		public int getFetchDirection() throws SQLException {
			return currentRs.getFetchDirection();
		}

		public void setFetchSize(int rows) throws SQLException {
			currentRs.setFetchSize(rows);
		}

		public int getFetchSize() throws SQLException {
			return currentRs.getFetchSize();
		}

		public int getType() throws SQLException {
			return currentRs.getType();
		}

		public int getConcurrency() throws SQLException {
			return currentRs.getConcurrency();
		}

		public boolean rowUpdated() throws SQLException {
			return currentRs.rowUpdated();
		}

		public boolean rowInserted() throws SQLException {
			return currentRs.rowInserted();
		}

		public boolean rowDeleted() throws SQLException {
			return currentRs.rowDeleted();
		}

		public void updateNull(int columnIndex) throws SQLException {
			currentRs.updateNull(columnIndex);
		}

		public void updateBoolean(int columnIndex, boolean x) throws SQLException {
			currentRs.updateBoolean(columnIndex, x);
		}

		public void updateByte(int columnIndex, byte x) throws SQLException {
			currentRs.updateByte(columnIndex, x);
		}

		public void updateShort(int columnIndex, short x) throws SQLException {
			currentRs.updateShort(columnIndex, x);
		}

		public void updateInt(int columnIndex, int x) throws SQLException {
			currentRs.updateInt(columnIndex, x);
		}

		public void updateLong(int columnIndex, long x) throws SQLException {
			currentRs.updateLong(columnIndex, x);
		}

		public void updateFloat(int columnIndex, float x) throws SQLException {
			currentRs.updateFloat(columnIndex, x);
		}

		public void updateDouble(int columnIndex, double x) throws SQLException {
			currentRs.updateDouble(columnIndex, x);
		}

		public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
			currentRs.updateBigDecimal(columnIndex, x);
		}

		public void updateString(int columnIndex, String x) throws SQLException {
			currentRs.updateString(columnIndex, x);
		}

		public void updateBytes(int columnIndex, byte[] x) throws SQLException {
			currentRs.updateBytes(columnIndex, x);
		}

		public void updateDate(int columnIndex, Date x) throws SQLException {
			currentRs.updateDate(columnIndex, x);
		}

		public void updateTime(int columnIndex, Time x) throws SQLException {
			currentRs.updateTime(columnIndex, x);
		}

		public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
			currentRs.updateTimestamp(columnIndex, x);
		}

		public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
			currentRs.updateAsciiStream(columnIndex, x, length);
		}

		public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
			currentRs.updateBinaryStream(columnIndex, x, length);
		}

		public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
			currentRs.updateCharacterStream(columnIndex, x, length);
		}

		public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
			currentRs.updateObject(columnIndex, x, scaleOrLength);
		}

		public void updateObject(int columnIndex, Object x) throws SQLException {
			currentRs.updateObject(columnIndex, x);
		}

		public void updateNull(String columnLabel) throws SQLException {
			currentRs.updateNull(columnLabel);
		}

		public void updateBoolean(String columnLabel, boolean x) throws SQLException {
			currentRs.updateBoolean(columnLabel, x);
		}

		public void updateByte(String columnLabel, byte x) throws SQLException {
			currentRs.updateByte(columnLabel, x);
		}

		public void updateShort(String columnLabel, short x) throws SQLException {
			currentRs.updateShort(columnLabel, x);
		}

		public void updateInt(String columnLabel, int x) throws SQLException {
			currentRs.updateInt(columnLabel, x);
		}

		public void updateLong(String columnLabel, long x) throws SQLException {
			currentRs.updateLong(columnLabel, x);
		}

		public void updateFloat(String columnLabel, float x) throws SQLException {
			currentRs.updateFloat(columnLabel, x);
		}

		public void updateDouble(String columnLabel, double x) throws SQLException {
			currentRs.updateDouble(columnLabel, x);
		}

		public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
			currentRs.updateBigDecimal(columnLabel, x);
		}

		public void updateString(String columnLabel, String x) throws SQLException {
			currentRs.updateString(columnLabel, x);
		}

		public void updateBytes(String columnLabel, byte[] x) throws SQLException {
			currentRs.updateBytes(columnLabel, x);
		}

		public void updateDate(String columnLabel, Date x) throws SQLException {
			currentRs.updateDate(columnLabel, x);
		}

		public void updateTime(String columnLabel, Time x) throws SQLException {
			currentRs.updateTime(columnLabel, x);
		}

		public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
			currentRs.updateTimestamp(columnLabel, x);
		}

		public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
			currentRs.updateAsciiStream(columnLabel, x, length);
		}

		public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
			currentRs.updateBinaryStream(columnLabel, x, length);
		}

		public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
			currentRs.updateCharacterStream(columnLabel, reader, length);
		}

		public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
			currentRs.updateObject(columnLabel, x, scaleOrLength);
		}

		public void updateObject(String columnLabel, Object x) throws SQLException {
			currentRs.updateObject(columnLabel, x);
		}

		public void insertRow() throws SQLException {
			currentRs.insertRow();
		}

		public void updateRow() throws SQLException {
			currentRs.updateRow();
		}

		public void deleteRow() throws SQLException {
			currentRs.deleteRow();
		}

		public void refreshRow() throws SQLException {
			currentRs.refreshRow();
		}

		public void cancelRowUpdates() throws SQLException {
			currentRs.cancelRowUpdates();
		}

		public void moveToInsertRow() throws SQLException {
			currentRs.moveToInsertRow();
		}

		public void moveToCurrentRow() throws SQLException {
			currentRs.moveToCurrentRow();
		}

		public Statement getStatement() throws SQLException {
			return currentRs.getStatement();
		}

		public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
			return currentRs.getObject(columnIndex, map);
		}

		public Ref getRef(int columnIndex) throws SQLException {
			return currentRs.getRef(columnIndex);
		}

		public Blob getBlob(int columnIndex) throws SQLException {
			return currentRs.getBlob(columnIndex);
		}

		public Clob getClob(int columnIndex) throws SQLException {
			return currentRs.getClob(columnIndex);
		}

		public Array getArray(int columnIndex) throws SQLException {
			return currentRs.getArray(columnIndex);
		}

		public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
			return currentRs.getObject(columnLabel, map);
		}

		public Ref getRef(String columnLabel) throws SQLException {
			return currentRs.getRef(columnLabel);
		}

		public Blob getBlob(String columnLabel) throws SQLException {
			return currentRs.getBlob(columnLabel);
		}

		public Clob getClob(String columnLabel) throws SQLException {
			return currentRs.getClob(columnLabel);
		}

		public Array getArray(String columnLabel) throws SQLException {
			return currentRs.getArray(columnLabel);
		}

		public Date getDate(int columnIndex, Calendar cal) throws SQLException {
			return currentRs.getDate(columnIndex, cal);
		}

		public Date getDate(String columnLabel, Calendar cal) throws SQLException {
			return currentRs.getDate(columnLabel, cal);
		}

		public Time getTime(int columnIndex, Calendar cal) throws SQLException {
			return currentRs.getTime(columnIndex, cal);
		}

		public Time getTime(String columnLabel, Calendar cal) throws SQLException {
			return currentRs.getTime(columnLabel, cal);
		}

		public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
			return currentRs.getTimestamp(columnIndex, cal);
		}

		public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
			return currentRs.getTimestamp(columnLabel, cal);
		}

		public URL getURL(int columnIndex) throws SQLException {
			return currentRs.getURL(columnIndex);
		}

		public URL getURL(String columnLabel) throws SQLException {
			return currentRs.getURL(columnLabel);
		}

		public void updateRef(int columnIndex, Ref x) throws SQLException {
			currentRs.updateRef(columnIndex, x);
		}

		public void updateRef(String columnLabel, Ref x) throws SQLException {
			currentRs.updateRef(columnLabel, x);
		}

		public void updateBlob(int columnIndex, Blob x) throws SQLException {
			currentRs.updateBlob(columnIndex, x);
		}

		public void updateBlob(String columnLabel, Blob x) throws SQLException {
			currentRs.updateBlob(columnLabel, x);
		}

		public void updateClob(int columnIndex, Clob x) throws SQLException {
			currentRs.updateClob(columnIndex, x);
		}

		public void updateClob(String columnLabel, Clob x) throws SQLException {
			currentRs.updateClob(columnLabel, x);
		}

		public void updateArray(int columnIndex, Array x) throws SQLException {
			currentRs.updateArray(columnIndex, x);
		}

		public void updateArray(String columnLabel, Array x) throws SQLException {
			currentRs.updateArray(columnLabel, x);
		}

		public RowId getRowId(int columnIndex) throws SQLException {
			return currentRs.getRowId(columnIndex);
		}

		public RowId getRowId(String columnLabel) throws SQLException {
			return currentRs.getRowId(columnLabel);
		}

		public void updateRowId(int columnIndex, RowId x) throws SQLException {
			currentRs.updateRowId(columnIndex, x);
		}

		public void updateRowId(String columnLabel, RowId x) throws SQLException {
			currentRs.updateRowId(columnLabel, x);
		}

		public int getHoldability() throws SQLException {
			return currentRs.getHoldability();
		}

		public boolean isClosed() throws SQLException {
			return currentRs.isClosed();
		}

		public void updateNString(int columnIndex, String nString) throws SQLException {
			currentRs.updateNString(columnIndex, nString);
		}

		public void updateNString(String columnLabel, String nString) throws SQLException {
			currentRs.updateNString(columnLabel, nString);
		}

		public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
			currentRs.updateNClob(columnIndex, nClob);
		}

		public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
			currentRs.updateNClob(columnLabel, nClob);
		}

		public NClob getNClob(int columnIndex) throws SQLException {
			return currentRs.getNClob(columnIndex);
		}

		public NClob getNClob(String columnLabel) throws SQLException {
			return currentRs.getNClob(columnLabel);
		}

		public SQLXML getSQLXML(int columnIndex) throws SQLException {
			return currentRs.getSQLXML(columnIndex);
		}

		public SQLXML getSQLXML(String columnLabel) throws SQLException {
			return currentRs.getSQLXML(columnLabel);
		}

		public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
			currentRs.updateSQLXML(columnIndex, xmlObject);
		}

		public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
			currentRs.updateSQLXML(columnLabel, xmlObject);
		}

		public String getNString(int columnIndex) throws SQLException {
			return currentRs.getNString(columnIndex);
		}

		public String getNString(String columnLabel) throws SQLException {
			return currentRs.getNString(columnLabel);
		}

		public Reader getNCharacterStream(int columnIndex) throws SQLException {
			return currentRs.getNCharacterStream(columnIndex);
		}

		public Reader getNCharacterStream(String columnLabel) throws SQLException {
			return currentRs.getNCharacterStream(columnLabel);
		}

		public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
			currentRs.updateNCharacterStream(columnIndex, x, length);
		}

		public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
			currentRs.updateNCharacterStream(columnLabel, reader, length);
		}

		public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
			currentRs.updateAsciiStream(columnIndex, x, length);
		}

		public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
			currentRs.updateBinaryStream(columnIndex, x, length);
		}

		public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
			currentRs.updateCharacterStream(columnIndex, x, length);
		}

		public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
			currentRs.updateAsciiStream(columnLabel, x, length);
		}

		public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
			currentRs.updateBinaryStream(columnLabel, x, length);
		}

		public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
			currentRs.updateCharacterStream(columnLabel, reader, length);
		}

		public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
			currentRs.updateBlob(columnIndex, inputStream, length);
		}

		public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
			currentRs.updateBlob(columnLabel, inputStream, length);
		}

		public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
			currentRs.updateClob(columnIndex, reader, length);
		}

		public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
			currentRs.updateClob(columnLabel, reader, length);
		}

		public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
			currentRs.updateNClob(columnIndex, reader, length);
		}

		public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
			currentRs.updateNClob(columnLabel, reader, length);
		}

		public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
			currentRs.updateNCharacterStream(columnIndex, x);
		}

		public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
			currentRs.updateNCharacterStream(columnLabel, reader);
		}

		public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
			currentRs.updateAsciiStream(columnIndex, x);
		}

		public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
			currentRs.updateBinaryStream(columnIndex, x);
		}

		public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
			currentRs.updateCharacterStream(columnIndex, x);
		}

		public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
			currentRs.updateAsciiStream(columnLabel, x);
		}

		public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
			currentRs.updateBinaryStream(columnLabel, x);
		}

		public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
			currentRs.updateCharacterStream(columnLabel, reader);
		}

		public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
			currentRs.updateBlob(columnIndex, inputStream);
		}

		public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
			currentRs.updateBlob(columnLabel, inputStream);
		}

		public void updateClob(int columnIndex, Reader reader) throws SQLException {
			currentRs.updateClob(columnIndex, reader);
		}

		public void updateClob(String columnLabel, Reader reader) throws SQLException {
			currentRs.updateClob(columnLabel, reader);
		}

		public void updateNClob(int columnIndex, Reader reader) throws SQLException {
			currentRs.updateNClob(columnIndex, reader);
		}

		public void updateNClob(String columnLabel, Reader reader) throws SQLException {
			currentRs.updateNClob(columnLabel, reader);
		}

		public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
			return currentRs.getObject(columnIndex, type);
		}

		public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
			return currentRs.getObject(columnLabel, type);
		}

		public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
				throws SQLException {
			currentRs.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
		}

		public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
				throws SQLException {
			currentRs.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
		}

		public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
			currentRs.updateObject(columnIndex, x, targetSqlType);
		}

		public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
			currentRs.updateObject(columnLabel, x, targetSqlType);
		}
}
