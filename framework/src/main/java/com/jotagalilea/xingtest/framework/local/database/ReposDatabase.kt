package com.jotagalilea.xingtest.framework.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jotagalilea.xingtest.framework.Utils.DATABASE_NAME
import com.jotagalilea.xingtest.framework.local.dao.CachedRepositoriesDao
import com.jotagalilea.xingtest.framework.local.repositories.RepositoryDBObject

@Database(
    entities = [
        RepositoryDBObject::class
    ],
    version = 1
)
abstract class ReposDatabase: RoomDatabase() {

    private var INSTANCE: ReposDatabase? = null
    private val sLock = Any()

    abstract fun cachedRepositoriesDao(): CachedRepositoriesDao

    fun getInstance(context: Context): ReposDatabase {
        if (INSTANCE == null) {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ReposDatabase::class.java, DATABASE_NAME
                    )
                        .build()
                }
                return INSTANCE!!
            }
        }
        return INSTANCE!!
    }
}