package com.jotagalilea.xingtest.data.interactor

abstract class GetUseCase<T, in Params> {

    protected abstract suspend fun buildUseCaseObservable(params: Params? = null): T

    suspend fun execute(params: Params? = null): T {
        return buildUseCaseObservable(params)
    }
}