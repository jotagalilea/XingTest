package com.jotagalilea.xingtest.data.repo.interactor

import com.jotagalilea.xingtest.data.executor.PostExecutionThread
import com.jotagalilea.xingtest.data.executor.ThreadExecutor
import com.jotagalilea.data.interactor.SingleUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.Single

class GetRemoteRepositoriesUseCase(
    private val repository: RepoRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : SingleUseCase<List<Repo>, Void?>(threadExecutor, postExecutionThread) {

    private var page = 0
    private val howMany = 15

    public override fun buildUseCaseObservable(params: Void?): Single<List<Repo>> {
        return repository.getRemoteRepositories(howMany, ++page).flatMap {
            Single.just(it)
        }
    }

}