package com.jotagalilea.xingtest.ui.main

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.xingtest.R
import com.jotagalilea.xingtest.databinding.ActivityScrollingBinding
import com.jotagalilea.xingtest.model.Repo
import com.jotagalilea.xingtest.sync.SyncResult
import com.jotagalilea.xingtest.sync.SyncService
import com.jotagalilea.xingtest.sync.SyncService.Companion.ACTION_SYNC_COMPLETED
import com.jotagalilea.xingtest.sync.SyncService.Companion.START_SERVICE
import com.jotagalilea.xingtest.sync.SyncService.Companion.SYNC_COMPLETED
import com.jotagalilea.xingtest.ui.common.ObjectStatus
import com.jotagalilea.xingtest.ui.common.ObjectStatus.*
import com.jotagalilea.xingtest.ui.common.ObservableEvent
import com.jotagalilea.xingtest.viewmodel.RepoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScrollingActivity : AppCompatActivity(), ReposAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityScrollingBinding
    private val viewModel: RepoViewModel by viewModel()
    private var localBroadcastManager: LocalBroadcastManager? = null
    private val synchronisationBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            intent?.let {
                when (intent.action) {
                    ACTION_SYNC_COMPLETED -> {
                        sendSynchronisationCompleted(it)
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        initializeSynchronisationBroadcastReceiver()
        setupUI()
        setupObservers()
    }

    private fun initializeSynchronisationBroadcastReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_SYNC_COMPLETED)
        }
        localBroadcastManager?.registerReceiver(synchronisationBroadcastReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.getRepositories().isEmpty())
            viewModel.fetchRepositories()
    }


    private fun setupObservers(){
        viewModel.getRepositoriesLiveData().observe(this,
            { status ->
                handleResultStatus(status)
            }
        )

        viewModel.observableEvent.observe(this,
            { event ->
                when (event){
                    ObservableEvent.StartReposSyncService ->
                    {
                        startSynchronisationProcess()
                    }
                }
            }
        )
    }

    private fun startSynchronisationProcess() {
        val synchroniseServiceIntent = Intent(applicationContext, SyncService::class.java)
        synchroniseServiceIntent.action = START_SERVICE
        applicationContext?.startService(synchroniseServiceIntent)
    }


    private fun setupUI(){
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.reposRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && (newState == RecyclerView.SCROLL_STATE_IDLE)){
                    try{
                        showLoading()
                        viewModel.fetchRepositories()
                    }
                    catch (e: Exception){
                        Log.e("ERROR LOADING", e.printStackTrace().toString())
                        showError(getString(R.string.got_repos_error))
                    }
                }
            }
        })
    }


    private fun <T> handleResultStatus(status: ObjectStatus<T>) {
        when (status) {
            is Loading -> showLoading()
            is Success -> setupScreenForSuccess(status.data)
            is Empty -> showEmpty(status.emptyMessage)
            is Error -> showError(status.errorMessage)
        }
    }


    fun showLoading(){
        binding.emptyView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.mainLoader.visibility = View.VISIBLE
    }


    fun showEmpty(message: String?){
        binding.reposRecycler.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.text = message
    }


    fun showError(message: String?){
        binding.reposRecycler.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
        binding.errorView.text = message
    }


    fun <T> setupScreenForSuccess(data: T){
        val repos = data as List<*>
        repos.filterIsInstance<Repo>().also {
            showSuccess()
            loadRecyclerContent(it.toMutableList())
        }
    }


    private fun loadRecyclerContent(repos: MutableList<Repo>){
        val recycler: RecyclerView = binding.reposRecycler
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ReposAdapter(this).apply { addItems(repos) }
    }


    fun showSuccess(){
        binding.emptyView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE
        binding.reposRecycler.visibility = View.VISIBLE
    }


    private fun sendSynchronisationCompleted(intent: Intent) {
        when (intent.getSerializableExtra(SYNC_COMPLETED)) {
            SyncResult.SUCCESS -> {
                viewModel.fetchRepositoriesAfterSync()
                Toast.makeText(this, getString(R.string.synchronisation_finished), Toast.LENGTH_SHORT).show()
            }
            SyncResult.ERROR -> {
                viewModel.fetchRepositoriesAfterSync()
                Toast.makeText(this, getString(R.string.synchronisation_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onLongClick(repo: Repo): Boolean {
        AlertDialog.Builder(this)
            .setMessage(R.string.ask_which_url)
            .setPositiveButton(R.string.repo_option,
                DialogInterface.OnClickListener { _, _ -> openUrl(repo.repo_html_url) })
            .setNegativeButton(R.string.owner_option,
                DialogInterface.OnClickListener { _, _ ->
                    openUrl(repo.owner_html_url)
                })
            .create().show()
        return true
    }


    private fun openUrl(url: String){
        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        localBroadcastManager?.unregisterReceiver(synchronisationBroadcastReceiver)
    }
}