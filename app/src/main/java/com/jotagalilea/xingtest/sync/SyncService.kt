package com.jotagalilea.xingtest.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.jotagalilea.xingtest.data.repo.interactor.GetCachedRepositoriesUseCase
import com.jotagalilea.xingtest.data.repo.interactor.GetRemoteRepositoriesUseCase
import com.jotagalilea.xingtest.data.repo.interactor.SaveRepositoriesUseCase
import com.jotagalilea.xingtest.sync.SyncResult.*
import com.jotagalilea.xingtest.R
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import org.koin.android.ext.android.inject
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@Keep
class SyncService: Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var reposDisposable: CompositeDisposable? = null
    private var localBroadcastManager: LocalBroadcastManager? = null
    private val getCachedReposUseCase: GetCachedRepositoriesUseCase by inject()
    private val getRemoteReposUseCase: GetRemoteRepositoriesUseCase by inject()
    private val saveReposUseCase: SaveRepositoriesUseCase by inject()


    companion object {
        const val ONGOING_NOTIFICATION_ID = 1001
        const val START_SERVICE: String = "start_foreground_service"
        const val STOP_SERVICE: String = "stop_foreground_service"
        const val SERVICE_ACTION: String = "service_action"
        const val SYNCHRONISE_CHANNEL_ID = "1001"
        const val SYNCHRONISE_CHANNEL_NAME = "Synchronise channel"
        const val SYNCHRONISE_CHANNEL_DESCRIPTION = "Synchronising data channel"
        const val ACTION_SYNC_COMPLETED = "com.jotagalilea.xingtest.sync.action.SYNC_COMPLETED"
        const val SYNC_COMPLETED = "sync_completed"
        const val TAG_SYNC_SERVICE = "SYNC SERVICE"
    }


    override fun onCreate() {
        compositeDisposable = CompositeDisposable()
        reposDisposable = CompositeDisposable()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val actionData = Bundle()
            actionData.putString(SERVICE_ACTION, intent.action)

            serviceHandler?.obtainMessage()?.also { message ->
                message.data = actionData
                serviceHandler?.sendMessage(message)
            }
        }

        return START_STICKY
    }


    //region Synchronisation
    fun startSynchroniseService() {
        startForegroundSynchroniseService()
        Log.d(TAG_SYNC_SERVICE, "Iniciado servicio de sincronización...")

        syncRepositories()
    }

    private fun startForegroundSynchroniseService() {
        val pendingIntent: PendingIntent =
            Intent(applicationContext, SyncService::class.java).let { notificationIntent ->
                notificationIntent.action = STOP_SERVICE
                PendingIntent.getService(applicationContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            }

        val notificationCompatBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = createNotificationChannel(this, SYNCHRONISE_CHANNEL_ID, SYNCHRONISE_CHANNEL_NAME, SYNCHRONISE_CHANNEL_DESCRIPTION)
            NotificationCompat.Builder(applicationContext, channelId)
        } else {
            NotificationCompat.Builder(applicationContext)
        }

        val dismissAction = NotificationCompat.Action.Builder(R.drawable.ic_delete, getString(R.string.cancel), pendingIntent).build()
        val notification: Notification = notificationCompatBuilder
            .setContentTitle(getText(R.string.notification_synchronise_title))
            .setContentText(getText(R.string.notification_synchronise_body))
            .setContentIntent(pendingIntent)
            .addAction(dismissAction)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }


    fun stopSynchroniseService() {
        compositeDisposable?.clear()
        reposDisposable?.clear()
        //Utils.REPOS_LOCAL_QUERY_OFFSET += Utils.REPOS_QUERY_SIZE
        stopForeground(true)
        stopSelf()
        Log.d(TAG_SYNC_SERVICE, "Se ha parado el servicio de sincronización...")
    }


    private fun syncRepositories(){
        try {
            reposDisposable?.add(getCachedReposUseCase.execute().flatMapCompletable { list ->
                if (list.isEmpty())
                    getRemoteRepositories()
                        .retry(3)
                        .timeout(60, TimeUnit.SECONDS) // Por motivos de depuración lo dejo en 60 segundos.
                else{
                    Completable.complete()
                }

            }.doFinally {
                stopSynchroniseService()
            }.subscribeWith(object : DisposableCompletableObserver() {
                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val errorCode = e.code()
                        if (errorCode != 200) {
                            Log.e(TAG_SYNC_SERVICE, "No se ha podido completar la sincronización. Código: " + e.code())
                            manageResult(ERROR)
                        }
                    } else {
                        e.message?.let { Log.e(TAG_SYNC_SERVICE, "No se ha podido completar la sincronización. Error: $it") }
                        e.printStackTrace()
                        manageResult(ERROR)
                    }
                }

                override fun onComplete() {
                    Log.d(TAG_SYNC_SERVICE, "Sincronización completada")
                    manageResult(SUCCESS)
                }
            }))
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }


    private fun getRemoteRepositories(): Completable {
        return getRemoteReposUseCase.execute()
            .flatMapCompletable {
                saveReposUseCase.execute(it)
            }
    }


    private fun manageResult(result: SyncResult){
        sendResult(result)
        stopSynchroniseService()
    }


    private fun sendResult(result: SyncResult) {
        val completeIntent = Intent()
        completeIntent.putExtra(SYNC_COMPLETED, result)
        completeIntent.action = ACTION_SYNC_COMPLETED
        localBroadcastManager?.sendBroadcast(completeIntent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, channelId: String, channelName: String, channelDescription: String): String {
        val importance = android.app.NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = channelDescription
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        mNotificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    //endregion


    //region Inner classes
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(message: Message) {
            when (message.data.getString(SERVICE_ACTION)) {
                START_SERVICE -> startSynchroniseService()
                STOP_SERVICE -> stopSynchroniseService()
            }
        }
    }
    //endregion
}