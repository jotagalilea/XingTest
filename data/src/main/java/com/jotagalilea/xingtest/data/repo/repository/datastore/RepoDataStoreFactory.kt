package com.jotagalilea.xingtest.data.repo.repository.datastore

class RepoDataStoreFactory(
    private val cachedDataStore: RepoDataStore,
    private val remoteDataStore: RepoDataStore
) {

    /**
     * Returns a DataStore based on whether or not there is content in the cache and the cache
     * has not expired
     */
    fun retrieveDataStore(isValidCache: Boolean): RepoDataStore {
        if (isValidCache) {
            return retrieveCachedDataStore()
        }
        return retrieveRemoteDataStore()
    }

    /**
     * Return an instance of the Cache Data Store
     */
    fun retrieveCachedDataStore(): RepoDataStore {
        return cachedDataStore
    }

    /**
     * Return an instance of the Remote Data Store
     */
    fun retrieveRemoteDataStore(): RepoDataStore {
        return remoteDataStore
    }

}