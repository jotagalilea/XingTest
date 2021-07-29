package com.jotagalilea.xingtest.data.repo.repository

import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface RepoRepository {

    fun getCachedRepositories(): Single<List<Repo>>
    fun getRemoteRepositories(): Single<List<Repo>>
    fun saveRepository(repo: Repo): Completable
}