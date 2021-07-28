package com.jotagalilea.xingtest.data.repo.interactor

import com.jotagalilea.xingtest.data.executor.PostExecutionThread
import com.jotagalilea.xingtest.data.executor.ThreadExecutor
import com.jotagalilea.xingtest.data.interactor.CompletableUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Completable

class SaveRepositoriesUseCase(private val repository: RepoRepository,
                              threadExecutor: ThreadExecutor,
                              postExecutionThread: PostExecutionThread
) : CompletableUseCase<List<Repo>?>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: List<Repo>?): Completable {
        return params?.let {
            Completable.merge(it.map { repo ->
                repository.saveRepository(repo)
            })

        } ?: Completable.error(IllegalArgumentException("Params no puede ser null"))
    }

}