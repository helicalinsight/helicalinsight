package com.helicalinsight.export.handler;

import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
/**
 * This class handles the writing of Helical Insight folder resources during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing folder resources.
 * The write method is responsible for writing the folder resource, managing dependencies, and handling data source export if required.
 */
@Component("folderWriterHandler")
public class FolderWriterHandler extends AbstractResourceWriterHandler {

	private static final String FOLDER_EXTENSION = ResourceExtension.FOLDER.getValue();
	private static final String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	@Autowired
	private ResourceEfwContentsService resourceEfwContent;
	/*
     * <p>
     * This method writes the folder resource, manages dependencies, and handles data source export if required during the export process.
     * </p>
     *
     * @param resource 		HIResourceDTO object representing the folder resource.
     * @param dir      		directory path where the resource is to be written.
     * @param manifest 		manifest object containing resource metadata.
     * @param options  		options for writing the resource.
     */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest, ResourceOptions options) {
		HIResource parent = null;
		if (!resource.getParentId().equals(0)) { 
			try {
				parent = serviceDB.getResourceByIdIgnoreFilter(resource.getParentId());
			}
			catch (Exception e) {
				return ;
			}
			String parentFilePath = parent.getResourceURL() + FOLDER_EXTENSION;
			if (!manifestUtils.pathExists(parentFilePath, manifest)) {
				HIResourceDTO parentDTO = dtoMapper.map(parent);
				write(parentDTO, dir, manifest, options);
			}
		}

		String uniqueUrlWithExtension = resource.getPath() + FOLDER_EXTENSION;
		HIResourceFolder folder = serviceDB.getHIResourceFolderByResourceId(resource.getResourceId());
		dataWriter.write(this.addOwner(folder,folder.getCreatedBy()), dir,resource,ResourceSuffix.FOLDER);
		manifestUtils.insertPath(uniqueUrlWithExtension, manifest);
		List<ResourceEfwContents> efwdFileContent=resourceEfwContent.fetchResourceEfwContentByResourceId(resource.getResourceId());
		Map<String,List<String>> imageMaps =manifest.getImages();
		if(imageMaps==null){
			imageMaps=new HashMap<>();
		}
		List<String> imageNames = new ArrayList<String>();
		if(efwdFileContent!=null && efwdFileContent.size()>0){
			for(ResourceEfwContents it:efwdFileContent){
				imageNames.add(it.getFileName()+"_hiimg_"+it.getContentType());
				String filePath = TEMPDIR + "/" + dir + "/images/" + resource.getPath() + "/" + it.getFileName();
				File file = new File(filePath);
				Path path = Paths.get(filePath);
                try {
                    Files.createDirectories(path.getParent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
					fos.write(it.getContent());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			imageMaps.put(uniqueUrlWithExtension,imageNames);
		}

		String depPath = substringBeforeLastIfExists(resource.getPath(), "/");
		if (!depPath.isEmpty() && !depPath.equals(uniqueUrlWithExtension)) {
			manifestUtils.insertDependency(uniqueUrlWithExtension,depPath, manifest);
		}
		if (options != null && options.getDataSource()) {
			DatasourceHandler dsHandler = DatasourceFactory.getHandler(resource.getType());
			dsHandler.write(resource, dir, manifest);
		}
		
	}
	/**
     * Utility method to extract substring before the last occurrence of a delimiter if it exists in the given string.
     *
     * @param str   		 input string.
     * @param delim 		 delimiter.
     * @return The substring before the last occurrence of the delimiter, or an empty string if the delimiter is not found.
     */
	private String substringBeforeLastIfExists(String str , String delim) {
		if(!str.contains(delim)) return "";
		return StringUtils.substringBeforeLast(str, delim);
	}
	
}
