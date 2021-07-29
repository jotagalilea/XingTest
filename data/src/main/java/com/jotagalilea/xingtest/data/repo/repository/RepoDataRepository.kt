package com.jotagalilea.xingtest.data.repo.repository

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStoreFactory
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class RepoDataRepository(private val factory: RepoDataStoreFactory) : RepoRepository {

    override fun getCachedRepositories(): Single<List<Repo>> {
        return factory.retrieveCachedDataStore().getRepositories()
    }

    override fun getRemoteRepositories(): Single<List<Repo>> {
        return factory.retrieveRemoteDataStore().getRepositories()
    }

    override fun saveRepository(repo: Repo): Completable {
        return factory.retrieveCachedDataStore().saveRepository(repo)
    }
}