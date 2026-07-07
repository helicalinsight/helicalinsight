package com.helicalinsight.externalauth;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.ExtendedMetadata;

import com.helicalinsight.externalauth.saml.CachingMetadataManager;

public class CachingMetadataManagerTest {

	@Test
	public void testRefreshMetadata() throws MetadataProviderException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		cachingMetadataManager.refreshMetadata();
	}
	
	@Test
	public void testGetEntityIdForAlias() throws MetadataProviderException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		cachingMetadataManager.getEntityIdForAlias("key");
	}
	
	@Test
	public void testGetEntityDescriptor() throws MetadataProviderException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		cachingMetadataManager.getEntityDescriptor("key");
	}
	
	@Test
	public void testGetEntityDescriptor_a2() throws MetadataProviderException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		byte[] hash = new byte[1];
		cachingMetadataManager.getEntityDescriptor(hash);
	}
	
	
	@Test
	public void testGetExtendedMetadata_a1() throws MetadataProviderException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		cachingMetadataManager.getExtendedMetadata(null);
	}
	@Test
	public void testGetExtendedMetadata_a2() throws MetadataProviderException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		ExtendedMetadata extendedMetadata = new ExtendedMetadata();
		Map<String, ExtendedMetadata> extendedMetadataCache = new HashMap<>();
		extendedMetadataCache.put("key", extendedMetadata);
		Field field = CachingMetadataManager.class.getDeclaredField("extendedMetadataCache");
		field.setAccessible(true);
		field.set(cachingMetadataManager, extendedMetadataCache);
		cachingMetadataManager.getExtendedMetadata("key");
	}
	@Test
	public void testGetExtendedMetadata_a3() throws MetadataProviderException {
	
		List<MetadataProvider> providers = new ArrayList<>();
		MetadataProvider metadataProvider = mock(MetadataProvider.class);
		providers.add(metadataProvider);
		CachingMetadataManager cachingMetadataManager = new CachingMetadataManager(providers);
		cachingMetadataManager.getExtendedMetadata("12");
	}
	
	
}
