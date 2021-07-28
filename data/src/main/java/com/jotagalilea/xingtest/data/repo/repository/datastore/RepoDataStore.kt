package com.jotagalilea.xingtest.data.repo.repository.datastore

import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

interface RepoDataStore {

    fun getRepositories(): Single<List<Repo>>
    fun saveRepository(repo: Repo): Completable

}