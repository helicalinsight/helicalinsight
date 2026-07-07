package com.helicalinsight.export.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceOptions implements Serializable {


	private static final long serialVersionUID = 1L;
	private Boolean schedules;
	private Boolean dataSource;
	private Boolean share;

	public Boolean getShare() {
		return share;
	}

	public void setShare(Boolean share) {
		this.share = share;
	}

	public Boolean getSchedules() {
		return schedules;
	}

	public Boolean getDataSource() {
		return dataSource;
	}

	public void setSchedules(Boolean schedules) {
		this.schedules = schedules;
	}

	public void setDataSource(Boolean dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public String toString() {
		return "{\"schedules\":\"" + schedules + "\", \"dataSource\":\"" + dataSource + "\", \"share\":\"" + share
				+ "\"}";
	}

}
