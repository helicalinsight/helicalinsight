package com.helicalinsight.export.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResponse{


	
	private String message;
	private int insertCount;
	private int updateCount;
	private int skipCount;
	private List<String> updates;
	private List<String> skips ;
	private List<String> inserts;
	
	public ImportResponse() {
		this.updates = new ArrayList<>();
		this.skips = new ArrayList<>();
		this.inserts = new ArrayList<>();
	}


	public String getMessage() {
		return message;
	}

	public int getInsertCount() {
		return insertCount;
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public List<String> getUpdates() {
		return updates;
	}

	public List<String> getSkips() {
		return skips;
	}

	public List<String> getInserts() {
		return inserts;
	}


	public void setMessage(String message) {
		this.message = message;
	}

	public void setInsertCount(int insertCount) {
		this.insertCount = insertCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public void setUpdates(List<String> updates) {
		this.updates = updates;
	}

	public void setSkips(List<String> skips) {
		this.skips = skips;
	}

	public void setInserts(List<String> inserts) {
		this.inserts = inserts;
	}


	@Override
	public String toString() {
		return "{\"message\":\"" + message + "\", \"insertCount\":\"" + insertCount + "\", \"updateCount\":\""
				+ updateCount + "\", \"skipCount\":\"" + skipCount + "\", \"updates\":\"" + updates + "\", \"skips\":\""
				+ skips + "\", \"inserts\":\"" + inserts + "\"}";
	}

	
	
	
	
	

}
