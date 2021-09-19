package my.com.hoperise.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.graphics.Bitmap
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.com.hoperise.R
import my.com.hoperise.util.toBlob
import java.util.*

val ORPHANAGE = Firebase.firestore.collection("orphanage")
val EVENT = Firebase.firestore.collection("event")
val VOLUNTEERAPPLICATION = Firebase.firestore.collection("volunteerApplication")
val VOLUNTEER = Firebase.firestore.collection("volunteer")
val MESSAGE = Firebase.firestore.collection("message")
val PHOTO = Firebase.firestore.collection("photo")
val USER = Firebase.firestore.collection("user")
val EMPLOYEE = Firebase.firestore.collection("user").whereIn("role", listOf("Manager","Employee"))
var currentUser: User? = null

var reason: String = ""
var returnFragment: Boolean = false
var returnVAID: String = ""
var cameraPhoto: Bitmap? = null
var galleryPhoto: Uri? = null

data class Location(
    var location: String  = "",
    var latitude: Double  = 0.0,
    var longitude: Double = 0.0,
)

data class Orphanage(
    @DocumentId
    var id: String        = "",
    var name: String      = "",
    var location: String  = "",
    var latitude: Double  = 0.0,
    var longitude: Double = 0.0,
    var photo: Blob?      = Blob.fromBytes(ByteArray(0))
){
    @get:Exclude
    var distance: Double = 0.0
}

data class Event(
    @DocumentId
    var id: String             = "",
    var name: String           = "",
    var category: String       = "",
    var date: String           = "",
    var time: String           = "",
    var volunteerRequired: Int = 0,
    var volunteerCount: Int    = 0,
    var description: String    = "",
    var orphanageID: String    = "" //Foreign key
){
    @get:Exclude
    var status: String = ""
    @get:Exclude
    var orphanage: Orphanage = Orphanage()
    @get:Exclude
    var participatedCount: Int = 0
}

data class VolunteerApplication(
    @DocumentId
    var id: String           = "",
    var ICFrontPhoto: Blob?  = Blob.fromBytes(ByteArray(0)),
    var ICSelfiePhoto: Blob? = Blob.fromBytes(ByteArray(0)),
    var status: String       = "",
    var reason: String       = "",
    var date: Date           = Date(),
    var userID: String       = "" //Foreign key
){
    @get:Exclude
    var user: User = User()
}

data class Volunteer(
    @DocumentId
    var id: String          = "",
    var attendance: Boolean = false,
    var userID: String      = "", //Foreign key
    var eventID: String     = "", //Foreign key
){
    @get:Exclude
    var user: User   = User()
    @get:Exclude
    var event: Event = Event()
}

data class Photo(
    @DocumentId
    var id: String         = "",
    var photo: Blob        = Blob.fromBytes(ByteArray(0)),
    var date: Date         = Date(),
    var eventID: String    = "", //Foreign key
){
    @get:Exclude
    var event: Event = Event()
}

data class Message(
    @DocumentId
    var id: String         = "",
    var content: String    = "",
    var date: Date         = Date(),
    var userID: String     = "", //Foreign key
    var eventID: String    = "", //Foreign key
){
    @get:Exclude
    var user: User   = User()
    @get:Exclude
    var event: Event = Event()
}

data class User(
    @DocumentId
    var id: String          = "",
    var email: String       = "",
    var name: String        = "",
    var password: String    = "",
    var role: String        = "",
    var status: String      = "",
    var count: Int          = 0,
    var photo: Blob?        = Blob.fromBytes(ByteArray(0)),
    var otp: Int?           = 0,
    var activateCode: Int?  = 0,
    var registerDate: Date  = Date(),
)

