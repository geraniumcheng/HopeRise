package my.com.hoperise.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoFireUtils

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

import com.google.firebase.firestore.QuerySnapshot

import com.google.android.gms.tasks.Tasks

import com.firebase.geofire.GeoQueryBounds
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import my.com.hoperise.util.getEventEndTime
import my.com.hoperise.util.parseEventDateTime


class EventViewModel: ViewModel() {

    private val event = MutableLiveData<List<Event>>()
    private var ev = listOf<Event>()
    private var lastID = ""

    private var name = ""
    private var status = ""
    private var category = ""
    private var field = ""
    private var reverse = false

    init {
        viewModelScope.launch {
            EVENT.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                ev = snap.toObjects<Event>()

                lastID = if(ev.last().id == ""){
                    "EV0000"
                }else{
                    ev.last().id
                }
                for (e in ev){
                    e.status = checkStatus(e)
                }
                updateResult()
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

    private fun updateResult() {
        var list = ev


        list = list.filter {
            e -> e.name.contains(name, true)
                && (category == "All" || category == e.category)
                && (status == "All" || status == e.status)
        }

        list = when(field){
            "id" -> list.sortedBy { e -> e.id }
            "name" -> list.sortedBy { e -> e.name }
            "date" -> list.sortedBy { e -> parseEventDateTime(e) }
            else -> list
        }

        if(reverse){
            list = list.reversed()
        }

        event.value = list
    }

    fun search(name: String) {
        this.name = name
        updateResult()
    }
    fun filterCategory(category: String) {
        this.category = category
        updateResult()
    }
    fun filterStatus(status: String) {
        this.status = status
        updateResult()
    }
    fun sort(field: String): Boolean {
        if(this.field == field)
            reverse = !reverse
        else
            reverse = false

        this.field = field
        updateResult()

        return reverse
    }

    fun get(id: String): Event?{
        return event.value?.find { e -> e.id == id }
    }

    fun getAll() = event

    fun delete(id: String){
        EVENT.document(id).delete()
    }

    fun deleteEvents(orpID: String){
        Log.d("orpid", orpID)
        event.value?.forEach { e ->
            if(e.orphanageID == orpID) {delete(e.id)}
        }
    }

    fun set(e: Event){
        EVENT.document(e.id).set(e)
    }

    fun validate(e: Event):String{
        var err = ""

        err += when (e.name) {
            "" -> "- Please fill in the event name!\n"
            else -> ""
        }

        err += if(e.date == "") "- Please choose a date!\n"
                else ""

        err += if(e.time == "") "- Please choose a time!\n"
                else ""

        err += if(e.volunteerRequired == 0) "- Please set a number for the volunteer capacity!\n"
                else ""

        err += if(e.description == "") "- Please fill in some description for the event!\n"
                else ""

        return err
    }

    fun generateID(): String {

        var idChar = lastID.takeWhile { it.isLetter() }
        var idNum = lastID.takeLastWhile { !it.isLetter() }

        val fmt = DecimalFormat("0000")
        val str = fmt.format(idNum.toInt() + 1)
        //var add = (idNum.toInt() + 1).toString()
        return idChar + str
    }

}