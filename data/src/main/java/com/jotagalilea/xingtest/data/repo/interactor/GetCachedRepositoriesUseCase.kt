package com.jotagalilea.xingtest.data.repo.interactor

import com.jotagalilea.xingtest.data.executor.PostExecutionThread
import com.jotagalilea.xingtest.data.executor.ThreadExecutor
import com.jotagalilea.xingtest.data.interactor.SingleUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import com.jotagalilea.xingtest.model.Repo
import io.reactivex.rxjava3.core.Single

class GetCachedRepositoriesUseCase(
    private val repository: RepoRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : SingleUseCase<List<Repo>, Void?>(threadExecutor, postExecutionThread) {

    public override fun buildUseCaseObservable(params: Void?): Single<List<Repo>> {
        return repository.getCachedRepositories()
    }
}