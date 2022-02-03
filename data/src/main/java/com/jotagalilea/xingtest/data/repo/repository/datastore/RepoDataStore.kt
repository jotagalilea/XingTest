package com.jotagalilea.xingtest.data.repo.repository.datastore

import com.jotagalilea.xingtest.model.Repo

interface RepoDataStore {

    suspend fun getRepos(): List<Repo>
}