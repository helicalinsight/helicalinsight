package com.helicalinsight.export.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.export.utils.ResourceFileUtils;

@Component
public class ResourceDataReader {

	@Autowired
	private ResourceFileUtils fileUtils;
	
	public  <T> T read(ImportManagerContext context , String resourceUrl , Class<T> clazz) {
		resourceUrl = context.removeDestination(resourceUrl);
		String fileName = resourceUrl.contains("/") ? StringUtils.substringAfterLast(resourceUrl, "/") : resourceUrl;
		String path =  context.getResourcesDirectory() +  ( Integer.valueOf(context.getManifest().getVersion()) > 0 ? resourceUrl : fileName );
		return   fileUtils.readFile(path, clazz);
	}
}
