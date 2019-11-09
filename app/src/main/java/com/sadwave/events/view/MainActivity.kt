package com.sadwave.events.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ShareCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
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
import com.sadwave.events.util.SadDateFormatter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_event.view.*
import kotlinx.android.synthetic.main.main_content.*
import org.koin.android.ext.android.get
import java.util.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : MvpAppCompatActivity(), MainView, CitiesAdapter.Listener,
    EventsAdapter.Listener {
    private val sadDateFormatter: SadDateFormatter = get()
    private val citiesAdapter: CitiesAdapter = CitiesAdapter(this)
    private val eventsAdapter: EventsAdapter = EventsAdapter(this, sadDateFormatter)
    private lateinit var searchItem: SearchView

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

        presenter.refresh(loadLastCityName())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        searchItem = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String): Boolean {
                eventsAdapter.filterEntities(text)
                searchItem.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                eventsAdapter.filterEntities(text)
                if (searchItem.query.isEmpty()) {
                    searchItem.isIconified = true
                    searchItem.clearFocus()
                }
                return true
            }
        })
        searchItem.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, p1: Boolean) {
                searchItem.clearFocus()
                // Todo event touch keyboard hide
            }
        }
        searchItem.setOnQueryTextFocusChangeListener(object : View.OnFocusChangeListener{
            override fun onFocusChange(view: View?, p1: Boolean) {
                // Todo what is this?
            }
        })

        return super.onCreateOptionsMenu(menu)
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
                    R.drawable.ic_error_black,
                    getString(R.string.error_title),
                    getString(R.string.error_description),
                    getString(R.string.error_btn_text)
                ) {
                    presenter.refresh(loadLastCityName())
                }
            }
            is State.OnData -> {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                progressLayout.showContent()

                if (state.cities.isEmpty()) {
                    toolbar.title = ""
                    cities.isVisible = false
                    emptyCitiesMessage.isVisible = true
                } else {
                    cities.isVisible = true
                    emptyCitiesMessage.isVisible = false

                    toolbar.title = state.currentCity.name
                    citiesAdapter.setData(state.cities, state.currentCity)
                }

                if (state.events.isEmpty()) {
                    events.isVisible = false
                    emptyEventsMessage.isVisible = true
                } else {
                    events.isVisible = true
                    emptyEventsMessage.isVisible = false
                    eventsAdapter.rawEvents = state.events
                }
            }
        }
    }

    override fun onCityClick(city: CityEntity) {
        if (city.name == loadLastCityName()) {
            drawerLayout.closeDrawers()
            return
        }
        searchItem.setQuery("",false)
        searchItem.clearFocus()
        presenter.selectCity(city)
        saveLastCityName(city.name)
    }

    override fun onEventClick(event: EventEntity) {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = event.url ?: return
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onEventShare(event: EventEntity) {
        val url = event.url
        if (url != null) {
            ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(
                    getString(
                        R.string.share,
                        event.name ?: getString(R.string.gig_default)
                    )
                )
                .setText(event.url)
                .startChooser()
        } else {
            ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(
                    getString(
                        R.string.share,
                        event.name ?: getString(R.string.gig_default)
                    )
                )
                .setText(event.overview)
                .startChooser()
        }
    }

    override fun onEventAddToCalendar(event: EventEntity) {
        val date = sadDateFormatter.parseDate(event.date?.date)
        if (date == null) {
            showWrongDateMessage()
            return
        }

        val calendar = Calendar.getInstance()
        val time = sadDateFormatter.parseTime(event.date?.time)
        var hasTime = false
        if (time != null) {
            hasTime = true
            date.time += time.time
        }
        calendar.time = date

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(Events.CONTENT_URI)
            .putExtra(Events.TITLE, event.name ?: getString(R.string.gig_default))
            .putExtra(Events.DESCRIPTION, event.overview ?: "")
            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)

        val gigCalendar = if (hasTime) {
            calendar.timeInMillis + TimeZone.getDefault().rawOffset
        } else {
            calendar.add(Calendar.HOUR, DEFAULT_HOURS)
            calendar.timeInMillis
        }

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, gigCalendar)

        startActivity(intent)
    }

    private fun showWrongDateMessage() {
        Toast.makeText(this, "Не удалось сохранить мероприятие", Toast.LENGTH_SHORT).show()
    }

    private fun saveLastCityName(name: String?) {
        val sharedPref =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.preferences_last_city), name)
            apply()
        }
    }

    private fun loadLastCityName(): String? {
        val sharedPref =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
                ?: return null
        return sharedPref.getString(getString(R.string.preferences_last_city), null)
    }

    //Obsolete (replace by SocialEntity and adapter
    fun onClickSocial(view: View) {
        val but : ImageButton = view as ImageButton
        //but.id todo костыль, заменю на mvp
    }

    companion object {
        private const val DEFAULT_HOURS = 19
    }


}
