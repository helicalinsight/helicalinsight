package com.helicalinsight.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.helicalinsight.admin.dto.HIMetadataConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataSecurityDTO;
import com.helicalinsight.resourcedb.HIResourceDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HIResourceMetadataDTO implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private String type;
    private String databaseType;
    private String fileName;
    private String connectionType;
    private Boolean isCached;
    private Date lastUpdatedTime;
    private Integer createdBy;
    private HIResourceDTO hiResource;
    private List<HIMetadataConnectionDTO> hiMetadataConnections = new ArrayList<>();
    private List<HIMetadataSecurityDTO> metadataSecurityList = new ArrayList<>();

}
