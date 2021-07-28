package com.jotagalilea.xingtest.model

data class Repo(
    val name: String,
    val description: String,
    val login: String,
    val repo_html_url: String,
    val owner_html_url: String,
    var fork: Boolean,
    val avatar_url: String,
    var avatar_file: String,
)