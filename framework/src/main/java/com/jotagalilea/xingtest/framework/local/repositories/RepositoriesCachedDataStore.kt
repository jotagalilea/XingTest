package com.jotagalilea.xingtest.framework.local.repositories

import android.util.Log
import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStore
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.local.database.ReposDatabase
import com.jotagalilea.xingtest.framework.local.mapper.RepositoryCacheMapper
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

class RepositoriesCachedDataStore constructor(
    private val database: ReposDatabase,
    private val mapper: RepositoryCacheMapper
): RepoDataStore {

    override fun clearRepositories(): Completable {
        throw NotImplementedError()
    }

    override fun getCachedRepositories(): Single<List<Repo>> {
        val result = Single.defer {
            Single.just(database.cachedRepositoriesDao().getRepositories(Utils.REPOS_QUERY_SIZE, Utils.REPOS_LOCAL_QUERY_OFFSET))
        }.map {
            it.map { repoDBobject ->
                mapper.mapToModel(repoDBobject)
            }
        }
        return result
    }

    override fun getRemoteRepositories(): Single<List<Repo>> {
        throw NotImplementedError()
    }

    override fun saveRepository(repo: Repo): Completable {
        return Completable.defer {
            database.cachedRepositoriesDao().insertRepository(
                mapper.mapToCached(repo))

            Log.d("INSERT en tabla ${Utils.REPOSITORIES_TABLE_NAME}", "guardado repo ${repo.name}")
            Completable.complete()
        }
    }
}