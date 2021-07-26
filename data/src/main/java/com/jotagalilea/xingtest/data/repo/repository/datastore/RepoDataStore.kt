package com.jotagalilea.xingtest.data.repo.repository.datastore

import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

interface RepoDataStore {

    fun clearRepositories(): Completable
    fun getCachedRepositories(): Single<List<Repo>>
    fun getRemoteRepositories(): Single<List<Repo>>
    fun saveRepository(repo: Repo): Completable

}