package my.com.hoperise.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentEventNearMeBinding
import my.com.hoperise.data.*
import my.com.hoperise.util.EventNearMeAdapter
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Typeface

class EventNearMeFragment : Fragment() {

    private lateinit var binding: FragmentEventNearMeBinding
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val vm: EventNearMeViewModel by activityViewModels()

    private val nav by lazy { findNavController() }
    private var filterEvent = ArrayList<Event>()

    private var currentLocation: Location? = null
    private var area = ""
    private var address : Address? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEventNearMeBinding.inflate(inflater, container, false)
        requireActivity().title = getString(R.string.eventNearYou)
        filterEvent.clear()
        filterEvent = ArrayList<Event>()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        doCallback(10)

        binding.txt5.setOnClickListener { doCallback(5) }
        binding.txt10.setOnClickListener { doCallback(10) }
        binding.txt15.setOnClickListener { doCallback(15) }
        binding.btnSearchArea.setOnClickListener {
             area = binding.edtArea.text.toString()
            if(area == ""){
                Toast.makeText(context, "Field is empty!", Toast.LENGTH_SHORT).show()
            }else{
                var addressList: List<Address>? = null
                val geoCoder = Geocoder(context)
                addressList = geoCoder.getFromLocationName(area, 1)

                if(addressList.isEmpty()){
                    Toast.makeText(context, "Area not found! Sorry.", Toast.LENGTH_SHORT).show()
                }else{
                    address = addressList!![0]
                }
            }
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
            when(requestCode){
                1000 -> fetchLocation(callback)
                else -> return
            }
            return
        }

        val task = fusedLocationProviderClient?.lastLocation
            task?.addOnSuccessListener {
                location ->
                if(location != null) {
                loc = location
                currentLocation = location
                }
                callback.invoke(loc)
            }
    }

    private fun doCallback(distance: Int){
        requireActivity().title = "Event Near You (within $distance km)"
        fetchLocation {
                result -> currentLocation = result

            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            var addresses: MutableList<Address>? = null
            var userLocation: GeoLocation? = null
            val radiusMetre = (distance * 1000).toDouble()

            filterEvent.clear()
            setBold(distance)

            if(address != null){
                addresses = geoCoder.getFromLocation(address!!.latitude, address!!.longitude, 1)
                binding.lblAddress.text = addresses[0].getAddressLine(0)
                userLocation = GeoLocation(address!!.latitude, address!!.longitude)

            }else{
                addresses = geoCoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1)
                binding.lblAddress.text = addresses[0].getAddressLine(0)
                userLocation = GeoLocation(currentLocation!!.latitude, currentLocation!!.longitude)

            }

            vm.getAll().observe(viewLifecycleOwner) {
                    event ->
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
            }

        }
    }

    private fun setBold(distance: Int) {
        when (distance) {
            5 -> {
                binding.txt5.setTypeface(null, Typeface.BOLD)
                binding.txt10.setTypeface(null, Typeface.NORMAL)
                binding.txt15.setTypeface(null, Typeface.NORMAL)
            }
            10 -> {
                binding.txt5.setTypeface(null, Typeface.NORMAL)
                binding.txt10.setTypeface(null, Typeface.BOLD)
                binding.txt15.setTypeface(null, Typeface.NORMAL)
            }
            15 -> {
                binding.txt5.setTypeface(null, Typeface.NORMAL)
                binding.txt10.setTypeface(null, Typeface.NORMAL)
                binding.txt15.setTypeface(null, Typeface.BOLD)
            }
            else -> {
                binding.txt5.setTypeface(null, Typeface.NORMAL)
                binding.txt10.setTypeface(null, Typeface.NORMAL)
                binding.txt15.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}
