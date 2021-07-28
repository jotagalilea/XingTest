package com.jotagalilea.xingtest.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
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
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Empty
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Error
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Loading
import com.jotagalilea.xingtest.ui.common.ObjectStatus.Success
import com.jotagalilea.xingtest.ui.common.ObservableEvent
import com.jotagalilea.xingtest.viewmodel.RepoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScrollingActivity : AppCompatActivity(), ReposAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityScrollingBinding
    private val viewModel: RepoViewModel by viewModel()
    private var localBroadcastManager: LocalBroadcastManager? = null
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()){
            granted: Boolean ->
        if (granted)
            initComponents()
        else {
            //TODO: ¿Esto es lo mejor?
            //finishAndRemoveTask()
        }
    }
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
        //TODO: Hacer que salga de la app.
        //      Mostrar toast diciendo que necesita el permiso.
        /*val requestPermissionLauncher = registerForActivityResult(RequestPermission()){
            granted: Boolean ->
            if (granted)
                initComponents()
            else {
                //TODO: ¿Esto es lo mejor?
                finishAndRemoveTask()
            }
        }*/
        checkWritePermission(requestPermissionLauncher)
    }

    //TODO: Quizá tengo que quitar el request como parámetro...
    private fun checkWritePermission(requestPermissionLauncher: ActivityResultLauncher<String>){
        when {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Entra en", "checkSelf")
                initComponents()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                Log.d("Entra en", "shouldShow")
                //TODO: Debería hacer que en caso de denegar el permiso continuara y simplemente no cargase las imágenes
                //      de fichero, pero sí de url. Poner tb un aviso de que en caso afirmativo cargará más rápido.
                showPermissionNoticeDialog(requestPermissionLauncher)
            }
            else -> {
                //TODO: Pensar qué sería correcto aquí:
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * Ver https://developer.android.com/training/permissions/requesting
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted, so nothing to do
                    Log.d("PERMISOS", "Concedida escritura en almacenamiento")
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Explain to the user that the feature is unavailable because
                        // the features requires a permission that the user has denied.
                        // At the same time, respect the user's decision. Don't link to
                        // system settings in an effort to convince the user to change
                        // their decision.
                        //showVoiceRecordingPermissionDeniedDialog()
                        showPermissionNoticeDialog(requestPermissionLauncher)
                    } else {
                        // Denied forever
                        //showVoiceRecordingPermissionDeniedForeverDialog()
                        //TODO: Dejar que inicie la app!!
                        finishAndRemoveTask()
                    }
                }
                return
            }
            // Add other 'when' lines to check for other permissions this fragment might request
            else -> {
                // Ignore all other requests
                //TODO: OJO!! para API 29 y habiendo denegado forever llega aquí!!
                //      ¿¡Y ha guardado la imagen!? Eso es porque no estoy escribiendo en el directorio correcto...
                Log.d("LLEGA", "llega")
                //showPermissionNoticeDialog(requestPermissionLauncher)
                initComponents()
            }
        }
    }

    private fun showPermissionNoticeDialog(requestPermissionLauncher: ActivityResultLauncher<String>){
        AlertDialog.Builder(this)
            .setMessage(R.string.permission_notice)
            .setPositiveButton(R.string.grant) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            .setNegativeButton(R.string.deny) { _, _ ->
                finishAndRemoveTask()
            }
            .create().show()
    }

    private fun initComponents(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        initializeSynchronisationBroadcastReceiver()
        setupUI()
        setupObservers()}

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

    private fun setupObservers() {
        viewModel.getRepositoriesLiveData().observe(this,
            { status ->
                handleResultStatus(status)
            }
        )

        viewModel.observableEvent.observe(this,
            { event ->
                when (event) {
                    ObservableEvent.StartReposSyncService -> {
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

    private fun setupUI() {
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.reposRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && (newState == RecyclerView.SCROLL_STATE_IDLE)) {
                    try {
                        showLoading()
                        viewModel.fetchRepositories()
                    } catch (e: Exception) {
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

    fun showLoading() {
        binding.emptyView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.mainLoader.visibility = View.VISIBLE
    }

    fun showEmpty(message: String?) {
        binding.reposRecycler.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.text = message
    }

    fun showError(message: String?) {
        binding.reposRecycler.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.mainLoader.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
        binding.errorView.text = message
    }

    fun <T> setupScreenForSuccess(data: T) {
        val repos = data as List<*>
        repos.filterIsInstance<Repo>().also {
            showSuccess()
            loadRecyclerContent(it.toMutableList())
        }
    }

    private fun loadRecyclerContent(repos: MutableList<Repo>) {
        val recycler: RecyclerView = binding.reposRecycler
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ReposAdapter(this).apply { addItems(repos) }
    }

    fun showSuccess() {
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

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        localBroadcastManager?.unregisterReceiver(synchronisationBroadcastReceiver)
    }
}