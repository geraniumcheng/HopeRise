package my.com.hoperise.data

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SharedViewModel: ViewModel() {
    var location = Location()

    fun insertLocation(address: String, latlng: LatLng){
        location = Location(address, latlng.latitude, latlng.longitude)
    }

    fun getSetLocation(): Location{
        return location
    }
}

