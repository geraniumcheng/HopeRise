package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.hoperise.util.getEventEndTime
import my.com.hoperise.util.parseEventDateTime
import java.util.*

class ParticipationViewModel : ViewModel() {

    private var volunteerListener: ListenerRegistration?                = null
    private var eventListener: ListenerRegistration?                    = null

    private var volunteers = listOf<Volunteer>()
    private val liveEvent  = MutableLiveData<List<Event>>()

    private var name                = ""
    private var participationStatus = "All"
    private var eventCategory       = "All"
    private var field               = "date"
    private var reverse             = true //to get the latest participated event to display first

    private var isLoaded            = false

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
                    v.event.status = checkStatus(v)
                    v.user = user
                }
                updateResult()

                isLoaded = true
            }

            //in case the event detail change by others then here will get instance update
            eventListener = EVENT.addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener

                val snapEvent = snap.toObjects<Event>()

                for (v in volunteers) {
                    val event = snapEvent.find { e -> e.id == v.eventID }!!

                    v.event = event
                    v.event.status = checkStatus(v)
                }

                updateResult()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        volunteerListener?.remove()
        eventListener?.remove()
    }

    private fun checkStatus(v: Volunteer): String {
        val eventDate    = parseEventDateTime(v.event)
        val eventEndDate = getEventEndTime(eventDate)

        return if (v.attendance)
            "Participated"
        // if the eventDate is not enough 1 day or the eventDate is after current date time
        else if ((!v.attendance && eventEndDate.after(Date())) || (!v.attendance && eventDate.after(Date())))
            "Current"
        else
            "Missed"
    }

    private fun updateResult() {
        var list = volunteers

        list = list.filter { v ->
            v.event.name.contains(name, true)
                    && v.userID == currentUser!!.id
                    && (participationStatus == "All" || participationStatus == v.event.status)
                    && (eventCategory == "All" || eventCategory == v.event.category)
        }

        list = when (field) {
            "id"    -> list.sortedBy { v -> v.eventID }
            "name"  -> list.sortedBy { v -> v.event.name }
            "date"  -> list.sortedBy { v -> parseEventDateTime(v.event) }
            else    -> list
        }

        if (reverse) list = list.reversed()

        liveEvent.value = list.map { v -> v.event }
    }

    fun getEvent() = liveEvent

    fun search(name: String) {
        this.name = name
        updateResult()
    }

    fun filterParticipationStatus(participationStatus: String) {
        this.participationStatus = participationStatus
        updateResult()
    }

    fun filterCategory(eventCategory: String) {
        this.eventCategory = eventCategory
        updateResult()
    }

    fun sort(field: String): Boolean {
        reverse = if (this.field == field) !reverse else false
        this.field = field
        updateResult()

        return reverse
    }

    fun set(v: Volunteer) {
        VOLUNTEER.document().set(v)
    }

    fun update(v: Volunteer) {
        VOLUNTEER.document(v.id).update("attendance", true)
    }

    fun isInEventDuration(v: Volunteer): String {
        var err = ""

        val eventDate    = parseEventDateTime(v.event)
        val eventEndDate = getEventEndTime(eventDate)
        val currentDate  = Date()

        if (currentDate.after(eventEndDate))
            err = "The event is over. Are you sure you want to sign the attendance?"
        else if (currentDate.before(eventDate))
            err = "The event haven't started. Are you sure to sign the attendance now?"

        return err
    }

    suspend fun get(eventId: String, userID: String): Volunteer? {
        if (!isLoaded) { delay(2000) }

        return volunteers.find { v -> v.eventID == eventId && v.userID == userID }
    }

    fun validate(event: Event): String {
        var err = ""

        err += detectCrash(event)

        if (event.volunteerRequired == event.volunteerCount)
            err += "- Event has been fully registered. \n"

        return err
    }

    private fun detectCrash(event : Event): String {
        var err          = ""
        var list         = volunteers
        val eventDate    = parseEventDateTime(event)
        val eventEndDate = getEventEndTime(eventDate)

        list = list.filter { v -> v.userID == currentUser!!.id }

        for (v in list) {
            val joinedEventDate = parseEventDateTime(v.event)
            val joinedEventEndDate = getEventEndTime(joinedEventDate)

            // return err message
            // 1.if the eventDate(event wanted to join) is between the joinedEvent Start and End Time
            // 2.if the eventDate End Start crash with joinedEvent Time
            if (eventDate.after(joinedEventDate) && eventDate.before(joinedEventEndDate))
                err += "- This event start time has crashed with previous joined event. \n"
            else if (eventEndDate.after(joinedEventDate) && eventEndDate.before(joinedEventEndDate))
                err += "- This event end time has crashed with previous joined event. \n"
        }

        return err
    }

    suspend fun isExist(eventID: String): Boolean {
        val volunteer = VOLUNTEER
            .whereEqualTo("eventID", eventID)
            .whereEqualTo("userID", currentUser!!.id)
            .get()
            .await()
            .toObjects<Volunteer>()
            .firstOrNull()

        return volunteer != null
    }

    /**
     * Refer to https://stackoverflow.com/questions/7128704/how-would-i-go-about-finding-the-closest-date-to-a-specified-date-java
     */
    suspend fun getNearest(): Volunteer? {
        if (!isLoaded) { delay(2000) }

        try {
            val list = volunteers
                .filter { v -> v.userID == currentUser!!.id }
                .sortedBy { v -> parseEventDateTime(v.event) }
            var nearest = list.last()
            val current = Date()

            for (v in list) {
                val date = parseEventDateTime(v.event)
                val diff = current.time - date.time

                // get the first negative value(nearest date)
                if (diff < 0) {
                    nearest = v
                    return nearest
                }
            }

            return nearest

        }
        catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun checkVolunteerStatus(): String {
        if (!isLoaded) { delay(2000) }//wait for volunteers fetch all the records }

        var status = "" // it is not redundant in case there are no value get

        try {
            var volunteerApplications  = VOLUNTEERAPPLICATION.get().await().toObjects<VolunteerApplication>()

            for (va in volunteerApplications) {
                volunteerApplications = volunteerApplications
                    .filter { va-> va.userID == currentUser!!.id }
                    .sortedBy { va -> va.date }
            }

            status = volunteerApplications.last().status
        }
        catch (e: Exception) {
            checkVolunteerStatus()
        }

        return status
    }
}