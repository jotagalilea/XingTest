package com.jotagalilea.xingtest.framework.local.dao

import androidx.room.*
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.framework.local.repositories.RepositoryDBObject

@Dao
abstract class CachedRepositoriesDao {

    @Transaction
    @Query("SELECT * FROM ${Utils.REPOSITORIES_TABLE_NAME} LIMIT :limit OFFSET :offset")
    abstract fun getRepositories(limit: Int, offset: Int): List<RepositoryDBObject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepository(repo: RepositoryDBObject)

}