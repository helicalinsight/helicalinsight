package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.CubeMetadataInformation;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

public interface CubeMetadataDao {
    Long addCubeMetadataInformation(CubeMetadataInformation cubeCubeMetadataInformation);

    void editCubeMetadataInformation(CubeMetadataInformation cubeCubeMetadataInformation);

    void deleteCubeMetadataInformation(Long id);

    CubeMetadataInformation getCubeMetadataInformation(Long id);

    CubeMetadataInformation findUniqueCubeMetadataInformation(CubeMetadataInformation cubeMetadataInformation);

    void deleteAllCubeMetadataInformation();

    List<CubeMetadataInformation> fetchAllPhases(LimitOffsetModel limitOffsetModel);

	void deleteCubeMetadataInformationByLastPhaseId(long phaseId);
}