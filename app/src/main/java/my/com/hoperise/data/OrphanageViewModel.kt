package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch

class OrphanageViewModel: ViewModel() {
    private val orphanage = MutableLiveData<List<Orphanage>>()
    private var orp = listOf<Orphanage>()
    private var field = ""
    private var reverse = false
    private var lastID = ""

    private var name = ""

    init {
        viewModelScope.launch {

            ORPHANAGE.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                orp = snap.toObjects<Orphanage>()

                lastID = if(orp.last().id == ""){
                    "OR0000"
                }else{
                    orp.last().id
                }
                updateResult()
            }
        }
    }

    private fun updateResult() {
        var list = orp

        list = when (field){
            "id" -> list.sortedBy { o -> o.id }
            "name" -> list.sortedBy { o -> o.name }
            else -> list
        }

        list = list.filter {
                o -> o.name.contains(name, true)
        }

        if (reverse){
            list = list.reversed()
        }

        orphanage.value = list
    }
    fun search(name: String) {
        this.name = name
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
    fun get(id: String): Orphanage?{
        return orphanage.value?.find { o -> o.id == id }
    }

     fun getAll() = orphanage

    fun delete(id: String){
        ORPHANAGE.document(id).delete()
    }

    fun set(o: Orphanage){
        ORPHANAGE.document(o.id).set(o)
    }

    fun validate(o: Orphanage):String{
        var err = ""

        err += when (o.name) {
            "" -> "- Please fill in the orphanage name!\n"
            //nameExists(o.name) -> "- ${o.name} has existed. Please provide a different name!\n"
            else -> ""
        }

        err += when{
            o.photo!!.toBytes().isEmpty() -> "- Photo is required!\n"
            else -> ""
        }

        err += when (o.location) {
            "" -> "- Please choose your orphanage's location!\n"
            else -> ""
        }

        return err
    }

    fun generateID(): String = my.com.hoperise.util.generateID(lastID)
}
