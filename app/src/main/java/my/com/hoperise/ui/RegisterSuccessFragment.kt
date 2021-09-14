package my.com.hoperise.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
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
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.databinding.FragmentRegisterSuccessBinding
import my.com.hoperise.util.SendEmail
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class RegisterSuccessFragment : Fragment() {
    private lateinit var binding: FragmentRegisterSuccessBinding
    private val nav by lazy { findNavController() }
    private val vm: EmployeeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterSuccessBinding.inflate(inflater, container, false)

        val activity: LoginActivity = activity as LoginActivity
        val loggedInId: String = activity.getFailedLoginId()

        //toast(loggedInId)
        //binding.lblOtpDetails.visibility = View.INVISIBLE
        binding.btnSendOtp.setOnClickListener {
            sendActivateCode(loggedInId)
        }

        return binding.root
    }

    private fun sendActivateCode(loggedInId: String) {
        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
        var currentDateandTime: String = sdf.format(Date())
        val n = (0..999999).random()
        val fmt = DecimalFormat("000000")
        val activationCode = fmt.format(n)

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

            val subject = "Thank You For Registering With Hope Rise! @ ${currentDateandTime}"
            val content = """
            <p>Hello <b>$loggedInId</b> 🤩 </p>
            <p>Welcome to <i>Hope Rise</i>!</p>
            <p>Confirm your account registration by entering the following <b> activation code</b>:</p>
            <h1 style="color: orange">$activationCode</h1>
            <p>One step closer to make charity with us! See you there! 🤗</p>
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

                vm.updateActivationCode(user, activationCode.toInt())
                AlertDialog.Builder(requireContext())
                    .setTitle("Hurray")
                    .setMessage("An activation code was sent to your account's registered email on " + currentDateandTime + " successfully ✨ Activate your account now?" )
                    .setIcon(R.drawable.ic_otp_confirm_dialog)
                    .setPositiveButton(android.R.string.yes, object :
                        DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                            nav.navigate(R.id.activateAccountFragment)
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show()

                //binding.lblOtpDetails.visibility = View.VISIBLE
                //binding.lblOtpDetails.text = "OTP Code is was sent to you on " + currentDateandTime

            }
    }
}

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}