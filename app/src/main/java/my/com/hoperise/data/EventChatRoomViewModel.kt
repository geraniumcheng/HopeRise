package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class EventChatRoomViewModel : ViewModel() {

    private var messageListener: ListenerRegistration? = null

    private var messages    = listOf<Message>()
    private val liveMessage = MutableLiveData<List<Message>>()

    private var eventID     = ""
    var userContent         = MutableLiveData("")

    init {
        viewModelScope.launch {
            val events = EVENT.get().await().toObjects<Event>()
            val users  = USER.get().await().toObjects<User>()

            messageListener = MESSAGE.addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener

                messages = snap.toObjects()

                for (m in messages) {
                    val event = events.find { e -> e.id == m.eventID }!!
                    val user  = users.find  { u -> u.id == m.userID }!!

                    m.event = event
                    m.user  = user
                }

                updateResult()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageListener?.remove()
    }

    private fun updateResult() {
        var list = messages

        list = list.filter { p -> p.eventID == eventID }.sortedBy { p -> p.date }

        liveMessage.value = list
    }

    fun getMessages() = liveMessage

    fun setEventID(eventID: String) {

        if (this.eventID != eventID)
            userContent.value = ""

        this.eventID = eventID
        updateResult()
    }

    fun sendMessage(text: String) {
        MESSAGE.document().set(Message(
            content = text,
            date    = Date(),
            userID  = currentUser!!.id,
            eventID = eventID
        ))
    }
}