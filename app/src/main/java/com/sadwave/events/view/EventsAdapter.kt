package com.sadwave.events.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sadwave.events.R
import com.sadwave.events.net.EventEntity
import kotlinx.android.synthetic.main.item_event.view.*

class EventsAdapter(private val listener: Listener) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    var events: List<EventEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
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
            containerView.setOnClickListener { listener.onEventClick(event) }
            containerView.share.setOnClickListener { listener.onEventShare(event) }
            containerView.calendar.setOnClickListener { listener.onEventAddToCalendar(event) }
        }

        fun bind(event: EventEntity) {
            this.event = event

            val imgUrl = event.photo
            if (imgUrl != null) {
                Glide
                    .with(itemView.context)
                    .load(imgUrl)
                    .placeholder(R.drawable.default_image)
                    .into(itemView.image)
            }

            val eventDate = event.date?.date
            val eventTime = event.date?.time
            
            var date = eventDate
            if (eventTime != null) {
                date += " в $eventTime"
            }
            itemView.date.textOrGone = date?.trim()
            itemView.description.textOrGone = event.overview?.trim()
        }
    }

    interface Listener {
        fun onEventClick(event: EventEntity)
        fun onEventShare(event: EventEntity)
        fun onEventAddToCalendar(event: EventEntity)
    }
}