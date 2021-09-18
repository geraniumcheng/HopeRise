package my.com.hoperise.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import my.com.hoperise.R
import my.com.hoperise.data.Photo

class EventGalleryAdapter (
    val fn: (ViewHolder, Photo) -> Unit = { _, _ -> }
) : ListAdapter<Photo, EventGalleryAdapter.ViewHolder>(EventGalleryAdapter)  {

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo)   = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Photo, newItem:Photo) = oldItem == newItem
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val photo : ImageView = view.findViewById(R.id.imgPhotoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_gallery_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photoItem = getItem(position)

//      holder.photo.setImageBitmap(photoItem.photo.toBitmap()!!) //normal load without loading animation
        holder.photo.load(photoItem.photo.toBitmap()!!) { placeholder(R.drawable.loading_ani) }

        fn(holder, photoItem)
    }
}