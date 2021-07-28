package com.jotagalilea.xingtest.data.repo.repository

import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable
import io.reactivex.Single

interface RepoRepository {

    fun getCachedRepositories(): Single<List<Repo>>
    fun getRemoteRepositories(howMany: Int, page: Int): Single<List<Repo>>
    fun saveRepository(repo: Repo): Completable

}