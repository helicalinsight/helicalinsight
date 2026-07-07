/*
package com.helicalinsight.adhoc;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.apache.commons.lang.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MultiConnectionMergeAdhocTableDB {
    private HIMetadataDTO metadata;

    public MultiConnectionMergeAdhocTableDB(HIMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public void merge() {
        //TODO need some clarification
        */
/*Boolean cached = this.metadata.getCached();
        if (cached == null || !cached) {
            return;
        }*//*


        MetadataDatabases database = this.metadata.getHiMetadata().getHiMetadataConnections().get(0).getMetadataDatabasesList().get(0);
        String name = this.metadata.getConnections().getId() + database.getName();
        updateTableName(database,name);
        updateMultipleDatabase();
        //TODO Need Some Clarification
      //  mergeTablesAndJoin();
        this.metadata.getHiMetadata().getHiMetadataConnections().get(0).getMetadataDatabasesList().get(0).setName("");
        this.metadata.getHiMetadata().setHiMetadataConnections(null);
    }

    private void mergeTablesAndJoin() {
        HIMetadataConnections connections = this.metadata.getHiMetadata().getHiMetadataConnections().get(0);
        if(connections==null){
            return;
        }
        List<MetadataDatabases> connectionDatabase = connections.getMetadataDatabasesList();
        if (connectionDatabase != null && connectionDatabase.size() > 0) {
            for (MetadataDatabases item : connectionDatabase) {
                //Database multiDatabase = item.getDatabase();
                List<HIMetadataTables> tableList = item.getMetadataTablesList();
                this.metadata.getHiMetadata().getHiMetadataConnections().get(0).getMetadataDatabasesList().get(0).getMetadataTablesList().addAll(tableList);
               */
/* List<Relationship> listOfRelations = multiDatabase.getRelationships().getListOfRelations();
                if(listOfRelations!=null){
                    Relationships relationships = this.metadata.getDatabase().getRelationships();
                    if(relationships==null){
                        relationships= ApplicationContextAccessor.getBean(Relationships.class);
                        relationships.setListOfRelations(new ArrayList<>());
                    }
                    relationships.getListOfRelations().addAll(listOfRelations);
                }*//*


            }
        }
    }


    private void updateMultipleDatabase() {
        HIMetadataConnections connections = this.metadata.getHiMetadata().getHiMetadataConnections().get(0);
        if(connections==null){
            return;
        }
        //TODO need some clarification
        List<MetadataDatabases> connectionDatabase = connections.getMetadataDatabasesList();
        if (connectionDatabase != null && connectionDatabase.size() > 0) {
            for (MetadataDatabases item : connectionDatabase) {
                String name = connections.getId() + item.getName();
                updateTableName(item,name);
            }
        }

    }

    private void updateTableName(MetadataDatabases database,String name) {

        List<HIMetadataTables> tableList = database.getMetadataTablesList();
        if (StringUtils.isNotBlank(name)) {
            String prefix = name + "_#1_";
            tablePrefix(tableList, prefix);
          //  relationPrefix(database, name, prefix);
        }
    }

    */
/*private void relationPrefix(MetadataDatabases database, String name, String prefix) {

        List<HIMetadataRelationships> listOfRelations = database.getMetadataRelationShipList();
        if (StringUtils.isNotBlank(name) && listOfRelations != null) {
            for (HIMetadataRelationships item : listOfRelations) {
item.setTable(prefix + item.getTable());
                item.setReferenceTable(prefix + item.getReferenceTable());

List<Join> joins = item.get;

                //joinPrefix(prefix, joins);
            }
        }
    }*//*


    //TODO need some clarification
 */
/*private void joinPrefix(String prefix, List<HIMetadataRelationships> joins) {
        for (HIMetadataRelationships join : joins) {
            HIMetadataTables leftTables = join.getLeftMetadataColumns().getHiMetadataTables();
            leftTables.setTableName(prefix + leftTa.getTable());
            RightTable rightTable = join.getRightTable();
            rightTable.setTable(prefix + rightTable.getTable());
        }
    }*//*



    private void tablePrefix(List<HIMetadataTables> tableList, String prefix) {
        for (HIMetadataTables item : tableList) {
            item.setTableName(prefix + item.getTableName());
        }
    }
}
*/
