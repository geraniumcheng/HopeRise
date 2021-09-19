package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.hoperise.util.generateID
import java.util.*

class EventGalleyViewModel : ViewModel() {

    private var photoListener: ListenerRegistration? = null

    private var photos      = listOf<Photo>()
    private val livePhotos  = MutableLiveData<List<Photo>>()
    private var eventID     = ""

    init {
        viewModelScope.launch {
            val events = EVENT.get().await().toObjects<Event>()

            photoListener = PHOTO.addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener

                photos = snap.toObjects()

                for (p in photos) {
                    val event = events.find { e -> e.id == p.eventID }!!

                    p.event = event
                }

                updateResult()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        photoListener?.remove()
    }

    private fun updateResult() {
        var list = photos

        list = list.filter { p -> p.eventID == eventID }.sortedBy { p -> p.date }

        livePhotos.value = list
    }

    fun getPhotos() = livePhotos

    fun setEventID(eventID: String) {
        this.eventID = eventID
        updateResult()
    }

    fun get(id: String): Photo? {
        return photos.find { p -> p.id == id }
    }

    fun delete(id: String){
        PHOTO.document(id).delete()
    }

    fun deleteAll(eventID: String) {
        val list = photos.filter { p -> p.eventID == eventID }

        for (p in list)
            PHOTO.document(p.id).delete()
    }

    fun upload(photo: Blob) {
        PHOTO.document(getNewID()).set(Photo(
            photo   = photo,
            eventID = eventID
        ))
    }

    private fun getNewID(): String {
        var list = photos

        list = list.sortedBy { p -> p.date }

        return generateID(list.last().id)
    }
}