package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.CubePhaseDetails;
import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import java.util.List;

/**
 * Created by helical021 on 1/21/2020.
 */
public interface PhaseDetailsService {
    Long addCubePhaseDetails(CubePhaseDetails CubePhaseDetails);

    void editCubePhaseDetails(CubePhaseDetails CubePhaseDetails);

    void deleteCubePhaseDetails(Long CubePhaseDetailsId);

    void deleteAllCubePhaseDetails();

    List<CubePhaseDetails> fetchAllPhases(LimitOffsetModel limitOffsetModel);

    CubePhaseDetails findCubePhaseDetails(Long processid);
    CubePhaseDetails findCubePhaseDetailsByMetadataId(String metadataId);

    CubePhaseDetails findUniqueCubePhaseDetails(CubePhaseDetails sampleCubePhaseDetails);
    List<CubePhaseDetails> findAllCubePhaseDetailsByMetadataId(String metadataId);
    
    HIPhase findTheLastPhaseId(Integer resourceId);
	void addPhase(HIPhase hiPhase);

	HIPhase getPhase(Integer id);

	HIPhase getHIPhaseByTypeAndStatus(String type, String status);

	List<HIPhase> getAllPhases();

	void addHIResourcePhaseStatus(HIResourcePhaseStatus status);
	HIResourcePhaseStatus findHIResourcePhaseStatusByResourceId(Integer resourceId);
	HIResourcePhaseStatus findHIResourcePhaseById(Integer statusId);
	void deleteHIResourcePhaseDetails(Integer statusId);
	List<HIResourcePhaseStatus> findAllResourcePhasesByResourceId(Integer resourceId);
    
}
