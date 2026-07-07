package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Dimension;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ProcessDetailsService;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * Handles cube-related operations such as creating, editing, and saving cubes to the database.
 * This component is responsible for managing cube metadata and resources.
 * It interacts with the database and the application context to perform cube-related tasks.
 * CubeHandler utilizes various services and DAOs to handle cube operations effectively.
 * It also populates cube metadata and resources based on the provided JSON input.
 * 
 * @author Somen
 * Created by helical021 on 1/28/2020.
 */

@Component
public class CubeHandler {

    @Autowired
    protected ProcessDetailsService processDetailsService;
    @Autowired
    private HICubeDAOService hiCubeDAOService;

    /**
     * Handles the creation or update of cubes based on the provided JSON input.
     * Retrieves cube metadata and resources, validates inputs, and persists cube details to the database.
     *
     * @param formJson 				 JSON object containing cube metadata and resource details.
     * @return A list of process details associated with the cube creation process.
     * @throws EfwServiceException 	 If HIResource object turns to null .
     */
    public List<ProcessDetails> handleCube(JsonObject formJson) {
        JsonObject metadata1 = formJson.getAsJsonObject("metadata");
        String metadataLocation = metadata1.get("location").getAsString();
        String metadataFileName = metadata1.get("metadataFileName").getAsString();
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);
        HIResource metadataResource = serviceDB.getResourceByUrl(metadataLocation + "/" + metadataFileName);
        if (metadataResource == null) {
            throw new EfwServiceException("The  metadata does not exists");
        }

        HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResourceMetadata hiResourceMetadata = hiResourceServiceDB.giveHIResourceMetadataByResourceId(metadataResource.getResourceId());


        String cubeLocation = GsonUtility.optString(formJson,"location");
        boolean modeSaveAs = formJson.has("newLocation");
        if (modeSaveAs) {
            cubeLocation = formJson.get("newLocation").getAsString();
        }
        String titleCube = formJson.get("fileName").getAsString();
        String cubePath =(formJson.has("uniqueId")?(DBProcessor.checkAndReplaceSpecialChars(titleCube).trim()+ "." + JsonUtils.getCubeExtension()):titleCube);



        String cubeUrl = cubeLocation + "/" + cubePath;
        HIResource cubeResource = serviceDB.getResourceByUrl(cubeUrl);

        boolean modeCreate = GsonUtility.optBooleanValue(formJson,"uniqueId", false);

        if (cubeResource != null && (modeCreate || modeSaveAs)) {

            cubePath = cubePath.replace("." + JsonUtils.getCubeExtension(), "_" + String.valueOf(System.currentTimeMillis()).substring(11, 13) + "." + JsonUtils.getCubeExtension());
            cubeUrl = cubeLocation + "/" + cubePath;


        }
        cubePath=cubePath.replace("." + JsonUtils.getCubeExtension(),"");

        if (modeCreate || modeSaveAs) {
            cubeResource = ResourceUtils.newHIResource(cubeLocation, false, titleCube, cubePath, cubeUrl, JsonUtils.getCubeExtension());
            ResourceType metadataExtension = resourceTypeServiceDB.getResourceTypeByTypeAndExtension("cube", ".cube");
            cubeResource.setResourceType(metadataExtension);
            serviceDB.addHIResource(cubeResource);
        }
        if(cubeResource == null ){
            throw new EfwServiceException("Could not locate cube with "+cubeUrl);
        }
        JsonObject dataOb = new JsonObject();
        dataOb.addProperty("path", cubeResource.getResourceURL());
        dataOb.addProperty("extension", "cube");
        dataOb.addProperty("permissionLevel", "5");
        dataOb.addProperty("name", cubeResource.getResourcePath());
        dataOb.addProperty("title", titleCube);
        dataOb.addProperty("lastModified", cubeResource.getLastUpdatedTime().getTime());
        dataOb.addProperty("type", "file");
        dataOb.add("options", new JsonObject());
        formJson.add("data", dataOb);

        List<ProcessDetails> pdList = addCubeList(formJson, hiResourceMetadata, cubeResource);

