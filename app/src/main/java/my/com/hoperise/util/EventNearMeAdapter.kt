package my.com.hoperise.util

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.com.hoperise.R
import my.com.hoperise.data.Event
import java.text.SimpleDateFormat
import java.util.*

class EventNearMeAdapter (
    val fn: (ViewHolder, Event) -> Unit = { _, _ -> }
) : ListAdapter<Event, EventNearMeAdapter.ViewHolder>(EventNearMeAdapter) {
    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val lblEvName  : TextView = view.findViewById(R.id.lblEvName)
        val lblEvCategory  : TextView = view.findViewById(R.id.lblEvCategory)
        val lblEvDateTime  : TextView = view.findViewById(R.id.lblEvDateTime)
        val lblEvDistance  : TextView = view.findViewById(R.id.lblEvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_event_near_me, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evNearMe = getItem(position)

        holder.lblEvName.text = evNearMe.name
        holder.lblEvCategory.text = evNearMe.category
        holder.lblEvDateTime.text = evNearMe.date + " " + evNearMe.time
        holder.lblEvDistance.text = String.format("%.2f", evNearMe.orphanage.distance) + " km"


        fn(holder, evNearMe)
    }
}