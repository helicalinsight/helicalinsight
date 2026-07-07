package com.helicalinsight.core.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private int id;
	private boolean deleted;

}
