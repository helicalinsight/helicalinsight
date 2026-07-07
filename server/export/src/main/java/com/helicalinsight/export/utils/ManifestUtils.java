package com.helicalinsight.export.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ManifestException;
import com.helicalinsight.resourcedb.HIResourceDTO;

/**
 * Utility class for handling Manifest files.
 */
@Component
public class ManifestUtils {

	@Autowired
	private ResourceFileUtils fileUtils;
	/**
     * Writes the manifest object to a `Manifest.json` file in the specified directory.
     *
     * @param manifest 		manifest object to be written.
     * @param dir      		directory in which the Manifest.json file will be created.
     */
	public void writeManifest(Manifest manifest, String dir) {
		try (FileOutputStream fos = new FileOutputStream(String.join(File.separator, dir, "Manifest.json"))) {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer();
			writer.writeValue(fos, manifest);
		} catch (IOException e) {
			throw new ManifestException("Error occurred : " + e.getLocalizedMessage());
		}
	}
	/**
     * Reads the `Manifest.json` file from the specified directory and returns a Manifest object.
     *
     * @param dir The directory containing the Manifest.json file.
     * @return The Manifest object.
     */
	public Manifest readManifest(String dir) {
		return fileUtils.readFile(String.join(File.separator, dir, "Manifest.json"), Manifest.class);
	}
	/**
     * Inserts a resource path into the manifest object.
     *
     * @param path     		resource path to be inserted.
     * @param manifest 		manifest object.
     */
	public void insertPath(String path, Manifest manifest) {
			manifest.getResourcePaths().add(path);
		
	}
	/**
     * Inserts a dependency into the manifest object.
     *
     * @param key      		key for the dependency.
     * @param value    		value for the dependency.
     * @param manifest The manifest object.
     */
	public void insertDependency(String key, String value, Manifest manifest) {
			value = StringUtils.reverseDelimited(value, ',');
			String[] valArr = value.split(",");
			for(int i=0;i<valArr.length;i++) {
				String str = valArr[i];
				String extension = FilenameUtils.getExtension(str);
				if( extension == null || "".equalsIgnoreCase(extension)) {
					valArr[i] = str + ".efwfolder";
				}
			}
			manifest.getDependencies().put(key, Arrays.asList(valArr));
			
	}

	
	public void insertShare(HIResourceDTO resource, Manifest manifest) {
		String value = (Integer.valueOf(manifest.getVersion()) > 0 ? resource.getPath() : resource.getName() ) + ResourceSuffix.SHARE;
		manifest.getShares().put(resource.getPath(), value);
	}
	

	
	public void insertDatasource(HIResourceDTO resource, Manifest manifest) {
		String value = (Integer.valueOf(manifest.getVersion()) > 0 ? resource.getPath() : resource.getName() ) + ResourceSuffix.DATASOURCE;
		manifest.getDataSources().put(resource.getPath(), value);
	}
	public void insertDatasourceEFWD(HIResourceDTO resource, Manifest manifest) {
		String value = (Integer.valueOf(manifest.getVersion()) > 0 ? resource.getPath() : resource.getName() ) + ResourceSuffix.DATASOURCE_EFWD;
		manifest.getDataSources().put(resource.getPath()+"efwd", value);
	}
	
	
	
	public void insertSchedules(HIResourceDTO resource, Manifest manifest) {
		String value = (Integer.valueOf(manifest.getVersion()) > 0 ? resource.getPath() : resource.getName() ) + ResourceSuffix.SCHEDULE;
		manifest.getSchedules().put(resource.getPath(), value);
	}
	

	public List<String> getDependency(String key, Manifest manifest) {
		return manifest.getDependencies().get(key);
	}
	/**
     * Returns the share value for a given key from the manifest object.
     *
     * @param key      		key for the share.
     * @param manifest 		manifest object.
     * @return The share value.
     */
	public String getShare(String key, Manifest manifest) {
			return manifest.getShares().get(key);
	}
	/**
     * Returns the datasource value for a given key from the manifest object.
     *
     * @param key      		key for the datasource.
     * @param manifest The manifest object.
     * @return The datasource value.
     */
	public String getDatasource(String key, Manifest manifest) {
			return manifest.getDataSources().get(key);
	}
	/**
     * Checks if a dependency exists for a given key in the manifest object.
     *
     * @param key      		key for the dependency.
     * @param manifest 		manifest object.
     * @return {@code true} if the dependency exists, {@code false} otherwise.
     */
	public boolean dependencyExists(String key, Manifest manifest) {
		return  !manifest.getDependencies().containsKey(key) ? false : true;
	}
	/**
     * Checks if a resource path exists in the manifest object.
     *
     * @param path     		resource path.
     * @param manifest 		manifest object.
     * @return {@code true} if the resource path exists, {@code false} otherwise.
     */
	public boolean pathExists(String path, Manifest manifest) {
		return !manifest.getResourcePaths().contains(path) ? false : true;
	}
	/**
     * Compares resource options from the request with the manifest.
     *
     * @param rOptions 		resource options from the request.
     * @param manifest 		manifest object.
     * @param option   		specific resource option to compare (e.g., "share", "datasource").
     * @return {@code true} if the options match, {@code false} otherwise.
     */
	public boolean compareOptions(ResourceOptions rOptions, Manifest manifest, String option) {
		switch (option.toLowerCase()) {
		case "share":
			if (rOptions.getShare() != null && rOptions.getShare()) {
				ResourceOptions mOptions = manifest.getOptions();
				if (mOptions != null) {
					return rOptions.getShare().equals(mOptions.getShare());
				}
			} else
				return false;

		case "datasource":
			if (rOptions.getDataSource() != null && rOptions.getDataSource()) {
				ResourceOptions mOptions = manifest.getOptions();
				if (mOptions != null) {
					return mOptions.getDataSource().equals(rOptions.getDataSource());
				}
			} else
				return false;

		default:
			return false;

		}
	}
}
