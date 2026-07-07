package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.CubePhaseDetails;
import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

public interface PhaseDetailsDao {
    Long addPhaseDetails(CubePhaseDetails cubePhaseDetails);

    void editPhaseDetails(CubePhaseDetails cubePhaseDetails);

    void deletePhaseDetails(Long id);

    CubePhaseDetails getPhaseDetails(Long id);

    CubePhaseDetails findUniquePhaseDetails(CubePhaseDetails cubePhaseDetails);

    void deleteAllPhaseDetails();

    List<CubePhaseDetails> fetchAllPhases(LimitOffsetModel limitOffsetModel);

	CubePhaseDetails findUniquePhaseDetailsByMetadataId(String metadataId);

	List<CubePhaseDetails> findAllCubePhaseDetailsByMetadataId(String metadataId);

	HIPhase findTheLastPhaseId(Integer resourceId);
	void addPhase(HIPhase hiPhase);

	HIPhase getPhase(Integer id);

	HIPhase getHIPhaseByTypeAndStatus(String type, String status);

	List<HIPhase> getAllPhases();
	
	HIResourcePhaseStatus findHIResourcePhaseStatusByResourceId(Integer resourceId);
	void addHIResourcePhaseStatus(HIResourcePhaseStatus status);

	HIResourcePhaseStatus findHIResourcePhaseById(Integer statusId);

	void deleteHIResourcePhaseDetails(Integer statusId);

	List<HIResourcePhaseStatus> findAllResourcePhasesByResourceId(Integer resourceId);
}