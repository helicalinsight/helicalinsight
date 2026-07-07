package com.helicalinsight.admin.service;

import java.util.List;


public interface IScheduleService {
	
	List<Long> findAllSchedulesByResourceId(int resourceId);
	void deleteScheduleByIds(List<Long> ids);
	void updateScheduleByResourceIdAndScheduleName(Integer resourceId,String oldName,String newName);

}
