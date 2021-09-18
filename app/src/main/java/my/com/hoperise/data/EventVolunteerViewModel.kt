package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventVolunteerViewModel : ViewModel() {

    private var volunteerListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration?      = null

    private var volunteers = listOf<Volunteer>()
    private val liveUser   = MutableLiveData<List<User>>()

    private var name     = ""
    private var field    = "id"
    private var reverse  = false

    private var eventID  = ""

    init {
        viewModelScope.launch {
            val events = EVENT.get().await().toObjects<Event>()
            val users  = USER.get().await().toObjects<User>()

            volunteerListener = VOLUNTEER.addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener

                volunteers = snap.toObjects()

                for (v in volunteers) {
                    val event = events.find { e -> e.id == v.eventID }!!
                    val user = users.find { u -> u.id == v.userID }!!

                    v.event = event
                    v.user = user
                }
                updateResult()
            }

            // to instant update in volunteer listing
            userListener = USER.addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener

                val snapUser = snap.toObjects<User>()

                for (v in volunteers) {
                    val user = snapUser.find { u -> u.id == v.userID }!!

                    v.user = user
                }

                updateResult()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        volunteerListener?.remove()
        userListener?.remove()
    }

    private fun updateResult() {
        var list = volunteers

        list = list.filter { v ->
            v.user.name.contains(name, true) && v.eventID == eventID
        }

        list = when (field) {
            "id"    -> list.sortedBy { v -> v.userID }
            "name"  -> list.sortedBy { v -> v.user.name }
            else    -> list
        }

        if (reverse) list = list.reversed()

        liveUser.value = list.map { v -> v.user }
    }

    fun getUser()  = liveUser

    fun setEventID(eventID: String) {
        this.eventID = eventID
    }

    fun search(name: String) {
        this.name = name
        updateResult()
    }

    fun sort(field: String): Boolean {
        reverse = if (this.field == field) !reverse else false
        this.field = field
        updateResult()

        return reverse
    }
}