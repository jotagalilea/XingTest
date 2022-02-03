package com.jotagalilea.xingtest.framework.remote.service

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

object ReposKtorServiceFactory {

    private const val TAG_ERROR = "ERROR"
    private const val TAG_SUCCESS = "SUCCESS"
    private lateinit var client: HttpClient


    fun makeClient(): HttpClient{
        client = HttpClient(Android) {
            engine {
                connectTimeout = 10_000
                socketTimeout = 10_000
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                gson()
            }
            install(ResponseObserver) {
                onResponse { response ->
                    //TODO: Falta depurar estos casos: 
                    if (response.status.isSuccess()) {
                        when (response.status) {
                            HttpStatusCode.OK -> {
                                Log.d(TAG_SUCCESS, "Conectado!")
                            }
                        }
                    } else when (response.status) {
                        HttpStatusCode.Unauthorized -> {
                            Log.d(TAG_ERROR, "Conexión no autorizada")
                        }
                        else -> Log.d(TAG_ERROR, "Error genérico, código ${response.status}")
                    }
                }
            }
            expectSuccess = false
        }
        return client
    }


    fun closeClient(){
        client.close()
    }
}