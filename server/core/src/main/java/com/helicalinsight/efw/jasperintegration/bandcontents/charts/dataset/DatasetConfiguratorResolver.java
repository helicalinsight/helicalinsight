package com.helicalinsight.efw.jasperintegration.bandcontents.charts.dataset;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.efw.exceptions.EfwServiceException;

import net.sf.jasperreports.engine.design.JRDesignChartDataset;

@Component
public class DatasetConfiguratorResolver {
	
	private final Map<Class<? extends JRDesignChartDataset>, DatasetConfigurator<?>> configuratorMap = new ConcurrentHashMap<>();
	
	
	@Autowired
	public DatasetConfiguratorResolver(List<DatasetConfigurator<?>> configurators) {
		for(DatasetConfigurator<?> configurator : configurators) {
			configuratorMap.put(configurator.getDatasetType(), configurator);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends JRDesignChartDataset> DatasetConfigurator<T> resolve(T dataset) {
		
		Class<?> clazz = dataset.getClass();
		
		while( clazz != null ) {
			DatasetConfigurator<?> configurator = configuratorMap.get(clazz);
			if( configurator != null ) {
				return  (DatasetConfigurator<T>) configurator;
			}
			clazz = clazz.getSuperclass();
		}
        throw new EfwServiceException("No DatasetConfigurator found for: " + dataset.getClass());
	}
	

}
