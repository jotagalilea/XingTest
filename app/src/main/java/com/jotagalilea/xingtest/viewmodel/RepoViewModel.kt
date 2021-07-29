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

class RepoViewModel(
    private val getCachedRepositoriesUseCase: GetCachedRepositoriesUseCase,
    private val context: Context
) : ViewModel() {

    private val repositoriesList: MutableList<Repo> = mutableListOf()
    private val repositoriesLiveData: MutableLiveData<ObjectStatus<List<Repo>>> = MutableLiveData()
    private var compositeDisposable: CompositeDisposable
    val observableEvent: SingleLiveEvent<ObservableEvent> = SingleLiveEvent()

    init {
        compositeDisposable = CompositeDisposable()
        repositoriesLiveData.value = Loading()
    }

    fun getRepositories() = repositoriesList
    fun getRepositoriesLiveData() = repositoriesLiveData

    fun fetchRepositories() {
        repositoriesLiveData.value = Loading()
        observableEvent.value = StartReposSyncService
    }

    fun fetchRepositoriesAfterSync() {
        compositeDisposable.add(
            getCachedRepositoriesUseCase.execute()
                .subscribe({ reposList ->
                    repositoriesList.addAll(reposList)

                    if (repositoriesList.isEmpty()) {
                        repositoriesLiveData.value =
                            Empty(context.getString(R.string.got_repos_empty))
                        Log.i("INFO", "Repos vacíos")
                    } else {
                        repositoriesLiveData.value = Success(repositoriesList)
                        Log.v("SUCCESS", "Repos obtenidos")
                    }
                    Utils.REPOS_LOCAL_QUERY_OFFSET += Utils.REPOS_QUERY_SIZE
                }, {
                    repositoriesLiveData.value = Error(context.getString(R.string.got_repos_error))
                    Log.e("ERROR", "Error al obtener los repos")
                })
        )
    }
}