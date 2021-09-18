package my.com.hoperise.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentActivateAccountBinding
import java.util.*

class ActivateAccountFragment : Fragment() {
    private lateinit var binding: FragmentActivateAccountBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentActivateAccountBinding.inflate(inflater, container, false)

        val activity: LoginActivity = activity as LoginActivity
        val loggedInId: String = activity.getFailedLoginId()

         otp1 = binding.edtOtp1
         otp2 = binding.edtOtp2
         otp3 = binding.edtOtp3
         otp4 = binding.edtOtp4
         otp5 = binding.edtOtp5
         otp6 = binding.edtOtp6

        binding.edtOtp1.requestFocus()

        setUpOtpInput()

        binding.btnActivateAccount.setOnClickListener {
            if(loggedInId == ""){  // When user is direct from register screen
                val newlyRegister = vm.getNewlyRegisteredId()
                verifyActivationCode(newlyRegister)
            }else{
                verifyActivationCode(loggedInId) // When user is direct from login screen
            }
        }

        // For prevent back press error happen
        activity.onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.leaveNow))
                    .setMessage(getString(R.string.accountNoteActivate))
                    .setIcon(R.drawable.ic_leave_confirm_dialog)
                    .setPositiveButton(android.R.string.yes) { _, _ -> nav.navigate(R.id.loginFragment) }
                    .setNegativeButton(android.R.string.no, null).show()
            }
        })

        return binding.root
    }

    private fun verifyActivationCode(loggedInId: String) {
        lifecycleScope.launch {
            var formattedOtp = 0
            val emp = vm.getLogIn(loggedInId)
            if (emp == null) {
                toast(getString(R.string.somethingWrong))
                return@launch
            } else {
                val activationCode = emp.activateCode
                val user = emp.id
                val enteredOtp = otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()

                // Format for validation purpose
                formattedOtp = if(enteredOtp == "") 0 else enteredOtp.toInt()

                // Verify entered OTP
                if(activationCode!! == formattedOtp){
                    vm.updateStatus(user, getString(R.string.active))
                    nav.navigate(R.id.verifyAccountSuccessFragment)
                }else{
                    toast(getString(R.string.otpNoMatch))
                }
            }
        }
    }

    // Cursor move next when number is entered
    private fun setUpOtpInput() {
        otp1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().trim { it <= ' ' }.isEmpty()) {
                    otp2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        otp2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().trim { it <= ' ' }.isEmpty()) {
                    otp3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        otp3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().trim { it <= ' ' }.isEmpty()) {
                    otp4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        otp4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().trim { it <= ' ' }.isEmpty()) {
                    otp5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        otp5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().trim { it <= ' ' }.isEmpty()) {
                    otp6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}