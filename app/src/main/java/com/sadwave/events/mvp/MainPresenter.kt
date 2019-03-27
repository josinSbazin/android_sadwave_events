package com.sadwave.events.mvp

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.sadwave.events.net.API
import com.sadwave.events.net.CityEntity
import kotlinx.coroutines.*
import timber.log.Timber

@InjectViewState
class MainPresenter(private val api: API) : MvpPresenter<MainView>() {
    private var job: Job? = null
    private lateinit var cities: List<CityEntity>

    init {
        refresh()
    }

    fun refresh() {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                cities = api.getCitiesAsync().await()
                val city = cities.first()
                loadEvents(city)
            } catch (e: CancellationException) {
                // no op
            } catch (e: Exception) {
                Timber.d(e)
                viewState.onState(State.Error(e))
            }
        }
    }

    fun selectCity(city: CityEntity) {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                loadEvents(city)
            } catch (e: CancellationException) {
                // no op
            } catch (e: Exception) {
                Timber.d(e)
                viewState.onState(State.Error(e))
            }
        }
    }

    private suspend fun loadEvents(city: CityEntity) {
        val events = api.getEventsByCityAsync(city.alias!!).await()
        viewState.onState(State.OnData(cities, city, events))
    }
}