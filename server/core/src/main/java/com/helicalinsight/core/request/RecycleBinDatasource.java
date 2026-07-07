package com.helicalinsight.core.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(Include.NON_NULL)
public class RecycleBinDatasource implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private boolean deleted;
	private int connectionId;
	private String directory;

}
