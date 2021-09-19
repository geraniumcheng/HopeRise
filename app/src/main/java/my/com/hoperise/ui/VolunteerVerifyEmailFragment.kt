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
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentVolunteerVerifyEmailBinding

class VolunteerVerifyEmailFragment : Fragment() {

        private lateinit var binding: FragmentVolunteerVerifyEmailBinding
        private val nav by lazy { findNavController() }
        private val vm: UserViewModel by activityViewModels()

        private lateinit var otp1: EditText
        private lateinit var otp2: EditText
        private lateinit var otp3: EditText
        private lateinit var otp4: EditText
        private lateinit var otp5: EditText
        private lateinit var otp6: EditText

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = FragmentVolunteerVerifyEmailBinding.inflate(inflater, container, false)

            requireActivity().title = "Verify Email"

            val userId = (activity as MainActivity).loggedInId

            otp1 = binding.edtOtp1
            otp2 = binding.edtOtp2
            otp3 = binding.edtOtp3
            otp4 = binding.edtOtp4
            otp5 = binding.edtOtp5
            otp6 = binding.edtOtp6

            binding.edtOtp1.requestFocus()

            setUpOtpInput()

            binding.btnVerifyRequest.setOnClickListener { verifyOtp(userId) }

            return binding.root
        }

    private fun verifyOtp(userId: String) {
        lifecycleScope.launch {
            var formattedOtp = 0
            val emp = vm.getLogIn(userId)
            if (emp == null) {
                toast("Opps! Something wrong!")
                return@launch
            } else {
                val otpCode = emp.otp
                val enteredOtp = otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()

                // Format for validation purpose
                if(enteredOtp.equals("")){
                    formattedOtp = 0
                }else{
                    formattedOtp = enteredOtp.toInt()
                }

                if(otpCode!!.equals(formattedOtp)){
                    val newEmail = vm.getNewlyVerifiedEmail()
                    vm.updateEmail(userId, newEmail)
                    nav.navigate(R.id.viewVolunteerProfileFragment)
                    toast("Verify email successfully!")
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