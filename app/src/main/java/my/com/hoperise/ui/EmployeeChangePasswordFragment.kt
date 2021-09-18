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
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentEmployeeChangePasswordBinding
import my.com.hoperise.util.SendEmail
import my.com.hoperise.util.hideKeyboard
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class EmployeeChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentEmployeeChangePasswordBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEmployeeChangePasswordBinding.inflate(inflater, container, false)

        val userId = (activity as StaffActivity).loggedInId

        resetErrorMessage()
        binding.edtCurrentPass.requestFocus()

        binding.btnConfirmChangePassword.setOnClickListener { getCurrentPassword(userId) }
        binding.btnForgetPassword.setOnClickListener { sendOtpResetPassword(userId) }

        return binding.root
    }

    private fun sendOtpResetPassword(userId: String) {
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)
            var email = emp!!.email

            AlertDialog.Builder(requireContext())
                .setTitle("Forget Password")
                .setMessage("An OTP will sent to your registered email to let you reset a new password. Proceed to OTP verification?\n")
                .setIcon(R.drawable.ic_otp_confirm_dialog)
                .setPositiveButton(android.R.string.yes, object :
                    DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
                        var currentDateandTime: String = sdf.format(Date())
                        val n = (0..999999).random()
                        val fmt = DecimalFormat("000000")
                        val verifyOtpCode = fmt.format(n).toString()
                        sendEmail(email, userId, verifyOtpCode, currentDateandTime)
                        vm.updateOtp(userId, verifyOtpCode.toInt())
                        nav.navigate(R.id.employeeVerifyOtpFragment)
                    }
                }).setNegativeButton(android.R.string.no, null).show()
        }
    }

    private fun sendEmail(email: String, userId: String, verifyOtpCode: String, currentDateandTime: String) {
        val subject = "Reset Hope Rise Account Password Request @ ${currentDateandTime}"
        val content = """
                    <p>Greetings from Hope Rise 😉 </p>
                    <p>Dear <b>$userId</b>,</p>
                    <p>Please entered the <b>OTP</b> code to continue for reset your password:</p>
                    <h1 style="color: orange">$verifyOtpCode</h1>
                    <p>Reminder: If you ignore this email, your password will not be changed. Please secure your account and password.</p>
                    <p>Thank you! 😁</p>
                    <p> </p>
                    <p><i>Hope Rise Team</i></p>
                    <p> </p>
                    <p> </p>
                """.trimIndent()

        SendEmail()
            .to(email)
            .subject(subject)
            .content(content)
            .isHtml()
            .send() {
            }
    }

    private fun getCurrentPassword(userId: String) {
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)

            if (emp == null) {
                nav.navigateUp() // If no record then return to previous page
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
                    hideKeyboard()
                    vm.changePass(emp.id,enteredConfirmNewPass)
                    nav.navigateUp()
                    toast("Password updated successfully!")

                }
                else{
                    binding.lblConfirmNewPasswordWarning.visibility = View.VISIBLE
                    binding.lblConfirmNewPasswordWarning.text = "Please ensure your new password is matched!"
                    binding.edtConfirmNewPass.requestFocus()
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
            binding.edtCurrentPass.requestFocus()
        }
    }

    private fun resetErrorMessage() {
        binding.lblCurrentPasswordWarning.visibility = View.GONE
        binding.lblNewPasswordWarning.visibility = View.GONE
        binding.lblConfirmNewPasswordWarning.visibility = View.GONE
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}