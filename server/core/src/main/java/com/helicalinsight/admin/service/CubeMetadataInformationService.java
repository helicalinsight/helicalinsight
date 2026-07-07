package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.CubeMetadataInformation;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

public interface CubeMetadataInformationService {


    public Long addCubeMetadataInformation(CubeMetadataInformation processDetails);

    public void editCubeMetadataInformation(CubeMetadataInformation processDetails);


    public void deleteCubeMetadataInformation(Long processDetailsId);


    public void deleteAllCubeMetadataInformation();


    public List<CubeMetadataInformation> fetchAllPhases(LimitOffsetModel limitOffsetModel);


    public CubeMetadataInformation findCubeMetadataInformation(Long processid);


    public CubeMetadataInformation findUniqueCubeMetadataInformation(CubeMetadataInformation sampleprocessDetails);
    
    void  deleteCubeMetadataInformationByLastPhaseId(long phaseId);
}
