package com.helicalinsight.admin.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIRecyclebinHelperService;
import com.helicalinsight.admin.service.HIResourceServiceDB;


@Service
public class HIRecyclebinHelperServiceImpl implements HIRecyclebinHelperService {

	private final HIRecycleBinService hiRecycleBinService;
	private final HIResourceServiceDB serviceDb;
	
	
	public HIRecyclebinHelperServiceImpl(HIRecycleBinService hiRecycleBinService,  HIResourceServiceDB serviceDb) {
		this.hiRecycleBinService = hiRecycleBinService;
		this.serviceDb = serviceDb;
	}
	
	@Transactional
	@Override
	public void deleteHIResourceAndRecyclebin(RecycleBinDTO bin) {
//		hiRecycleBinService.delete(bin.getRecycleBinId());
//		serviceDb.deleteHIResourceById(bin.getResourceId());
	    serviceDb.deleteHIResourceById(bin.getResourceId());
	    // Only delete bin row if hardDelete did not already remove it
	    if (hiRecycleBinService.isRecycleBinPresent(bin.getRecycleBinId())) {
	        hiRecycleBinService.delete(bin.getRecycleBinId());
	    }
	}

}
