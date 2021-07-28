package com.jotagalilea.xingtest.data.repo.repository.datastore

class RepoDataStoreFactory(
    private val cachedDataStore: RepoDataStore,
    private val remoteDataStore: RepoDataStore
) {

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