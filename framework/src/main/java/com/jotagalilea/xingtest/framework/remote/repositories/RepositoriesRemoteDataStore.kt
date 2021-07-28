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
) : RepoDataStore {

    //TODO: Quitar de la interfaz getRemote... y getCached... y dejarlo como get... solo.
    //      Ver si puedo meter rápido lo de la plantilla y hablar con Xavi.
    override fun getRepositories(): Single<List<Repo>> {
        return service.getReposList(Utils.REPOS_QUERY_SIZE, Utils.REPOS_REMOTE_QUERY_PAGE++)
            .map { response ->
                val entities = mutableListOf<Repo>()
                response.forEach {
                    val model = mapper.mapToModel(it)
                    if (!avatarCacher.savedAvatars.containsKey(model.avatar_url))
                        avatarCacher.cacheAvatar(model)
                    entities.add(model)
                }
                entities
            }
    }

    override fun saveRepository(repo: Repo): Completable {
        //TODO: Ver plantilla para poder cargarme esto:
        throw NotImplementedError()
    }
}