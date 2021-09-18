package my.com.hoperise.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.hoperise.util.getEventEndTime
import my.com.hoperise.util.parseEventDateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventNearMeViewModel: ViewModel() {

    private val nearEvent = MutableLiveData<List<Event>>()
    private var event = listOf<Event>()
    private var currentEvent = listOf<Event>()

    private var filterEvent = ArrayList<Event>()

    init{
        viewModelScope.launch {
            val orphanage = ORPHANAGE.get().await().toObjects<Orphanage>()

            EVENT.addSnapshotListener { snap, _ -> if( snap == null) return@addSnapshotListener
                event = snap.toObjects()
                for(e in event){
                    e.orphanage = orphanage.find { o -> o.id == e.orphanageID }!!
                    e.status = checkStatus(e)
                }
                //call nearby event
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
        var list = event

        //list = list.filter {e -> e.status == "Current"}

        nearEvent.value = list
    }
    //TODO (RUN ORPHANAGE launch block at here to get all the orphanage's lat,long. Then loop every single record of orphanage, if address within 10km, save into nearbyEvent array list)
    //TODO CONT (before saving into array list, need to check whether the event status is completed or current. If current, only save)
    //TODO TO CONSIDER (open a EventNearMeViewModel and EventNearMeAdapter)
    //TODO CONT2 (once getting the user current location, pass to vm to do validation, and store those value into mutablelivedata)
//        for(a in orp){
//            a.geohash = GeoFireUtils.getGeoHashForLocation(GeoLocation(a.latitude, a.longitude))
//        }
    //TODO this code put inside Eventnearme VM
    fun getNearbyEvent(lat: Double, long: Double): Int{
        val userLocation = GeoLocation(lat, long)
        val radiusMetre = (10 * 1000).toDouble() //10km

        var list = event
        list = list.filter {e -> e.status == "Current"}

        for(e in list){
            val orpLocation = GeoLocation(e.orphanage.latitude,e.orphanage.longitude )
            val distanceMetre = GeoFireUtils.getDistanceBetween(orpLocation, userLocation)
            if (distanceMetre <= radiusMetre) {
                filterEvent.add(e)
            }
        }
        event = filterEvent

        updateResult()
        return filterEvent.size
    }

    fun getAll() = nearEvent
}