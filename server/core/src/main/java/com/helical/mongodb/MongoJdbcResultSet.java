package com.helical.mongodb;

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
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class MongoJdbcResultSet implements ResultSet {

    private final Statement statement;
    private final List<String> columnLabels;
    private final List<Object[]> rows;
    private int cursor = -1;
    private boolean closed = false;
    private boolean lastWasNull = false;

    private MongoJdbcResultSet(Statement statement, List<String> columnLabels, List<Object[]> rows) {
        this.statement = statement;
        this.columnLabels = columnLabels;
        this.rows = rows;
    }

    static MongoJdbcResultSet create(
            Statement statement,
            List<String> columnLabels,
            List<Object[]> rows) {
        return new MongoJdbcResultSet(statement, columnLabels, rows);
    }

    static MongoJdbcResultSet singleValueResultSet(Statement statement, String sql) {
        List<String> columns = new ArrayList<>();
        columns.add("1");
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{1});
        return new MongoJdbcResultSet(statement, columns, data);
    }

    static MongoJdbcResultSet empty(Statement statement, List<String> columnLabels) {
        return new MongoJdbcResultSet(statement, new ArrayList<>(columnLabels), new ArrayList<>());
    }

    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("This result set has already been closed.");
        }
    }

    private int columnIndex(String columnLabel) throws SQLException {
        for (int i = 0; i < columnLabels.size(); i++) {
            if (columnLabels.get(i).equalsIgnoreCase(columnLabel)) {
                return i + 1;
            }
        }
        throw new SQLException("Unknown column: " + columnLabel);
    }

    private Object rawValue(int columnIndex) throws SQLException {
        checkClosed();
        if (cursor < 0 || cursor >= rows.size()) {
            throw new SQLException("The cursor is not positioned on a valid row.");
        }
        Object[] row = rows.get(cursor);
        if (columnIndex < 1 || columnIndex > row.length) {
            throw new SQLException("Invalid column index: " + columnIndex);
        }
        Object value = row[columnIndex - 1];
        lastWasNull = value == null;
        return value;
    }

    private static SQLFeatureNotSupportedException readOnly() {
        return new SQLFeatureNotSupportedException("This result set is read-only.");
    }

    // ----- Navigation -----

    @Override
    public boolean next() throws SQLException {
        checkClosed();
        if (cursor + 1 < rows.size()) {
            cursor++;
            return true;
        }
        cursor = rows.size();
        return false;
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return lastWasNull;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return cursor < 0 && !rows.isEmpty();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return cursor >= rows.size() && !rows.isEmpty();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return cursor == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        return !rows.isEmpty() && cursor == rows.size() - 1;
    }

    @Override
    public void beforeFirst() throws SQLException {
        cursor = -1;
    }

    @Override
    public void afterLast() throws SQLException {
        cursor = rows.size();
    }

    @Override
    public boolean first() throws SQLException {
        if (rows.isEmpty()) {
            return false;
        }
        cursor = 0;
        return true;
    }

    @Override
    public boolean last() throws SQLException {
        if (rows.isEmpty()) {
            return false;
        }
        cursor = rows.size() - 1;
        return true;
    }

    @Override
    public int getRow() throws SQLException {
        return (cursor < 0 || cursor >= rows.size()) ? 0 : cursor + 1;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        int target = row >= 0 ? row - 1 : rows.size() + row;
        if (target < 0 || target >= rows.size()) {
            cursor = target < 0 ? -1 : rows.size();
            return false;
        }
        cursor = target;
        return true;
    }

    @Override
    public boolean relative(int rowsOffset) throws SQLException {
        return absolute(getRow() + rowsOffset);
    }

    @Override
    public boolean previous() throws SQLException {
        if (cursor <= 0) {
            cursor = -1;
            return false;
        }
        cursor--;
        return true;
    }

    // ----- Fetch direction / size / type -----

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        //Forward-only; accepted for compatibility.
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        //no-op, this result set is already fully materialized in memory
    }

    @Override
    public int getFetchSize() throws SQLException {
        return rows.size();
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    // ----- Metadata / misc -----

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new MongoJdbcResultSetMetaData(columnLabels);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return columnIndex(columnLabel);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        //no-op
    }

    @Override
    public String getCursorName() throws SQLException {
        throw readOnly();
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    // ----- Typed getters (by column index) -----

    @Override
    public String getString(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? null : String.valueOf(v);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        if (v == null) return false;
        if (v instanceof Boolean) return (Boolean) v;
        return Boolean.parseBoolean(String.valueOf(v)) || "1".equals(String.valueOf(v));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? 0 : Byte.parseByte(String.valueOf(v));
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? 0 : Short.parseShort(String.valueOf(v));
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(String.valueOf(v));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(String.valueOf(v));
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        if (v == null) return 0f;
        if (v instanceof Number) return ((Number) v).floatValue();
        return Float.parseFloat(String.valueOf(v));
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        if (v == null) return 0d;
        if (v instanceof Number) return ((Number) v).doubleValue();
        return Double.parseDouble(String.valueOf(v));
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return getBigDecimal(columnIndex);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? null : String.valueOf(v).getBytes();
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? null : (v instanceof Date ? (Date) v : Date.valueOf(String.valueOf(v)));
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? null : (v instanceof Time ? (Time) v : Time.valueOf(String.valueOf(v)));
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? null : (v instanceof Timestamp ? (Timestamp) v : Timestamp.valueOf(String.valueOf(v)));
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw readOnly();
    }

    // ----- Typed getters (by column label) -----

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(columnIndex(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(columnIndex(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(columnIndex(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(columnIndex(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(columnIndex(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(columnIndex(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(columnIndex(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(columnIndex(columnLabel));
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(columnIndex(columnLabel));
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(columnIndex(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(columnIndex(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(columnIndex(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(columnIndex(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return rawValue(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(columnIndex(columnLabel));
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnLabel);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        Object v = rawValue(columnIndex);
        return v == null ? null : type.cast(v);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(columnIndex(columnLabel), type);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        Object v = rawValue(columnIndex);
        if (v == null) return null;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        return new BigDecimal(String.valueOf(v));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(columnIndex(columnLabel));
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return getDate(columnIndex);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(columnLabel);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getTime(columnIndex);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getTimestamp(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(columnLabel);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw readOnly();
    }

    // ----- Update methods: this result set is strictly read-only -----

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw readOnly();
    }

    // ----- Row editing: not supported (read-only, forward-only) -----

    @Override
    public void insertRow() throws SQLException {
        throw readOnly();
    }

    @Override
    public void updateRow() throws SQLException {
        throw readOnly();
    }

    @Override
    public void deleteRow() throws SQLException {
        throw readOnly();
    }

    @Override
    public void refreshRow() throws SQLException {
        //no-op: data is already fully materialized
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        //no-op
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw readOnly();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        //no-op
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface != null && iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLFeatureNotSupportedException("Not a wrapper for " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface != null && iface.isInstance(this);
    }
}
