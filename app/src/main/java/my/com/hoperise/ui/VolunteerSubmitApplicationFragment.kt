package my.com.hoperise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentAccountBinding
import my.com.hoperise.databinding.FragmentVolunteerSubmitApplicationBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import java.util.*

class VolunteerSubmitApplicationFragment : Fragment() {
    private lateinit var binding: FragmentVolunteerSubmitApplicationBinding
    private val nav by lazy { findNavController() }
    private val vm: VolunteerApplicationViewModel by activityViewModels()
    private val status by lazy { requireArguments().getString("status") ?: "" }
    private val id by lazy { requireArguments().getString("id") ?: "" }


    private val launcherSelfie = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgSelfie.setImageURI(it.data?.data)
        }
    }
    private val launcherFrontIC = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgFrontIC.setImageURI(it.data?.data)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVolunteerSubmitApplicationBinding.inflate(inflater, container, false)

        binding.imgSelfie.setOnClickListener { select("Selfie") }
        binding.imgFrontIC.setOnClickListener { select("IC") }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener {
            lifecycleScope.launch{
                submit()
            }
        }

        return binding.root
    }

    private suspend fun submit() {
        val newID = vm.generateID()
        val va = VolunteerApplication(
            id = newID,
            ICFrontPhoto = binding.imgFrontIC.cropToBlob(300, 300),
            ICSelfiePhoto = binding.imgSelfie.cropToBlob(300, 300),
            status = "Pending",
            reason = "",
            date = Date(),
            userID = "volunteer4"//currentUser.id
        )

        val err = vm.validate(va)
        if( err != ""){
            errorDialog(err)
            return
        }
        vm.set(va)
        reset()
        if(status == "Rejected" && id != ""){
            VOLUNTEERAPPLICATION.document(id).delete()
        }
        Toast.makeText(context, "Volunteer application submitted successfully", Toast.LENGTH_SHORT).show()
        returnFragment = true
        returnVAID = newID
        nav.navigateUp()
    }

    private fun reset() {
        binding.imgSelfie.setImageDrawable(null)
        binding.imgFrontIC.setImageDrawable(null)
    }

    private fun select(item: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if(item == "Selfie"){
            launcherSelfie.launch(intent)
        }else{
            launcherFrontIC.launch(intent)
        }
    }
}