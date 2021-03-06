package my.com.hoperise.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.com.hoperise.R
import my.com.hoperise.data.Event

class EventAdapter (
    val fn: (ViewHolder, Event) -> Unit = { _, _ -> }
) : ListAdapter<Event, EventAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val eventID        : TextView = view.findViewById(R.id.lblEventName)
        val eventCategory  : TextView = view.findViewById(R.id.lblEventCategory)
        val eventDateTime  : TextView = view.findViewById(R.id.lblEventDateTime)
        val eventName      : TextView = view.findViewById(R.id.lblEventName)
        val eventStatus    : TextView = view.findViewById(R.id.lblEventStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_event_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventItem = getItem(position)

        holder.eventID.text       = eventItem.id
        holder.eventCategory.text = eventItem.category
        holder.eventDateTime.text = eventItem.date + " " + eventItem.time
        holder.eventName.text     = eventItem.name
        holder.eventStatus.text   = eventItem.status

//        val eventDate    = parseEventDateTime(eventItem)
//        val eventEndDate = getEventEndTime(eventDate)
//        if(eventEndDate.after(Date()) || eventDate.after(Date())){
//            holder.eventStatus.text = "Current"
//        }else {
//            holder.eventStatus.text = "Completed"
//        }

        when (eventItem.status) {
            "Missed"       -> holder.eventStatus.setTextColor(Color.RED)
            "Current"      -> holder.eventStatus.setTextColor(Color.parseColor("#23BF13")) // green
            "Participated" -> holder.eventStatus.setTextColor(Color.parseColor("#F6BE00")) // yellow
            "Completed"    -> holder.eventStatus.setTextColor(Color.parseColor("#F6BE00"))
        }

        fn(holder, eventItem)
    }
}