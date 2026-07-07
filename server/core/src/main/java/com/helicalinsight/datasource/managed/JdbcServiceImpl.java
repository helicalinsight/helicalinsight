package com.helicalinsight.datasource.managed;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.GlobalDsGlobalIdFinder;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
public class JdbcServiceImpl implements IJdbcService {

    @Autowired
    private IJdbcDao jdbcDao;

    @Autowired
    private IGlobalXmlReader globalXmlReader;

    @Autowired
    private IJdbcConnectionService jdbcConnectionService;
    private Integer limit;

    @Override
    public JsonObject execute(@NotNull String connectionsFile, Integer connectionId, String sql) {
        return  this.jdbcDao.query(getConnection(connectionsFile, connectionId), sql);
    }
    @Override
    public ResultSet executeForResultSet(@NotNull String connectionsFile, Integer connectionId, String sql) {
        ResultSet result = this.jdbcDao.getResultSet(getConnection(connectionsFile, connectionId), sql);
        return result;
    }
    
    private final Connection getConnection(@NotNull String connectionsFile, Integer connectionId) {
    	
    	 Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
         String json = "";
         if(!dsTypeStorageDatabase){
             json=this.globalXmlReader.getDataSourceJson(connectionId);
         }else{
             GlobalDsGlobalIdFinder globalDsGlobalIdFinder = new GlobalDsGlobalIdFinder();
             json=globalDsGlobalIdFinder.getDataSourceJson(connectionId);
         }
         Connection connection = this.jdbcConnectionService.getDatabaseConnection(json);
         if (limit != null && (this.jdbcDao instanceof JdbcDaoImpl)) {
             ((JdbcDaoImpl) this.jdbcDao).setLimit(limit);
         }
         
         return connection;
    }
    

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    @Override
    public void streamForResultSet(String connectionFile, Integer connectionId, String sql, CallBack<ResultSet> callBack) {
         this.jdbcDao.streamResult(getConnection(connectionFile, connectionId), sql, callBack);
    }
}