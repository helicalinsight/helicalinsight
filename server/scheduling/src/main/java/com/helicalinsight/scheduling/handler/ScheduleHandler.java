package com.helicalinsight.scheduling.handler;

import java.io.File;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.helicalinsight.adhoc.utils.MapperUtils;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.handler.FolderWriterHandler;
import com.helicalinsight.export.handler.importres.FolderImportHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.scheduling.EnhancedScheduleProcessCall;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.SchedulesService;
/**
 * ScheduleHandler class implements {@link ResourceIOHandler}
 * This class handles operations related to scheduling resources. 
 */
@Component("scheduleIOHandler")
@Scope("prototype")
public class ScheduleHandler extends ResourceIOHandler {


	@Autowired
	private SchedulesService scheduleService;

	@Autowired
	private JobParametersService jobParameterService;
	
	@Autowired
	private FolderWriterHandler folderWriterHandler;
	
	@Autowired
	private FolderImportHandler folderImportHandler;

	
	private static final Set<String> SUPPORTED = Set.of("hr","efwdd");


	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest) {

		if(!SUPPORTED.contains(resource.getExtension() != null ? resource.getExtension():"")) {
			return ;
		}
		
		if(manifest.getSchedules().containsKey(resource.getPath())) {
			return ;
		}
		
		List<Schedules> scheduleList = scheduleService.findSchedulesByResourceId(resource.getResourceId());
		if (!scheduleList.isEmpty()) {
				String url = resource.getPath();
				String directory = FilenameUtils.getPathNoEndSeparator(url);
				String reportFile = FilenameUtils.getName(url);
				List<HIResource> efwsrResourceFiles = serviceDb.findAllEfwsrFilesByReportDirAndFile(directory, reportFile);
				if(!efwsrResourceFiles.isEmpty()) {
					dataWriter.write(scheduleList, dir, resource,ResourceSuffix.SCHEDULE);
					dataWriter.write(efwsrResourceFiles, dir, resource, ResourceSuffix.SCHEDULE_RESULT);
					manifestUtils.insertSchedules(resource, manifest);
					efwsrResourceFiles.forEach(file -> {
						shareHandler.write(file, dir, manifest);
						HIResourceDTO efwsrDTO = dtoMapper.map(file);
						String depPath = StringUtils.chop(StringUtils.substringBefore(efwsrDTO.getPath(),efwsrDTO.getName()));
						HIResource folder =  serviceDb.getResourceByUrl(depPath, false);
						if (!manifestUtils.pathExists( depPath + ResourceExtension.FOLDER.getValue(),manifest) && folder != null ) {
							folderWriterHandler.write(dtoMapper.map(folder), dir, manifest, manifest.getOptions());
					}});
				}
			}
	}
	/**
	 * importResource(HIResource resource, String path, String fileName, String onConflict)
	 * Imports a {@link HIResource} using the specified parameters.
	 * @param resource            object provides dependencies
	 * @param path				  path for reading the resource
	 * @param fileName            file name
	 * @param onConflict   		  
	 */
	@Override
	public void importResource(HIResource resource, String fileName, String onConflict) {
		
		String resourcePath = context.getResourcesDirectory();
		fileName = Integer.valueOf(context.getManifest().getVersion()) > 0 ? context.removeDestination(resource.getResourceURL()) : StringUtils.substringAfterLast(fileName, "/");
		
		File file = new File(String.join(File.separator, resourcePath, fileName + ResourceSuffix.SCHEDULE));
		if (!file.exists()) {
			return;
		}
		
		List<Schedules> dbSchedules = scheduleService.findSchedulesByResourceId(resource.getResourceId());
		List<Schedules> schedules = jsonMapperUtils.asList(file,new TypeReference<List<Schedules>>() {});
		
		
		if(!dbSchedules.isEmpty()) {
			dbSchedules.forEach(s -> scheduleService.deleteSchedule(s.getId()));
		}
		
		if(schedules.isEmpty()) {
			return ;
		}
		
		EnhancedScheduleProcessCall enhancedProcessCall = new EnhancedScheduleProcessCall();
		for( Schedules schedule : schedules) {
			schedule.setHIResource(resource);
			User createdBy = resource.getCreatedBy() != null ? userService.findUser(resource.getCreatedBy()) : null;
			Calendar cal = Calendar.getInstance();
			String timezone = cal.getTimeZone().getID();
			schedule.setCreatedBy(createdBy);
			schedule.setTimeZone(timezone);
			scheduleService.addSchedule(schedule);
			List<JobParameters> jobParameters =  schedule.getJobParameters();
			for( JobParameters params : jobParameters ) {
				params.setScheduleIdOfJobParameter(schedule);
				jobParameterService.addJobParameter(params);
			}
			enhancedProcessCall.scheduleSpecificJob(schedule, "");
		}
		List<HIResource> efwsrResources = jsonMapperUtils.asList(new File(String.join(File.separator, resourcePath,
				FilenameUtils.removeExtension(fileName) + ResourceSuffix.SCHEDULE_RESULT)), new TypeReference<List<HIResource>>() {});
		for(HIResource efwsrResource : efwsrResources) {
			String efwsrUrl = context.addDestination(efwsrResource.getResourceURL());
			String parentPath = StringUtils.chop(StringUtils.substringBefore(efwsrUrl, efwsrResource.getResourcePath()));
			parentPath = parentPath+"."+JsonUtils.getFolderFileExtension();
			HIResource existing = serviceDb.getResourceByUrl(efwsrUrl,Deleted.FALSE);
			HIResource parent = context.getResourceUrlMap().get(parentPath);
			if ( existing == null && parent == null ) {
				folderImportHandler.setContext(context);
				parent = folderImportHandler.importResource(parentPath);
			}
			
			boolean canShare = true;
			if( existing == null ) {
				efwsrResource.setCreated_date(context.getDate());
				efwsrResource.setParentId(parent.getResourceId());
				efwsrResource.setResourceURL(efwsrUrl);
				efwsrResource.setDeleted(false);
				serviceDb.addHIResource(efwsrResource);
				
			}
			else {
				if("update".equalsIgnoreCase(onConflict) && context.recover(existing)) {
					existing.setLastUpdatedTime(context.getDate());
					existing.setCreatedBy(resource.getCreatedBy());
					existing.getHiResourceEFWSR().setLastUpdatedTime(context.getDate());
					existing.getHiResourceEFWSR().setCreatedBy(efwsrResource.getCreatedBy());
					existing.setCreatedBy(efwsrResource.getCreatedBy());
					existing.setTitle(efwsrResource.getTitle());
					existing.setResourcePath(efwsrResource.getResourcePath());
					existing.setHiResourceEFWSR(efwsrResource.getHiResourceEFWSR());
					serviceDb.editHIResource(existing);
					efwsrResource = existing;
				}
				else {
					canShare = false;
				}
			}
			if(canShare) {
				shareHandler.setContext(context);
				shareHandler.importResource(efwsrResource,context.getRequest(),context.getManifest());
			}
		}
	}
}
