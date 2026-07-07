/* Copyright 2011 Vladimir Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helicalinsight.externalauth.saml;

import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * CachingMetadataManager extends {@link MetadataManager}
 * Metadata manager caches all results of EntityDescriptors loaded from the providers. Cache is cleaned
 * whenever some of the providers published an observed message.
 * <p>
 * The class is synchronized using the superclass ReentrantReadWriteLock.
 *
 * @author Vladimir Schaefer
 */
public class CachingMetadataManager extends MetadataManager {

    // Cache for alias data
    private Map<String, String> aliasCache;

    // Cache for basic metadata
    private Map<String, EntityDescriptor> basicMetadataCache;

    // Cache for basic metadata based on SHA-1 hash of the entityID
    private Map<byte[], EntityDescriptor> hashMetadataCache;

    // Cache for extended metadata
    private Map<String, ExtendedMetadata> extendedMetadataCache;

    // Lock for the cache
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * CachingMetadataManager(List<MetadataProvider> providers)
     * Creates caching metadata provider.
     * @param providers providers to include
     * @throws MetadataProviderException error initializing
     */
    public CachingMetadataManager(List<MetadataProvider> providers) throws MetadataProviderException {

        super(providers);

        this.aliasCache = new HashMap<String, String>();
        this.basicMetadataCache = new HashMap<String, EntityDescriptor>();
        this.hashMetadataCache = new HashMap<byte[], EntityDescriptor>();
        this.extendedMetadataCache = new HashMap<String, ExtendedMetadata>();

    }

    /**
     * refreshMetadata()
     * Guaranteed to be called by the superclass as part of the initialization.
     */
    @Override
    public void refreshMetadata() {

        try {

            lock.writeLock().lock();
            log.debug("Clearing metadata cache");

            // Clear the caches so they get reinitialized as needed from the new data
            this.aliasCache = new HashMap<String, String>();
            this.basicMetadataCache = new HashMap<String, EntityDescriptor>();
            this.hashMetadataCache = new HashMap<byte[], EntityDescriptor>();
            this.extendedMetadataCache = new HashMap<String, ExtendedMetadata>();

            // Do whatever it takes to refresh the metadata
            //super.refreshMetadata();

        } finally {

            lock.writeLock().unlock();

        }

    }

    /**
     * getEntityIdForAlias(String entityAlias)
     * Locates name of the entity for the given alias.
     *
     * @param entityAlias to load entityId for
     * @return entityId or null if not found
     * @throws MetadataProviderException provider in case alias is not unique or missing
     */
    public String getEntityIdForAlias(String entityAlias) throws MetadataProviderException {
        return getFromCacheOrUpdate(aliasCache, entityAlias, aliasLoader);
    }

    /**
     * getEntityDescriptor(String entityID)
     * In case entity exists in the cache it is returned, otherwise mechanism from the super class is used to locate it.
     *
     * @param entityID id to load descriptor for
     * @return entity descriptor or null if not found
     * @throws MetadataProviderException provider
     */
    @Override
    public EntityDescriptor getEntityDescriptor(String entityID) throws MetadataProviderException {
        return getFromCacheOrUpdate(basicMetadataCache, entityID, entityLoader);
    }

    /**
     * getEntityDescriptor(byte[] hash)
     * Locates entity descriptor whose entityId SHA-1 hash equals the one in the parameter.
     *
     * @param hash hash of the entity descriptor
     * @return found descriptor or null
     */
    @Override
    public EntityDescriptor getEntityDescriptor(byte[] hash) throws MetadataProviderException {
        return getFromCacheOrUpdate(hashMetadataCache, hash, entityHashLoader);
    }

    /**
     * getExtendedMetadata(String entityID)
     * In case entity exists in the cache it is returned, otherwise mechanism from the super class is used to locate it.
     *
     * @param entityID id to load extended metadata for
     * @return entity descriptor or null if not found
     * @throws MetadataProviderException provider
     */
    @Override
    public ExtendedMetadata getExtendedMetadata(String entityID) throws MetadataProviderException {
        return getFromCacheOrUpdate(extendedMetadataCache, entityID, extendedLoader);
    }

    /**
     * getFromCacheOrUpdate(Map<U, T> cache, U key, ValueLoader<T, U> valueLoader)
     * Attempts to load value from the cache, in case it doesn't exist locates it from the chainingProvider and adds
     * to the cache.
     *
     * @param cache       caching map
     * @param key         key to find the value
     * @param valueLoader loader to load value in case it is not present in the cache
     * @param <T>         type of cache
     * @return found value or null if not found
     * @throws MetadataProviderException error or null key
     */
    private <T, U> T getFromCacheOrUpdate(Map<U, T> cache, U key, ValueLoader<T, U> valueLoader) throws MetadataProviderException {

        if (key == null) {
            return null;
        }

        // Try to load the cached value
        lock.readLock().lock();
        if (cache.containsKey(key)) {
            T item = cache.get(key);
            lock.readLock().unlock();
            return item;
        }

        // Upgrade lock when not found
        lock.readLock().unlock();
        lock.writeLock().lock();

        // Re-verify cache
        if (cache.containsKey(key)) {
            T item = cache.get(key);
            lock.writeLock().unlock();
            return item;
        }

        try {

            // Load new value
            T value = valueLoader.getValue(key);
            cache.put(key, value);
            return value;

        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * ValueLoader<T, U>
     * Interface whose implementations should load value related to the given identifier.
     *
     * @param <T> found value, null if not found
     */
    private interface ValueLoader<T, U> {
        T getValue(U identifier) throws MetadataProviderException;
    }
    /**
     * aliasLoader implements {@link ValueLoader} interface
     * Loads extended metadata based on string value using the method 'getEntityIdForAlias' from the parent class.
     */
    private final ValueLoader<String, String> aliasLoader = new ValueLoader<String, String>() {
        public String getValue(String identifier) throws MetadataProviderException {
            return CachingMetadataManager.super.getEntityIdForAlias(identifier);
        }
    };
    /**
     * entityLoader implements {@link ValueLoader} interface
     * Loads extended metadata based on string value using the method 'getEntityDescriptor' from the parent class.
     */
    private final ValueLoader<EntityDescriptor, String> entityLoader = new ValueLoader<EntityDescriptor, String>() {
        public EntityDescriptor getValue(String identifier) throws MetadataProviderException {
            return CachingMetadataManager.super.getEntityDescriptor(identifier);
        }
    };
    /**
     * entityHashLoader implements {@link ValueLoader} interface
     * Loads extended metadata based on byte array value using the method 'getEntityDescriptor' from the parent class.
     */
    private final ValueLoader<EntityDescriptor, byte[]> entityHashLoader = new ValueLoader<EntityDescriptor, byte[]>() {
        public EntityDescriptor getValue(byte[] identifier) throws MetadataProviderException {
            return CachingMetadataManager.super.getEntityDescriptor(identifier);
        }
    };
    /**
     * extendedLoader implements {@link ValueLoader} interface
     * Loads extended metadata based on value using the method 'getExtendedMetadata' from the parent class.
     */
    private final ValueLoader<ExtendedMetadata, String> extendedLoader = new ValueLoader<ExtendedMetadata, String>() {
        public ExtendedMetadata getValue(String identifier) throws MetadataProviderException {
            return CachingMetadataManager.super.getExtendedMetadata(identifier);
        }
    };

}