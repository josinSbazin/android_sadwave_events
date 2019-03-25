package com.sadwave.events.net

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface API {
    @GET("https://sadwaveeventsapi.azurewebsites.net/api/cities")
    fun getCitiesAsync(): Deferred<List<CityEntity>>

    @GET("https://sadwaveeventsapi.azurewebsites.net/api/events/{alias}")
    fun getEventsByCityAsync(@Path("alias") alias: String): Deferred<List<EventEntity>>
}