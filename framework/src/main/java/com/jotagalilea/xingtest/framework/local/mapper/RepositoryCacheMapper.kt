package com.jotagalilea.xingtest.framework.local.mapper

import com.jotagalilea.xingtest.framework.local.repositories.RepositoryDBObject
import com.jotagalilea.xingtest.model.Repo

class RepositoryCacheMapper {

    fun mapToModel(dbObject: RepositoryDBObject): Repo {
        return Repo(
            name = dbObject.name,
            description = dbObject.description,
            login = dbObject.login,
            repo_html_url = dbObject.repo_html_url,
            owner_html_url = dbObject.owner_html_url,
            fork = dbObject.fork,
            avatar_url = dbObject.avatar_url,
            avatar_file = dbObject.avatar_file
        )
    }

    fun mapToCached(repo: Repo): RepositoryDBObject {
        return RepositoryDBObject(
            name = repo.name,
            description = repo.description,
            login = repo.login,
            repo_html_url = repo.repo_html_url,
            owner_html_url = repo.owner_html_url,
            fork = repo.fork,
            avatar_url = repo.avatar_url,
            avatar_file = repo.avatar_file
        )
    }
}