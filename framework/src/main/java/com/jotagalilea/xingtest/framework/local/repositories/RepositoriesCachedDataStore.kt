package com.jotagalilea.xingtest.framework.local.repositories

import android.util.Log
import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoCacheDataStore
import com.jotagalilea.xingtest.framework.AvatarCacher
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.local.database.ReposDatabase
import com.jotagalilea.xingtest.framework.local.mapper.RepositoryCacheMapper
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class RepositoriesCachedDataStore constructor(
    private val database: ReposDatabase,
    private val mapper: RepositoryCacheMapper,
    private val avatarCacher: AvatarCacher
) : RepoCacheDataStore {

    override fun getRepositories(): Single<List<Repo>> {
        return Single.defer {
            Single.just(database.cachedRepositoriesDao().getRepositories(Utils.REPOS_QUERY_SIZE, Utils.REPOS_LOCAL_QUERY_OFFSET))
        }.map {
            it.map { repoDBobject ->
                mapper.mapToModel(repoDBobject)
            }
        }
    }

    override fun saveRepository(repo: Repo): Completable {
        return Completable.defer {
            database.cachedRepositoriesDao().insertRepository(
                mapper.mapToCached(repo)
            )
            avatarCacher.savedAvatars[repo.avatar_url] = repo.avatar_file
            Log.d("INSERT en tabla ${Utils.REPOSITORIES_TABLE_NAME}", "guardado repo ${repo.name}")
            Completable.complete()
        }
    }
}