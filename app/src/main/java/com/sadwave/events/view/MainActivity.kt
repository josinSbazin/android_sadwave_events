package com.sadwave.events.view

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.sadwave.events.R
import com.sadwave.events.mvp.MainPresenter
import com.sadwave.events.mvp.MainView
import com.sadwave.events.mvp.State
import com.sadwave.events.net.CityEntity
import com.sadwave.events.net.EventEntity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*
import org.koin.android.ext.android.get

class MainActivity : MvpAppCompatActivity(), MainView, CitiesAdapter.Listener,
    EventsAdapter.Listener {
    private val citiesAdapter: CitiesAdapter = CitiesAdapter(this)
    private val eventsAdapter: EventsAdapter = EventsAdapter(this)

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun provide(): MainPresenter = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cities.adapter = citiesAdapter
        events.adapter = eventsAdapter
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onState(state: State) {
        when (state) {
            State.Loading -> {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                progressLayout.showLoading()
            }
            is State.Error -> {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                progressLayout.showError(
                    R.drawable.ic_error_black_24dp,
                    getString(R.string.error_title),
                    getString(R.string.error_description),
                    getString(R.string.error_btn_text)
                ) {
                    presenter.refresh()
                }
            }
            is State.OnData -> {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                progressLayout.showContent()
                toolbar.title = state.currentCity.name
                citiesAdapter.setData(state.cities, state.currentCity)
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
