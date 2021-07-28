package com.jotagalilea.xingtest.data.interactor

import com.jotagalilea.xingtest.data.executor.PostExecutionThread
import com.jotagalilea.xingtest.data.executor.ThreadExecutor
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Abstract class for a UseCase that returns an instance of a [Completable].
 */
abstract class CompletableUseCase<in Params> protected constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) {

    /**
     * Builds a [Completable] which will be used when the current [CompletableUseCase] is executed.
     */
    protected abstract fun buildUseCaseObservable(params: Params? = null): Completable

    /**
     * Executes the current use case.
     */
    fun execute(params: Params? = null): Completable {
        return this.buildUseCaseObservable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler)
    }
}