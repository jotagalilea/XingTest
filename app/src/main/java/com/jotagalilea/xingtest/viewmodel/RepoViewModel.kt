package com.jotagalilea.xingtest.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jotagalilea.xingtest.R
import com.jotagalilea.xingtest.data.repo.interactor.GetCachedRepositoriesUseCase
import com.jotagalilea.xingtest.framework.Utils
import com.jotagalilea.xingtest.model.Repo
import com.jotagalilea.xingtest.ui.common.ObjectStatus
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Empty
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Error
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Loading
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Success
import com.jotagalilea.xingtest.ui.common.ObservableEvent
import com.jotagalilea.xingtest.ui.common.ObservableEvent.StartReposSyncService
import com.jotagalilea.xingtest.ui.common.SingleLiveEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RepoViewModel(
    private val getCachedRepositoriesUseCase: GetCachedRepositoriesUseCase,
) : ViewModel() {

    private val repositoriesList: MutableList<Repo> = mutableListOf()
    private val reposStateFlow = MutableStateFlow<ObjectStatus<List<Repo>>>(Loading())
    val observableEvent: SingleLiveEvent<ObservableEvent> = SingleLiveEvent()


    fun getRepositories() = repositoriesList
    fun getRepositoriesStateFlow() = reposStateFlow

    fun fetchRepositories() {
        reposStateFlow.value = Loading()
        observableEvent.value = StartReposSyncService
    }
    
    fun fetchReposAfterSync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            getCachedRepositoriesUseCase.execute().also { repos ->
                repositoriesList.addAll(repos)
                if (repositoriesList.isEmpty()) {
                    reposStateFlow.value = Empty(context.getString(R.string.got_repos_empty))
                    Log.i("INFO", "Repos vacíos")
                } else {
                    reposStateFlow.value = Success(repos)
                    Log.v("SUCCESS", "Repos obtenidos")
                }
            }
            Utils.REPOS_LOCAL_QUERY_OFFSET += Utils.REPOS_QUERY_SIZE
            ++Utils.REPOS_REMOTE_QUERY_PAGE
            //TODO: Falta el caso de error al obtener de BD, ¿puede ocurrir?.
        }
    }
}