        return pdList;


    }

    /**
     * Adds cube metadata and resources to the database based on the provided JSON input.
     * Parses the JSON object to extract cube details and populates the cube resource accordingly.
     *
     * @param formJson           		 JSON object containing cube metadata and resource details.
     * @param hiResourceMetadata 		 HIResourceMetadata object
     * @param cubeResource               HIResource cube resource object.
     * @return A list of process details associated with the cube creation process.
     */
    private List<ProcessDetails> addCubeList(JsonObject formJson, HIResourceMetadata hiResourceMetadata, HIResource cubeResource) {
        JsonArray cubesArray = formJson.getAsJsonArray("cubes");
        List<ProcessDetails> pdList = new ArrayList<>();
        for (int index = 0; index < cubesArray.size(); index++) {
            JsonObject item = cubesArray.get(index).getAsJsonObject();
            HIMetadataCube cube = getMetadataCube(hiResourceMetadata, item, cubeResource);
            if (cube != null) {
                addDimensions(cube, item);
                JsonArray measureArray = item.getAsJsonArray("measures");
                for (int indexMeasure = 0; indexMeasure < measureArray.size(); indexMeasure++) {
                    JsonObject itemMeasure = measureArray.get(indexMeasure).getAsJsonObject();
                    measure(cube, itemMeasure);
                }

                ProcessDetails processDetails = new ProcessDetails();
                processDetails.setCubeId(""+cube.getHiResource().getResourceId());
                processDetails.setCubeName(cube.getName());
                processDetails.setIs_schedule(false);

                pdList.add(processDetails);
            }
        }
        return pdList;
    }


    private void addLevel(HIDimensionHierarchy hiDimensionHierarchy, JsonObject formJson) {

        JsonArray levelsArray = formJson.getAsJsonArray("levels");
        for (int index = 0; index < levelsArray.size(); index++) {
            JsonObject item = levelsArray.get(index).getAsJsonObject();
            getHiCubeHierarchyLevel(item, hiDimensionHierarchy);
        }


    }
    /**
     * Adds cube hierarchy levels to the cube dimension based on the provided JSON input.
     * Parses the JSON object to extract hierarchy level details and populates the cube dimension with hierarchy levels.
     *
     * @param hiCubeDimension 		 HICubeDimension object.
     * @param formJson        		 JSON object containing hierarchy level details.
     */
    private void addHierarchy(HICubeDimension hiCubeDimension, JsonObject formJson) {

        JsonArray hierarchyArray = formJson.getAsJsonArray("hierarchies");
        for (int index = 0; index < hierarchyArray.size(); index++) {

            JsonObject item = hierarchyArray.get(index).getAsJsonObject();
            HIDimensionHierarchy hiDimensionHierarchy = getHiDimensionHierarchy(hiCubeDimension, item);
            if (hiDimensionHierarchy != null) {
                addLevel(hiDimensionHierarchy, item);
            }


        }

    }

    /**
     * Adds cube dimensions to the cube metadata based on the provided JSON input.
     * Parses the JSON object to extract dimension details and populates the cube metadata with dimensions.
     *
     * @param cube     		 HIMetadataCube object.
     * @param formJson 		 JSON object containing dimension details.
     */
    private void addDimensions(HIMetadataCube cube, JsonObject formJson) {

        JsonArray dimensionArray = formJson.getAsJsonArray("dimensions");
        for (int index = 0; index < dimensionArray.size(); index++) {
            JsonObject item = dimensionArray.get(index).getAsJsonObject();

            HICubeDimension hiCubeDimension = getHiCubeDimension(cube, item);

            if (hiCubeDimension != null) {
                addHierarchy(hiCubeDimension, item);
            }

        }

    }
    /**
     * Populates dimension metadata based on the provided JSON input.
     * Parses the JSON object to extract dimension details and populates the dimension metadata accordingly.
     *
     * @param item      		 JSON object containing dimension details such as dimensionName, dimensionType.
     * @param dimension 		 dimension object provides name and type of dimension.
     */
    protected void populateDimensions(JsonObject item, Dimension dimension) {
        dimension.setName(GsonUtility.optStringValue(item,"dimensionName", dimension.getName()));
        dimension.setType(GsonUtility.optStringValue(item,"dimensionType", dimension.getType()));
        dimension.setVisible(GsonUtility.optBoolean(item,"visible"));
    }

    /**
     * Saves cube creation details to the database.
     * Persists cube creation process details, including metadata and resource information, to the database.
     *
     * @param pdList    		 list of process details associated with the cube creation process.
     * @param metadata  		 metadata object containing cube connection details and provides fileName.
     * @param response  		 JSON object containing cube creation response details which provides uuid and location.
     */
    public void saveToDatabase(List<ProcessDetails> pdList, Metadata metadata, JsonObject response) {
        ConnectionDetails connectionDetails = metadata.getConnectionDetails();
        String connectionId = connectionDetails.getConnectionId();
        String connectionType = connectionDetails.getConnectionType();
        for (int index = 0; index < pdList.size(); index++) {
            ProcessDetails processDetails = pdList.get(index);
            processDetails.setMetadataFile(response.get("uuid").getAsString());
            processDetails.setMetadatDir(response.get("location").getAsString());
            processDetails.setProcessStatus("CREATED");
            processDetails.setComments("Cube created during Saving Metadata");
            processDetails.setConnectionId(connectionId);
            processDetails.setDialect(connectionDetails.getDialect());
            processDetails.setMetadataName(metadata.getFileName());
            //processDetails.setProcessingClass();
            processDetails.setConnectionType(connectionType);
            processDetails.setCreatedBy(AuthenticationUtils.getUserId());
            processDetails.setCreatedDate(new Date());
            processDetails.setDriver(connectionDetails.getDriverClass().getDriverClass());
            processDetailsService.addProcessDetails(processDetails);
        }
    }

    /**
     * Retrieves or creates a metadata cube based on the provided JSON input.
     * If the cube already exists, updates its details; otherwise, creates a new cube metadata.
     *
     * @param hiResourceMetadata 		 HIResourceMetadata object provides id.
     * @param item               		 JSON object containing cube details such as setCache, caption etc.
     * @param cubeResource               HIResource object provides resourceId.
     * @return {@code HIMetadataCube} object.
     */
    private HIMetadataCube getMetadataCube(HIResourceMetadata hiResourceMetadata, JsonObject item, HIResource cubeResource) {

        boolean isEdit = item.has("id");
        HIMetadataCube cube;
        if (isEdit) {
            cube = hiCubeDAOService.findCube(hiResourceMetadata.getId(), cubeResource.getResourceId(), item.get("id").getAsString());
            if (item.has("action") && item.get("action").getAsString().equals("delete")) {
                hiCubeDAOService.delete(cube);
                return null;
            }
        } else {
            cube = new HIMetadataCube();
        }
        cube.setCache(GsonUtility.optBoolean(item,"setCache"));
        cube.setCaption(GsonUtility.optString(item,"caption"));
        cube.setDefaultMeasure(GsonUtility.optString(item,"defaultMeasure"));
        cube.setDescription(GsonUtility.optString(item,"description"));

        cube.setEnabled(GsonUtility.optBoolean(item,"enabled"));
        cube.setName(GsonUtility.optString(item,"cubeName"));
        cube.setVisible(GsonUtility.optBoolean(item,"visible"));
        cube.setDomainName(GsonUtility.optString(item,"domainName"));
        cube.setHiResourceMetadata(hiResourceMetadata);
        cube.setHiResource(cubeResource);

        if (isEdit) {
            hiCubeDAOService.edit(cube);
        } else {
            String cubeId = AdhocUtils.getUuid();
            item.addProperty("id", cubeId);
            cube.setCubeId(cubeId);
            hiCubeDAOService.addCube(cube);
        }
        return cube;
    }

    /**
     * Retrieves or creates a cube dimension based on the provided JSON input.
     * If the dimension already exists, updates its details; otherwise, creates a new cube dimension.
     *
     * @param cube 			 HIMetadataCube object which provides id.
     * @param item 			 JSON object containing dimension details such dimensionName, table.
     * @return {@code HICubeDimension} object.
     */
    private HICubeDimension getHiCubeDimension(HIMetadataCube cube, JsonObject item) {
        boolean isEdit = item.has("id");

        HICubeDimension hiCubeDimension;
        if (isEdit) {
            hiCubeDimension = hiCubeDAOService.findHICubeDimension(""+cube.getId(), item.get("id").getAsString());
            if (item.has("action") && item.get("action").getAsString().equals("delete")) {
                hiCubeDAOService.delete(hiCubeDimension);
                return null;
            }
        } else {
            hiCubeDimension = new HICubeDimension();
        }

        hiCubeDimension.setName(GsonUtility.optString(item,"dimensionName"));
        hiCubeDimension.setTable(GsonUtility.optString(item,"table"));
        hiCubeDimension.setTableId(GsonUtility.optString(item,"tableId"));
        hiCubeDimension.setColumnName(GsonUtility.optString(item,"columnName"));
        hiCubeDimension.setColumnId(GsonUtility.optString(item,"columnId"));
        hiCubeDimension.setType(GsonUtility.optString(item,"dimensionType"));
        hiCubeDimension.setVisible(GsonUtility.optBoolean(item,"visible"));
        hiCubeDimension.setColumn(GsonUtility.optString(item,"column"));
        if (isEdit) {
            hiCubeDAOService.edit(hiCubeDimension);
        } else {
            hiCubeDimension.setMetadataCube(cube);
            String uuid = AdhocUtils.getUuid();
            item.addProperty("id", uuid);
            hiCubeDimension.setDimId(uuid);

            hiCubeDAOService.add(hiCubeDimension);
        }
        return hiCubeDimension;
    }

    /**
     * Retrieves or creates a cube hierarchy level based on the provided JSON input.
     * If the hierarchy level already exists, updates its details; otherwise, creates a new hierarchy level.
     *
     * @param item                  	 JSON object containing hierarchy level details like columnId, levelName.
     * @param hiDimensionHierarchy 		 HIDimensionHierarchy object provides id.
     * @return {@code HICubeHierarchyLevel}  object.
     */
    private HICubeHierarchyLevel getHiCubeHierarchyLevel(JsonObject item, HIDimensionHierarchy hiDimensionHierarchy) {
        boolean isEdit = item.has("id");

        HICubeHierarchyLevel hiCubeLvl;
        if (isEdit) {
            hiCubeLvl = hiCubeDAOService.findHICubeHierarchyLevel(hiDimensionHierarchy.getId(), item.get("id").getAsString());
            if (item.has("action") && item.get("action").getAsString().equals("delete")) {
                hiCubeDAOService.delete(hiCubeLvl);
                return null;
            }
        } else {
            hiCubeLvl = new HICubeHierarchyLevel();
        }


        hiCubeLvl.setColumnId(GsonUtility.optString(item,"columnId"));
        hiCubeLvl.setName(GsonUtility.optString(item,"levelName"));
        hiCubeLvl.setCaption(GsonUtility.optString(item,"caption"));
        hiCubeLvl.setVisible(GsonUtility.optBoolean(item,"visible"));
        hiCubeLvl.setTableId(GsonUtility.optString(item,"tableId"));
        hiCubeLvl.setHierarchyTable(GsonUtility.optString(item,"table"));
        hiCubeLvl.setColumnName(GsonUtility.optString(item,"columnName"));
        hiCubeLvl.setColumn(GsonUtility.optString(item,"column"));
        hiCubeLvl.setApproximateRowCount(GsonUtility.optInt(item,"approxRowCount"));
        hiCubeLvl.setDataType(GsonUtility.optString(item,"dataType"));
        hiCubeLvl.setDescription(GsonUtility.optString(item,"description"));

        hiCubeLvl.setLevelType(GsonUtility.optString(item,"levelType"));
        hiCubeLvl.setNestedMemberFormatter(GsonUtility.optString(item,"nestedMemberFormatter"));
        hiCubeLvl.setHideMemberIfExpression(GsonUtility.optString(item,"hideMemberIfExpression"));
        hiCubeLvl.setFormula(GsonUtility.optString(item,"formula"));
        hiCubeLvl.setExample(GsonUtility.optString(item,"example"));
        hiCubeLvl.setFilter(GsonUtility.optString(item,"filter"));
        hiCubeLvl.setSynonyms(GsonUtility.optString(item,"synonyms"));
        hiCubeLvl.setTopics(GsonUtility.optString(item,"topic"));
        hiCubeLvl.setSemanticType(GsonUtility.optString(item,"semanticType"));
        hiCubeLvl.setHideMemberIfExpression(GsonUtility.optString(item,"hideMemberIfExpression"));

        hiCubeLvl.setHiDimensionHierarchy(hiDimensionHierarchy);
        if (isEdit) {
            hiCubeDAOService.edit(hiCubeLvl);
        } else {
            String uuid = AdhocUtils.getUuid();
            item.addProperty("id", uuid);
            hiCubeLvl.setLevelId(uuid);
            hiCubeDAOService.add(hiCubeLvl);
        }

        return hiCubeLvl;
    }

    /**
     * Retrieves or creates a cube measure based on the provided JSON input.
     * If the measure already exists, updates its details; otherwise, creates a new cube measure.
     *
     * @param cube 		 HIMetadataCube object provides id.
     * @param item 		 JSON object containing measure details like aggregator, caption , columnId.
     * @return {@code HICubeMeasure} object
     */
    private HICubeMeasure measure(HIMetadataCube cube, JsonObject item) {
        boolean isEdit = item.has("id");
        HICubeMeasure hiCubeMeasure;
        if (isEdit) {
            hiCubeMeasure = hiCubeDAOService.findCubeMeasure(cube.getId(), item.get("id").getAsString());
            if (item.has("action") && item.get("action").getAsString().equals("delete")) {
                hiCubeDAOService.delete(hiCubeMeasure);
                return null;
            }
        } else {
            hiCubeMeasure = new HICubeMeasure();
        }


        hiCubeMeasure.setAggregator(GsonUtility.optString(item,"aggregator"));
        hiCubeMeasure.setCaption(GsonUtility.optString(item,"caption"));
        hiCubeMeasure.setColumnId(GsonUtility.optString(item,"columnId"));
        hiCubeMeasure.setTableId(GsonUtility.optString(item,"tableId"));
        hiCubeMeasure.setDataType(GsonUtility.optString(item,"dataType"));
        hiCubeMeasure.setDescription(GsonUtility.optString(item,"description"));
        hiCubeMeasure.setFormatString(GsonUtility.optString(item,"formatString"));
        hiCubeMeasure.setFormatter(GsonUtility.optString(item,"formatter"));
        hiCubeMeasure.setMeasureTable(GsonUtility.optString(item,"table"));
        hiCubeMeasure.setColumnName(GsonUtility.optString(item,"columnName"));
        hiCubeMeasure.setColumn(GsonUtility.optString(item,"column"));
        hiCubeMeasure.setMeasureType(GsonUtility.optString(item,"measureType"));

        hiCubeMeasure.setName(GsonUtility.optString(item,"measureName"));
        hiCubeMeasure.setVisible(GsonUtility.optBoolean(item,"visible"));
        hiCubeMeasure.setFormula(GsonUtility.optString(item,"formula"));
        hiCubeMeasure.setExample(GsonUtility.optString(item,"example"));
        hiCubeMeasure.setFilter(GsonUtility.optString(item,"filter"));
        hiCubeMeasure.setSynonyms(GsonUtility.optString(item,"synonyms"));
        hiCubeMeasure.setTopics(GsonUtility.optString(item,"topic"));
        hiCubeMeasure.setSemanticType(GsonUtility.optString(item,"semanticType"));
        hiCubeMeasure.setMetadataCube(cube);
        if (isEdit) {
            hiCubeDAOService.edit(hiCubeMeasure);
        } else {
            String uuid = AdhocUtils.getUuid();
            item.addProperty("id", uuid);
            hiCubeMeasure.setMeasureId(uuid);
            hiCubeDAOService.add(hiCubeMeasure);
        }
        return hiCubeMeasure;
    }

    /**
     * Retrieves or creates a dimension hierarchy based on the provided JSON input.
     * If the hierarchy already exists, updates its details; otherwise, creates a new dimension hierarchy.
     *
     * @param hiCubeDimension 		 cube dimension object provides id 
     * @param item            		 JSON object containing hierarchy details provides hierarchyName,description etc.
     * @return {@code HIDimensionHierarchy} object.
     */
    private HIDimensionHierarchy getHiDimensionHierarchy(HICubeDimension hiCubeDimension, JsonObject item) {
        boolean isEdit = item.has("id");
        HIDimensionHierarchy hiDimensionHierarchy;
        if (isEdit) {
            hiDimensionHierarchy = hiCubeDAOService.findDimensionHierarchy(hiCubeDimension.getId(), item.get("id").getAsString());
            if (item.has("action") && item.get("action").getAsString().equals("delete")) {
                hiCubeDAOService.delete(hiDimensionHierarchy);
                return null;
            }
        } else {
            hiDimensionHierarchy = new HIDimensionHierarchy();
        }

        hiDimensionHierarchy.setCaption(GsonUtility.optString(item,"hierarchyName"));
        hiDimensionHierarchy.setDescription(GsonUtility.optString(item,"description"));
        hiDimensionHierarchy.setHierarchyTable(GsonUtility.optString(item,"table"));
        hiDimensionHierarchy.setColumnName(GsonUtility.optString(item,"columnName"));
        hiDimensionHierarchy.setColumn(GsonUtility.optString(item,"column"));
        hiDimensionHierarchy.setHierarchyType(GsonUtility.optString(item,"hierarchyType"));

        hiDimensionHierarchy.setName(GsonUtility.optString(item,"hierarchyName"));
        hiDimensionHierarchy.setPrimaryKeyColumnId(GsonUtility.optString(item,"primaryColumnId"));
        hiDimensionHierarchy.setTableId(GsonUtility.optString(item,"tableId"));
        hiDimensionHierarchy.setVisible(GsonUtility.optBoolean(item,"visible"));
        hiDimensionHierarchy.setHiCubeDimension(hiCubeDimension);
        if (isEdit) {
            hiCubeDAOService.edit(hiDimensionHierarchy);
        } else {
            String uuid = AdhocUtils.getUuid();
            item.addProperty("id", uuid);
            hiDimensionHierarchy.setHierarchyId(uuid);
            hiCubeDAOService.add(hiDimensionHierarchy);
        }
        return hiDimensionHierarchy;
    }

}
