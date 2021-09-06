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
import my.com.hoperise.data.Orphanage
import my.com.hoperise.data.OrphanageViewModel
import my.com.hoperise.databinding.FragmentEditOrphanageBinding
import my.com.hoperise.util.toBitmap

class EditOrphanageFragment : Fragment() {

    private lateinit var binding: FragmentEditOrphanageBinding
    private val nav by lazy { findNavController() }
    private val vm: OrphanageViewModel by activityViewModels()

    private val id by lazy { requireArguments().getString("id") ?: "" }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEditOrphanageBinding.inflate(inflater, container, false)

        reset()
        binding.orpPhoto.setOnClickListener { select() }

        return binding.root
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.orpPhoto.setImageURI(it.data?.data)
        }
    }

    private fun select() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun reset(){
        val o = vm.get(id)
        if(o == null){
            nav.navigateUp()
            return
        }
        load(o)
    }
    private fun load(o: Orphanage) {
        binding.edtName.setText(o.name)
        binding.lblLocation.text = o.location
        binding.orpPhoto.setImageBitmap(o.photo?.toBitmap())


        binding.edtName.requestFocus()
    }

    private fun submit(){

    }

}