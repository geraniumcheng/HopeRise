package my.com.hoperise.data

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.*

class SharedViewModel: ViewModel() {
//    var location = MutableLiveData<Location>()
        var location = Location()
        var photo = TempPhoto()
        //var size = Size()

    fun select(item: Location){
        //location.value = item
    }


    fun insertLocation(address: String, latlng: LatLng){
        location = Location(address, latlng.latitude, latlng.longitude)
    }

    fun getSetLocation(): Location{
        return location
    }

    fun insertTempPhoto(data: Uri?) {
        photo = TempPhoto(data)
    }

    fun getTempPhoto(): Uri?{
        return photo.photo
    }



//    fun getSize(field: String): Int{
//        return when(field){
//            "Orphanage" -> size.orphanage
//            "Event" -> size.event
//            else -> 0
//        }
//    }
//
//    fun updateSize(field: String){
//        when(field){
//            "Orphanage" -> size.orphanage.plus(1)
//            "Event" -> size.event.plus(1)
//            else -> 0
//        }
//    }




}

