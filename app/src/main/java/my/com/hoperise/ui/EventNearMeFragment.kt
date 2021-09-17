package my.com.hoperise.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentEventNearMeBinding
import my.com.hoperise.databinding.FragmentOrphanageDetailsBinding
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.tasks.OnSuccessListener
import my.com.hoperise.data.*
import my.com.hoperise.util.EventNearMeAdapter
import my.com.hoperise.util.OrphanageAdapter
import java.util.*
import kotlin.collections.ArrayList


class EventNearMeFragment : Fragment() {

    private lateinit var binding: FragmentEventNearMeBinding
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val vm: EventNearMeViewModel by activityViewModels()

    private val nav by lazy { findNavController() }
    private var nearbyEvent = ArrayList<Event>()
    private var event = listOf<Event>()
    private var filterEvent = ArrayList<Event>()

    private var currentLocation: Location? = null
    private var area = ""
    private var address : Address? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEventNearMeBinding.inflate(inflater, container, false)
        requireActivity().title = "Event Near You (within 10km)"
        filterEvent.clear()
        filterEvent = ArrayList<Event>()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        doCallback(10)

        binding.txt5.setOnClickListener { doCallback(5) }
        binding.txt10.setOnClickListener { doCallback(10) }
        binding.txt15.setOnClickListener { doCallback(15) }
        binding.btnSearchArea.setOnClickListener {
             area = binding.edtArea.text.toString()
            var addressList: List<Address>? = null
            val geoCoder = Geocoder(context)
            addressList = geoCoder.getFromLocationName(area, 1)
            address = addressList!![0]

            doCallback(10)
        }

        return binding.root
    }

     private fun fetchLocation( callback: (Location?) -> Unit) {
         var loc: Location? = null

         if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            val requestCode = 1000
            ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
//            when(requestCode){
//                1000 -> fetchLocation()
//                else -> return
//            }
//            return
        }

        val task = fusedLocationProviderClient?.lastLocation
            task?.addOnSuccessListener {
                location ->
                if(location != null) {
                //userLocation = location
                //this.currentLocation = location
                loc = location
                currentLocation = location
                //Log.d("current location value in listener", currentLocation!!.latitude.toString() + " " + currentLocation!!.longitude.toString())
                }
                callback.invoke(loc)
            }
    }


    private fun doCallback(distance: Int){
        requireActivity().title = "Event Near You (within $distance km)"
        fetchLocation {
                result -> currentLocation = result

            if(address != null){
                //Log.d("current location value inside callback", currentLocation!!.latitude.toString() + " " + currentLocation!!.longitude.toString())
                filterEvent.clear()
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geoCoder.getFromLocation(address!!.latitude, address!!.longitude, 1)
                //Toast.makeText(context, addresses[0].getAddressLine(0), Toast.LENGTH_SHORT).show()
                binding.lblAddress.text = addresses[0].getAddressLine(0)

                vm.getAll().observe(viewLifecycleOwner) {
                        event -> //adapter.submitList(event)
                    //this.event = event

                    val userLocation = GeoLocation(address!!.latitude, address!!.longitude)
                    val radiusMetre = (distance * 1000).toDouble()
                    var list = event

                    list = list.filter {e -> e.status == "Current"}

                    for(e in list){
                        val orpLocation = GeoLocation(e.orphanage.latitude,e.orphanage.longitude )
                        val distanceMetre = GeoFireUtils.getDistanceBetween(orpLocation, userLocation)
                        if (distanceMetre <= radiusMetre) {
                            e.orphanage.distance = distanceMetre / 1000.0
                            filterEvent.add(e)
                        }
                    }
                    binding.lblNearMeRecords.text = filterEvent.size.toString() + " record(s)"

                    val adapter = EventNearMeAdapter(){
                            holder, event -> holder.root.setOnClickListener {
                        val args = bundleOf(
                            "id" to event.id,
                            "isEvent" to false
                        )
                        nav.navigate(R.id.eventDetailsFragment, args)
                    }
                    }
                    binding.rvEvNearMe.adapter = adapter
                    binding.rvEvNearMe.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                    binding.rvEvNearMe.removeItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

                    adapter.submitList(filterEvent)
                    // binding.lblNearMeRecords.text = vm.getNearbyEvent(currentLocation!!.latitude, currentLocation!!.longitude).toString()
                }
            }else{
                //Log.d("current location value inside callback", currentLocation!!.latitude.toString() + " " + currentLocation!!.longitude.toString())
                filterEvent.clear()
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geoCoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1)
                //Toast.makeText(context, addresses[0].getAddressLine(0), Toast.LENGTH_SHORT).show()
                binding.lblAddress.text = addresses[0].getAddressLine(0)

                vm.getAll().observe(viewLifecycleOwner) {
                        event -> //adapter.submitList(event)
                    //this.event = event

                    val userLocation = GeoLocation(currentLocation!!.latitude, currentLocation!!.longitude)
                    val radiusMetre = (distance * 1000).toDouble()
                    var list = event

                    list = list.filter {e -> e.status == "Current"}

                    for(e in list){
                        val orpLocation = GeoLocation(e.orphanage.latitude,e.orphanage.longitude )
                        val distanceMetre = GeoFireUtils.getDistanceBetween(orpLocation, userLocation)
                        if (distanceMetre <= radiusMetre) {
                            e.orphanage.distance = distanceMetre / 1000.0
                            filterEvent.add(e)
                        }
                    }
                    binding.lblNearMeRecords.text = filterEvent.size.toString() + " record(s)"

                    val adapter = EventNearMeAdapter(){
                            holder, event -> holder.root.setOnClickListener {
                        val args = bundleOf(
                            "id" to event.id,
                            "isEvent" to false
                        )
                        nav.navigate(R.id.eventDetailsFragment, args)
                    }
                    }
                    binding.rvEvNearMe.adapter = adapter
                    binding.rvEvNearMe.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

                    adapter.submitList(filterEvent)
                    // binding.lblNearMeRecords.text = vm.getNearbyEvent(currentLocation!!.latitude, currentLocation!!.longitude).toString()
                }
            }


        }
    }



}
