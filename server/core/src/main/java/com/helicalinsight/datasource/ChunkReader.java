package com.helicalinsight.datasource;
import java.io.File;

public interface ChunkReader<T> {
	T read(File file);
}