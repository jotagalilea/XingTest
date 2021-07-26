package com.jotagalilea.xingtest.data.repo.interactor

import com.jotagalilea.xingtest.data.executor.PostExecutionThread
import com.jotagalilea.xingtest.data.executor.ThreadExecutor
import com.jotagalilea.data.interactor.CompletableUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import io.reactivex.Completable

class ClearRepositoriesUseCase(
    private val repository: RepoRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<Void?>(threadExecutor, postExecutionThread) {

    public override fun buildUseCaseObservable(params: Void?): Completable {
        return repository.clearRepositories()
    }

}