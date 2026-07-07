package com.helicalinsight.export.handler;

import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.resourcedb.HIResourceDTO;

/**
 * This abstract class serves as the base class for handling the writing of resources in the Helical Insight export module.
 * It provides common functionality and dependencies that can be shared across different resource writer handlers.
 * Classes extending this abstract class must implement the {@code write} method, which defines the logic for writing a 
 * specific type of resource.
 *
 */
public abstract class AbstractResourceWriterHandler {

	@Autowired
	protected HIResourceServiceDB serviceDB;


	@Autowired
	protected HIResourceConstituentMappingService pathService;


	@Autowired
	@Qualifier("userDetailsService")
	protected UserService userService;
	@Autowired
	protected HIMetadataResourceServiceDB mdServiceDB;
	
	@Autowired
	protected ResourceDataWriter dataWriter;
	
	@Autowired
	protected ResourceDTOMapper dtoMapper;

	@Autowired
	protected JsonMapperUtils jsonMapperUtils;
	
	@Autowired
	protected ManifestUtils manifestUtils;

	/**
     * Abstract method to be implemented by classes extending this abstract class.
     * It defines the logic for writing a specific type of resource.
     *
     * @param resource 		HIResourceDTO object representing the resource.
     * @param dir      		directory path where the resource is to be written.
     * @param manifest 		manifest object containing resource metadata.
     * @param options  		options for writing the resource.
     */
	public abstract void write(HIResourceDTO resource, String dir, Manifest manifest,ResourceOptions options);
	/**
     * Adds the owner information to the JSON object.
     *
     * @param object   	object to which owner information is to be added.
     * @param ownerId   ID of the owner.
     * @return The object with owner information.
     */
	public ObjectNode addOwner(Object object , Integer ownerId) {
		ObjectNode objectNode =  JacksonUtility.mapToJson(object);
		if(objectNode.has("createdBy") && ownerId != null) {
			User owner = userService.findUser(ownerId);
			objectNode.putPOJO("createdBy",owner);
		}
		return objectNode;
	}
	/**
     * Shares the resource based on specified options.
     *
     * @param resource 		HIResourceDTO object containing files of resource, resourceId.
     * @param manifest 	    manifest object containing resource metadata.
     * @param options       options for writing the resource.
     * @param dir           directory path where the resource is to be written.
     */
	public void share(HIResourceDTO resource, Manifest manifest, ResourceOptions options, String dir) {
		if (null != options.getShare() && options.getShare()) {
			ResourceIOHandler shareHandler = ApplicationContextAccessor.getBean(ShareHandler.class);
			shareHandler.write(resource, dir, manifest);
		}
	}
	/**
     * Schedules the resource based on specified options.
     *
     * @param resource 		HIResourceDTO object representing the resource.
     * @param manifest 		manifest object containing resource metadata.
     * @param options  		options for writing the resource.
     * @param dir      		directory path where the resource is to be written.
     */
	public void schedule(HIResourceDTO resource, Manifest manifest, ResourceOptions options, String dir) {
		if (null != options.getSchedules() && options.getSchedules()) {
			ResourceIOHandler scheduleHandler = (ResourceIOHandler) ApplicationContextAccessor
					.getBean("scheduleIOHandler");
			scheduleHandler.write(resource, dir, manifest);
		}
	}

}
