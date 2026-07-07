package com.helicalinsight.adhoc.service.impl;

import java.util.List;

import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.dao.HICubeDAO;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import jakarta.transaction.Transactional;

@Service
public class HICubeDAOServiceImpl implements HICubeDAOService {
    @Autowired
    private HICubeDAO hiCubeDao;

    @Override
    @Transactional
    public void addCube(HIMetadataCube cube) {
        hiCubeDao.addCube(cube);
    }

    @Override
    @Transactional
    public void add(Object obj) {
        hiCubeDao.add(obj);
    }

    @Override
    @Transactional
    public void edit(Object obj) {
        hiCubeDao.edit(obj);
    }

    @Override
    @Transactional
    public HIMetadataCube findCube(Integer metadataId, Integer cubeId, String uuid) {
        return hiCubeDao.findCube(metadataId, cubeId, uuid);
    }

    @Override
    @Transactional
    public HICubeDimension findHICubeDimension(String cubeId, String id) {
        return hiCubeDao.findHICubeDimension(cubeId, id);
    }

    @Override
    @Transactional
    public HIDimensionHierarchy findDimensionHierarchy(Integer id, String id1) {
        return hiCubeDao.findDimensionHierarchy(id, id1);
    }

    @Override
    @Transactional
    public List<HIMetadataCube> findAllCubeWithResourceId(Integer resourceId) {
        return hiCubeDao.findAllCubeWithResourceId(resourceId);
    }

    @Override
    @Transactional
    public JsonObject getCubeAsJsonObj(Integer resourceId) {
        List<HIMetadataCube> cubeList = findAllCubeWithResourceId(resourceId);
        return getCubeObj(cubeList);
    }

    @Override
    @Transactional
    public HICubeHierarchyLevel findHICubeHierarchyLevel(Integer hiecId, String levelId) {
        return hiCubeDao.findHICubeHierarchyLevel(hiecId,levelId);
    }

    @Override
    @Transactional
    public HICubeMeasure findCubeMeasure(Integer cubeId, String measureId) {
        return hiCubeDao.findCubeMeasure(cubeId,measureId);
    }

    @Override
    @Transactional
    public void delete(Object anyObject) {
         hiCubeDao.delete(anyObject);
    }



    @NotNull
    private JsonObject getCubeObj(List<HIMetadataCube> cubeList) {
        JsonArray cubeArray = new JsonArray();
        HIResourceMetadata hiResourceMetadata = null;
        for (HIMetadataCube item : cubeList) {
            JsonObject cubeObj = new JsonObject();
            hiResourceMetadata = item.getHiResourceMetadata();
            cubeObj.addProperty("setCache", item.getCache());
            cubeObj.addProperty("caption", item.getCaption());
            cubeObj.addProperty("defaultMeasure", item.getDefaultMeasure());
            cubeObj.addProperty("description", item.getDescription());
            cubeObj.addProperty("enabled", item.getEnabled());
            cubeObj.addProperty("cubeName", item.getName());
            JsonArray domainArray = JsonParser.parseString(item.getDomainName()).getAsJsonArray();
            cubeObj.add("domainName",domainArray);
            cubeObj.addProperty("id", item.getCubeId());
            handleDimensions(item, cubeObj);
            populateMeasures(item, cubeObj);
            cubeArray.add(cubeObj);
        }

        HIResource hiResource = hiResourceMetadata.getHiResource();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cubes", cubeArray);
        JsonObject md = new JsonObject();
        String mdUrl = hiResource.getResourceURL();
        String[] resourceURL = mdUrl.split("/");
        String fileName = resourceURL[resourceURL.length - 1];
        md.addProperty("location", mdUrl.replace("/" + fileName, ""));
        md.addProperty("metadataFileName", fileName);
        jsonObject.add("metadata", md);
        return jsonObject;
    }

    private void populateMeasures(HIMetadataCube item, JsonObject cubeObj) {
        List<HICubeMeasure> measures = item.getMeasures();
        if (measures != null) {
            JsonArray measuresArray = new JsonArray();
            for (HICubeMeasure mItem : measures) {
                JsonObject measureJson = new JsonObject();
                measureJson.addProperty("id",mItem.getMeasureId());
                measureJson.addProperty("visible",mItem.getVisible());
                measureJson.addProperty("aggregator",mItem.getAggregator());
                measureJson.addProperty("columnId",mItem.getColumnId());
                measureJson.addProperty("tableId",mItem.getTableId());
                measureJson.addProperty("measureName",mItem.getName());
                measureJson.addProperty("formatString",mItem.getFormatString());
                measureJson.addProperty("table",mItem.getMeasureTable());
                measureJson.addProperty("columnName",mItem.getColumnName());
                measureJson.addProperty("column",mItem.getColumn());
                measureJson.addProperty("measureType",mItem.getMeasureType());
                JsonArray synArray = JsonParser.parseString(mItem.getSynonyms()).getAsJsonArray();
                measureJson.add("synonyms", synArray);
                measureJson.addProperty("semanticType", mItem.getSemanticType());
                measureJson.addProperty("description", mItem.getDescription());
                measureJson.addProperty("example", mItem.getExample());
                measureJson.addProperty("filter", mItem.getFilter());
                measureJson.addProperty("formula", mItem.getFormula());
                JsonArray topicArray = JsonParser.parseString(mItem.getTopics()).getAsJsonArray();

                measureJson.add("topic",topicArray);

                measuresArray.add(measureJson);
            }
            cubeObj.add("measures",measuresArray);
        }
    }

