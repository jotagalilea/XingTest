package com.jotagalilea.xingtest.di

import androidx.room.Room
import com.jotagalilea.xingtest.UIThread
import com.jotagalilea.xingtest.data.executor.JobExecutor
import com.jotagalilea.xingtest.data.executor.PostExecutionThread
import com.jotagalilea.xingtest.data.executor.ThreadExecutor
import com.jotagalilea.xingtest.data.repo.interactor.GetCachedRepositoriesUseCase
import com.jotagalilea.xingtest.data.repo.interactor.GetRemoteRepositoriesUseCase
import com.jotagalilea.xingtest.data.repo.interactor.SaveRepositoriesUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoDataRepository
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoCacheDataStore
import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStore
import com.jotagalilea.xingtest.data.repo.repository.datastore.RepoDataStoreFactory
import com.jotagalilea.xingtest.framework.AvatarCacher
import com.jotagalilea.xingtest.framework.Utils.DATABASE_NAME
import com.jotagalilea.xingtest.framework.local.database.ReposDatabase
import com.jotagalilea.xingtest.framework.local.mapper.RepositoryCacheMapper
import com.jotagalilea.xingtest.framework.local.repositories.RepositoriesCachedDataStore
import com.jotagalilea.xingtest.framework.remote.mapper.RepositoryRemoteMapper
import com.jotagalilea.xingtest.framework.remote.repositories.RepositoriesRemoteDataStore
import com.jotagalilea.xingtest.framework.remote.service.ReposKtorService
import com.jotagalilea.xingtest.framework.remote.service.ReposKtorServiceFactory
import com.jotagalilea.xingtest.framework.remote.service.ReposServiceFactory
import com.jotagalilea.xingtest.viewmodel.RepoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationModule = module() {
    single { JobExecutor() as ThreadExecutor }
    single { UIThread() as PostExecutionThread }
    single { AvatarCacher(androidContext()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            ReposDatabase::class.java, DATABASE_NAME
        )
            .build()
    }

    factory { get<ReposDatabase>().cachedRepositoriesDao() }
    factory { RepositoryRemoteMapper() }
    factory { RepositoryCacheMapper() }
    factory { ReposServiceFactory.makeService() }
    factory { ReposKtorServiceFactory }
    factory { ReposKtorService(get()) }
    factory<RepoRepository> { RepoDataRepository(get()) }
    factory<RepoDataStore>(named("remote")) { RepositoriesRemoteDataStore(get(), get(), get(), get()) }
    factory<RepoCacheDataStore>(named("local")) { RepositoriesCachedDataStore(get(), get(), get()) }
    factory { RepoDataStoreFactory(get(named("local")), get(named("remote"))) }
}

val repositoriesModule = module() {
    factory { GetCachedRepositoriesUseCase(get(), get(), get()) }
    factory { GetRemoteRepositoriesUseCase(get(), get(), get()) }
    factory { SaveRepositoriesUseCase(get(), get(), get()) }
    viewModel { RepoViewModel(get()) }
}