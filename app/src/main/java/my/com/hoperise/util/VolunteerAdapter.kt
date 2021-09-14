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
import my.com.hoperise.data.User
import java.text.SimpleDateFormat
import java.util.*

class VolunteerAdapter (
    val fn: (ViewHolder, User) -> Unit = { _, _ -> }
) : ListAdapter<User, VolunteerAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val volListID  : TextView = view.findViewById(R.id.volListID)
        val volListName  : TextView = view.findViewById(R.id.volListName)
        val volListPhoto  : ImageView = view.findViewById(R.id.volListPhoto)
        val volListStatus  : TextView = view.findViewById(R.id.volListStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_volunteer_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val volItem = getItem(position)

        holder.volListID.text = volItem.id
        holder.volListName.text = volItem.name
        holder.volListPhoto.setImageBitmap(volItem.photo?.toBitmap())
        holder.volListStatus.text = volItem.status

        fn(holder, volItem)
    }
}