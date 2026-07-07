package com.helicalinsight.core.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecycleBinResourceItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String path;
	private boolean deleted;
	private int resourceId;

	public RecycleBinResourceItem(String name, boolean deleted) {
		this.name = name;
		this.deleted = deleted;
	}

}
