package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIAuditDetails;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIMetadataSecurity;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIMetadataView;
import com.helicalinsight.admin.model.HIRelationshipPositions;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.admin.service.AuditService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * This class HiResourceMetadataCopyHandler extends
 * {@link HiResourceCopyHandler} Handles the copying of HIResource metadata,
 * including metadata connections, databases, tables, views, columns, and
 * security. This class ensures that metadata is properly copied to the
 * destination.
 */
@Component
public class HiResourceMetadataCopyHandler extends HiResourceCopyHandler {

	@Autowired
	HIResourceServiceDB hiResourceServiceDB;

	@Autowired
	HIMetadataResourceServiceDB hiMetadataResourceServiceDB;

	@Autowired
	AuditService auditService;

	@Autowired
	PhaseDetailsService phaseDetailsService;

	@Autowired
	HiResourceCCPUtility hiResourceCCPUtility;

	private static final String GLOBAL = "global.jdbc";

	/**
	 * it checks for unique ids and it also checks already copied or not. This
	 * method ensures that resources are copied to the destination while handling
	 * conflicts.
	 */
	@Override
	public void copyResource() {

		HIResource source = getSource();
		HIResource destination = getDestinationResourceId();
		if (source.getParentId() != destination.getResourceId()) {
			HIResource isSameResouceNameAlreadyExisted = hiResourceServiceDB.getResourceByUrl(getPrefix(),
					Boolean.FALSE);
			if (isSameResouceNameAlreadyExisted != null && !isSameResouceNameAlreadyExisted.getDeleted()) {
				whenNotConflictSkip(isSameResouceNameAlreadyExisted);
			} else {
				whenSameResourceExisted(isSameResouceNameAlreadyExisted);
			}
		} else
			doCopy(getPrefix(), getSourcePath());
	}

	private void whenSameResourceExisted(HIResource isSameResouceNameAlreadyExisted) {
		if (isSameResouceNameAlreadyExisted != null) {
			Format secondsFormat = new SimpleDateFormat("ss");
			String generatedSourcePath = DBProcessor.checkAndReplaceSpecialChars(getSourcePath()).trim() + "_"
					+ secondsFormat.format(new Date()).substring(0, 2);
			String generatedUrl = getPrefix().substring(0, getPrefix().length() - getSourcePath().length())
					+ generatedSourcePath;
			doCopy(generatedUrl, generatedSourcePath);
		} else
			doCopy(getPrefix(), getSourcePath());
	}

	private void whenNotConflictSkip(HIResource isSameResouceNameAlreadyExisted) {
		if (!getOnConflictSkip()) {
			String sourcePath = UUIDGenerator.getUuid();
			String prefixUrl = getDestinationResourceId().getResourceURL() + "/" + sourcePath
					+ getSource().getResourceType().getExtension();
			HIResource hiResource = doCopy(prefixUrl, sourcePath);
			hiResourceCCPUtility.deleteOverridenResourceAndUpdateCopiedResource(hiResource,
					isSameResouceNameAlreadyExisted);
		}
	}

