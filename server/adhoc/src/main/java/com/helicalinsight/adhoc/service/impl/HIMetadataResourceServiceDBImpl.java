package com.helicalinsight.adhoc.service.impl;

import com.helicalinsight.adhoc.MetadataCacheStatus;
import com.helicalinsight.adhoc.MetadataDriverReferences;
import com.helicalinsight.adhoc.MetadataProperties;
import com.helicalinsight.adhoc.dao.HIMetadataResourceDAO;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.dto.HIMetadataConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataSecurityDTO;
import com.helicalinsight.admin.dto.MetadataDatabasesDTO;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.resourcedb.MetadataDumpDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HIMetadataResourceServiceDBImpl implements HIMetadataResourceServiceDB {
    
	@Autowired
    private HIMetadataResourceDAO hiResourceDBDAO;

	@Autowired
	private ResourceDTOMapper mapper;

    @Override
    @Transactional
    public Integer addHIResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        return hiResourceDBDAO.addHIResourceMetadata(hiResourceMetadata);
    }

    @Override
    @Transactional
    public Integer editHIResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        return hiResourceDBDAO.editHIResourceMetadata(hiResourceMetadata);
    }

    @Override
    @Transactional
    public void deleteHIResourceMetadata(Integer id) {
        hiResourceDBDAO.deleteHIResourceMetadata(id);
    }

    @Override
    @Transactional
    public Metadata getHIResourceMetadataByResourceId(Integer resourceId) {
        return hiResourceDBDAO.getHIResourceMetadataByResourceId(resourceId);

    }
    @Override
    @Transactional
    public HIResourceMetadata giveHIResourceMetadataByResourceId(Integer resourceId)  {
        return hiResourceDBDAO.giveHIResourceMetadataByResourceId(resourceId);

    }
    @Override
    @Transactional
    public HIResourceMetadataDTO giveHIResourceMetadataByResId(Integer resourceId)  {
        HIResourceMetadata md = hiResourceDBDAO.giveHIResourceMetadataByResourceId(resourceId);
        HIResourceMetadataDTO dto = new HIResourceMetadataDTO();
        dto.setIsCached(md.getCached());
        dto.setType(md.getType());
        
        List<HIMetadataSecurity> securityList =  md.getMetadataSecurityList();
        List<HIMetadataSecurityDTO> securityDTOList =  securityList.stream()
        		.map(security -> mapper.map(security)).toList();
        dto.setMetadataSecurityList(securityDTOList);
        dto.setConnectionType(md.getConnectionType());
        
        List<HIMetadataConnectionDTO> mdConnectionList = new ArrayList<>();
        List<MetadataDatabasesDTO> dbList = new ArrayList<>();
        
        md.getHiMetadataConnections().forEach(connection -> {
        	
        	HIMetadataConnectionDTO connectionDTO = mapper.map(connection);
        	List<MetadataDatabases> dbs =  connection.getMetadataDatabases();
        	dbList.addAll(dbs.stream().map(db -> mapper.map(db)).toList());
        	connectionDTO.setMetadataDatabases(dbList);
        	mdConnectionList.add(connectionDTO);
        });
        
        dto.setHiMetadataConnections(mdConnectionList);
        dto.setDatabaseType(md.getDatabaseType());
        dto.setLastUpdatedTime(md.getLastUpdatedTime());
        dto.setCreatedBy(md.getCreatedBy());
        dto.setId(md.getId());
        dto.setFileName(md.getFileName());
        return  dto;

    }

    @Override
    @Transactional
    public void addHIMetadataTables(HIMetadataTables hiMetadataTables) {
        hiResourceDBDAO.addHIMetadataTables(hiMetadataTables);
    }

    @Override
    @Transactional
    public void addHIMetadataRelationships(HIMetadataRelationships hiMetadataRelationships) {
        hiResourceDBDAO.addHIMetadataRelationships(hiMetadataRelationships);
    }
    

    @Override
    @Transactional
    public Integer addHIMetadataConnections(HIMetadataConnections hiMetadataConnections) {
        return hiResourceDBDAO.addHIMetadataConnections(hiMetadataConnections);
    }

    @Override
    @Transactional
    public Integer editHIMetadataConnections(HIMetadataConnections hiMetadataConnections) {
        return hiResourceDBDAO.editHIMetadataConnections(hiMetadataConnections);
    }

    @Override
    @Transactional
    public List<HIMetadataConnections> getHIMetadataConnections(Integer metadataId) {
        return hiResourceDBDAO.getHIMetadataConnections(metadataId);
    }

    @Override
    @Transactional
    public Integer addHIMetadataColumns(HIMetadataColumns hiMetadataColumns) {
        return hiResourceDBDAO.addHIMetadataColumns(hiMetadataColumns);
    }
    @Override
    @Transactional
    public void editHIMetadataColumns(HIMetadataColumns hiMetadataColumns) {
         hiResourceDBDAO.editHIMetadataColumns(hiMetadataColumns);
    }


    @Override
    @Transactional
    public Integer saveHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal) {
        return hiResourceDBDAO.saveHIMetadataConnectionGlobal(hiMetadataConnectionGlobal);
    }
    @Override
    @Transactional
    public Integer editHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal) {
        return hiResourceDBDAO.editHIMetadataConnectionGlobal(hiMetadataConnectionGlobal);
    }
    @Override
    @Transactional
    public HIMetadataConnectionGlobal getHIMetadataConnectionGlobal(Integer metadataId,String xmlGlobalConnectionId) {
        return hiResourceDBDAO.getHIMetadataConnectionGlobal(metadataId,xmlGlobalConnectionId);
    }

    @Override
    @Transactional
    public MetadataDatabases getHIMetadataDatabases(Integer metadataId, String connectionId) {
        return hiResourceDBDAO.getHIMetadataDatabases(metadataId,connectionId);
    }

    @Override
    @Transactional
    public Integer addHIMetadataDatabases(MetadataDatabases hiMetadataDatabases) {

        return hiResourceDBDAO.addHIMetadataDatabases(hiMetadataDatabases);
    }

    @Override
    @Transactional
    public void editHIMetadataDatabases(MetadataDatabases metadataDatabase) {
         hiResourceDBDAO.editHIMetadataDatabases(metadataDatabase);
    }

    @Override
    @Transactional
    public void editHIMetadataTables(HIMetadataTables hiMetadataTables) {
        hiResourceDBDAO.editHIMetadataTables(hiMetadataTables);
    }
    @Override
    @Transactional
    public List<HIMetadataTables> getMetadataTablesList(Integer id, Integer dbId)  {
       return hiResourceDBDAO.getMetadataTablesList(id, dbId);
    }

    @Override
    @Transactional
    public List<HIMetadataView> getMetadataViewList(Integer id, Integer dbId)  {
        return hiResourceDBDAO.getMetadataViewList(id,dbId);
    }
    @Override
    @Transactional
    public void addHIMetadataView(HIMetadataView mdview)  {
     hiResourceDBDAO.addHIMetadataView(mdview);
    }
    @Override
    @Transactional

    public void editHIMetadataView(HIMetadataView mdview)  {
        hiResourceDBDAO.editHIMetadataView(mdview);
    }

    @Override
    @Transactional
    public void deleteHIMetadataColumn(Integer columnId,Integer metadataId) {
        hiResourceDBDAO.deleteHIMetadataColumn(columnId,metadataId);
    }

    @Override
    @Transactional
    public void deleteHIMetadataTable(Integer tableId, Integer dbId) {
        hiResourceDBDAO.deleteHIMetadataTable(tableId, dbId);
    }

    @Override
    @Transactional
    public void deleteAllRelationships(Integer metadataId, Integer dbId) {
        hiResourceDBDAO.deleteAllRelationships(metadataId, dbId);
    }

    @Override
    @Transactional
    public List<HIMetadataColumns> getMetadataColumnsList(Integer metadataId,Integer dbId) {
        return hiResourceDBDAO.getMetadataColumnsList(metadataId,dbId);

    }

    @Override
    @Transactional
    public List<HIMetadataSecurity> getMetaSecurity(Integer metadataId) {
        return hiResourceDBDAO.getMetaSecurity(metadataId);
    }

    @Override
    @Transactional
    public void addHIMetadataSecurity(HIMetadataSecurity metadataSecurity) {
         hiResourceDBDAO.addHIMetadataSecurity(metadataSecurity);
    }

    @Override
    @Transactional
    public void editHIMetadataSecurity(HIMetadataSecurity metadataSecurity) {
         hiResourceDBDAO.editHIMetadataSecurity(metadataSecurity);
    }

    @Override
    @Transactional
    public void deleteSecurity(Integer id) {
        hiResourceDBDAO.deleteSecurity(id);
    }

    @Override
    @Transactional
    public void addCube(HIMetadataCube cube) {
        hiResourceDBDAO.addCube(cube);
    }

    @Override
    @Transactional
    public void add(Object obj) {
        hiResourceDBDAO.add(obj);
    }

	@Override
	@Transactional
	public HIMetadataConnectionEFWD getHIMetadataConnectionEFWD(Integer efwdConnectionId) {
		return hiResourceDBDAO.getHIMetadataConnectionEFWD(efwdConnectionId);
	}

	@Override
	@Transactional
	public Integer saveHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd) {
		return hiResourceDBDAO.saveHIMetadataConnectionEfwd(metadataConnectionEfwd);
	}

	@Override
	@Transactional
	public Integer editHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd) {
		return hiResourceDBDAO.editHIMetadataConnectionEfwd(metadataConnectionEfwd);
	}

	@Override
	@Transactional
	public void deleteMetadataGlobalConnection(HIMetadataConnectionGlobal globalCon) {
		hiResourceDBDAO.deleteMetadataGlobalConnection(globalCon);
	}

	@Override
	@Transactional
	public void deleteMetadataEfwdConnection(HIMetadataConnectionEFWD efwdCon) {
		hiResourceDBDAO.deleteMetadataEfwdConnection(efwdCon);
	}

	@Override
	@Transactional
	public HIMetadataConnections getHIMetadataConnection(Integer metadataId, Integer dsId, String dsType) {
		return hiResourceDBDAO.getHIMetadataConnection(metadataId,dsId,dsType);
	}

	@Override
	@Transactional
	public List<MetadataDumpDTO> getDumpedMetadataList() {
		return hiResourceDBDAO.getDumpedMetadataList();
	}

	@Override
	@Transactional
	public void deleteAllExternalRelationships(Integer metadataId) {
		 hiResourceDBDAO.deleteAllExternalRelationships(metadataId);
	}


	@Override
	@Transactional
	public void setCache(Integer metadataId,boolean value) {
		hiResourceDBDAO.setCache(metadataId,value);
	}

	@Override
	@Transactional
	public Integer removeMetadataConnection(String metadataId, String dataSource,String mode) {
		 return hiResourceDBDAO.removeMetadataConnection(metadataId,dataSource,mode);
	}

	@Override
	@Transactional
	public HIMetadataRelationships findJoinById(String joinId) {
		return hiResourceDBDAO.findJoinById(joinId);
	}

	@Override
	@Transactional
	public void deleteHIMetadataRelationship(List<Integer> joinsToDelete) {
			hiResourceDBDAO.deleteHIMetadataRelationship(joinsToDelete);
	}

	@Override
	@Transactional
	public MetadataDatabases getHIMetadataDatabaseById(Integer dbId) {
		return hiResourceDBDAO.getHIMetadataDatabaseById(dbId);
	}

	@Override
	@Transactional
	public Table findTableById(Integer id) {
		return hiResourceDBDAO.findTableById(id);
	}

	@Override
	@Transactional
	public HIMetadataColumns findColumnById(Integer colId) {
		return hiResourceDBDAO.findColumnById(colId);
	}

	@Override
	@Transactional
	public List<HIMetadataRelationships> getRelationshipListByMetadataIdAndDbId(Integer mdId, Integer dbId) {
		return hiResourceDBDAO.getRelationshipListByMetadataIdAndDbId(mdId,dbId);
	}

	@Transactional
	@Override
	public void deleteHIMetadataColumnById(Integer id) {
		hiResourceDBDAO.deleteHIMetadataColumnById(id);
	}

	@Transactional
	@Override
	public List<HIMetadataColumns> getMetadataColumns(Integer tableId, Integer metadataId) {
		return hiResourceDBDAO.getMetadataColumns(tableId, metadataId);
	}

	@Transactional
	@Override
	public void deleteAllSecuritiesByMetadataId(Integer metadataId) {
		hiResourceDBDAO.deleteAllSecuritiesByMetadataId(metadataId);
	}

	@Transactional
	@Override
	public MetadataCacheStatus getMetadataCacheStatusAndUpdateTime(Integer resourceId) {
		 return hiResourceDBDAO.getMetadataCacheStatusAndUpdateTime(resourceId);
	}

	@Transactional
	@Override
	public MetadataProperties loadHiResourceMetadataPropertiesById(Integer resourceId) {
		return hiResourceDBDAO.loadHiResourceMetadataPropertiesById(resourceId);
	}

	@Transactional
	@Override
	public List<ConnectionDetails> getConnectionList(Integer resourceId) {
		return hiResourceDBDAO.getMetadataConnection(resourceId);
	}
	@Transactional
	@Override
	public MetadataDriverReferences getConnectionRefAndDriver(Integer metadaid) {
		return hiResourceDBDAO.getConnectionRefAndDriver(metadaid);
	}

	@Transactional
	@Override
	public Integer getViewEntryByViewNameAndDbIdAndMetadataId(String viewName, Integer dbId,
			Integer metadataId) {
		return hiResourceDBDAO.getViewEntryByViewNameAndDbIdAndMetadataId(viewName, dbId, metadataId);
	}

	@Transactional
	@Override
	public List<HIMetadataView> getMetadataViewList(Integer metadata) {
		return hiResourceDBDAO.getMetadataViewList(metadata);
	}
}

