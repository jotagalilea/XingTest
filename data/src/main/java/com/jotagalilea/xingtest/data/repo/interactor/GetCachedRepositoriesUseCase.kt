package com.jotagalilea.xingtest.data.repo.interactor

import com.jotagalilea.xingtest.data.interactor.GetUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import com.jotagalilea.xingtest.model.Repo

class GetCachedRepositoriesUseCase(
    private val repository: RepoRepository
) : GetUseCase<List<Repo>, Void?>(){

    override suspend fun buildUseCaseObservable(params: Void?): List<Repo> {
        return repository.getCachedRepos()
    }
}