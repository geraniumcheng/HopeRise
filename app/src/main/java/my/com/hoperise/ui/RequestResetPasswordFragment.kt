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
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentRequestResetPasswordBinding
import my.com.hoperise.util.SendEmail
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RequestResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentRequestResetPasswordBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRequestResetPasswordBinding.inflate(inflater, container, false)

        binding.edtResetPassEmail.requestFocus()
        binding.btnSendOtpResetPass.setOnClickListener {
            sendOtpResetPassword()
        }
        return binding.root
    }


    private fun sendOtpResetPassword() {
        var email = binding.edtResetPassEmail.text.toString()
        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
        var currentDateandTime: String = sdf.format(Date())

        lifecycleScope.launch {
            val emp = vm.getUserByEmail(email)
            if (emp == null) {
             toast("Please ensure that you enter your email address correctly!")
             return@launch
             } else {
                 val userId = emp.id

                val n = (0..999999).random()
                val fmt = DecimalFormat("000000")
                val otpCode = fmt.format(n)

                val subject = "Reset Hope Rise Account Password Request @ ${currentDateandTime}"
                val content = """
                    <p>Greetings from Hope Rise 😉 </p>
                    <p>Dear <b>$userId</b>,</p>
                    <p>Please entered the <b>OTP</b> code to continue for reset your password:</p>
                    <h1 style="color: orange">$otpCode</h1>
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

                vm.updateOtp(userId,otpCode.toInt())
                vm.setLoginFailedId(userId)
                nav.navigate(R.id.confirmResetPasswordRequestFragment)
            }
      }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}