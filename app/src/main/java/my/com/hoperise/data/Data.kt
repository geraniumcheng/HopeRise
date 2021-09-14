package my.com.hoperise.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
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
val USER = Firebase.firestore.collection("User")
val EMPLOYEES = Firebase.firestore.collection("User").whereIn("role", listOf("Manager","Employee"))
var currentUser: User? = null

data class Location(
    var location: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)

data class TempPhoto(
    var photo: Uri? = null
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
    @get:Exclude
    var count: Int = 0
}

data class Event(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var category: String = "",
    //var date: Date = Timestamp.now().toDate(),
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

data class Volunteer(
    @DocumentId
    var userID: String      = "", //Composite key
    var eventID: String     = "", //Composite key
    var attendance: Boolean = false,
){
    @get:Exclude
    var user: User = User()
    @get:Exclude
    val event: Event = Event()
}

data class Photo(
    @DocumentId
    var id: String         = "",
    var photo: Blob        = Blob.fromBytes(ByteArray(0)),
    var date: Date         = Date(),
    var eventID: String    = "", //Foreign key
){
    @get:Exclude
    val event: Event = Event()
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
    var user: User = User()
    @get:Exclude
    val event: Event = Event()
}

data class User(
    @DocumentId
    var id: String           = "",
    var email: String        = "",
    var name: String         = "",
    var password: String     = "",
    var role: String         = "",
    var status: String       = "",
    var count: Int           = 0,
    var photo: Blob?         = Blob.fromBytes(ByteArray(0)),
    var otp: Int?            = 0,
    var activateCode: Int?   = 0,
    var registerDate: Date   = Date(),
)

fun RESTORE_DATA(ctx: Context) {

    ORPHANAGE.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> ORPHANAGE.document(doc.id).delete() }

        val orphanages = listOf(
            Orphanage("OR0001", "Chew2 Orphanage", "9, Jalan Melati Utama 2, Melati Utama, 53100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.2219526971649777, 101.7286130413413, null),
            Orphanage("OR0002", "ABC Orphanage", "134, Jalan Development, Taman Kepong, 52100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.2219526971649777, 101.7286130413413, null),
        )

        for (o in orphanages)
            ORPHANAGE.document(o.id).set(o)
    }

    EVENT.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> EVENT.document(doc.id).delete() }

        val events = listOf(
            Event("EV0001", "Event 1", "Cleaning", "30/12/2021", "12:30", 50, 3, "Cleaning is the best thing to do", "OR0001"),
            Event("EV0002", "Event 2", "Playing", "30/11/2021", "12:30", 30, 1, "Playing is the best thing to do", "OR0001"),
            Event("EV0003", "Event 3", "Others", "30/10/2021", "12:30", 10, 0, "let's go sleep", "OR0002"),
            Event("EV0004", "Event 4", "Cleaning", "30/09/2021", "12:30", 10, 0, "Nice Event I like it", "OR0002")
        )

        for (e in events)
            EVENT.document(e.id).set(e)
    }

    PHOTO.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> PHOTO.document(doc.id).delete() }

        //val photos = listOf(
            //Photo("PH0001", BitmapFactory.decodeResource(ctx.resources, R.drawable.cleaning1).toBlob(), Date(), "EV0001"),
            //Photo("PH0002", BitmapFactory.decodeResource(ctx.resources, R.drawable.cleaning2).toBlob(), Date(), "EV0001"),
           // Photo("PH0003", BitmapFactory.decodeResource(ctx.resources, R.drawable.cleaning3).toBlob(), Date(), "EV0001"),
           //Photo("PH0004", BitmapFactory.decodeResource(ctx.resources, R.drawable.playing1).toBlob(), Date(), "EV0002"),
           //Photo("PH0005", BitmapFactory.decodeResource(ctx.resources, R.drawable.playing2).toBlob(), Date(), "EV0002"),
           //Photo("PH0006", BitmapFactory.decodeResource(ctx.resources, R.drawable.other1).toBlob(), Date(), "EV0003"),
        //)

        //for (p in photos)
            //PHOTO.document(p.id).set(p)
    }

    USER.get().addOnSuccessListener { snap ->
        snap.documents.forEach { doc -> USER.document(doc.id).delete() }

//        val users = listOf(
//            User("chengzhiying", "czy2k@gmail.com", "Cheng Zhi Ying", "Abcd123!", "Manager", "Active", 3, null, null, "453612", Date()),
//            User("chewhongyu", "hyhyhy@gmail.com", "Chew Hong Yu", "Abcd123!", "Manager", "Active", 3, null, null, "587851", Date()),
//            User("teohshuzi", "shuzizizi@gmail.com", "Teoh Shuzi", "Abcd123!", "Manager", "Active", 3, null, null, "201478", Date()),
//            User("employeeno1", "employee1@gmail.com", "Employee One", "Abcd123!", "Employee", "Unactivated", 3, null, null, "698741", Date()),
//            User("employeeno2", "employee2@gmail.com", "Employee Two", "Abcd123!", "Employee", "Deactivated", 3, null, null, "214784", Date()),
//            User("volunteer1", "volunteerno1@gmail.com", "Volunteer One", "Abcd123!", "Volunteer", "Unactivated", 3, null, null, "998524", Date()),
//            User("volunteer2", "volunteerno2@gmail.com", "Volunteer Two", "Abcd123!", "Volunteer", "Unverified", 3, null, null, "777558", Date()),
//            User("volunteer3", "volunteerno3@gmail.com", "Volunteer Three", "Abcd123!", "Volunteer", "Pending", 3, null, null, "575757", Date()),
//            User("volunteer4", "volunteerno4@gmail.com", "Volunteer Four", "Abcd123!", "Volunteer", "Verified", 3, null, null, "827222", Date()),
//            User("volunteer5", "volunteerno5@gmail.com", "Volunteer Five", "Abcd123!", "Volunteer", "Verified", 3, null, null, "827222", Date()),
//            User("volunteer6", "volunteerno6@gmail.com", "Volunteer Six", "Abcd123!", "Volunteer", "Verified", 3, null, null, "827222", Date()),
//            User("volunteer7", "volunteerno7@gmail.com", "Volunteer Seven", "Abcd123!", "Volunteer", "Verified", 3, null, null, "827222", Date()),
//            User("volunteer8", "volunteerno8@gmail.com", "Volunteer Eight", "Abcd123!", "Volunteer", "Verified", 3, null, null, "827222", Date()),
//        )

        val users = listOf(
            User("chengzhiying", "czy2k@gmail.com", "Cheng Zhi Ying", "Abcd123!", "Manager", "Active", 3, null, null, 553612, Date()),
            User("chewhongyu", "hyhyhy@gmail.com", "Chew Hong Yu", "Abcd123!", "Manager", "Active", 3, null, null, 587851, Date()),
            User("teohshuzi", "shuzizizi@gmail.com", "Teoh Shuzi", "Abcd123!", "Manager", "Active", 3, null, null, 201478, Date()),
            User("employeeno1", "employee1@gmail.com", "Employee One", "Abcd123!", "Employee", "Unactivated", 3, null, null, 698741, Date()),
            User("employeeno2", "employee2@gmail.com", "Employee Two", "Abcd123!", "Employee", "Deactivated", 3, null, null, 214784, Date()),
            User("volunteer1", "volunteerno1@gmail.com", "Volunteer One", "Abcd123!", "Volunteer", "Unactivated", 3, null, null, 998524, Date()),
            User("volunteer2", "volunteerno2@gmail.com", "Volunteer Two", "Abcd123!", "Volunteer", "Unverified", 3, null, null, 777558, Date()),
            User("volunteer3", "volunteerno3@gmail.com", "Volunteer Three", "Abcd123!", "Volunteer", "Pending", 3, null, null, 575757, Date()),
            User("volunteer4", "volunteerno4@gmail.com", "Volunteer Four", "Abcd123!", "Volunteer", "Verified", 3, null, null, 827222, Date()),
            User("volunteer5", "volunteerno5@gmail.com", "Volunteer Five", "Abcd123!", "Volunteer", "Verified", 3, null, null, 827222, Date()),
            User("volunteer6", "volunteerno6@gmail.com", "Volunteer Six", "Abcd123!", "Volunteer", "Verified", 3, null, null, 827222, Date()),
            User("volunteer7", "volunteerno7@gmail.com", "Volunteer Seven", "Abcd123!", "Volunteer", "Verified", 3, null, null, 827222, Date()),
            User("volunteer8", "volunteerno8@gmail.com", "Volunteer Eight", "Abcd123!", "Volunteer", "Verified", 3, null, null, 827222, Date()),
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
            Volunteer("volunteer6", "EV0001"),
            Volunteer("volunteer7", "EV0001"),
            Volunteer("volunteer6", "EV0002"),
            Volunteer("volunteer8", "EV0002"),
        )

        for (v in volunteers)
            VOLUNTEER.document(v.userID).set(v)
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