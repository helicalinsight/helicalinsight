package com.helicalinsight.datasource;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.RowSetProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChunkIterator<T> implements Iterator<T> {
	private static final Logger logger = LoggerFactory.getLogger(ChunkIterator.class);
	private static final Pattern CHUNK_PATTERN = Pattern.compile("^chunk_(\\d+).*");

	private  File[] chunkFiles;
	private final ChunkReader<T> chunkReader;

	private final int from;
	private final int to;

	private int cursor;
	private final File directory;
	
	private static final int MAX_RETRY = 500;
	
	private boolean completed = false;
	
	private final File singleFile;
	private boolean streamingMode = true;
	
	public ChunkIterator(File directory, ChunkReader<T> chunkReader) {
		this(directory, chunkReader, 0, Integer.MAX_VALUE);
	}

	public ChunkIterator(File directory, ChunkReader<T> chunkReader, int startIdx, int endIdx) {

		if (directory == null ) {
			throw new IllegalArgumentException("Invalid chunk directory : " + directory);
		}
		
		if (startIdx < 0 || endIdx < startIdx) {
			throw new IllegalArgumentException("Invalid range : startIdx=" + startIdx + ", endIdx=" + endIdx);
		}
		
		this.directory = directory;
		
		File[] files  = null;
		
		if (!directory.isDirectory()) {
			files = new File[] {directory};
			singleFile = files[0];
			streamingMode = false;
		}
		else {
			singleFile = null;
            files = directory.listFiles(f -> f.isFile() && f.getName().startsWith("chunk_") && !f.getName().endsWith(".tmp"));
		}
		
		this.chunkReader = chunkReader;

		if (files == null) {
			throw new IllegalStateException("Failed to list chunk files in " + directory);
		}
		
		
		Arrays.sort(files, this::compareChunkFiles);
		this.chunkFiles = files;
		this.from = Math.min(startIdx, chunkFiles.length);
		this.to = Math.min(endIdx, chunkFiles.length);
		this.cursor = this.from;
	}

	private int compareChunkFiles(File f1, File f2) {
		return Integer.compare(extractChunkIndex(f1), extractChunkIndex(f2));
	}

	private int extractChunkIndex(File file) {
		Matcher matcher = CHUNK_PATTERN.matcher(file.getName());
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group(1));
		}
		throw new IllegalArgumentException("Invalid chunk file " + file.getName());
	}

	private File chunkFile(int index) {
		return new File(directory, "chunk_" + index + ".cache");
	}

	private File completeFile() {
		return new File(directory, ".cache_complete");
	}
	
	 @Override
	    public boolean hasNext() {
		 
		 	if (completed) {
		 		return false;
		 	}
		 	
		 	 if (!streamingMode) {
			        return singleFile != null && singleFile.exists();
			 }
		 	
	        if (chunkFile(cursor).exists()) {
	            return true;
	        }

	        if (completeFile().exists()) {
	        	completed = true;
	            return false;
	        }
	        return true;
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public T next() {
	    	
	    	if (!hasNext())  throw new NoSuchElementException();
	    	
	    	if (!streamingMode) {
	            try {
	                T data = chunkReader.read(singleFile);
	                completed = true;
	                return data;
	            } catch (Exception e) {
	                throw new RuntimeException(e);
	            }
	        }
	    	
	        int retry = 0;

	        while (retry < MAX_RETRY) {

	            File file = chunkFile(cursor);

	            if (file.exists() && file.length() > 0) {
	                try {
	                    T data = chunkReader.read(file);
	                    cursor++;
	                    return data;
	                } catch (Exception e) {
	                    throw new RuntimeException("Failed reading chunk: " + file, e);
	                }
	            }
	            
	            

	            if (completeFile().exists()) {
	            	if (file.exists() && file.length() > 0) {
	            		continue;
	            	}
	            	completed = true;
	            	return empty();
	            }

	            try {
	                Thread.sleep(20); // streaming wait
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                throw new NoSuchElementException();
	            }
	            logger.debug("Retrying for chunk :  {} , {} time", file, retry);
	            retry++;
	        }
	        
	        completed = true;
	        
	        return empty();
	    }

	    @SuppressWarnings("unchecked")
	    private T empty() {
	        try {
	            return (T) RowSetProvider.newFactory().createCachedRowSet();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	    }
}
