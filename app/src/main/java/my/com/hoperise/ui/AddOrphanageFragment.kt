package my.com.hoperise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.MapViewModel
import my.com.hoperise.data.Orphanage
import my.com.hoperise.data.OrphanageViewModel
import my.com.hoperise.databinding.FragmentAddOrphanageBinding
import my.com.hoperise.util.errorDialog

class AddOrphanageFragment : Fragment() {

    private lateinit var binding: FragmentAddOrphanageBinding
    private val nav by lazy { findNavController() }
    private val vmMap: MapViewModel by activityViewModels()
    private val vmOrphanage: OrphanageViewModel by activityViewModels()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.orpPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddOrphanageBinding.inflate(inflater, container, false)


        vmMap.get()
        if(vmMap.location.location != ""){
            binding.edtLocation.text = vmMap.location.location
        }
        binding.orpPhoto.setOnClickListener { select() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }
        binding.btnLocation.setOnClickListener { nav.navigate(R.id.mapsFragment) }
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
    }

    private fun submit() {
        val o = Orphanage(
            id = "O3",
            name = binding.edtName.text.toString().trim(),
            //location = binding.edtLocation.text.toString().trim(),
            location = vmMap.location.location,
            latitude = vmMap.location.latitude,
            longitude = vmMap.location.longitude,
            photo = binding.orpPhoto.cropToBlob(300, 300),
        )

        val err = vmOrphanage.validate(o)
        if( err != ""){
            errorDialog(err)
            return
        }
        vmOrphanage.set(o)
        nav.navigateUp()

    }

}