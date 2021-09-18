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
import my.com.hoperise.data.Message
import my.com.hoperise.data.currentUser

class EventChatRoomAdapter : ListAdapter<Message, EventChatRoomAdapter.ViewHolder>(EventChatRoomAdapter) {

    private val MESSAGE_FROM = 0
    private val MESSAGE_TO   = 1

    companion object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message)   = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Message, newItem:Message) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val txtMessage : TextView =  view.findViewById(R.id.txtMessage)
        val imgAvatar  : ImageView = view.findViewById(R.id.imgAvatar)
    }

    override fun getItemViewType(position: Int): Int {
        val messageItem = getItem(position)
        return if (messageItem.userID == currentUser!!.id) MESSAGE_TO
        else MESSAGE_FROM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MESSAGE_TO) {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_message_to, parent, false)
            ViewHolder(view)
        }
        else {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_message_from, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageItem = getItem(position)

        holder.txtMessage.text = messageItem.content
        holder.imgAvatar.load(messageItem.user.photo!!.toBitmap()!!) { placeholder(R.drawable.loading_ani) }
    }
}