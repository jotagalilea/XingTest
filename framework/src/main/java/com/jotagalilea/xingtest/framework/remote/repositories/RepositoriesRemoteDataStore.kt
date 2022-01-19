package com.jotagalilea.xingtest.framework.remote.repositories

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStore
import com.jotagalilea.xingtest.framework.AvatarCacher
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.remote.mapper.RepositoryRemoteMapper
import com.jotagalilea.xingtest.framework.remote.service.ReposService
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Single

class RepositoriesRemoteDataStore(
    private var service: ReposService,
    private val mapper: RepositoryRemoteMapper,
    private val avatarCacher: AvatarCacher
) : RepoDataStore {

    override fun getRepositories(): Single<List<Repo>> {
        return service.getReposList(Utils.REPOS_QUERY_SIZE, Utils.REPOS_REMOTE_QUERY_PAGE)
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
}