package my.com.hoperise.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentViewStaffProfileBinding
import my.com.hoperise.util.toBitmap
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.LoginViewModel


class ViewStaffProfileFragment : Fragment() {
   private lateinit var binding: FragmentViewStaffProfileBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()
    private val loginVm: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentViewStaffProfileBinding.inflate(inflater, container, false)

        loadProfileData()
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnChangePassword.setOnClickListener {
            nav.navigate(R.id.employeeChangePasswordFragment)
        }
        return binding.root
    }

    private fun loadProfileData() {

        val userId = (activity as StaffActivity).loggedInId
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
        binding.imgEmployeePhoto.setImageBitmap(emp.photo?.toBitmap())
        binding.lblEmployeeId.setText(emp.id)
        binding.lblEmployeeEmail.setText(emp.email)
        binding.lblEmployeeName.setText(emp.name)
        binding.lblEmployeeRole.setText(emp.role)
        binding.lblEmployeeStatus.setText(emp.status)

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
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}