package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
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
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentEmployeeForgetPasswordBinding
import my.com.hoperise.util.errorDialog

class EmployeeForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentEmployeeForgetPasswordBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEmployeeForgetPasswordBinding.inflate(inflater, container, false)

        resetErrorMessage()
        val userId = (activity as StaffActivity).loggedInId
        binding.btnResetNow.setOnClickListener { validatePassword(userId) }

        return binding.root
    }

    private fun validatePassword(userId: String) {
        val regexPassword = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[=+@$#!%*?&,._-])[A-Za-z\d=+@$#!%*?&,._-]{8,}$""")
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)
            if (emp == null) {
                toast("Opps! Something wrong!")
                return@launch
            } else {
                val newPass = binding.edtNewPassword.text.toString()
                val confirmNewPass = binding.edtConfirmNewPassword.text.toString()

                if(newPass.equals("") || confirmNewPass.equals("")){
                    errorDialog("Please filled in your new password!")
                }
                else {
                    if (newPass.matches(regexPassword)) {
                        binding.lblNewPasswordWarning.visibility = View.GONE
                        if (newPass.equals(confirmNewPass)) {
                            binding.lblConfirmNewPasswordWarning.visibility = View.GONE
                            vm.updatePassword(emp.id,confirmNewPass)
                            nav.popBackStack(R.id.viewStaffProfileFragment, false)
                            toast("Password reset successfully!")
                        } else {
                            binding.lblConfirmNewPasswordWarning.visibility = View.VISIBLE
                            binding.lblConfirmNewPasswordWarning.text = "Please ensure your new password is matched!"
                        }
                    } else {
                        binding.lblNewPasswordWarning.visibility = View.VISIBLE
                        binding.lblNewPasswordWarning.text = "Password must be minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                    }
                }
            }
        }
    }

    private fun resetErrorMessage(){
        binding.edtNewPassword.requestFocus()
        binding.lblNewPasswordWarning.visibility = View.GONE
        binding.lblConfirmNewPasswordWarning.visibility = View.GONE
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}