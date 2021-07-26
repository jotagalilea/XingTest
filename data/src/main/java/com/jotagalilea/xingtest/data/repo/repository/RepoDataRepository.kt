package com.jotagalilea.xingtest.data.repo.repository

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStoreFactory
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

class RepoDataRepository(private val factory: RepoDataStoreFactory): RepoRepository {

    override fun clearRepositories(): Completable {
        return factory.retrieveCachedDataStore().clearRepositories()
    }

    override fun getCachedRepositories(): Single<List<Repo>> {
        return factory.retrieveCachedDataStore().getCachedRepositories()
    }

    override fun getRemoteRepositories(howMany: Int, page: Int): Single<List<Repo>> {
        return factory.retrieveRemoteDataStore().getRemoteRepositories()
    }

    override fun saveRepository(repo: Repo): Completable {
        return factory.retrieveCachedDataStore().saveRepository(repo)
    }
}