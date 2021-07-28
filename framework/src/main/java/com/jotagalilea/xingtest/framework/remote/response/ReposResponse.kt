package com.jotagalilea.xingtest.framework.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ReposResponse(
    val name: String?,
    val description: String?,
    val owner: Owner,
    val html_url: String?,
    val fork: Boolean?
){
    @Serializable
    data class Owner(
        val login: String?,
        val html_url: String?,
        val avatar_url: String?
    )
}