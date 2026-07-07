package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.CubeMetadataDao;
import com.helicalinsight.admin.model.CubeMetadataInformation;
import com.helicalinsight.admin.service.CubeMetadataInformationService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by helical021 on 1/21/2020.
 */
@Service
public class CubeMetadataServiceImpl implements CubeMetadataInformationService {

    @Autowired
    private CubeMetadataDao cubeMetadataDao;

    @Override
    @Transactional
    public Long addCubeMetadataInformation(CubeMetadataInformation processDetails) {
        return cubeMetadataDao.addCubeMetadataInformation(processDetails);
    }

    @Override
    @Transactional
    public void editCubeMetadataInformation(CubeMetadataInformation processDetails) {
        cubeMetadataDao.editCubeMetadataInformation(processDetails);
    }

    @Override
    @Transactional
    public void deleteCubeMetadataInformation(Long processDetailsId) {
        cubeMetadataDao.deleteCubeMetadataInformation(processDetailsId);
    }

    @Override
    @Transactional
    public void deleteAllCubeMetadataInformation() {
        cubeMetadataDao.deleteAllCubeMetadataInformation();
    }

    @Transactional
    @Override
    public List<CubeMetadataInformation> fetchAllPhases(LimitOffsetModel limitOffsetModel) {
        return cubeMetadataDao.fetchAllPhases(limitOffsetModel);
    }

    @Override
    @Transactional
    public CubeMetadataInformation findCubeMetadataInformation(Long processid) {
        return cubeMetadataDao.getCubeMetadataInformation(processid);
    }

    @Override
    @Transactional
    public CubeMetadataInformation findUniqueCubeMetadataInformation(CubeMetadataInformation sampleprocessDetails) {
        return cubeMetadataDao.findUniqueCubeMetadataInformation(sampleprocessDetails);
    }

	@Override
	@Transactional
	public void deleteCubeMetadataInformationByLastPhaseId(long phaseId) {
		cubeMetadataDao.deleteCubeMetadataInformationByLastPhaseId(phaseId);
	}
}
