package com.sadwave.events.net

import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface API {
    /**
     * Запрос на отправку токена для пушей
     */
    @GET("https://sadwaveeventsapi.azurewebsites.net/api/cities")
    fun getCitiesAwait(): Deferred<List<City>>
}