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
import my.com.hoperise.LoginActivity
import my.com.hoperise.R
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentActivateAccountBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ActivateAccountFragment : Fragment() {
    private lateinit var binding: FragmentActivateAccountBinding
    private val nav by lazy { findNavController() }
    private val vm: EmployeeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActivateAccountBinding.inflate(inflater, container, false)

        val activity: LoginActivity = activity as LoginActivity
        val loggedInId: String = activity.getUnactivatedId()

        binding.btnActivateAccount.setOnClickListener { nav.navigate(R.id.verifyAccountSuccessFragment) }

        return binding.root
    }

    private fun sendActivateCode(loggedInId: String) {
        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
        var currentDateandTime: String = sdf.format(Date())
        val n = (0..999999).random()
        val fmt = DecimalFormat("000000")
        val activationCode = fmt.format(n)
        var user = ""

        //${System.currentTimeMillis()}"

            //val emp = vm.getLogIn(loggedInId)
            val emp = currentUser
            if (emp == null) {
                toast("Opps! Something wrong!")
                return
            } else {
                val email = emp.email
                user = emp.id
                toast(user)

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

//        SendEmail()
//            .to(email)
//            .subject(subject)
//            .content(content)
//            .isHtml()
//            .send() {
//            }
                //
        }
        vm.updateActivationCode(user, activationCode.toInt())
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }


}