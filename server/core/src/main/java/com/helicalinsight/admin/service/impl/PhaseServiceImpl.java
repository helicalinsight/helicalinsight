package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.PhaseDetailsDao;
import com.helicalinsight.admin.model.CubePhaseDetails;
import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by helical021 on 1/21/2020.
 */
@Service
public class PhaseServiceImpl implements PhaseDetailsService {

    @Autowired
    private PhaseDetailsDao processDetailsDao;

    @Override
    @Transactional
    public Long addCubePhaseDetails(CubePhaseDetails processDetails) {
        return processDetailsDao.addPhaseDetails(processDetails);
    }

    @Override
    @Transactional
    public void editCubePhaseDetails(CubePhaseDetails processDetails) {
        processDetailsDao.editPhaseDetails(processDetails);
    }

    @Override
    @Transactional
    public void deleteCubePhaseDetails(Long processDetailsId) {
        processDetailsDao.deletePhaseDetails(processDetailsId);
    }

    @Override
    @Transactional
    public void deleteAllCubePhaseDetails() {
        processDetailsDao.deleteAllPhaseDetails();
    }

    @Transactional
    @Override
    public List<CubePhaseDetails> fetchAllPhases(LimitOffsetModel limitOffsetModel) {
        return processDetailsDao.fetchAllPhases(limitOffsetModel);
    }

    @Override
    @Transactional
    public CubePhaseDetails findCubePhaseDetails(Long processid) {
        return processDetailsDao.getPhaseDetails(processid);
    }

    @Override
    @Transactional
    public CubePhaseDetails findUniqueCubePhaseDetails(CubePhaseDetails sampleprocessDetails) {
        return processDetailsDao.findUniquePhaseDetails(sampleprocessDetails);
    }

	@Override
	@Transactional
	public CubePhaseDetails findCubePhaseDetailsByMetadataId(String metadataId) {
		return processDetailsDao.findUniquePhaseDetailsByMetadataId(metadataId);
	}

	@Override
	@Transactional
	public List<CubePhaseDetails> findAllCubePhaseDetailsByMetadataId(String metadataId) {
		return processDetailsDao.findAllCubePhaseDetailsByMetadataId(metadataId);
	}
	
	@Transactional
	@Override
	public HIPhase findTheLastPhaseId(Integer resourceId) {
		return processDetailsDao.findTheLastPhaseId(resourceId);
	}
	
	@Transactional
	@Override
	public void addPhase(HIPhase hiPhase) {
		processDetailsDao.addPhase(hiPhase);
	}

	@Transactional
	@Override
	public HIPhase getPhase(Integer id) {
		return processDetailsDao.getPhase(id);
	}

	@Transactional
	@Override
	public HIPhase getHIPhaseByTypeAndStatus(String type, String status) {
		return processDetailsDao.getHIPhaseByTypeAndStatus(type, status);
	}

	@Transactional
	@Override
	public List<HIPhase> getAllPhases() {
		return processDetailsDao.getAllPhases();
	}

	@Transactional
	@Override
	public void addHIResourcePhaseStatus(HIResourcePhaseStatus status) {
		processDetailsDao.addHIResourcePhaseStatus(status);
	}
	
	@Transactional
	@Override
	public HIResourcePhaseStatus findHIResourcePhaseStatusByResourceId(Integer resourceId) {
		return processDetailsDao.findHIResourcePhaseStatusByResourceId(resourceId);
	}

	@Transactional
	@Override
	public HIResourcePhaseStatus findHIResourcePhaseById(Integer statusId) {
		return processDetailsDao.findHIResourcePhaseById(statusId);
	}
	@Transactional
	@Override
	public void deleteHIResourcePhaseDetails(Integer statusId) {
		processDetailsDao.deleteHIResourcePhaseDetails(statusId);
	}

	@Transactional
	@Override
	public List<HIResourcePhaseStatus> findAllResourcePhasesByResourceId(Integer resourceId) {
		return processDetailsDao.findAllResourcePhasesByResourceId(resourceId);
	}
}
