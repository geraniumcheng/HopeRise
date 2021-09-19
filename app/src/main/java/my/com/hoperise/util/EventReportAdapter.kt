package my.com.hoperise.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.com.hoperise.R
import my.com.hoperise.data.Event

class EventReportAdapter : ListAdapter<Event, EventReportAdapter.ViewHolder>(EventReportAdapter) {

    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val eventID         : TextView = view.findViewById(R.id.lblEventID)
        val eventCategory   : TextView = view.findViewById(R.id.lblEventCategory)
        val eventDateTime   : TextView = view.findViewById(R.id.lblEventDateTime)
        val eventName       : TextView = view.findViewById(R.id.lblEventName)
        val volParticipated : TextView = view.findViewById(R.id.txtVolunteerParticipated)
        val volAttended     : TextView = view.findViewById(R.id.txtAttended)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_event_report, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventItem = getItem(position)

        holder.eventID.text         = eventItem.id
        holder.eventCategory.text   = eventItem.category
        holder.eventDateTime.text   = eventItem.date + " " + eventItem.time
        holder.eventName.text       = eventItem.name
        holder.volParticipated.text = eventItem.participatedCount.toString()
        holder.volAttended.text     = eventItem.attendedCount.toString()
    }
}