package com.sadwave.events.mvp

import com.arellomobile.mvp.MvpView
import com.sadwave.events.net.CityEntity
import com.sadwave.events.net.EventEntity
import java.lang.Exception

interface MainView : MvpView {
    fun onState(state: State)
}

sealed class State {
    object Loading : State()
    class Error(val error: Exception) : State()
    class OnData(val cities: List<CityEntity>, val currentCity: CityEntity, val events: List<EventEntity>) : State()
}