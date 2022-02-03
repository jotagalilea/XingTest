package com.jotagalilea.xingtest.framework.remote.repositories

import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStore
import com.jotagalilea.xingtest.framework.AvatarCacher
import com.jotagalilea.xingtest.framework.remote.mapper.RepositoryRemoteMapper
import com.jotagalilea.xingtest.framework.remote.service.ReposKtorService
import com.jotagalilea.xingtest.model.Repo

class RepositoriesRemoteDataStore(
    private val ktorService: ReposKtorService,
    private val mapper: RepositoryRemoteMapper,
    private val avatarCacher: AvatarCacher
) : RepoDataStore {

    override suspend fun getRepos(): List<Repo> {
        val repos = ktorService.getRepos().map { response ->
            val model = mapper.mapToModel(response)
            if (!avatarCacher.savedAvatars.containsKey(model.avatar_url))
                avatarCacher.cacheAvatar(model)
            model
        }
        ktorService.closeService()
        return repos
    }
}