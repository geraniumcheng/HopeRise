package my.com.hoperise.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.com.hoperise.R
import my.com.hoperise.data.Orphanage

class OrphangeEventAdapter (
    val fn: (ViewHolder, Orphanage) -> Unit = { _, _ -> }
) : ListAdapter<Orphanage, OrphangeEventAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Orphanage>() {
        override fun areItemsTheSame(oldItem: Orphanage, newItem: Orphanage)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Orphanage, newItem: Orphanage) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val lblOrphName  : TextView = view.findViewById(R.id.lblOrphName)
        val OrphImg : ImageView = view.findViewById(R.id.OrphImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_choose_orphanage_for_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orphanageItem = getItem(position)

        holder.lblOrphName.text = orphanageItem.name
        holder.OrphImg.setImageBitmap(orphanageItem.photo?.toBitmap())


        fn(holder, orphanageItem)
    }
}