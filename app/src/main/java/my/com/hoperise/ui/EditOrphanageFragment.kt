package my.com.hoperise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import my.com.hoperise.R
import my.com.hoperise.data.Orphanage
import my.com.hoperise.data.OrphanageViewModel
import my.com.hoperise.data.SharedViewModel
import my.com.hoperise.databinding.FragmentEditOrphanageBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.toBitmap

class EditOrphanageFragment : Fragment() {

    private lateinit var binding: FragmentEditOrphanageBinding
    private val nav by lazy { findNavController() }
    private val vmOrphanage: OrphanageViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id", "") }
    private val vmShared: SharedViewModel by activityViewModels()

    var latitude = 0.0
    var longitude = 0.0
    var loc = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEditOrphanageBinding.inflate(inflater, container, false)

        reset()
        vmShared.getSetLocation()
        if(vmShared.location.location != ""){
            binding.lblOrpLocation.text = vmShared.location.location
        }
        binding.orpPhotoListing.setOnClickListener { select() }
        binding.btnMap.setOnClickListener {
            val args = bundleOf(
                "location" to vmOrphanage.get(id)!!.location
            )
            nav.navigate(R.id.mapsFragment, args)
        }
        binding.btnReset.setOnClickListener {
            vmShared.insertTempPhoto(null)
            vmShared.insertLocation("",  LatLng(0.0,0.0))
            reset()
        }
        binding.btnConfirm.setOnClickListener { submit() }
        return binding.root
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.orpPhotoListing.setImageURI(it.data?.data)
        }
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset(){
        val o = vmOrphanage.get(id)
        if(o == null){
            nav.navigateUp()
            return
        }
        load(o)

    }

    private fun load(o: Orphanage) {
        binding.orpIDListing.text = o.id
        binding.edtOrpName.setText(o.name)
        binding.lblOrpLocation.text = o.location
        binding.orpPhotoListing.setImageBitmap(o.photo?.toBitmap())
        binding.edtOrpName.requestFocus()
        latitude = o.latitude
        longitude = o.longitude
        loc = o.location
    }

    private fun submit(){
        var lat = 0.0
        var long = 0.0
        var newLoc = ""
        if(vmShared.location.location != ""){
            lat = vmShared.location.latitude
            long = vmShared.location.longitude
            newLoc = vmShared.location.location
        }else{
            lat = latitude
            long = longitude
            newLoc = loc
        }
        val o = Orphanage(
            id = id,
            name = binding.edtOrpName.text.toString().trim(),
            location = newLoc,
            latitude = lat,
            longitude = long,
            photo = binding.orpPhotoListing.cropToBlob(300, 300),
        )
        val err = vmOrphanage.validate(o)
        if( err != ""){
            errorDialog(err)
            return
        }
        vmOrphanage.set(o)
        vmShared.insertTempPhoto(null)
        vmShared.insertLocation("",  LatLng(0.0,0.0))
        Toast.makeText(context, "Orphanage updated successfully", Toast.LENGTH_SHORT).show()


        nav.navigateUp()
    }

}