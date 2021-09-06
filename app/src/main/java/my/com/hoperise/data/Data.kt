package my.com.hoperise.data

import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


val ORPHANAGE = Firebase.firestore.collection("Orphanage")
val EVENT = Firebase.firestore.collection("Event")
val VOLUNTEERAPPLICATION = Firebase.firestore.collection("VolunteerApplication")

data class Location(
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)

data class Orphanage(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var photo: Blob? = Blob.fromBytes(ByteArray(0))
){
    //things that you dw to store in firestore
    @get:Exclude
    var count: Int = 0
}

data class Event(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var category: String = "",
    var status: String = "",
    var date: Date = Date(),
    //var time: Time
    var volunteerRequired: Int = 0,
    var volunteerCount: Int = 0,
    var description: String = "",
    var orphanageID: String = "" //Foreign key
){
    @get:Exclude
    var count: Int = 0
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