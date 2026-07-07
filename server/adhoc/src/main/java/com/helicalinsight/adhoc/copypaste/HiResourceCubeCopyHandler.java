package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * Class HiResourceCubeCopyHandler extends {@link HiResourceCopyHandler} .
 * This class handles the copying of cube resources along with their associated dimensions, 
 * hierarchies, levels, and measures. It ensures that all dependent entities are properly copied and 
 * associated with the new cube resource
 */
@Component
public class HiResourceCubeCopyHandler extends HiResourceCopyHandler{

	@Autowired
	HIResourceServiceDB hiResourceServiceDB;
	
	@Autowired
	HiResourceCCPUtility hiResourceCCPUtility;
	
	@Autowired
	HICubeDAOService hiCubeDAOService;
	
	/**
     * Copies the resource, ensuring uniqueness and handling associated data.
     */
	@Override
	public void copyResource() {
		
		if(getSource().getParentId()!=getDestinationResourceId().getResourceId()) {
			HIResource isSameResouceNameAlreadyExisted=hiResourceServiceDB.getResourceByUrl(getPrefix(),Boolean.FALSE);
			if(isSameResouceNameAlreadyExisted!=null && !isSameResouceNameAlreadyExisted.getDeleted()) {
				if(!getOnConflictSkip()) {
					String sourcePath=UUIDGenerator.getUuid();
					String prefixUrl=getDestinationResourceId().getResourceURL()+"/"+sourcePath+getSource().getResourceType().getExtension();
					HIResource hiResource=doCopy(prefixUrl,sourcePath);
					hiResourceCCPUtility.deleteOverridenResourceAndUpdateCopiedResource(hiResource, isSameResouceNameAlreadyExisted);
				}
			}
			else {
				if(isSameResouceNameAlreadyExisted!=null) {
					Format secondsFormat = new SimpleDateFormat("ss");
					String generatedSourcePath=DBProcessor.checkAndReplaceSpecialChars(getSourcePath()).trim() +
							"_"+ secondsFormat.format(new Date()).substring(0, 2);
					String generatedUrl=getPrefix().substring(0,getPrefix().length()-getSourcePath().length())+generatedSourcePath;
					doCopy(generatedUrl,generatedSourcePath);
				}
				else
					doCopy(getPrefix(),getSourcePath());
			}
		}
		else
			doCopy(getPrefix(),getSourcePath());
	}
	/**
     * Performs the copy operation, prepares a replica, sets properties,
     * adds to the database, and handles security.
     *
     * @param prefixUrl  Prefix URL for the new replica.
     * @param sourcePath Source path for the new replica.
     * @return The copied HIResource object.
     */
	private HIResource doCopy(String prefixUrl, String sourcePath) {
		HIResource hiResource=hiResourceCCPUtility.prepareNewReplica(getSource(), getDestinationResourceId(), prefixUrl, sourcePath);
		hiResourceServiceDB.addHIResource(hiResource);
		hiResourceCCPUtility.saveSecurityInfoReplica(getSource().getResourceId(), hiResource);
		copyAllDependents(hiResource);
		if(getSource().getDeleted())
			hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());
		return hiResource;
	}
	/**
     * This method copies all the dependent Cube entities associated with the cube  resource.
     * It retrieves existing cubes associated with the source resource and iterates over them.
     * For each cube, it copies dimensions, dimension hierarchies, hierarchy levels, and measures to the new cube.
     * It creates new instances of these entities and associates them with the newly created cube.
     * @param createdHiResource The newly created HIResource object.
     */
	private void copyAllDependents(HIResource createdHiResource) {
		
		List<HIMetadataCube> existingCubes=hiCubeDAOService.findAllCubeWithResourceId(getSource().getResourceId());
		existingCubes.forEach(cube->{
			
			HIMetadataCube hiMetadataCube=HiResourceCCPUtility.prepareEntity(cube,HIMetadataCube.class);
			hiMetadataCube.setDimensions(null);
			hiMetadataCube.setHiResource(createdHiResource);
			hiMetadataCube.setMeasures(null);
			hiMetadataCube.setHiResourceMetadata(cube.getHiResourceMetadata());
			hiCubeDAOService.addCube(hiMetadataCube);
			
			List<HICubeDimension> existingCubeDimensions=hiCubeDAOService.findAllCubeDimentions(cube.getId());
			existingCubeDimensions.forEach(dim->{
				
				HICubeDimension hiCubeDimension=HiResourceCCPUtility.prepareEntity(dim,HICubeDimension.class);
				hiCubeDimension.setHierarchies(null);
				hiCubeDimension.setHiMetadataCube(hiMetadataCube);
				hiCubeDimension.setId(null);
				hiCubeDAOService.add(hiCubeDimension);
				
				List<HIDimensionHierarchy> existingCubeDimHierarchy=hiCubeDAOService.findAllDimHierarchy(dim.getId());
				existingCubeDimHierarchy.forEach(dimHir->{
					
					HIDimensionHierarchy hiDimensionHierarchy=HiResourceCCPUtility.prepareEntity(dimHir,HIDimensionHierarchy.class);
					hiDimensionHierarchy.setHiCubeDimension(hiCubeDimension);
					hiDimensionHierarchy.setLevels(null);
					hiCubeDAOService.add(hiDimensionHierarchy);
					
					
					List<HICubeHierarchyLevel> existinCubHirLevels=hiCubeDAOService.findAllHierarchyLevels(dimHir.getId());
					existinCubHirLevels.forEach(hirLevel->{
						HICubeHierarchyLevel hiCubeHierarchyLevel=HiResourceCCPUtility.prepareEntity(hirLevel,HICubeHierarchyLevel.class);
						hiCubeHierarchyLevel.setHiDimensionHierarchy(hiDimensionHierarchy);
						hiCubeHierarchyLevel.setId(null);
						hiCubeDAOService.add(hiCubeHierarchyLevel);
					});
				});
				
			});
			
			List<HICubeMeasure> existingCubeMeasures=hiCubeDAOService.findAllCubeMeasuresByCubeId(cube.getId());
			existingCubeMeasures.forEach(cubeMes->{
				HICubeMeasure hiCubeMeasure=HiResourceCCPUtility.prepareEntity(cubeMes,HICubeMeasure.class);
				hiCubeMeasure.setId(null);
				hiCubeMeasure.setMetadataCube(hiMetadataCube);
				hiCubeDAOService.add(hiCubeMeasure);
			});
			
		});
	}

}
