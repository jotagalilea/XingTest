package com.jotagalilea.xingtest.framework.remote.service

import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.remote.response.ReposResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

class ReposKtorService(
    private val factory: ReposKtorServiceFactory
) {

    private companion object {
        const val BASE_URL = "https://api.github.com/orgs/xing/"
        const val REPOS_URL = "repos"
    }


    suspend fun getRepos(): List<ReposResponse> {
        return factory.makeClient().get(BASE_URL + REPOS_URL) {
            parameter("per_page", Utils.REPOS_QUERY_SIZE)
            parameter("page", Utils.REPOS_REMOTE_QUERY_PAGE)
        }.body()
    }

    fun closeService(){
        factory.closeClient()
    }

}