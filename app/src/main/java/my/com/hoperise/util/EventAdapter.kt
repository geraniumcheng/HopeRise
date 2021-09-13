package my.com.hoperise.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.type.DateTime
import my.com.hoperise.R
import my.com.hoperise.data.Event
import my.com.hoperise.data.Orphanage
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class EventAdapter (
    val fn: (ViewHolder, Event) -> Unit = { _, _ -> }
) : ListAdapter<Event, EventAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val eventID  : TextView = view.findViewById(R.id.lblEventID)
        val eventCategory  : TextView = view.findViewById(R.id.lblEventCategory)
        val eventDateTime  : TextView = view.findViewById(R.id.lblEventDateTime)
        val eventName  : TextView = view.findViewById(R.id.lblEventName)
        val eventStatus  : TextView = view.findViewById(R.id.lblEventStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_event_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventItem = getItem(position)

        holder.eventID.text = eventItem.id
        holder.eventCategory.text = eventItem.category
        holder.eventDateTime.text = eventItem.date + " " + eventItem.time
        holder.eventName.text = eventItem.name

        val date1 = SimpleDateFormat("dd-MM-yyyy").parse(eventItem.date)
        if(date1.after(Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur")).time)){
            holder.eventStatus.text = "Current"
        }else {
            holder.eventStatus.text = "Completed"
        }

        //holder.eventStatus.text = "Completed"

        fn(holder, eventItem)
    }
}
