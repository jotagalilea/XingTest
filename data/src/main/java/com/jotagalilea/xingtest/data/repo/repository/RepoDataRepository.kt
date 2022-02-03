package com.jotagalilea.xingtest.data.repo.repository

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStoreFactory
import com.jotagalilea.xingtest.model.Repo
import kotlinx.coroutines.Job

class RepoDataRepository(private val factory: RepoDataStoreFactory) : RepoRepository {

    override suspend fun getCachedRepos(): List<Repo> {
        return factory.retrieveCachedDataStore().getRepos()
    }

    override suspend fun getRemoteRepos(): List<Repo> {
        return factory.retrieveRemoteDataStore().getRepos()
    }

    override suspend fun saveRepoJob(repo: Repo): Job {
        return factory.retrieveCachedDataStore().saveRepoJob(repo)
    }
}