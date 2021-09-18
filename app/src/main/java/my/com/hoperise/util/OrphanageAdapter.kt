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

class OrphanageAdapter (
    val fn: (ViewHolder, Orphanage) -> Unit = { _, _ -> }
) : ListAdapter<Orphanage, OrphanageAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Orphanage>() {
        override fun areItemsTheSame(oldItem: Orphanage, newItem: Orphanage)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Orphanage, newItem: Orphanage) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val orpName     : TextView  = view.findViewById(R.id.orpName)
        val orpPhoto    : ImageView = view.findViewById(R.id.orpPhoto)
        val orpID       : TextView  = view.findViewById(R.id.orpId)
        val orpAddress  : TextView  = view.findViewById(R.id.orpAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_orphanage_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orphanageItem = getItem(position)

        holder.orpID.text       = orphanageItem.id
        holder.orpName.text     = orphanageItem.name
        holder.orpAddress.text  = orphanageItem.location
        holder.orpPhoto.setImageBitmap(orphanageItem.photo?.toBitmap())

        fn(holder, orphanageItem)
    }
}
