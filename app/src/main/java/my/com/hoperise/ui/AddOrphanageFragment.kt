package my.com.hoperise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import my.com.hoperise.R
import my.com.hoperise.data.SharedViewModel
import my.com.hoperise.data.Orphanage
import my.com.hoperise.data.OrphanageViewModel
import my.com.hoperise.databinding.FragmentAddOrphanageBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import kotlin.coroutines.CoroutineContext

class AddOrphanageFragment : Fragment() {

    private lateinit var binding: FragmentAddOrphanageBinding
    private val nav by lazy { findNavController() }
    private val vmShared: SharedViewModel by activityViewModels()
    private val vmOrphanage: OrphanageViewModel by activityViewModels()


    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.orpPhoto.setImageURI(it.data?.data)
            vmShared.insertTempPhoto(it.data?.data)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddOrphanageBinding.inflate(inflater, container, false)

        vmShared.getSetLocation()
        if(vmShared.location.location != ""){
            binding.edtLocation.text = vmShared.location.location
        }
        if(vmShared.photo.photo != null){
            binding.orpPhoto.setImageURI(vmShared.getTempPhoto())
        }

        binding.orpPhoto.setOnClickListener { select() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }
        binding.btnLocation.setOnClickListener {
            val args = bundleOf("location" to "")
            nav.navigate(R.id.mapsFragment, args)
        }
        return binding.root
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset() {
        binding.edtName.text.clear()
        binding.edtName.requestFocus()
        binding.orpPhoto.setImageDrawable(null)
        binding.edtLocation.text  = ""
        vmShared.insertTempPhoto(null)
        vmShared.insertLocation("",  LatLng(0.0,0.0))
    }

    private fun submit() {
        val o = Orphanage(
            id = vmOrphanage.generateID(),
            name = binding.edtName.text.toString().trim(),
            location = vmShared.location.location,
            latitude = vmShared.location.latitude,
            longitude = vmShared.location.longitude,
            photo = binding.orpPhoto.cropToBlob(300, 300),
        )
        val err = vmOrphanage.validate(o)
        if( err != ""){
            errorDialog(err)
            return
        }
        vmOrphanage.set(o)
        reset()
        nav.navigateUp()
    }

}