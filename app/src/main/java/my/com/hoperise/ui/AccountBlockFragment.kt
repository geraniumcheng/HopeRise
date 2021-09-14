package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
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
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.databinding.FragmentAccountBlockBinding
import my.com.hoperise.util.SendEmail
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class AccountBlockFragment : Fragment() {
    private lateinit var binding: FragmentAccountBlockBinding
    private val nav by lazy { findNavController() }
    private val vm: EmployeeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountBlockBinding.inflate(inflater, container, false)

        binding.btnSendOtpUnlock.setOnClickListener {
            val loggedInId = vm.getLoginFailedId()
            sendOtpUnbockAccount(loggedInId)
            //nav.navigate(R.id.unblockAccountFragment)
        }
        return binding.root
    }

    private fun sendOtpUnbockAccount(loggedInId: String) {
        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
        var currentDateandTime: String = sdf.format(Date())
        val n = (0..999999).random()
        val fmt = DecimalFormat("000000")
        val unblockOtpCode = fmt.format(n).toString()

        //${System.currentTimeMillis()}"
        lifecycleScope.launch {
            val emp = vm.getLogIn(loggedInId)
            if (emp == null) {
                toast("Opps! Something wrong!")
                return@launch
            } else {
                val email = emp.email
                val user = emp.id
                //toast(email)

                sendEmail(email,user,unblockOtpCode,currentDateandTime)
//                val subject = "Unblock Hope Rise Account Request @ ${currentDateandTime}"
//                val content = """
//                    <p>Greetings from Hope Rise 😇 </p>
//                    <p>Dear <b>$user</b>,</p>
//                    <p>We are sorry to tell you that we are compelled to block your account due to the exceeding login attempts that made by your account 😩</p>
//                    <p>In order to unblock your Hope Rise account, please entered the <b>OTP</b> code to continue for your account unblock request:</p>
//                    <h1 style="color: orange">$unblockOtpCode</h1>
//                    <p>Thank you! 😁</p>
//                    <p> </p>
//                    <p><i>Hope Rise Team</i></p>
//                    <p> </p>
//                    <p> </p>
//                """.trimIndent()
//
//                SendEmail()
//                    .to(email)
//                    .subject(subject)
//                    .content(content)
//                    .isHtml()
//                    .send() {
//                    }

                vm.updateOtp(user, unblockOtpCode.toInt())
                AlertDialog.Builder(requireContext())
                    .setTitle("Hurray")
                    .setMessage("An otp code was sent to your account's registered email on " + currentDateandTime + " successfully ✨ \nUnblock your account now?" )
                    .setIcon(R.drawable.ic_otp_confirm_dialog)
                    .setPositiveButton(android.R.string.yes, object :
                        DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                            nav.navigate(R.id.unblockAccountFragment)
                        }
                    })
                    .setNegativeButton("I didnt receive it", null).show()
            }
        }
    }

    private fun sendEmail(email: String, user: String, unblockOtpCode: String, currentDateandTime: String) {
        val subject = "Unblock Hope Rise Account Request @ ${currentDateandTime}"
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
                    .send() {
                    }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }


}