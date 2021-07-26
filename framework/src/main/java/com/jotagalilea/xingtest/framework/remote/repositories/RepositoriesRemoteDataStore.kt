package com.jotagalilea.xingtest.framework.remote.repositories

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStore
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.remote.mapper.RepositoryRemoteMapper
import com.jotagalilea.xingtest.framework.remote.service.ReposService
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

class RepositoriesRemoteDataStore(
    private var service: ReposService,
    private val mapper: RepositoryRemoteMapper
): RepoDataStore {

    override fun clearRepositories(): Completable {
        throw NotImplementedError()
    }

    override fun getCachedRepositories(): Single<List<Repo>> {
        throw NotImplementedError()
    }

    override fun getRemoteRepositories(): Single<List<Repo>> {
        return service.getReposList(Utils.REPOS_QUERY_SIZE, Utils.REPOS_REMOTE_QUERY_PAGE++)
            .map { model ->
                val entities = mutableListOf<Repo>()
                model.forEach { entities.add(mapper.mapToModel(it)) }
                entities
            }
    }

    override fun saveRepository(repo: Repo): Completable {
        throw NotImplementedError()
    }
}