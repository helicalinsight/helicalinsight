package com.helicalinsight.export.handler.importres;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.Deleted;
/**
 * Handles the import of cube metadata resources.
 *
 * This class is responsible for importing cube metadata resources from a specified file. It reads the cube metadata
 * from the file, determines the import mode (create, edit, or skip), and processes the import accordingly.
 *
 * The cube metadata includes information about dimensions, hierarchies, levels, measures, and other cube-related
 * properties. The import process involves creating or updating the cube metadata, its associated resource, and related
 * database entities.
 *
 * The cube import handler is designed to be a prototype-scoped component, allowing for multiple instances to handle
 * concurrent imports independently.
 */
@Component("cubeImportHandler")
@Scope("prototype")
public class CubeImportHandler extends AbstractResourceImportHandler {
	
	
	
	@Autowired
	private HICubeDAOService cubeService;
	
	private HIMetadataCube newCube;
	/**
     * Imports a cube metadata resource.
     *
     * @param resourceUrl 		URL of the cube metadata resource to import.
     * @return The imported HIResource representing the cube metadata.
     */
	@Override
	public HIResource importResource(String resourceUrl) {
		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		String onConflict = context.getRequest().getOnConflict();
		HIMetadataCube  cube = fileReader.read(context, resourceUrl, HIMetadataCube.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl,Deleted.FALSE);
		if (null != resource) {
			String mode = onConflict;
			if ("skip".equalsIgnoreCase(mode)) {
				context.appendSkip(resource.getResourceURL());
			} else if(context.recover(resource)) {
				HIMetadataCube existingCube = cubeService.findCubeByResourceId(resource.getResourceId());
				resource = createCube(existingCube, parentUrl, "edit");
				context.appendUpdate(resource.getResourceURL());
			}
		} else {
			newCube = new HIMetadataCube();
			resource = createCube(cube, parentUrl, "create");
			context.appendInsert(resource.getResourceURL());
		}
		return resource;
	}
	/**
     * Creates or updates a cube metadata resource based on the import mode.
     *
     * @param oldCube    The existing cube metadata, if any.
     * @param parentUrl  The URL of the parent resource.
     * @param mode       The import mode (create, edit, or skip).
     * @return The HIResource representing the cube metadata.
     */
	public HIResource createCube(HIMetadataCube oldCube, String parentUrl , String mode) {
		boolean isEdit = "edit".equalsIgnoreCase(mode);
		HIMetadataCube cube  =  isEdit ? oldCube : getNewCube(oldCube);
		String mdResourceUrl = cube.getHiResourceMetadata().getHiResource().getResourceURL();
		cube.setHiResource(createResource(cube,parentUrl,isEdit));
		HIResource mdResource = context.getResourceUrlMap().get(mdResourceUrl);
		HIResourceMetadata metadata = mdServiceDb.giveHIResourceMetadataByResourceId(mdResource.getResourceId());
		cube.setHiResourceMetadata(metadata);
		if(isEdit) cubeService.edit(cube);
		else {
			cubeService.addCube(cube);
			newCube = cube;
			cube.setDimensions(oldCube.getDimensions());
			cube.setMeasures(oldCube.getMeasures());
		}
		cube.setDimensions(saveDimensions(cube,mdResourceUrl,isEdit));
		cube.setMeasures(saveMeasures(cube,mdResourceUrl,isEdit));
		return cube.getHiResource();
	}
	/**
     * Creates a new cube metadata instance based on an existing one.
     *
     * @param oldCube 		The existing cube metadata.
     * @return A new cube metadata instance with the same property values.
     */
	private HIMetadataCube getNewCube(HIMetadataCube oldCube) {
		HIMetadataCube cube = new HIMetadataCube();
		cube.setCache(oldCube.getCache());
		cube.setCaption(oldCube.getCaption());
		cube.setCubeId(oldCube.getCubeId());
		cube.setDefaultMeasure(oldCube.getDefaultMeasure());
		cube.setDescription(oldCube.getDescription());
		cube.setEnabled(oldCube.getEnabled());
		cube.setName(oldCube.getName());
		cube.setVisible(oldCube.getVisible());
		cube.setHiResource(oldCube.getHiResource());
		cube.setDomainName(oldCube.getDomainName());
		cube.setHiResourceMetadata(oldCube.getHiResourceMetadata());
		return cube;
	}
	/**
     * Saves or updates the dimensions of a cube metadata.
     *
     * @param cube         		cube metadata instance.
     * @param mdResourceUrl 	URL of the associated metadata resource.
     * @param isEdit       		Indicates whether the import mode is edit.
     * @return The list of saved or updated cube dimensions.
     */
	private List<HICubeDimension>  saveDimensions(HIMetadataCube cube,String mdResourceUrl,boolean isEdit) {
		
		List<HICubeDimension> dimensionList = cube.getDimensions();
		Map<Integer, Integer> tableIdMap = context.getTableIdMap(mdResourceUrl);
		Map<Integer, Integer> columnIdMap = context.getColumnIdMap(mdResourceUrl);
		for (HICubeDimension eachDimension : dimensionList) {
			if(!isEdit) {
				eachDimension.setId(null);
				eachDimension.setHiMetadataCube(newCube);
				eachDimension.setColumnId("" + columnIdMap.get(Integer.valueOf(eachDimension.getColumnId())));
				eachDimension.setTableId("" + tableIdMap.get(Integer.valueOf(eachDimension.getTableId())));
				cubeService.add(eachDimension);
			}			
			else
				cubeService.edit(eachDimension);
			
			List<HIDimensionHierarchy>  hierarchyList = eachDimension.getHierarchies();
			for(HIDimensionHierarchy eachHierarchy : hierarchyList) {
				eachHierarchy.setHiCubeDimension(eachDimension);
				eachHierarchy.setPrimaryKeyColumnId(eachDimension.getColumnId());
				if(isEdit) cubeService.edit(eachHierarchy);
				else {
					eachHierarchy.setId(null);
					eachHierarchy.setTableId("" + tableIdMap.get(Integer.valueOf(eachHierarchy.getTableId())));
					cubeService.add(eachHierarchy);
				}
				List<HICubeHierarchyLevel> levels = eachHierarchy.getLevels();
				for(HICubeHierarchyLevel eachLevel : levels) {
					
					eachLevel.setHiDimensionHierarchy(eachHierarchy);
					if (isEdit) cubeService.edit(eachLevel);
					else {
						eachLevel.setColumnId("" + columnIdMap.get(Integer.valueOf(eachLevel.getColumnId())));
						eachLevel.setTableId("" + tableIdMap.get(Integer.valueOf(eachLevel.getTableId())));
						eachLevel.setId(null);
						cubeService.add(eachLevel);
					}
				}
			}
		}
		return dimensionList;
	}
	/**
     * Saves the measures of a cube metadata.
     *
     * @param cube         		cube metadata instance.
     * @param mdResourceUrl 	URL of the associated metadata resource.
     * @param isEdit       		Indicates whether the import mode is edit.
     * @return The list of saved or updated cube measures.
     */
	private List<HICubeMeasure> saveMeasures(HIMetadataCube cube,String mdResourceUrl,boolean isEdit){
		
		List<HICubeMeasure> measureList =  cube.getMeasures();
		Map<Integer,Integer> tableIdMap = context.getTableIdMap(mdResourceUrl);
		Map<Integer,Integer> columnIdMap = context.getColumnIdMap(mdResourceUrl);
		for(HICubeMeasure measure : measureList) {
			if(!isEdit) {
				measure.setId(null);
				measure.setMetadataCube(newCube);
				measure.setTableId(""+tableIdMap.get(Integer.valueOf(measure.getTableId())));
				measure.setColumnId(""+columnIdMap.get(Integer.valueOf(measure.getColumnId())));
				cubeService.add(measure);
			}
			else cubeService.edit(measure);
		}
		return measureList;
		
	} 
	/**
     * Creates  the associated HIResource for the cube metadata.
     *
     * @param cube    		cube metadata instance.
     * @param parentUrl 	URL of the parent resource.
     * @param isEdit  		Indicates whether the import mode is edit.
     * @return The HIResource representing the cube metadata.
     */
	public HIResource createResource(HIMetadataCube cube, String parentUrl, boolean isEdit) {
		HIResource resource =  cube.getHiResource();
		HIResource parent = context.getResourceUrlMap().get(parentUrl+"."+JsonUtils.getFolderFileExtension());
		resource.setParentId(parent.getResourceId());
		resource.setLastUpdatedTime(context.getDate());
		resource.setCreated_date(context.getDate());
		if(isEdit) serviceDb.editHIResource(resource);
		else serviceDb.addHIResource(resource);
		return resource;
	}

}
