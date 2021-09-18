package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch


class VolunteerViewModel: ViewModel() {
    private val volunteer = MutableLiveData<List<User>>()
    private var vol = ArrayList<User>()
    private var volu = listOf<User>()

    private var field = ""
    private var status = ""
    private var reverse = false
    private var name = ""

    init{
        viewModelScope.launch {
            USER.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                for(a in snap.toObjects<User>()){
                    if(a.role == "Volunteer"){
                        vol.add(a)
                    }
                }
                volu = vol
                updateResult()
            }
        }
    }

    private fun updateResult() {
        var list = volu

        list = list.filter {
                e -> e.name.contains(name, true)
                && (status == "All" || status == e.status)
        }

        list = when(field){
            "id" -> list.sortedBy { e -> e.id }
            "name" -> list.sortedBy { e -> e.name }
            else -> list
        }

        if(reverse){
            list = list.reversed()
        }

        volunteer.value = list
    }
    fun get(id: String): User?{
        return volunteer.value?.find { e -> e.id == id }
    }

    fun getAll() = volunteer

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
    fun filterStatus(status: String) {
        this.status = status
        updateResult()
    }
}