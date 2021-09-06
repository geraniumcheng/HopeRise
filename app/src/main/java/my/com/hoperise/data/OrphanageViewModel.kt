package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class OrphanageViewModel: ViewModel() {
    private val colOrphanage = Firebase.firestore.collection("orphanage")
    private val orphanage = MutableLiveData<List<Orphanage>>()

    init {
        colOrphanage.addSnapshotListener { value, _ -> orphanage.value = value?.toObjects<Orphanage>()  }
    }

    fun get(id: String): Orphanage?{
        return orphanage.value?.find { o -> o.id == id }
    }

    fun getAll() = orphanage

    fun delete(id: String){
        colOrphanage.document(id).delete()
    }

    fun set(o: Orphanage){
        colOrphanage.document(o.id).set(o)
    }

    private fun idExists(id: String): Boolean{
        return orphanage.value?.any { o -> o.id == id } ?: false
    }

    fun validate(o: Orphanage, insert: Boolean = true):String{
        var err = ""

        return err
    }
}