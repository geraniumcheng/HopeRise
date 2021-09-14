package my.com.hoperise.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentConfirmResetPasswordRequestBinding
import my.com.hoperise.util.hideKeyboard

class ConfirmResetPasswordRequestFragment : Fragment() {
    private lateinit var binding: FragmentConfirmResetPasswordRequestBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentConfirmResetPasswordRequestBinding.inflate(inflater, container, false)

        otp1 = binding.edtOtp1
        otp2 = binding.edtOtp2
        otp3 = binding.edtOtp3
        otp4 = binding.edtOtp4
        otp5 = binding.edtOtp5
        otp6 = binding.edtOtp6

        binding.edtOtp1.requestFocus()

        setUpOtpInput()

        binding.btnVerifyRequest.setOnClickListener {
            val loggedInId = vm.getLoginFailedId()
            verifyPassResetOtpCode(loggedInId)
            //nav.navigate(R.id.resetPasswordFragment)
            }

        return binding.root
    }

    private fun verifyPassResetOtpCode(loggedInId: String) {
        lifecycleScope.launch {
            val emp = vm.getLogIn(loggedInId)
            if (emp == null) {
                toast("Opps! Something wrong!")
                return@launch
            } else {
                val otp = emp.otp
                val enteredOtp = otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()

                if(otp!!.equals(enteredOtp.toInt())){
                    hideKeyboard()
                    nav.navigate(R.id.resetPasswordFragment)
                }else{
                    toast("OTP mismatch, please try again!")
                }
            }
        }
    }

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