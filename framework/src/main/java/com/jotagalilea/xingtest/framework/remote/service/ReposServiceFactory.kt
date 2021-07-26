package com.jotagalilea.xingtest.framework.remote.service

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ReposServiceFactory {

    fun makeService(): ReposService{
        return Retrofit.Builder()
            .baseUrl(ReposService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ReposService::class.java)
    }

}