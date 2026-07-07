package com.helicalinsight.admin.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.fileupload.disk.DiskFileItem;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.helicalinsight.admin.dao.ResourceEfwContentDao;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import com.helicalinsight.datasource.DuplicateEntryException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.utility.FileUtils;

@Service
public class ResourceEfwContentServiceImpl implements ResourceEfwContentsService {
	
	@Autowired
	private ResourceEfwContentDao resourceEfwContentDao;
	
	@Override
	@Transactional
	public List<ResourceEfwContents> fetchResourceEfwContentByResourceId(Integer resourceId) {
		return resourceEfwContentDao.fetchResourceEfwContentByResourceId(resourceId);
	}

	@Override
	@Transactional
	public void deleteResourceEwfContentByResourceId(String fileName,Integer resourceId) {
		resourceEfwContentDao.deleteResourceEwfContentByResourceId(fileName,resourceId);
	}

	@Override
	@Transactional
	public Integer addHIResourceEfwContent(String destination, Integer resourceId, DiskFileItem fileObject, String type) {

		String fileName=fileObject.getName();
		if(fetchEfwContentByResourceIdAndFileName(resourceId, fileName)!=null)
			throw new DuplicateEntryException("A file named with "+fileName+" already existed in "+destination+" path. Please Provide Distinct File Name");

		ResourceEfwContents entity=new ResourceEfwContents();
		entity.setContent(getByteArrayFromDiskFileItem(fileObject));
		String extension = FileUtils.getExtension(fileName);
		entity.setContentType(extension !=null?extension.toLowerCase():"");

		entity.setResourceId(resourceId);
		entity.setFileName(fileName);
		return resourceEfwContentDao.addHIResourceEfwContent(entity);
	}

	@Transactional
	@Override
	public Integer addHIResourceEfwContent(ResourceEfwContents entity){
		return resourceEfwContentDao.addHIResourceEfwContent(entity);
	}
	public byte[] getByteArrayFromDiskFileItem(DiskFileItem fileItem) {
		// Check if the item is a form field or a file upload
		if (!fileItem.isFormField()) {
			// Create an InputStream from the file item
			try (InputStream inputStream = fileItem.getInputStream();
				 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				// Copy the input stream to the output stream
				IOUtils.copy(inputStream, outputStream);
				// Return the byte array
				return outputStream.toByteArray();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return null; // or handle the case where it's a form field
	}

	@Override
	@Transactional
	public ResourceEfwContents fetchEfwContentByResourceIdAndFileName(Integer resourceId, String fileName) {
		return resourceEfwContentDao.fetchEfwContentByResourceIdAndFileName(resourceId, fileName);
	}

	@Override
	@Transactional
	public File loadFile(Integer resourceId, String fileName) {
		ResourceEfwContents resourceEfwContent = fetchEfwContentByResourceIdAndFileName(resourceId, fileName);
		if (resourceEfwContent != null) {
			File file = new File(resourceEfwContent.getFileName());
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(resourceEfwContent.getContent());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return file;
		}
		throw new ResourceNotFoundException("The file named "+fileName+" is not found");
	}
	@Transactional
	@Override
	public List<ResourceEfwContents> findAllImageResources() {
		return resourceEfwContentDao.findAllImageResources();
	}
}
