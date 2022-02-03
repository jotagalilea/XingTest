package com.jotagalilea.xingtest.data.interactor

abstract class SaveUseCase<T> {

    protected abstract suspend fun buildUseCaseObservable(params: T? = null)

    suspend fun execute(params: T? = null) {
        return buildUseCaseObservable(params)
    }
}