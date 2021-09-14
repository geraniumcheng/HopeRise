package my.com.hoperise.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.data.LoginViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentViewVolunteerProfileBinding
import my.com.hoperise.util.toBitmap
import my.com.hoperise.util.toBlob

class ViewVolunteerProfileFragment : Fragment() {
    private lateinit var binding: FragmentViewVolunteerProfileBinding
    private val nav by lazy { findNavController() }
    private val loginVm: LoginViewModel by activityViewModels()
    private val vm: EmployeeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentViewVolunteerProfileBinding.inflate(inflater, container, false)


        loadProfileData()
        binding.btnEditProfile.setOnClickListener {
            nav.navigate(R.id.editVolunteerProfileFragment)
        }
        binding.btnChangePassword.setOnClickListener{
            nav.navigate(R.id.volunteerChangePasswordFragment)
        }
        binding.btnLogOut.setOnClickListener{
            logout()
        }

        return binding.root
    }

    private fun loadProfileData() {
        val userId = (activity as MainActivity).loggedInId
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
    }

    private fun logout(){
        // TODO(4): Logout -> auth.logout(...)
        //          Clear navigation backstack
        loginVm.logout(requireContext())
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}