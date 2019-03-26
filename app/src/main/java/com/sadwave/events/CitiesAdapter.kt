package com.sadwave.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sadwave.events.net.CityEntity

class CitiesAdapter(private val listener: Listener) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {
    var cities: List<CityEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var selectedIndex: Int = -1
    var selectedCity: CityEntity? = null
        set(value) {
            field = value
            selectedIndex = cities.indexOf(value)
            if (selectedIndex == -1) {
                notifyDataSetChanged()
            } else {
                notifyItemChanged(selectedIndex)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == selectedIndex) return R.layout.item_selected_city
        return R.layout.item_city
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun getItemId(position: Int): Long {
        return cities[position].id.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    inner class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
        private val textView: TextView = itemView as TextView
        private lateinit var city: CityEntity

        init {
            textView.setOnClickListener { listener.onCityClick(city) }
        }

        fun bind(city: CityEntity) {
            this.city = city
            textView.text = city.name
        }
    }

    interface Listener {
        fun onCityClick(city: CityEntity)
    }
}