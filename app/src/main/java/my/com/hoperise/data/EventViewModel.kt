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

class EventViewModel: ViewModel() {

    private val event = MutableLiveData<List<Event>>()
    private var ev = listOf<Event>()
    private var lastID = ""

    private var name = ""       // Search
    private var status = ""     // Filter
    private var category = "" // Filter2
    private var field = ""      // Sort
    private var reverse = false // Sort

    init {
        viewModelScope.launch {
            //val event = EVENT.get().await().toObjects<Event>()
            EVENT.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                ev = snap.toObjects<Event>()

                lastID = if(ev.last().id == ""){
                    "EV0001"
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
        val date1 = SimpleDateFormat("dd-MM-yyyy").parse(status.date)
        return if(date1.after(Date())){
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
            "date" -> sortDate(list)//list.sortedBy { e -> e.date } //need to change func check year>month>date
            else -> list
        }

        if(reverse){
            list = list.reversed()
        }

        event.value = list
    }
    private fun sortDate(list: List<Event>): List<Event>{
//        val dateTimeStrToLocalDateTime: (String) -> LocalDateTime = {
//            LocalDateTime.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//        }
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        list.sortedBy { LocalDate.parse(it.date+" "+it.time, dateTimeFormatter) }
        Log.d("list",list.toString())
        Log.d("list",list[0].date + " " + list[0].time)
        Log.d("list",list[1].date+ " " + list[1].time)
        Log.d("list",list[2].date+ " " + list[2].time)
        Log.d("list",list[3].date+ " " + list[3].time)
        Log.d("list",list[4].date+ " " + list[4].time)
        Log.d("list",list[5].date+ " " + list[5].time)

//        val cmp = compareBy<String> { LocalDateTime.parse(it , DateTimeFormatter.ofPattern("dd-MM-yyyy")) }
//        list.sortedWith(cmp)

       //list.sortedBy {dateTimeStrToLocalDateTime }
//        var year = 0
//        var month = 0
//        var day = 0
//        var hour = 0
//        var minute = 0
//        for(e in list){
//            if(e.year > year)
//        }
        return list
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