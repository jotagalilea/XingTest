package com.jotagalilea.xingtest.data.repo.repository.datastore

import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Single

interface RepoDataStore {

    fun getRepositories(): Single<List<Repo>>
}