package my.com.hoperise.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import my.com.hoperise.R
import my.com.hoperise.data.User

class EventVolunteerAdapter (
    val fn: (ViewHolder, User) -> Unit = { _, _ -> }
) : ListAdapter<User, EventVolunteerAdapter.ViewHolder>(EventVolunteerAdapter)  {

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User)    = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val userID     : TextView  = view.findViewById(R.id.txtUserID)
        val userName   : TextView  = view.findViewById(R.id.txtUserName)
        val userEmail  : TextView  = view.findViewById(R.id.txtUserEmail)
        val userPhoto  : ImageView = view.findViewById(R.id.imgPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_event_volunteer_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        holder.userID.text    = user.id
        holder.userName.text  = user.name
        holder.userEmail.text = user.email
//        holder.userPhoto.setImageBitmap(user.photo.toBitmap())  //normal get
        holder.userPhoto.load(user.photo!!.toBitmap())            // with loading animation

        fn(holder, user)
    }
}