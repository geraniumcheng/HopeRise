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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.MainActivity
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentEditVolunteerProfileBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.toBitmap

class EditVolunteerProfileFragment : Fragment() {
   private lateinit var binding: FragmentEditVolunteerProfileBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgVolunteerPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditVolunteerProfileBinding.inflate(inflater, container, false)

        requireActivity().title = "Update Volunteer Profile"

        loadProfileData()
        binding.btnReset.setOnClickListener { loadProfileData() }
        binding.btnPickImage.setOnClickListener { pickImage() }
        binding.btnConfirm.setOnClickListener { updateVolunteer() }
        return binding.root
    }
    private fun loadProfileData() {
        val userId = (activity as MainActivity).loggedInId
        //toast(userId)
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)

            if (emp == null) {
                nav.navigateUp() //if no record then return to previous page
                return@launch
            }
            loadProfile(emp)
        }
    }

    private fun loadProfile(emp: User){
        binding.imgVolunteerPhoto.setImageBitmap(emp.photo?.toBitmap())
        binding.lblVolunteerId.setText(emp.id)
        binding.lblVolunteerEmail.setText(emp.email)
        binding.edtVolunteerName.setText(emp.name)
        binding.lblVolunteerStatus.setText(emp.status)

        binding.edtVolunteerName.requestFocus()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun updateVolunteer() {
        val userId = (activity as MainActivity).loggedInId
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)
            val empUpdate = User(
                id = emp!!.id,
                email = emp!!.email,
                name  = binding.edtVolunteerName.text.toString().trim(),
                password = emp!!.password,
                role = emp!!.role,
                status = emp!!.status,
                count = emp!!.count,
                photo = binding.imgVolunteerPhoto.cropToBlob(300, 300),
                otp = emp!!.otp,
                activateCode = emp!!.activateCode,
                registerDate = emp.registerDate
            )
            val err = vm.validate(empUpdate, false)
            if (err != "") {
                errorDialog(err)
                return@launch
            }

            vm.update(empUpdate)
            nav.navigateUp()
        }

        // means its not inserting a new record

    }
}