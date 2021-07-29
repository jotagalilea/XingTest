package com.jotagalilea.xingtest.data.repo.repository.datastore

class RepoDataStoreFactory(
    private val cachedDataStore: RepoCacheDataStore,
    private val remoteDataStore: RepoDataStore
) {

    fun retrieveCachedDataStore(): RepoCacheDataStore {
        return cachedDataStore
    }

    fun retrieveRemoteDataStore(): RepoDataStore {
        return remoteDataStore
    }
}