package com.helicalinsight.adhoc.copypaste;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.datasource.service.EFWDConnectionService;

/**
 * This Class EfwdPlainConCopyHandler extends {@link HiResourceCopyHandler}.
 * this class handles the copying of EFWD connections, ensuring that both the main EFWD and 
 * its associated connections are properly copied and saved. It interacts with the database
 * through the provided service classes and ensures that relevant attributes are set appropriately
 * during the copy operation.
 */
@Component
public class EfwdPlainConCopyHandler extends HiResourceCopyHandler{
	
	@Autowired
	private HIResourceServiceDB hiResourceServiceDB;
	
	@Autowired
	private EFWDConnectionService efwdConnectionService;
	
	@Autowired
	private ResourceDTOMapper mapper;
	
	
	private static final String SQLJDBC="sql.jdbc";
	
	 /**
     * Copies the EFWD resource along with its connections.
     */
	@Override
	public void copyResource() {
		HIEfwdDTO source = getEfwdDto();
		HIEFWD hiefwd=  copyEfwd(source);
		List<EfwdConnDTO> connections =  efwdConnectionService.findConnectionByResourceId(source.getResourceId(), Boolean.TRUE, Boolean.FALSE);
		copyConnections(hiefwd, connections);
	}
	
	/**
     * Copies the HIEFWD connections resource.It iterates over each HIEfwdConnection in the list and copies it along with 
     * its associated sub-connections and saves it using the EFWDConnectionService.
     * 
     * @param createdHiefwd      	copied HIEFWD instance.
     * @param connections  	list of Efwd connections .
     */
	private void copyConnections(HIEFWD efwd, List<EfwdConnDTO> connections) {

		connections.forEach(c -> {
			HIEfwdConnection connection =  mapper.map(c);
			connection.setConnectionId(UUIDGenerator.getUuid());
			connection.setHiResourceEFWD(efwd);
			connection.setId(null);
			efwdConnectionService.saveEFWDConnection(connection);
				c.getPlainConnections().forEach(plain-> {
					if ( plain.getType().equalsIgnoreCase(SQLJDBC)) {
						EFWDConnSqlJDBC hiEfwdConnSqlJDBC= mapper.toSqlEntity(plain);
						hiEfwdConnSqlJDBC.setHiEfwdConnection(connection);
						hiEfwdConnSqlJDBC.setId(null);
						efwdConnectionService.save(hiEfwdConnSqlJDBC);
					}
					else {
						EFWDConnGroovy efwdConnGroovy= mapper.toGroovyEntity(plain);
						efwdConnGroovy.setHiEfwdConnection(connection);
						efwdConnGroovy.setId(null);
						efwdConnectionService.save(efwdConnGroovy);
					}
				});
		});
	}
	
	/**
	 * It creates a new instance of HIEFWD, sets the necessary attributes, and saves it using the HIResourceServiceDB.
	 * @param source        to copy the resource.
	 * @return new instance of {@code HIEFWD} .
	 */
	private HIEFWD copyEfwd(HIEfwdDTO source) {
		HIEFWD hiefwd=  mapper.map(source);
		hiefwd.setParentResource(getSource());
		hiefwd.setId(null);
		hiefwd.setCreatedDate(new Date());
		hiefwd.setLastUpdatedTime(new Date());
		hiefwd.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		hiResourceServiceDB.addHIResourceEFWD(hiefwd);
		return hiefwd;
	}

}
