package com.sadwave.events.view

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Transformation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sadwave.events.R
import com.sadwave.events.net.EventEntity
import com.sadwave.events.util.RoundedCornersTransformation
import com.sadwave.events.util.SadDateFormatter
import kotlinx.android.synthetic.main.item_event.view.*

class EventsAdapter(
    private val listener: Listener,
    private val sadDateFormatter: SadDateFormatter
) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
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

    override fun onViewRecycled(holder: ViewHolder) {
        holder.recycle()
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
                    .transition(DrawableTransitionOptions().crossFade())
                    .transform(RoundedCorners(15))
                    .into(itemView.image)
            }

            val eventDate = event.date?.date
            val eventTime = event.date?.time

            var dateString = ""
            if (eventDate != null) {
                dateString = sadDateFormatter.getFormattedDate(eventDate)
            }

            var timeString = ""
            if (eventTime != null) {
                timeString = sadDateFormatter.getFormattedTime(eventTime)
            }

            itemView.name.textOrGone = event.name?.trim()
            itemView.date.textOrGone = dateString.trim()
            itemView.time.textOrGone = timeString.trim()
            itemView.description.textOrGone = event.overview?.trim()

        }

        fun recycle() {
            Glide
                .with(itemView.context)
                .clear(itemView.image)
        }
    }

    interface Listener {
        fun onEventClick(event: EventEntity)
        fun onEventShare(event: EventEntity)
        fun onEventAddToCalendar(event: EventEntity)
    }
}