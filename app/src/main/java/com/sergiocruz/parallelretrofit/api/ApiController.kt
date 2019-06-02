package com.sergiocruz.parallelretrofit.api

import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiController {

    val dispatcher: Dispatcher by lazy { Dispatcher() }

    val controller: AppAPI by lazy {

        dispatcher.maxRequests = 64

        val okHttpClient = OkHttpClient
            .Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool())
            .build()

        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AppAPI::class.java)
    }

}