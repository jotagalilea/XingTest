package com.jotagalilea.xingtest.data.repo.repository.datastore

import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Completable

interface RepoCacheDataStore : RepoDataStore {

    fun saveRepository(repo: Repo): Completable
}