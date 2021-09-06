package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel: ViewModel() {
//    var location = MutableLiveData<Location>()
        var location = Location()

    fun select(item: Location){

        //location.value = item
    }

//    fun insert(loc: String, latitude: Double, longitude: Double){
//        location = Location(loc,latitude,longitude)
//        //location.value(loc, latitude, longitude)
//    }

    fun insert(address: String, latlng: LatLng){
        location = Location(address, latlng.latitude, latlng.longitude)
    }

    fun get(): Location{
        return location
    }
}