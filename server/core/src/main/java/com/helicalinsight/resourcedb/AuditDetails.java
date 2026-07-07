package com.helicalinsight.resourcedb;

import java.util.Date;
import java.util.List;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;

import lombok.Data;

@Data
public class AuditDetails {
	private String action;
	private Date startTime;
	private Date endTime;
	private HIResource hiResource;
	private User user;
	private String status;
	private String type;
	private String description;
	private List<String> dbNames;
}
