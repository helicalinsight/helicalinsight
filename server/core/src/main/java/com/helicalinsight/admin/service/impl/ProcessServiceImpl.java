package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.service.ProcessDetailsService;
import com.helicalinsight.admin.dao.ProcessDetailsDao;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helical021 on 1/21/2020.
 */
@Service
public class ProcessServiceImpl implements ProcessDetailsService {

    @Autowired
    private ProcessDetailsDao processDetailsDao;

    @Override
    @Transactional
    public Long addProcessDetails(ProcessDetails processDetails) {
        return processDetailsDao.addProcessDetails(processDetails);
    }

    @Override
    @Transactional
    public void editProcessDetails(ProcessDetails processDetails) {
        processDetailsDao.editProcessDetails(processDetails);
    }

    @Override
    @Transactional
    public void deleteProcessDetails(Long processDetailsId) {
        processDetailsDao.deleteProcessDetails(processDetailsId);
    }

    @Override
    @Transactional
    public void deleteAllProcessDetails() {
        processDetailsDao.deleteAllProcessDetails();
    }

    @Transactional
    @Override
    public List<ProcessDetails> fetchAllCubes(LimitOffsetModel limitOffsetModel) {
        return processDetailsDao.fetchAllCubes(limitOffsetModel);
    }

    @Override
    @Transactional
    public ProcessDetails findProcessDetails(Long processid) {
        return processDetailsDao.getProcessDetails(processid);
    }

    @Override
    @Transactional
    public ProcessDetails findUniqueProcessDetails(ProcessDetails sampleprocessDetails) {
        return processDetailsDao.findUniqueProcessDetails(sampleprocessDetails);
    }
}
