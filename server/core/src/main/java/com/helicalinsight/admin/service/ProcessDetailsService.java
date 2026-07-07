package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import java.util.List;

/**
 * Created by helical021 on 1/21/2020.
 */
public interface ProcessDetailsService {
    Long addProcessDetails(ProcessDetails processDetails);

    void editProcessDetails(ProcessDetails processDetails);

    void deleteProcessDetails(Long processDetailsId);

    void deleteAllProcessDetails();

    List<ProcessDetails> fetchAllCubes(LimitOffsetModel limitOffsetModel);

    ProcessDetails findProcessDetails(Long processid);

    ProcessDetails findUniqueProcessDetails(ProcessDetails sampleprocessDetails);
}
