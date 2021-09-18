package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.hoperise.util.getEventEndTime
import my.com.hoperise.util.parseEventDateTime
import java.util.*

class EventNearMeViewModel: ViewModel() {

    private val nearEvent = MutableLiveData<List<Event>>()
    private var event = listOf<Event>()

    init{
        viewModelScope.launch {
            val orphanage = ORPHANAGE.get().await().toObjects<Orphanage>()

            EVENT.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                event = snap.toObjects()
                for(e in event){
                    e.orphanage = orphanage.find { o -> o.id == e.orphanageID }!!
                    e.status = checkStatus(e)
                }
                nearEvent.value = event
            }
        }
    }

    private fun checkStatus(status: Event): String {
        val eventDate    = parseEventDateTime(status)
        val eventEndDate = getEventEndTime(eventDate)
        return if(eventEndDate.after(Date()) || eventDate.after(Date())){
            "Current"
        }else {
            "Completed"
        }
    }

    fun getAll() = nearEvent
}