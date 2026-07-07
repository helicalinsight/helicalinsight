package com.helicalinsight.core.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.helicalinsight.admin.enums.RecycleBinType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Data
public class RecycleBinItem {
	
	private Long recycleBinId;
	private String recycleBinType;
	private String deletedBy;
	private Date deletedOn;
	private Data data;
	
	@JsonInclude(Include.NON_NULL)
	@Setter
	@Getter
	public static class Data {
		private Integer id;
		private String name;
		private String path;
		private String dir;
		private Object ownerId;
	}
	
}
