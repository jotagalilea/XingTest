package com.jotagalilea.xingtest.framework.remote.service

import com.jotagalilea.xingtest.framework.remote.response.ReposResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ReposService {

    companion object {
        const val BASE_URL = "https://api.github.com/orgs/xing/"
        const val REPOS_URL = "repos"
    }

    @GET(REPOS_URL)
    fun getReposList(
        @Query("per_page") limit: Int,
        @Query("page") offset: Int
    ): Single<List<ReposResponse>>
}