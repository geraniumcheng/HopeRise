package my.com.hoperise.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentAccountBlockBinding
import my.com.hoperise.util.SendEmail
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class AccountBlockFragment : Fragment() {
    private lateinit var binding: FragmentAccountBlockBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccountBlockBinding.inflate(inflater, container, false)

        binding.btnSendOtpUnlock.setOnClickListener {
            val loggedInId = vm.getLoginFailedId()
            sendOtpUnbockAccount(loggedInId)
        }

        // For prevent back press error happen
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nav.popBackStack(R.id.loginFragment, false)
            }
        })

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun sendOtpUnbockAccount(loggedInId: String) {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
        val currentDateandTime: String = sdf.format(Date())
        val n = (0..999999).random()
        val fmt = DecimalFormat("000000")
        val unblockOtpCode = fmt.format(n).toString()

        lifecycleScope.launch {
            val emp = vm.getLogIn(loggedInId)
            if (emp == null) {
                toast(getString(R.string.somethingWrong))
                return@launch
            } else {
                val email = emp.email
                val user = emp.id

                sendEmail(email,user,unblockOtpCode,currentDateandTime)

                vm.updateOtp(user, unblockOtpCode.toInt()) // Update the generated OTP to database
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.hurray))
                    .setMessage(getString(R.string.optSent, currentDateandTime))
                    .setIcon(R.drawable.ic_otp_confirm_dialog)
                    .setPositiveButton(getString(R.string.yes)) { _, _ -> nav.navigate(R.id.unblockAccountFragment) }
                    .setNegativeButton(getString(R.string.didntReceive), null).show()
            }
        }
    }

    private fun sendEmail(email: String, user: String, unblockOtpCode: String, currentDateandTime: String) {
        val subject = "Unblock Hope Rise Account Request @ $currentDateandTime"
        val content = """
                    <p>Greetings from Hope Rise 😇 </p>
                    <p>Dear <b>$user</b>,</p>
                    <p>We are sorry to tell you that we are compelled to block your account due to the exceeding login attempts that made by your account 😩</p>
                    <p>In order to unblock your Hope Rise account, please entered the <b>OTP</b> code to continue for your account unblock request:</p>
                    <h1 style="color: orange">$unblockOtpCode</h1>
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
                    .send {}
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}