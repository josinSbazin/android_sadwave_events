package com.sadwave.events

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

val mainModule = module {
    single { createOkHttpClient() }
    single { createRetrofitClient("https://sadwaveeventsapi.azurewebsites.net/api/", get()) }
}

private fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .build()
}

private fun createRetrofitClient(baseUrl: String, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()
}