package com.helicalinsight.efw.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

public class CutCopyFileInfo extends FileInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private List<CutCopyFileInfo> children;
	
	public CutCopyFileInfo() {
		// No Args Constructor.
	}
	
	
	public List<CutCopyFileInfo> getChildren() {
		return children;
	}

	public void setChildren(List<CutCopyFileInfo> children) {
		this.children = children;
	}
	


}
