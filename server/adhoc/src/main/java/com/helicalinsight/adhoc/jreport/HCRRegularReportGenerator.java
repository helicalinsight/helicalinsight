package com.helicalinsight.adhoc.jreport;

import java.sql.ResultSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.CacheKeyDTO;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.store.ApplicationCacheStore;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.jasperintegration.JReportExecutionContext;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;
import com.helicalinsight.stream.EventType;
import com.helicalinsight.stream.StreamEvent;
import com.helicalinsight.stream.StreamSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fill.JRBaseFiller;

/**
 * The `HCRRegularReportGenerator` class implements {@link IHCRGenerator} and is responsible for generating
 * HCR (Helical Canned Report) reports using the provided form data. It manages caching and retrieves report data,
 * providing a mechanism to refresh the cache when necessary.
 * 
 * Created by author on 12/5/2019.
 * @author Rajesh
 */
@Component("HCRRegularReportGenerator")
@Scope("prototype")
public class HCRRegularReportGenerator implements IHCRGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HCRRegularReportGenerator.class);
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private HCRHelper hcrHelper;
    
    @Autowired
    private TaskExecutorService taskExecutorService;
    
    private final ApplicationCacheStore store = CacheUtils.getCacheStore();
    

    /**
     * Generates an HCR report based on the provided form data. This method handles caching, refreshing cache if required,
     * and processes the report generation.
     * 
     * @param formData 		 JSON object containing form data for report generation
     * @return the JSON object containing the generated report details
     */
    @Override
	public JsonObject generateHCReport(JsonObject formData) {
    	
    	String cacheKey = GsonUtility.optStringValue(formData, "designCacheKey", "");
    	boolean cacheRefresh = GsonUtility.optBooleanValue(formData, "refresh", false);
    	
    	if(StringUtils.isNotBlank(cacheKey)) {
    		Object object = store.get(cacheKey);
    		if ( object != null &&  object instanceof JReportExecutionContext context) {
    			if (!cacheRefresh) {
	    			logger.debug("Found design cache for key : {}" , cacheKey);
	    			JsonObject clonedFormData = formData;
	    			clonedFormData.addProperty("format", context.getFormat());
	    			return hcrHelper.provideResponseUsingPrint(clonedFormData,context.getJasperPrint(), context.getTotalPages());
    			}
    			else {
    				store.remove(cacheKey);
    			}
    		}
    		else {
    			throw new EfwServiceException("Design cache not found for cache id : " + cacheKey);
    		}
    		
    	}
    	
        formData= HCRUtils.checkForDefaultValues(formData);
        HCRUtils.loadFontDirectories();
        return regularFlow(formData);
	}
    /**
     * Generates a JasperPrint object based on the provided form data.
     * 
     * @param formData 		 JSON object containing form data for report generation
     * @return the generated JasperPrint object
     */
	@Override
    public JasperPrint generateHCRPrint(JsonObject formData) {
        ResultSet resultSet =  prepareExecuteJasperReport(formData);
        return  hcrHelper.provideJasperPrint(formData, resultSet);
    }
	
	private JsonObject regularFlow(JsonObject formData) {
        JasperPrint jasperPrint = null;
        String uuid = hcrHelper.getUuid(formData);
        JsonObject designerChange = GsonUtility.optJsonObject(formData,"designerChange");
        String printUUID = designerChange!=null?GsonUtility.optStringValue(designerChange,"printUUID", null):null;
       
        logger.debug("requestedPrintId :" + printUUID);
        printUUID = printUUID != null ? printUUID : uuid;
        ResultSet resultSet = prepareExecuteJasperReport(formData);
        JsonObject jsonObject = hcrHelper.prepareResponse(formData, resultSet, jasperPrint, printUUID);
        return jsonObject;
    }
	
	
	/**
     * Prepares and executes the Jasper report based on the provided form data, handling cache where applicable.
     * 
     * @param formData 			 JSON object containing form data for report generation
     * @return the ResultSet obtained from executing the report
     */
	public ResultSet prepareExecuteJasperReport(JsonObject formData) {
        boolean refreshCache = GsonUtility.optBooleanValue(formData,"refresh", false);
        Boolean cacheRequired = false;
        CacheManager cacheManager = null;
        ResultSet resultSet = null;
        JsonObject connectionDetails = formData.getAsJsonObject("connectionDetails");
        if (!connectionDetails.entrySet().isEmpty()) {
        	String tempUuid = GsonUtility.optStringValue(connectionDetails, "temp_uuid","");
        	if ( StringUtils.isNotBlank(tempUuid)) {
	            String cacheName = CacheUtils.getCacheNameFromConnection(formData);
	            cacheManager = CacheUtils.getCacheManager("/hcrResultSet");
	            cacheManager.setRequestData(formData.toString());
	            Cache cache = cacheHelper.prepareCacheFromRequest(cacheManager);
	            cacheRequired = cacheHelper.processCache(null, null, cacheName, refreshCache, cache, cacheManager);
	            String designCacheKey = cacheHelper.designCacheKeyFor(cache);
	            if (designCacheKey != null) {
	                formData.addProperty("designCacheKey", designCacheKey);
	            }
	            if (cacheRequired && cacheManager instanceof HCRQueryProcessCacheManagerForResultSet) {
	                HCRQueryProcessCacheManagerForResultSet hcrObj = (HCRQueryProcessCacheManagerForResultSet) cacheManager;
	                resultSet = hcrObj.getResult();
	            }
        	}
        }
        return resultSet;
    }
	
	/**
   
	/**
     * Provides a response using the JasperPrint object and number of records found in cache.
     * 
     * @param formData           JSON object containing form data for report generation
     * @param jasperPrint        JasperPrint object
     * @param noOfRecords        number of records found in cache
     * @return the JSON object containing the response
     */
    @Override
    public JsonObject getResponseUsingPrint(JsonObject formData, JasperPrint jasperPrint, Integer noOfRecords) {
        return hcrHelper.provideResponseUsingPrint(formData, jasperPrint, noOfRecords);
    }
    
    
    public void generateHCReportStreaming(JsonObject formData, StreamSession session) {
    	
    	String requestId = RequestContext.get();
    	AtomicReference<JRBaseFiller> atomicFiller = new AtomicReference<>();
    	boolean cacheRefresh = GsonUtility.optBooleanValue(formData, "refresh", false);
    	taskExecutorService.submit(() -> {
    		
    		String cacheKey = "";
    		
            try {
                final JsonObject  finalFormDataa = HCRUtils.checkForDefaultValues(formData);
                final JsonObject clonedFormData = finalFormDataa.deepCopy();
                HCRUtils.loadFontDirectories();
                
                ResultSet resultSet = prepareExecuteJasperReport(finalFormDataa);
                
                JsonObject hcrData = new JsonObject();
                JDesignHelper helper = new JDesignHelper(finalFormDataa);
                JasperDesign jasperDesign = helper.createDesigner();
                
                formData.addProperty("mainDataKey", GsonUtility.optStringValue(finalFormDataa,"designCacheKey", ""));
				cacheKey = JasperPrintCacheKeyGenerator.generate(jasperDesign, formData);
                formData.addProperty("designCacheKey", cacheKey);

                
                boolean isDesignCacheEnabled = CacheUtils.getHCRPrintCacheConfig().getEnabled();
                
                AtomicInteger pageCounter = new AtomicInteger(0);
                
                ReportFillListener listener =  new ReportFillListener(session, pageCounter, formData, hcrHelper);
                
                JReportExecutionContext cachedContext = null;
                
                if (isDesignCacheEnabled) {
                	if (cacheRefresh) {
                		store.remove(cacheKey);
                	} else {
	                	cachedContext = (JReportExecutionContext) store.get(cacheKey);
	                	if (cachedContext != null) {
	                		final int firstPage = 0;
	                		logger.debug("Generated final Page : {}", firstPage);
	                		JasperPrint cachedPrint = cachedContext.getJasperPrint();
	                		pageCounter.set(cachedPrint.getPages().size());
	                		listener.processEvent(cachedPrint, firstPage, pageCounter.get(), false);
	                		return;
	                	}
                	}
                }
                
              logger.debug("Design cache key : {}" , cacheKey);
              
              PrintContext context =   hcrHelper.provideJasperFillerForStreaming(
                        		finalFormDataa,
                                resultSet,
                                helper,
                                jasperDesign,
                                hcrData
                        );
                
                
                final JRBaseFiller filler = context.getFiller();
                atomicFiller.set(filler);
                session.send(new StreamEvent(EventType.BEGIN.value(), "{\"status\":\"STARTED\"}"));
                
                boolean waitUntilFirstPageLoad = JsonUtils.waitUntilFirstPageLoad() ;
                
                if (!waitUntilFirstPageLoad) {
                	filler.addFillListener(listener);
                }
                Future<?> fillFuture = taskExecutorService.submit(() -> {
                	try {
                		filler.fill(context.getParameters(), context.getDataSource());
                	} catch (JRException e) {
                		throw new EfwServiceException(e.getMessage());
                	}
                }, requestId);
                

                String format = GsonUtility.optStringValue(formData, "format", "html");

                if (isDesignCacheEnabled) {
                	String cacheReference = CacheUtils.getCacheNameFromConnection(clonedFormData);
                	store.put(new CacheKeyDTO(cacheKey, cacheReference), new JReportExecutionContext(filler.getJasperPrint(), context.getVirtualizer(), pageCounter, format, context.getSwapDir()));
                }
				
        		// final page
				// ReportListener does not send any callback for final page. Handling it
				// separately.
                
                fillFuture.get();
                
        		final int totalPages = filler.getJasperPrint().getPages().size();
        		int finalPage = 0;
        		if(!waitUntilFirstPageLoad) {
        		 finalPage =  totalPages-1;
        		}
				logger.debug("Generated final Page : {}", finalPage);
				pageCounter.set(totalPages);
				listener.processEvent(filler.getJasperPrint(), finalPage, totalPages, false);
				listener.flushRemaining(finalPage, totalPages);
               
				listener.stopBatchSender();
            }
            
            catch (CancellationException  e) {
            	taskExecutorService.cancelTaskByRequestId(requestId);
            	cancelFill(atomicFiller.get(), cacheKey);
            }
            catch (Exception e) {
            	session.error(e);
            	cancelFill(atomicFiller.get(), cacheKey);
            }
            finally {
            		session.complete();
            }
            	
        }, requestId);
    }
    
    
    
    private void cancelFill(JRBaseFiller filler, String cacheKey) {
    	if ( filler != null ) {
    		try {
				filler.cancelFill();
				logger.debug("Removing the partially filled Design cache.");
				store.remove(cacheKey);
			} catch (JRException e1) {
				e1.printStackTrace();
			}
    	}
    }
	
}