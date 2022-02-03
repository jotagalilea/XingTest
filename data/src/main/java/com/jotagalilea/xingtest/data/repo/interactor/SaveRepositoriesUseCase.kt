package com.jotagalilea.xingtest.data.repo.interactor

import com.jotagalilea.xingtest.data.interactor.SaveUseCase
import com.jotagalilea.xingtest.data.repo.repository.RepoRepository
import com.jotagalilea.xingtest.model.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveRepositoriesUseCase(
    private val repository: RepoRepository
) : SaveUseCase<List<Repo>>(){

    override suspend fun buildUseCaseObservable(params: List<Repo>?) {
        CoroutineScope(Dispatchers.IO).launch {
            params?.forEach {
                repository.saveRepoJob(it)
            }
        }
    }

}