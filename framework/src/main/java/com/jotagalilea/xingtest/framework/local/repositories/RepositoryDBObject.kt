package com.jotagalilea.xingtest.framework.local.repositories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jotagalilea.xingtest.framework.Utils

@Entity(tableName = Utils.REPOSITORIES_TABLE_NAME)
data class RepositoryDBObject(
    @PrimaryKey
    @ColumnInfo(name = "repo_name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "repo_html_url") val repo_html_url: String,
    @ColumnInfo(name = "owner_html_url") val owner_html_url: String,
    @ColumnInfo(name = "fork") val fork: Boolean,
    @ColumnInfo(name = "avatar_url") val avatar_url: String,
    @ColumnInfo(name = "avatar_file") val avatar_file: String
)