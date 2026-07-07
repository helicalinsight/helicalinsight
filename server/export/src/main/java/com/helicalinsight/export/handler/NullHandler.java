package com.helicalinsight.export.handler;

import org.springframework.stereotype.Component;

import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * NullHandler implementation of AbstractResourceWriterHandler.
 * This handler does nothing when the write method is called.
 * It can be used as a fallback handler when no specific handler is available for a given resource type.
 */
@Component
public class NullHandler extends AbstractResourceWriterHandler   {
	/**
     * Does nothing when called. This is a placeholder implementation for cases where no specific handler is available.
     * 
     * @param resource 			The resource to be written.
     * @param dir 				directory in which to write the resource.
     * @param manifest 			manifest associated with the export.
     * @param options 			Additional options for the resource export.
     */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest, ResourceOptions options) {
		return ;
	}

}
