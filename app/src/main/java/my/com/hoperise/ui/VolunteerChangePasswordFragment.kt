package my.com.hoperise.ui

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
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentVolunteerChangePasswordBinding
import my.com.hoperise.util.hideKeyboard

class VolunteerChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentVolunteerChangePasswordBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVolunteerChangePasswordBinding.inflate(inflater, container, false)

        requireActivity().title = "Change Password"

        resetErrorMessage()
        binding.btnConfirmChangePassword.setOnClickListener { getCurrentPassword() }
        binding.btnForgetPassword.setOnClickListener { nav.navigate(R.id.volunteerVefiryOtpFragment) }
        return binding.root
    }

    private fun getCurrentPassword() {
        val userId = (activity as MainActivity).loggedInId
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)

            if (emp == null) {
                nav.navigateUp() //if no record then return to previous page
                return@launch
            }
            changePassword(emp)
        }
    }

    private fun changePassword(emp: User) {
        val regexPassword = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[=+@$#!%*?&,._-])[A-Za-z\d=+@$#!%*?&,._-]{8,}$""")
        val currentPass = emp.password
        val enteredCurrentPass = binding.edtCurrentPassword.text.toString()
        val enteredNewPass = binding.edtNewPassword.text.toString()
        val enteredConfirmNewPass = binding.edtConfirmNewPassword.text.toString()

        if (currentPass.equals(enteredCurrentPass)) {
            binding.lblCurrentPasswordWarning.visibility = View.GONE
            if(enteredNewPass.matches(regexPassword)){
                binding.lblNewPasswordWarning.visibility = View.GONE
                if(currentPass.equals(enteredNewPass)){
                    binding.lblNewPasswordWarning.visibility = View.VISIBLE
                    binding.lblNewPasswordWarning.text = "Please set a password that different with current password!"
                    binding.edtNewPassword.requestFocus()
                    return
                }
                if(enteredNewPass.equals(enteredConfirmNewPass)){
                    //toast("You are all set")
                    //resetErrorMessage()
                    hideKeyboard()
                    vm.changePass(emp.id,enteredConfirmNewPass)
                    nav.navigateUp()
                    toast("Password updated successfully!")
                }
                else{
                    binding.lblConfirmNewPasswordWarning.visibility = View.VISIBLE
                    binding.lblConfirmNewPasswordWarning.text = "Please ensure your new password is matched!"
                    binding.edtCurrentPassword.requestFocus()
                }
            }
            else{
                binding.lblNewPasswordWarning.visibility = View.VISIBLE
                binding.lblNewPasswordWarning.text = "Password must be minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                binding.edtNewPassword.requestFocus()
            }
        } else {
            binding.lblCurrentPasswordWarning.visibility = View.VISIBLE
            binding.lblCurrentPasswordWarning.text = "Password mismatched. Please try again"
            binding.edtCurrentPassword.requestFocus()
        }


    }

    private fun resetErrorMessage() {
        binding.edtCurrentPassword.requestFocus()
        binding.lblCurrentPasswordWarning.visibility = View.GONE
        binding.lblNewPasswordWarning.visibility = View.GONE
        binding.lblConfirmNewPasswordWarning.visibility = View.GONE
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}