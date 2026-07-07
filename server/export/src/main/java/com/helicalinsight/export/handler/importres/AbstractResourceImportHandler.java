package com.helicalinsight.export.handler.importres;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;

/**
 * This abstract class serves as the base class for handling the import of resources in the Helical Insight export module.
 * It provides common functionality and dependencies that can be shared across different resource import handlers.
 * Classes extending this abstract class must implement the {@code importResource} method, which defines the logic for
 * importing a specific type of resource.
 *
 */
public abstract class AbstractResourceImportHandler {
	
	protected   ImportManagerContext context = null; 


	@Autowired
	protected HIResourceServiceDB serviceDb;

	@Autowired
	protected HIResourceConstituentMappingService pathService;

	@Autowired
	@Qualifier("userDetailsService")
	protected UserService userService;
	
	@Autowired
	protected ShareHandler shareHandler;

	@Autowired
	protected HIMetadataResourceServiceDB mdServiceDb;

	@Autowired
	protected JsonMapperUtils mapperUtils;

	@Autowired
	protected ResourceTypeServiceDB resourceTypeService;

	@Autowired
	protected ManifestUtils manifestUtils;
	
	@Autowired
	protected ResourceFileUtils fileUtils;
	
	@Autowired
	protected ResourceDataReader fileReader;
	
	@Autowired
	protected ResourceDTOMapper mapper;

	/**
     * Abstract method to be implemented by classes extending this abstract class.
     * It defines the logic for importing a specific type of resource.
     *
     * @param fileUrl 		URL of the file to be imported.
     * @return The imported HIResource object.
     */
	public abstract  HIResource importResource(String fileUrl );
	
	/**
     * Sets the context for the import operation.
     *
     * @param context 			ImportManagerContext object containing import-related information.
     * @return The current instance of the AbstractResourceImportHandler.
     */
	public AbstractResourceImportHandler setContext(ImportManagerContext context) {
		this.context = context;
		fileUtils.setRequest(this.context.getRequest());
		return this;
	}

}
