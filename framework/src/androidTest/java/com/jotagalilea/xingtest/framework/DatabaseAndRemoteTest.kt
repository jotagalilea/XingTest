package com.jotagalilea.xingtest.framework

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.jotagalilea.xingtest.framework.local.dao.CachedRepositoriesDao
import com.jotagalilea.xingtest.framework.local.database.ReposDatabase
import com.jotagalilea.xingtest.framework.local.mapper.RepositoryCacheMapper
import com.jotagalilea.xingtest.framework.remote.mapper.RepositoryRemoteMapper
import com.jotagalilea.xingtest.framework.remote.response.ReposResponse
import com.jotagalilea.xingtest.model.Repo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class DatabaseAndRemoteTest {

    private var count = 0
    private val limit = 15
    private val offset = 1
    private lateinit var db: ReposDatabase
    private lateinit var dao: CachedRepositoriesDao
    private lateinit var service: ReposService
    private val dbMapper: RepositoryCacheMapper = RepositoryCacheMapper()
    private val responseMapper: RepositoryRemoteMapper = RepositoryRemoteMapper()
    private val remoteRepos = mutableListOf<ReposResponse>()
    private val localRepos = mutableListOf<Repo>().apply {
        while (count < 6)
            this.add(Repo(
                "name${++count}",
                "description",
                "login${count}",
                "repo${count}",
                "owner${count}",
                count%2 == 0,
                "avatar_url${count}",
                "avatar_file${count}"
            ))
    }



    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    /**
     * Se necesita inicializar la base de datos en memoria, pero no se accede a la base de datos
     * real para agilizar la prueba.
     */
    @Before
    fun setup(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReposDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.cachedRepositoriesDao()
    }


    /**
     * Tras cada ejecución se cierra la base de datos.
     */
    @After
    fun onFinish(){
        db.close()
    }


    /**
     * Prueba de la inserción de una serie de elementos y del mapper de la base de datos.
     */
    @ExperimentalCoroutinesApi
    @Test
    fun insertRepositories() = runBlockingTest {
        localRepos.forEach { dao.insertRepository(dbMapper.mapToCached(it)) }
        val notMappedRepos = dao.getRepositories(limit, offset)
        val reposFromDB: MutableList<Repo> = notMappedRepos.map{ dbMapper.mapToModel(it) }.toMutableList()
        assertThat(reposFromDB).isEqualTo(localRepos)
        reposFromDB.add(Repo("", "", "", "", "", false, "", ""))
        assertThat(reposFromDB).isNotEqualTo(localRepos)
    }


    /**
     * Prueba de la obtención de datos del servicio y del mapper para la respuesta del servicio.
     */
    @ExperimentalCoroutinesApi
    @Test
    fun getReposFromRemote() = runBlockingTest {
        service = ReposServiceFactory.makeService()
        remoteRepos.clear()
        remoteRepos.addAll(service.getReposList(limit, 2).blockingGet())
        assertThat(remoteRepos).isNotNull()
        assertThat(remoteRepos).isNotEmpty()
        assertThat(remoteRepos[0]).isInstanceOf(ReposResponse::class.java)

        val mappedRepos = remoteRepos.map { responseMapper.mapToModel(it) }
        assertThat(mappedRepos).isNotEmpty()
        assertThat(mappedRepos.size).isEqualTo(remoteRepos.size)
        assertThat(mappedRepos[mappedRepos.size/2]).isEqualTo(responseMapper.mapToModel(remoteRepos[remoteRepos.size/2]))
    }

}