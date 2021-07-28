package com.jotagalilea.xingtest.framework.remote.repositories

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStore
import com.jotagalilea.xingtest.framework.AvatarCacher
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.remote.mapper.RepositoryRemoteMapper
import com.jotagalilea.xingtest.framework.remote.service.ReposService
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

class RepositoriesRemoteDataStore(
    private var service: ReposService,
    private val mapper: RepositoryRemoteMapper,
    private val avatarCacher: AvatarCacher
): RepoDataStore {

    override fun clearRepositories(): Completable {
        throw NotImplementedError()
    }

    override fun getCachedRepositories(): Single<List<Repo>> {
        throw NotImplementedError()
    }

    //TODO: Quitar de la interfaz getRemote... y getCached... y dejarlo como get... solo.
    //      Hacer lo mismo en el Repository. Quitar de las interfaces todo lo que tire NotImplementedError.
    //      MIRAR EN LA PLANTILLA CÓMO ESTÁ LA INTERFAZ.
    override fun getRemoteRepositories(): Single<List<Repo>> {
        return service.getReposList(Utils.REPOS_QUERY_SIZE, Utils.REPOS_REMOTE_QUERY_PAGE++)
            .map { response ->
                val entities = mutableListOf<Repo>()
                response.forEach {
                    //////////////////////////////////////////////
                    val model = mapper.mapToModel(it)
                    avatarCacher.cacheAvatar(model)
                    entities.add(model)
                    //////////////////////////////////////////////
                    //entities.add(mapper.mapToModel(it))
                }
                entities
            }
    }

    override fun saveRepository(repo: Repo): Completable {
        throw NotImplementedError()
    }
}