    private void handleDimensions(HIMetadataCube item, JsonObject cubeObj) {
        List<HICubeDimension> dimensions = item.getDimensions();
        if (dimensions != null) {
            JsonArray dimArray = new JsonArray();
            for (HICubeDimension dItem : dimensions) {
                JsonObject dimJson = new JsonObject();
                dimJson.addProperty("dimensionName", dItem.getName());
                dimJson.addProperty("table", dItem.getTable());
                dimJson.addProperty("tableId", dItem.getTableId());
                dimJson.addProperty("columnName", dItem.getColumnName());
                dimJson.addProperty("column", dItem.getColumn());
                dimJson.addProperty("columnId", dItem.getColumnId());
                dimJson.addProperty("dimensionType", dItem.getType());
                dimJson.addProperty("visible", dItem.getVisible());
                dimJson.addProperty("id", dItem.getDimId());

                populateHierarchies(dItem, dimJson);
                dimArray.add(dimJson);
            }
            cubeObj.add("dimensions", dimArray);
        }
    }

    private void populateHierarchies(HICubeDimension dItem, JsonObject dimJson) {
        List<HIDimensionHierarchy> hierarchies = dItem.getHierarchies();
        if (hierarchies != null) {
            JsonArray hieArray = new JsonArray();
            for (HIDimensionHierarchy hieItem : hierarchies) {
                JsonObject hiJson = new JsonObject();
                hiJson.addProperty("hierarchyName", hieItem.getName());
                hiJson.addProperty("visible", hieItem.getVisible());
                hiJson.addProperty("primaryColumnId", hieItem.getPrimaryKeyColumnId());
                hiJson.addProperty("tableId", hieItem.getTableId());
                hiJson.addProperty("columnName", hieItem.getColumnName());
                hiJson.addProperty("column", hieItem.getColumn());
                hiJson.addProperty("table", hieItem.getHierarchyTable());
                hiJson.addProperty("hierarchyType", hieItem.getHierarchyType());
                hiJson.addProperty("id", hieItem.getHierarchyId());
                List<HICubeHierarchyLevel> levels = hieItem.getLevels();
                populateLevels(hiJson, levels);
                hieArray.add(hiJson);
            }

            dimJson.add("hierarchies", hieArray);
        }
    }

    private void populateLevels(JsonObject hiJson, List<HICubeHierarchyLevel> levels) {
        if (levels != null) {
            JsonArray levelsArray = new JsonArray();
            for (HICubeHierarchyLevel hLevel : levels) {
                JsonObject levelJson = new JsonObject();
                levelJson.addProperty("id", hLevel.getLevelId());
                levelJson.addProperty("levelName", hLevel.getName());
                levelJson.addProperty("columnId", hLevel.getColumnId());
                levelJson.addProperty("visible", hLevel.getVisible());
                levelJson.addProperty("levelType", hLevel.getLevelType());
                levelJson.addProperty("tableId", hLevel.getTableId());
                levelJson.addProperty("table", hLevel.getHierarchyTable());
                levelJson.addProperty("columnName", hLevel.getColumnName());
                levelJson.addProperty("column", hLevel.getColumn());
                JsonArray synArray = JsonParser.parseString(hLevel.getSynonyms()).getAsJsonArray();
                levelJson.add("synonyms", synArray);
                levelJson.addProperty("semanticType", hLevel.getSemanticType());
                levelJson.addProperty("description", hLevel.getDescription());
                levelJson.addProperty("example", hLevel.getExample());
                levelJson.addProperty("filter", hLevel.getFilter());
                levelJson.addProperty("formula", hLevel.getFormula());
                JsonArray topicArray = JsonParser.parseString(hLevel.getTopics()).getAsJsonArray();

                levelJson.add("topic",topicArray);

                levelsArray.add(levelJson);
            }
            hiJson.add("levels", levelsArray);
        }
    }

	@Override
	@Transactional
	public HIMetadataCube findCubeByResourceId(Integer resourceId) {
		HIMetadataCube cube =  hiCubeDao.findCubeByResourceId(resourceId);
		if( cube == null) {
			throw new EfwServiceException("Cube does not exists.");
		}
		return cube;
	}

	@Override
	@Transactional
	public List<HICubeDimension> findAllCubeDimentions(Integer cubeId) {
		return hiCubeDao.findAllCubeDimentions(cubeId);
	}

	@Override
	@Transactional
	public List<HIDimensionHierarchy> findAllDimHierarchy(Integer dimId) {
		return hiCubeDao.findAllDimHierarchy(dimId);
	}

	@Override
	@Transactional
	public List<HICubeHierarchyLevel> findAllHierarchyLevels(Integer hirId) {
		return hiCubeDao.findAllHierarchyLevels(hirId);
	}

	@Override
	@Transactional
	public List<HICubeMeasure> findAllCubeMeasuresByCubeId(Integer cubeId) {
		return hiCubeDao.findAllCubeMeasuresByCubeId(cubeId);
	}
}
