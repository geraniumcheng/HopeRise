package my.com.hoperise.data

import android.net.Uri
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


val ORPHANAGE = Firebase.firestore.collection("orphanage")
val EVENT = Firebase.firestore.collection("event")
val VOLUNTEERAPPLICATION = Firebase.firestore.collection("volunteerApplication")

data class Location(
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)

data class TempPhoto(
    var photo: Uri? = null
)

data class currentUser(
    var role: String = ""
)
//
//data class Size(
//    var orphanage: Int = 4,
//    var event: Int = 0
//)

data class Orphanage(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var photo: Blob? = Blob.fromBytes(ByteArray(0))
){
    @get:Exclude
    var count: Int = 0
    @get:Exclude
    var counter: Int = 0
}

data class Event(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var date: String = "",
    var time: String = "",
    var volunteerRequired: Int = 0,
    var volunteerCount: Int = 0,
    var description: String = "",
    var orphanageID: String = "" //Foreign key
){
    @get:Exclude
    var count: Int = 0
    @get:Exclude
    var status: String = ""
    @get:Exclude
    var orphanage: Orphanage = Orphanage()
}

data class VolunteerApplication(
    @DocumentId
    var id: String = "",
    var ICFrontPhoto: Blob? = Blob.fromBytes(ByteArray(0)),
    var ICSelfiePhoto: Blob? = Blob.fromBytes(ByteArray(0)),
    var status: String = "",
    var reason: String = "",
    var date: Date = Date(),
    var userID: String = "" //Foreign key
){
    @get:Exclude
    var count: Int = 0
}