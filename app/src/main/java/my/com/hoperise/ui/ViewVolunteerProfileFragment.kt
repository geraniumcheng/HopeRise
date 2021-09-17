package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.LoginViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentViewVolunteerProfileBinding
import my.com.hoperise.util.toBitmap

class ViewVolunteerProfileFragment : Fragment() {
    private lateinit var binding: FragmentViewVolunteerProfileBinding
    private val nav by lazy { findNavController() }
    private val loginVm: LoginViewModel by activityViewModels()
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentViewVolunteerProfileBinding.inflate(inflater, container, false)
        requireActivity().title = "Volunteer Profile"

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

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getActivity()?.finish()
            }
        })

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
        binding.lblVolunteerName.setText(emp.name)
        binding.lblVolunteerEmail.setText(emp.email)
    }

    private fun logout(){
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure want to log out?" )
            .setIcon(R.drawable.ic_leave_confirm_dialog)
            .setPositiveButton("Logout", object :
                DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                    loginVm.logout(requireContext())
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            })
            .setNegativeButton("Stay", null).show()
    }
    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}