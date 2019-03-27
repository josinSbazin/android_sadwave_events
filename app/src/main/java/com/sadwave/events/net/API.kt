package com.sadwave.events.net

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface API {
    @GET("cities")
    fun getCitiesAsync(): Deferred<List<CityEntity>>

    @GET("events/{alias}")
    fun getEventsByCityAsync(@Path("alias") alias: String): Deferred<List<EventEntity>>
}