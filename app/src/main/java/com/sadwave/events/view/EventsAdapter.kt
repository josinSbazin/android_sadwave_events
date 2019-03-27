package com.sadwave.events.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sadwave.events.net.EventEntity

class EventsAdapter(private val listener: Listener) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    var events: List<EventEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun getItemId(position: Int): Long {
        return events[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }

    inner class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
        private lateinit var event: EventEntity

        init {
//            textView.setOnClickListener { listener.onCityClick(event) }
        }

        fun bind(event: EventEntity) {
            this.event = event
            // todo
        }
    }

    interface Listener {
        fun onEventClick(event: EventEntity)
        fun onEventShare(event: EventEntity)
        fun onEventAddToCalendar(event: EventEntity)
    }
}