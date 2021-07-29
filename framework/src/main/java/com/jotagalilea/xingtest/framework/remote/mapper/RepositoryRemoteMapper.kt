package com.jotagalilea.xingtest.framework.remote.mapper

import com.jotagalilea.xingtest.framework.remote.response.ReposResponse
import com.jotagalilea.xingtest.model.Repo

class RepositoryRemoteMapper {

    fun mapToModel(response: ReposResponse): Repo {
        return Repo(
            name = response.name ?: "",
            description = response.description ?: "",
            login = response.owner.login ?: "",
            repo_html_url = response.html_url ?: "",
            owner_html_url = response.owner.html_url ?: "",
            fork = response.fork ?: false,
            avatar_url = response.owner.avatar_url ?: "",
            avatar_file = ""
        )
    }
}