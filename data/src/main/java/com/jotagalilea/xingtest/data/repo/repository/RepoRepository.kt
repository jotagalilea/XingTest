package com.jotagalilea.xingtest.data.repo.repository

import com.jotagalilea.xingtest.model.Repo
import kotlinx.coroutines.Job

interface RepoRepository {

    suspend fun getCachedRepos(): List<Repo>
    suspend fun getRemoteRepos(): List<Repo>
    suspend fun saveRepoJob(repo: Repo): Job
}