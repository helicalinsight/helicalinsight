package com.helicalinsight.scheduling;

import com.helicalinsight.efw.utility.ApplicationUtilities;

/**
 * Created by author on 27-08-2015.
 * @author Rajasekhar
 * @author Somen
 * DereferenceScheduling
 * this class deletes the scheduling references. 
 * 
 */
public class DereferenceScheduling {
	/**
	 * deleteSchedule(String scheduleReference)
	 * method is responsible for delete scheduler
	 * @param scheduleReference       String which specify the job key which has to be delete.
	 * 
	 */
    public static void deleteSchedule(String scheduleReference) {
        String SchedulerPath = ApplicationUtilities.schedulerPath();
        ScheduleProcess scheduleProcess = new ScheduleProcess();
        scheduleProcess.delete(scheduleReference);
        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
        xmlOperationWithParser.removeElementFromXml(SchedulerPath, scheduleReference);
    }
}
