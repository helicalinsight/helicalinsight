package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import java.util.List;

public interface ProcessDetailsDao {
    Long addProcessDetails(ProcessDetails processDetails);

    void editProcessDetails(ProcessDetails processDetails);

    void deleteProcessDetails(Long processDetailsId);

    ProcessDetails getProcessDetails(Long processDetailsId);

    ProcessDetails findUniqueProcessDetails(ProcessDetails sampleprocessDetails);

    void deleteAllProcessDetails();

    List<ProcessDetails> fetchAllCubes(LimitOffsetModel limitOffsetModel);
}