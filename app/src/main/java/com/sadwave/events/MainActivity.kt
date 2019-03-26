package com.sadwave.events

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.sadwave.events.mvp.MainPresenter
import com.sadwave.events.mvp.MainView
import com.sadwave.events.mvp.State
import com.sadwave.events.net.CityEntity
import com.sadwave.events.net.EventEntity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.android.ext.android.get

class MainActivity : MvpAppCompatActivity(), MainView, CitiesAdapter.Listener, EventsAdapter.Listener {
    private val citiesAdapter: CitiesAdapter = CitiesAdapter(this)
    private val eventsAdapter: EventsAdapter = EventsAdapter(this)

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun provide() = get<MainPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        cities.adapter = citiesAdapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onState(state: State) {
        when (state) {
            State.Loading -> {
            }
            is State.Error -> {
                progress_layout.showError(
                    R.drawable.ic_error_black_24dp,
                    "Беда!",
                    "Не получилось",
                    "Повторить"
                ) {
                    presenter.refresh()
                }
            }
            is State.OnData -> {
                citiesAdapter.cities = state.cities
                citiesAdapter.selectedCity = state.currentCity
                eventsAdapter.events = state.events
            }
        }
    }

    override fun onCityClick(city: CityEntity) {
        presenter.selectCity(city)
    }

    override fun onEventClick(event: EventEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEventShare(event: EventEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEventAddToCalendar(event: EventEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
