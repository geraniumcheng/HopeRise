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
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    private val nav by lazy { findNavController() }
    private val vm: EmployeeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)

        binding.btnResetNow.setOnClickListener {
            val loggedInId = vm.getLoginFailedId()
            validatePassword(loggedInId)
            //nav.navigate(R.id.resetPasswordSuccessFragment)
        }

        return binding.root
    }

    private fun validatePassword(loggedInId: String) {
        lifecycleScope.launch {
            val emp = vm.getLogIn(loggedInId)
            if (emp == null) {
                toast("Opps! Something wrong!")
                return@launch
            } else {
                val newPass = binding.edtNewPassword.text.toString()
                val confirmNewPass = binding.edtConfirmNewPassword.text.toString()
                vm.updatePassword(emp.id,confirmNewPass)
                nav.navigate(R.id.resetPasswordSuccessFragment)
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}