	/**
	 * Performs the copy operation of metadata resource to the destination.
	 * 
	 * @param prefixUrl  url to create new replica.
	 * @param sourcePath to set the path in new replica.
	 * @return newly created HIResource instance.
	 */
	private HIResource doCopy(String prefixUrl, String sourcePath) {
		HIResource hiResource = hiResourceCCPUtility.prepareNewReplica(getSource(), getDestinationResourceId(),
				prefixUrl, sourcePath);
		hiResourceServiceDB.addHIResource(hiResource);
		HIResourceMetadata sourceMetadata = hiMetadataResourceServiceDB
				.giveHIResourceMetadataByResourceId(getSource().getResourceId());
		sourceMetadata.setFileName(hiResource.getTitle());
		hiResourceCCPUtility.saveSecurityInfoReplica(getSource().getResourceId(), hiResource);
		saveCoreDependends(hiResource, sourceMetadata);
		copyAuditEntries(hiResource);
		if (getSource().getDeleted())
			hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());
		return hiResource;
	}

	/**
	 * Copies the audit entries associated with the source HIResource to the
	 * destination HIResource.
	 * 
	 * @param hiResource The destination HIResource.
	 */
	private void copyAuditEntries(HIResource hiResource) {
		List<HIAuditDetails> auditDetails = auditService.fetchAllBasedOnResourceId(getSource().getResourceId());
		auditDetails.forEach(e -> {
			HIAuditDetails audit = HiResourceCCPUtility.prepareEntity(e, HIAuditDetails.class);
			if(audit==null) audit = new HIAuditDetails();
			audit.setHiResource(hiResource);
			audit.setLastPhaseId(e.getLastPhaseId());
			audit.setPhaseId(e.getPhaseId());
			audit.setTriggeredBy(e.getTriggeredBy());
			audit.setId(null);
			auditService.save(audit);
		});

		List<HIResourcePhaseStatus> hiResourcePhaseStatus = phaseDetailsService
				.findAllResourcePhasesByResourceId(getSource().getResourceId());
		hiResourcePhaseStatus.forEach(e -> {
			HIResourcePhaseStatus phase = HiResourceCCPUtility.prepareEntity(e, HIResourcePhaseStatus.class);
			if(phase==null) phase = new HIResourcePhaseStatus();
			phase.setHiPhase(e.getHiPhase());
			phase.setUser(e.getUser());
			phase.setHiResource(hiResource);
			phase.setId(null);
			phase.setLastUpdatedDate(new Date());
			phaseDetailsService.addHIResourcePhaseStatus(phase);
		});
	}

	/**
	 * Saves the core dependencies of the copied metadata, including metadata
	 * connections, databases, tables, views, columns, relationships, and security.
	 * 
	 * @param createdHiResource newly created HIResource instance.
	 * @param sourceMetadata    HIResourceMetadata instance provides id.
	 */
	private void saveCoreDependends(HIResource createdHiResource, HIResourceMetadata sourceMetadata) {
		Map<Integer, Integer> colSecurityMap = new HashMap<>();
		Map<Integer, Integer> tabSecurityMap = new HashMap<>();
		List<HIMetadataConnections> conns = hiMetadataResourceServiceDB
				.getHIMetadataConnections(sourceMetadata.getId());
		HIResourceMetadata createdMetadata = saveMetadata(createdHiResource, sourceMetadata);
		List<HIMetadataSecurity> metadataSecurities = hiMetadataResourceServiceDB
				.getMetaSecurity(sourceMetadata.getId());
		segragateExpressionOns(colSecurityMap, tabSecurityMap, metadataSecurities);
		saveConnsDbsTabsAndViews(conns, createdMetadata, sourceMetadata, colSecurityMap, tabSecurityMap);
		saveAllMetadataSecurities(metadataSecurities, createdMetadata, colSecurityMap, tabSecurityMap);
	}

	private void segragateExpressionOns(Map<Integer, Integer> colSecurityMap, Map<Integer, Integer> tabSecurityMap,
			List<HIMetadataSecurity> metadataSecurities) {
		for (HIMetadataSecurity sec : metadataSecurities) {
			String[] expressionOnArr = sec.getExpressionOn().split(",");
			for (String expressionOn : expressionOnArr) {

				if (sec.getExpressionType().equals("column")) {
					colSecurityMap.put(Integer.parseInt(expressionOn), -1);
				} else {
					tabSecurityMap.put(Integer.parseInt(expressionOn), -1);
				}
			}

		}
	}

	/**
	 * Saves the connections, databases, tables, views, columns, and relationships
	 * associated with the copied metadata.
	 * 
	 * @param conns           List of metadata connections.
	 * @param createdMetadata newly created HIResourceMetadata instance used to set
	 *                        in connection.
	 * @param sourceMetadata  HIResourceMetadata instance to fetch from .
	 */
	private void saveConnsDbsTabsAndViews(List<HIMetadataConnections> conns, HIResourceMetadata createdMetadata,
			HIResourceMetadata sourceMetadata, Map<Integer, Integer> colSecurityMap,
			Map<Integer, Integer> tabSecurityMap) {
		List<MetadataDatabases> sourceDbs = new ArrayList<>();
		List<MetadataDatabases> destDbs = new ArrayList<>();
		Map<Integer, HIMetadataColumns> createdMdCols = new HashMap<>();

		for (HIMetadataConnections e : conns) {

			copyMetadataConDetails(createdMetadata, sourceMetadata, sourceDbs, destDbs, e);
		}

		for (Integer index = 0; index < sourceDbs.size(); index++) {

			copyDatabase(createdMetadata, sourceMetadata, colSecurityMap, tabSecurityMap, sourceDbs, destDbs,
					createdMdCols, index);

		}
	}

	private void copyDatabase(HIResourceMetadata createdMetadata, HIResourceMetadata sourceMetadata,
			Map<Integer, Integer> colSecurityMap, Map<Integer, Integer> tabSecurityMap,
			List<MetadataDatabases> sourceDbs, List<MetadataDatabases> destDbs,
			Map<Integer, HIMetadataColumns> createdMdCols, Integer index) {
		List<HIMetadataTables> tables = hiMetadataResourceServiceDB.getMetadataTablesList(sourceMetadata.getId(),
				sourceDbs.get(index).getId());
		List<HIMetadataRelationships> relations = hiMetadataResourceServiceDB
				.getRelationshipListByMetadataIdAndDbId(sourceMetadata.getId(), sourceDbs.get(index).getId());
		Set<Integer> uniqueColId = relations.stream().map(e -> e.getLeftMetadataColumns().getId())
				.collect(Collectors.toSet());
		uniqueColId
				.addAll(relations.stream().map(e -> e.getRightMetadataColumns().getId()).collect(Collectors.toSet()));

		for (HIMetadataTables table : tables) {

			handleTable(createdMetadata, sourceMetadata, colSecurityMap, tabSecurityMap, destDbs, createdMdCols, index,
					uniqueColId, table);
		}

		List<HIMetadataView> views = hiMetadataResourceServiceDB.getMetadataViewList(sourceMetadata.getId(),
				sourceDbs.get(index).getId());
		for (HIMetadataView view : views) {
			copyView(createdMetadata, destDbs, index, view);
		}

		for (HIMetadataRelationships relation : relations) {
			handleRelationships(createdMetadata, destDbs, createdMdCols, index, relation);
		}

		createdMdCols.clear();
	}

	private void handleRelationships(HIResourceMetadata createdMetadata, List<MetadataDatabases> destDbs,
			Map<Integer, HIMetadataColumns> createdMdCols, Integer index, HIMetadataRelationships relation) {
		HIMetadataRelationships hrelation = HiResourceCCPUtility.prepareEntity(relation,
				HIMetadataRelationships.class);
		hrelation.setId(null);
		hrelation.setHiResourceMetadata(createdMetadata);
		hrelation.setHiMetadataDatabases(destDbs.get(index));
		hrelation.setLeftMetadataColumns(createdMdCols.get(relation.getLeftMetadataColumns().getId()));
		hrelation.setRightMetadataColumns(createdMdCols.get(relation.getRightMetadataColumns().getId()));
		hrelation.setJoinsPositions(prepare(relation, createdMetadata.getId()));
		hrelation.setId(null);
		hiMetadataResourceServiceDB.addHIMetadataRelationships(hrelation);
	}

	private void copyView(HIResourceMetadata createdMetadata, List<MetadataDatabases> destDbs, Integer index,
			HIMetadataView view) {
		HIMetadataView hview = HiResourceCCPUtility.prepareEntity(view, HIMetadataView.class);
		hview.setHiMetadataDatabases(destDbs.get(index));
		hview.setHiResourceMetadata(createdMetadata);
		Integer viewEntryId = hiMetadataResourceServiceDB.getViewEntryByViewNameAndDbIdAndMetadataId(view.getViewName(),
				destDbs.get(index).getId(), createdMetadata.getId());
		hview.setViewId("" + viewEntryId);
		hview.setId(null);
		hiMetadataResourceServiceDB.addHIMetadataView(hview);
	}

	private void handleTable(HIResourceMetadata createdMetadata, HIResourceMetadata sourceMetadata,
			Map<Integer, Integer> colSecurityMap, Map<Integer, Integer> tabSecurityMap, List<MetadataDatabases> destDbs,
			Map<Integer, HIMetadataColumns> createdMdCols, Integer index, Set<Integer> uniqueColId,
			HIMetadataTables table) {
		HIMetadataTables tab = HiResourceCCPUtility.prepareEntity(table, HIMetadataTables.class);
		tab.setColumnsList(null);
		tab.setHiMetadataDatabases(destDbs.get(index));
		tab.setHiResourceMetadata(createdMetadata);
		tab.setId(null);
		hiMetadataResourceServiceDB.addHIMetadataTables(tab);

		List<HIMetadataColumns> cols = hiMetadataResourceServiceDB.getMetadataColumns(table.getId(),
				sourceMetadata.getId());
		for (HIMetadataColumns col : cols) {
			copyColumn(createdMetadata, colSecurityMap, createdMdCols, uniqueColId, tab, col);
		}
		if (tabSecurityMap.containsKey(table.getId()))
			tabSecurityMap.replace(table.getId(), tab.getId());
	}

	private void copyColumn(HIResourceMetadata createdMetadata, Map<Integer, Integer> colSecurityMap,
			Map<Integer, HIMetadataColumns> createdMdCols, Set<Integer> uniqueColId, HIMetadataTables tab,
			HIMetadataColumns col) {
		HIMetadataColumns hcol = HiResourceCCPUtility.prepareEntity(col, HIMetadataColumns.class);
		hcol.setHiMetadataTables(tab);
		hcol.setHiResourceMetadata(createdMetadata);
		hcol.setId(null);
		hiMetadataResourceServiceDB.addHIMetadataColumns(hcol);
		if (uniqueColId.contains(col.getId())) {
			createdMdCols.put(col.getId(), hcol);
		}
		if (colSecurityMap.containsKey(col.getId()))
			colSecurityMap.replace(col.getId(), hcol.getId());
	}

	private void copyMetadataConDetails(HIResourceMetadata createdMetadata, HIResourceMetadata sourceMetadata,
			List<MetadataDatabases> sourceDbs, List<MetadataDatabases> destDbs, HIMetadataConnections e) {
		HIMetadataConnections conn = HiResourceCCPUtility.prepareEntity(e, HIMetadataConnections.class);
		conn.setMetadataConnectionEfwd(null);
		conn.setMetadataDatabases(null);
		conn.setMetadataGlobalConnList(null);
		conn.setHiResourceMetadata(createdMetadata);
		conn.setId(null);
		hiMetadataResourceServiceDB.addHIMetadataConnections(conn);
		MetadataDatabases db = hiMetadataResourceServiceDB.getHIMetadataDatabases(sourceMetadata.getId(),
				String.valueOf(e.getId()));
		sourceDbs.add(db);

		if (e.getConnectionType().equals(GLOBAL)) {
			handleGlobalCons(e, conn);
		} else {
			handleEfwdCons(e, conn);
		}

		MetadataDatabases mdb = HiResourceCCPUtility.prepareEntity(db, MetadataDatabases.class);
		mdb.setHiResourceMetadata(createdMetadata);
		mdb.setMetadataRelationShipList(null);
		mdb.setMetadataTablesList(null);
		mdb.setMetadataViewList(null);
		mdb.setMetadataConnections(conn);
		mdb.setId(null);
		hiMetadataResourceServiceDB.addHIMetadataDatabases(mdb);
		destDbs.add(mdb);
	}

	private void handleEfwdCons(HIMetadataConnections e, HIMetadataConnections conn) {
		HIMetadataConnectionEFWD efwd = hiMetadataResourceServiceDB.getHIMetadataConnectionEFWD(e.getId());
		HIMetadataConnectionEFWD con = HiResourceCCPUtility.prepareEntity(efwd,
				HIMetadataConnectionEFWD.class);
		con.setHiEfwdConnection(efwd.getHiEfwdConnection());
		con.setHiMetadataConnections(conn);
		con.setId(null);
		hiMetadataResourceServiceDB.saveHIMetadataConnectionEfwd(con);
	}

	private void handleGlobalCons(HIMetadataConnections e, HIMetadataConnections conn) {
		HIMetadataConnectionGlobal globalCon = hiMetadataResourceServiceDB.getHIMetadataConnectionGlobal(e.getId(),
				null);
		HIMetadataConnectionGlobal con = HiResourceCCPUtility.prepareEntity(globalCon,
				HIMetadataConnectionGlobal.class);
		con.setGlobalConnections(globalCon.getGlobalConnections());
		con.setHiMetadataConnections(conn);
		con.setId(null);
		hiMetadataResourceServiceDB.saveHIMetadataConnectionGlobal(con);
	}

	/**
	 * Prepares and saves the relationship positions for the metadata relationships.
	 * 
	 * @param relation   HIMetadataRelationships for entity prepare..
	 * @param metadataId ID of the metadata.
	 * @return The prepared HIRelationshipPositions instance.
	 */
	private HIRelationshipPositions prepare(HIMetadataRelationships relation, Integer metadataId) {

		HIRelationshipPositions positions = HiResourceCCPUtility.prepareEntity(relation.getJoinsPositions(),
				HIRelationshipPositions.class);
		positions.setMetadataId(metadataId);
		return positions;
	}

	/**
	 * Saves all metadata securities associated with the copied metadata.
	 * 
	 * @param metadataSecurities List of metadata securities.
	 * @param createdMetadata    The newly created HIResourceMetadata instance.
	 * @param colSecurityMap     provides id for column
	 * @param tabSecurityMap	 map provides table id.
	 */
	private void saveAllMetadataSecurities(List<HIMetadataSecurity> metadataSecurities,
			HIResourceMetadata createdMetadata, Map<Integer, Integer> colSecurityMap,
			Map<Integer, Integer> tabSecurityMap) {

		for (HIMetadataSecurity sec : metadataSecurities) {

			HIMetadataSecurity createdSec = HiResourceCCPUtility.prepareEntity(sec, HIMetadataSecurity.class);

			String[] expressionOnArr = sec.getExpressionOn().split(",");
			StringBuilder sb = new StringBuilder();
			for (String expressionOn : expressionOnArr) {
				String newId;
				if (sec.getExpressionType().equals("column")) {
					newId = "" + colSecurityMap.get(Integer.valueOf(expressionOn));
				} else {
					newId = "" + tabSecurityMap.get(Integer.valueOf(expressionOn));
				}
				sb.append(newId + ",");
			}
			String newExpressionOn = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
			createdSec.setExpressionOn(newExpressionOn);
			createdSec.setHiResourceMetadata(createdMetadata);
			createdSec.setId(null);
			hiMetadataResourceServiceDB.addHIMetadataSecurity(createdSec);
		}
	}

	/**
	 * Saves the metadata including metadata connections, databases, tables, views,
	 * and columns.
	 * 
	 * @param createdHiResource newly created HIResource instance used to to set
	 *                          {@code HIResourceMetadata}.
	 * @param source            source HIResourceMetadata instance used to generate
	 *                          entity so that metadata can saved.
	 * @return newly created HIResourceMetadata instance.
	 */
	private HIResourceMetadata saveMetadata(HIResource createdHiResource, HIResourceMetadata source) {

		HIResourceMetadata hiResourceMetadata = new HIResourceMetadata();
		hiResourceMetadata.setCached(source.getCached());
		hiResourceMetadata.setFileName(source.getFileName());
		hiResourceMetadata.setHiResource(createdHiResource);
		hiResourceMetadata.setHiMetadataConnections(null);
		hiResourceMetadata.setMetadataSecurityList(null);
		hiResourceMetadata.setId(null);
		hiResourceMetadata.setLastUpdatedTime(new Date());
		hiResourceMetadata.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		hiResourceMetadata.setDatabaseType(source.getDatabaseType());
		hiResourceMetadata.setType(source.getType());
		hiResourceMetadata.setConnectionType(source.getConnectionType());
		hiMetadataResourceServiceDB.addHIResourceMetadata(hiResourceMetadata);
		return hiResourceMetadata;
	}
}
