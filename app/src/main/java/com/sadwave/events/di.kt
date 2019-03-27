package com.sadwave.events

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sadwave.events.mvp.MainPresenter
import com.sadwave.events.net.API
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val baseUrl = "https://sadwaveeventsapi.azurewebsites.net/api/"

val mainModule = module {
    single { createOkHttpClient() }
    single { createRetrofitClient(baseUrl, get()) }
    single { get<Retrofit>().create(API::class.java) }
    factory {
        MainPresenter(get())
    }
}

private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .build()
}

private fun createRetrofitClient(baseUrl: String, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()
}