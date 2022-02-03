package com.jotagalilea.xingtest.data.repo.repository.datastore

import com.jotagalilea.xingtest.model.Repo
import kotlinx.coroutines.Job

interface RepoCacheDataStore : RepoDataStore {

    fun saveRepoJob(repo: Repo): Job
}