fun RESTORE_DATA(ctx: Context) {

    ORPHANAGE.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> ORPHANAGE.document(doc.id).delete() }

        val orphanages = listOf(
            Orphanage("OR0001", "Chew2 Orphanage", "9, Jalan Melati Utama 2, Melati Utama, 53100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.2219526971649777, 101.7286130413413, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_orphanage1).toBlob()),
            Orphanage("OR0002", "ABC Orphanage", "134, Jalan Development, Taman Kepong, 52100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.2219526971649777, 101.7286130413413, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_orphanage2).toBlob()),
        )

        for (o in orphanages)
            ORPHANAGE.document(o.id).set(o)
    }

    EVENT.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> EVENT.document(doc.id).delete() }

        val events = listOf(

            Event("EV0001", "Event 1", "Cleaning", "15-06-2021", "09:30", 50, 2, "Cleaning is the best thing to do", "OR0001"),
            Event("EV0002", "Event 2", "Playing", "30-06-2021", "12:30", 30, 2, "Playing is the best thing to do", "OR0001"),
            Event("EV0003", "Event 3", "Others", "30-07-2021", "10:30", 10, 0, "Let's go sleep", "OR0002"),
            Event("EV0004", "Event 4", "Cleaning", "30-08-2021", "07:30", 10, 0, "Nice Event I like it", "OR0002"),
            Event("EV0005", "Event 5", "Exercise", "14-09-2021", "17:30", 30, 1, "Exercising make us healthy", "OR0001"),
            Event("EV0006", "Event 6", "Playing",  "16-09-2021", "11:00",30, 1, "Let's play together", "OR0001"),
            Event("EV0007", "Event 7", "Exercise", "30-11-2021", "08:30", 10, 1, "Let's go exercise", "OR0002"),
            Event("EV0008", "Event 8", "Interaction", "30-12-2021", "12:00", 15, 1, "Talking to your heart", "OR0002"),
            Event("EV0009", "Event 9", "Interaction", "30-11-2021", "11:00", 15, 0, "Share you opinion", "OR0001"),
            Event("EV0010", "Event 10", "Others", "30-11-2021", "07:00", 15, 0, "Let's work together", "OR0001"),
            Event("EV0011", "Event 11", "Playing", "29-11-2021", "07:00", 15, 0, "Board game is the best thing to play", "OR0002"),
            Event("EV0012", "Event 12", "Others", "01-01-2022", "08:00", 15, 0, "New Year celebration", "OR0002"),
            Event("EV0013", "Event 13", "Cleaning", "02-01-2022", "07:00", 2, 2, "Gotong-royong bersama-sama", "OR0002"),
        )

        for (e in events)
            EVENT.document(e.id).set(e)
    }

    PHOTO.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> PHOTO.document(doc.id).delete() }

        val photos = listOf(
            Photo("PH0001", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_cleaning1).toBlob(), Date(), "EV0001"),
            Photo("PH0002", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_cleaning2).toBlob(), Date(), "EV0001"),
            Photo("PH0003", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_cleaning3).toBlob(), Date(), "EV0001"),
            Photo("PH0004", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_playing1).toBlob(), Date(), "EV0002"),
            Photo("PH0005", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_playing2).toBlob(), Date(), "EV0002"),
            Photo("PH0006", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_playing3).toBlob(), Date(), "EV0002"),
            Photo("PH0007", BitmapFactory.decodeResource(ctx.resources, R.drawable.init_other1).toBlob(), Date(), "EV0005"),
        )

        for (p in photos)
            PHOTO.document(p.id).set(p)
    }

    USER.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> USER.document(doc.id).delete() }

        val users = listOf(
            User("chengzhiying", "chengzy-am18@student.tarc.edu.my", "Cheng Zhi Ying", "Abcd123!", "Manager", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_staff1).toBlob(), null, null, Date()),
            User("chewhongyu", "hyhyhy@gmail.com", "Chew Hong Yu", "Abcd123!", "Manager", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_staff2).toBlob(), null, null, Date()),
            User("teohshuzi", "teohs-am18@student.tarc.edu.my", "Teoh Shuzi", "Abcd123!", "Manager", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_staff3).toBlob(), null, null, Date()),
            User("employeeno1", "employee1@gmail.com", "Employee One", "Abcd123!", "Employee", "Unactivated", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_staff4).toBlob(), null, null, Date()),
            User("employeeno2", "employee2@gmail.com", "Employee Two", "Abcd123!", "Employee", "Deactivated", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_staff5).toBlob(), null, null, Date()),
            User("volunteer1", "shuzi1717@gmail.com", "Volunteer One", "Abcd123!", "Volunteer", "Unactivated", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user1).toBlob(), null, null, Date()),
            User("volunteer2", "volunteerno2@gmail.com", "Volunteer Two", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user2).toBlob(), null, null, Date()),
            User("volunteer3", "volunteerno3@gmail.com", "Volunteer Three", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user3).toBlob(), null, null, Date()),
            User("volunteer4", "volunteerno4@gmail.com", "Volunteer Four", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user4).toBlob(), null, null, Date()),
            User("volunteer5", "volunteerno5@gmail.com", "Volunteer Five", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user5).toBlob(), null, null, Date()),
            User("volunteer6", "geraniumcheng@gmail.com", "Volunteer Six", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user6).toBlob(), null, null, Date()),
            User("volunteer7", "volunteerno7@gmail.com", "Volunteer Seven", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user7).toBlob(), null, null, Date()),
            User("volunteer8", "volunteerno8@gmail.com", "Volunteer Eight", "Abcd123!", "Volunteer", "Active", 3, BitmapFactory.decodeResource(ctx.resources, R.drawable.init_user8).toBlob(), null, null, Date()),
        )

        for (u in users)
            USER.document(u.id).set(u)
    }

    VOLUNTEERAPPLICATION.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> VOLUNTEERAPPLICATION.document(doc.id).delete() }

        val volunteerApplications = listOf(
            VolunteerApplication("VA0001", null, null, "Approved", "", Date(), "volunteer6"),
            VolunteerApplication("VA0002", null, null, "Pending", "",  Date(), "volunteer5"),
            VolunteerApplication("VA0003", null, null, "Rejected", "IC Photo not clear", Date(), "volunteer4"),
            VolunteerApplication("VA0004", null, null, "Approved", "", Date(), "volunteer7"),
            VolunteerApplication("VA0005", null, null, "Approved", "", Date(), "volunteer8"),
        )

        for (va in volunteerApplications)
            VOLUNTEERAPPLICATION.document(va.id).set(va)
    }

    VOLUNTEER.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> VOLUNTEER.document(doc.id).delete() }

        val volunteers = listOf(
            Volunteer("Mig57S0VvyLQPXfPLeB", false,"volunteer6","EV0001"), //Missed
            Volunteer("MikgeR-HNTMZPoIhwBI", false,"volunteer7","EV0001"), //Missed
            Volunteer("Mig-xXyuBwMzQ5vTGNn", false,"volunteer6","EV0002"), //Missed
            Volunteer("Mig4f01TKx1asdVJLkY", false,"volunteer8","EV0002"), //Missed
            Volunteer("Mikg0_7Hsi8lcutLy6X", true, "volunteer6","EV0005"), //Participated
            Volunteer("MikgHchHIXPfaSzZ01q", false,"volunteer6","EV0006"), //Current
            Volunteer("MilGgF12ZTb-eM9UkHT", false,"volunteer6","EV0007"), //Current
            Volunteer("MilHzPtNNz003TuOcOt", false,"volunteer6","EV0008"), //Current
            Volunteer("MilHzPtNNz003TuOcOt", false,"volunteer7","EV0013"), //Current
            Volunteer("MilHzPtNNz003TuOcOt", false,"volunteer8","EV0013"), //Current
        )

        for (v in volunteers)
            VOLUNTEER.document(v.id).set(v)
    }

    MESSAGE.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> MESSAGE.document(doc.id).delete() }

        val messages = listOf(
            Message("MillXwiG0FkSjE1df9s", "Hihi", Date(), "volunteer6", "EV0001"),
            Message("MilnDKe1W-iS_ts-1Tm", "Hello hello", Date(), "volunteer7", "EV0001"),
            Message("MilottWW80MMrgOS2jw", "Welcome here", Date(), "volunteer6", "EV0001"),
            Message("Milqe86UIsQuIHhQpMH", "Nice to meet you", Date(), "volunteer7", "EV0001"),

            Message("MinY8UoVFPdzev8A-lp", "Welcome join our event", Date(), "chengzhiying", "EV0002"),
            Message("MinaStOnKWdjWBmkGr2", "Nice to meet you", Date(), "volunteer6", "EV0002"),
            Message("Mina_DwBLpM-w-yW-By", "Hi", Date(), "volunteer8", "EV0002"),
            Message("MinhF80z_k0x_mFr9Ph", "Hello all", Date(), "teohshuzi", "EV0002"),
        )

        for (m in messages)
            MESSAGE.document(m.id).set(m)
    }
}