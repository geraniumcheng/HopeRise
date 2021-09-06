package my.com.hoperise.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import my.com.hoperise.R
import my.com.hoperise.data.MapViewModel
import my.com.hoperise.databinding.FragmentMapsBinding
import java.util.*

class MapsFragment : Fragment() {

    private var currentMarker: Marker? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private var defaultLocation: Location? = null
    private var latestLatLng: LatLng = LatLng(0.0,0.0)

    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val nav by lazy { findNavController() }

    private val vmMap: MapViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.btnSearch.setOnClickListener { searchLocation() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }
        binding.btnCancel.setOnClickListener { nav.navigateUp() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fetchLocation()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1000 -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL


        val latlong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        latestLatLng = latlong
        drawMarker(latlong)

        map.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker?) {

            }

            override fun onMarkerDragEnd(p0: Marker?) {
                    val newLatLng = LatLng(p0?.position!!.latitude, p0.position.longitude)
                    latestLatLng = newLatLng
                    drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker?) {

            }
        })
    }

    private fun fetchLocation() {
        if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }
        val task = fusedLocationProviderClient?.lastLocation
        task?.addOnSuccessListener {
                location -> if(location != null){
            this.currentLocation = location
            this.defaultLocation = location
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
        }
    }

    private fun drawMarker(latlong: LatLng){
        if(currentMarker != null){
            currentMarker?.remove()
        }
//        if(map == null){
//            map = googleMap
//        }

        val markerOption = MarkerOptions().position(latlong).title("Your orphanage location")
            .snippet(getAddress(latlong.latitude, latlong.longitude)).draggable(true)

        map.animateCamera(CameraUpdateFactory.newLatLng(latlong))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
        currentMarker = map.addMarker(markerOption)
        currentMarker?.showInfoWindow()
    }

    private fun getAddress(lat: Double, lon: Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat, lon, 1)

//        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        val city = addresses[0].locality
//        val state = addresses[0].adminArea
//        val country = addresses[0].countryName
//        val postalCode = addresses[0].postalCode

        return addresses[0].getAddressLine(0)
    }

    private fun searchLocation(){
        val location = binding.edtLocation.text.toString()
        var addressList: List<Address>? = null

        if(location == ""){
            Toast.makeText(context, "Please provide a location", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(context)
            addressList = geoCoder.getFromLocationName(location, 1)
            //Log.d(addressList.toString(), addressList.toString())

            if(addressList.isEmpty()){
                Toast.makeText(context, "Address not found. Please type a valid address", Toast.LENGTH_SHORT).show()
            }else{
                val address = addressList!![0]
                val newLatLong = LatLng(address.latitude,address.longitude)
                latestLatLng = newLatLong
                drawMarker(newLatLong)
                Toast.makeText(context, address.getAddressLine(0) + "\n " + address.latitude.toString() + " , " + address.longitude, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun reset() {
        val defLatLong = LatLng(defaultLocation?.latitude!!, defaultLocation?.longitude!!)
        latestLatLng = defLatLong
        drawMarker(defLatLong)
    }

    private fun submit() {
        //STORE VALUE
        vmMap.insert(getAddress(latestLatLng.latitude, latestLatLng.longitude)!!, latestLatLng)
        Toast.makeText(context, "Address stored successfully", Toast.LENGTH_LONG).show()
        nav.navigateUp()
    }

}