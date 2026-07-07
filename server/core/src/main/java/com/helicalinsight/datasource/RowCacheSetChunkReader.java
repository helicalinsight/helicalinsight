package com.helicalinsight.datasource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import javax.sql.rowset.CachedRowSet;

import com.helicalinsight.efw.exceptions.EfwServiceException;

public class RowCacheSetChunkReader implements ChunkReader<ResultSet> {
	
	public RowCacheSetChunkReader() {
	}

	@Override
	public ResultSet read(File file) {
		try (InputStream stream = new BufferedInputStream(new FileInputStream(file));
				ObjectInputStream ois = new ObjectInputStream(stream)) {
			return (CachedRowSet) ois.readObject();
		} catch (Exception e) {
			throw new EfwServiceException("Failed to read chunk : " + file.getAbsolutePath(),e);
		}
	}

}
