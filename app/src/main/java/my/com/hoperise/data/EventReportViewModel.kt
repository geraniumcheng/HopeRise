package my.com.hoperise.data

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.hoperise.util.getEventEndTime
import my.com.hoperise.util.parseEventDateTime
import java.text.SimpleDateFormat
import java.util.*

class EventReportViewModel : ViewModel() {

    private var volunteers = listOf<Volunteer>()
    private var events     = listOf<Event>()
    private val liveEvent  = MutableLiveData<List<Event>>()

    private var eventCategory = "All"

    @SuppressLint("SimpleDateFormat")
    val formatter    = SimpleDateFormat("MM/yyyy")
    var dateSelected = MutableLiveData(formatter.format(Date()))

    init { getRecord() }

    fun getRecord() {
        viewModelScope.launch {
            events     = EVENT.get().await().toObjects()
            volunteers = VOLUNTEER.get().await().toObjects()

            for (e in events) {
                e.participatedCount = 0
                e.attendedCount = 0

                for (v in volunteers) {
                    if (e.id == v.eventID) {
                        e.participatedCount++

                        if (v.attendance)
                            e.attendedCount++
                    }
                }
                e.status = checkStatus(e)
            }

            updateResult()
            filterCategory("All")
        }
    }

    private fun checkStatus(e: Event): String {
        val eventDate    = parseEventDateTime(e)
        val eventEndDate = getEventEndTime(eventDate)

        return if (eventEndDate.before(Date())) "Completed" else "Current"
    }

    fun updateResult() {
        var list = events
        val selectedDate = dateSelected.value!!.split("/")

        list = list.filter { e -> e.status == "Completed"
            && (e.date.split("-")[1] == selectedDate[0] && e.date.split("-")[2] == selectedDate[1])
            && (eventCategory == "All" || e.category == eventCategory) }

        liveEvent.value = list
    }

    fun getEvent() = liveEvent

    fun filterCategory(eventCategory: String) {
        this.eventCategory = eventCategory
        updateResult()
    }
}