package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat

class VolunteerApplicationViewModel: ViewModel() {

    private val volApp = MutableLiveData<List<VolunteerApplication>>()
    private var app = listOf<VolunteerApplication>()
    private var lastID = ""
    private var status = ""
    private var field = ""
    private var reverse = false

    init {
        viewModelScope.launch {
            val users = USER.get().await().toObjects<User>()
            VOLUNTEERAPPLICATION.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                app = snap.toObjects<VolunteerApplication>()
                for(va in app){
                    val retrieve = users.find { c -> c.id == va.userID }!!
                    va.user = retrieve
                }
                updateResult()
            }
        }
    }

    private fun updateResult() {
        var list = app

        list = list.filter { va -> status == "All" || status == va.status }

        list = when(field){
            "name" -> list.sortedBy { va -> va.user.name }
            "date" -> list.sortedBy { va -> va.date }
            else -> list
        }

        if(reverse){
            list = list.reversed()
        }

        volApp.value = list
    }

    fun get(id: String): VolunteerApplication?{
        return volApp.value?.find { va -> va.id == id }
    }

    fun getAll() = volApp

    fun set(va: VolunteerApplication){
        VOLUNTEERAPPLICATION.document(va.id).set(va)
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
    fun validate(va: VolunteerApplication):String{
        var err = ""

        err += when{
            va.ICFrontPhoto!!.toBytes().isEmpty() -> "- Front IC Photo is required!\n"
            else -> ""
        }
        err += when{
            va.ICSelfiePhoto!!.toBytes().isEmpty() -> "- Selfie Photo with IC is required!\n"
            else -> ""
        }
        return err
    }
    private suspend fun getLastID(){
        val objects = VOLUNTEERAPPLICATION.get().await().toObjects<VolunteerApplication>()
        app = objects
        lastID = if(app.last().id == ""){
            "VA0000"
        }else{
            app.last().id
        }

    }
    suspend fun generateID(): String {
        getLastID()
        var idChar = lastID.takeWhile { it.isLetter() }
        var idNum = lastID.takeLastWhile { !it.isLetter() }

        val fmt = DecimalFormat("0000")
        val str = fmt.format(idNum.toInt() + 1)
        return idChar + str
    }


}