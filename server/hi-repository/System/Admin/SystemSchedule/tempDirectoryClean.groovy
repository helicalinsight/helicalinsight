import com.helicalinsight.admin.management.TempDirectoryCleanHandler
import groovy.json.JsonOutput
import org.slf4j.LoggerFactory

/**
 * Deletes temp files older than the configured number of days (default 1 = 24 hours)
 * by invoking TempDirectoryCleanHandler's deleteAll API.
 */
def execute(Map schedule) {
    def logger = LoggerFactory.getLogger("SystemSchedule.tempDirectoryClean")
    int noOfDays = schedule.noOfDays != null ? schedule.noOfDays as int : 1
    if (noOfDays <= 0) {
        noOfDays = 1
    }

    String formData = JsonOutput.toJson([
            action  : "deleteAll",
            noOfDays: noOfDays
    ])

    TempDirectoryCleanHandler handler = new TempDirectoryCleanHandler()
    String response = handler.executeComponent(formData)
    logger.info("Temp directory clean completed for schedule {} with noOfDays={}: {}",
            schedule.id, noOfDays, response)
    return response
}
