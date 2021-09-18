package my.com.hoperise.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.com.hoperise.R
import my.com.hoperise.data.VolunteerApplication
import java.text.SimpleDateFormat

class VolunteerApplicationAdapter (
    val fn: (ViewHolder, VolunteerApplication) -> Unit = { _, _ -> }
) : ListAdapter<VolunteerApplication, VolunteerApplicationAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<VolunteerApplication>() {
        override fun areItemsTheSame(oldItem: VolunteerApplication, newItem: VolunteerApplication)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VolunteerApplication, newItem: VolunteerApplication) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val volAppImage  : ImageView = view.findViewById(R.id.volAppImage)
        val volAppName   : TextView  = view.findViewById(R.id.volAppName)
        val volAppDate   : TextView  = view.findViewById(R.id.volAppDate)
        val volAppStatus : TextView  = view.findViewById(R.id.volAppStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_volunteer_application_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val format = SimpleDateFormat("dd-MM-yyyy")
        val volAppItem = getItem(position)
        holder.volAppImage.setImageBitmap(volAppItem.user.photo?.toBitmap())
        holder.volAppName.text   = volAppItem.user.name
        holder.volAppDate.text   = format.format(volAppItem.date)
        holder.volAppStatus.text = volAppItem.status

        fn(holder, volAppItem)
    }
}