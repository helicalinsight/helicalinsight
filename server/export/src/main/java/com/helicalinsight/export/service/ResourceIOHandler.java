package com.helicalinsight.export.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * Abstract class that provides common functionality for importing and exporting resources.
 */
public abstract class ResourceIOHandler {

	@Autowired
	protected HIResourceServiceDB serviceDb;
	@Autowired
	protected ResourceDataWriter dataWriter;
	@Autowired
	@Qualifier("userDetailsService")
	protected UserService userService;
	@Autowired
	protected OrganizationService orgService;
	@Autowired
	protected RoleService roleService;

	@Autowired
	protected JsonMapperUtils mapperUtils;

	@Autowired
	protected ManifestUtils manifestUtils;
	@Autowired
	protected ResourceFileUtils fileUtils;
	
	@Autowired
	protected JsonMapperUtils jsonMapperUtils;
	
	@Autowired
	protected ResourceShareUtils shareUtils;
	
	protected ImportManagerContext context;
	
	@Autowired
	protected ShareHandler shareHandler;
	
	@Autowired
	protected ResourceDTOMapper dtoMapper;

	/**
     * Writes the resource data to a file.
     *
     * @param resource 		resource provides id to get metadata and file name.
     * @param dir      	    directory where the resource data will be exported.
     * @param manifest      manifest file to store data source related info.
     */
	public abstract void write(HIResourceDTO resource, String dir, Manifest manifest);
	/**
     * Imports the resource data from a file.
     *
     * @param resource   		resource to be imported.
     * @param fileName   		name of the file containing resource data.
     * @param onConflict 		conflict resolution strategy.
     */
	public abstract void importResource(HIResource resource, String fileName, String onConflict);
	
	 /**
     * Sets the context for the import manager.
     * @param context 		instance of ImportManagerContext.
     */
	public final void setContext(ImportManagerContext context) {
		this.context = context;
	}

}
