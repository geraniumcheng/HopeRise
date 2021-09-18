package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.LoginViewModel
import my.com.hoperise.databinding.FragmentLoginBinding
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.hideKeyboard

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val nav by lazy { findNavController() }
    private val loginVm: LoginViewModel by activityViewModels()
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.edtLoginId.requestFocus()
        binding.btnForgetPassword.setOnClickListener { nav.navigate(R.id.requestResetPasswordFragment) }
        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegister.setOnClickListener {  nav.navigate(R.id.registerFragment) }

        // For prevent back press error happen
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getActivity()?.finish()
            }
        })

        return binding.root
    }

    private fun login() {
        hideKeyboard()

        val ctx = requireContext()
        val id = binding.edtLoginId.text.toString().trim()
        val password = binding.edtLoginPassword.text.toString().trim()
        val remember = binding.chkRememberMe.isChecked

        // Catch empty login credentials
        if(id.equals("") || password.equals(""))
        {
            errorDialog("Please filled in your login credentials!")
            return
        }

        lifecycleScope.launch {
            val success = loginVm.login(ctx, id, password, remember)
            if (!success) { // When login credentials mismatched will go here
                lifecycleScope.launch {
                    val user = vm.getLogIn(id)
                    if (user == null) {
                        errorDialog("Invalid login credentials.")
                    }else if(user.status.equals("Deactivated")){
                        // If password mismatched + user account has been deactivated
                        AlertDialog.Builder(requireContext())
                            .setTitle("Attention")
                            .setMessage("Please be informed that your account has been deactivated by the manager!")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton("OK", object :
                                DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                                    return
                                }
                            }).show()
                    }
                    else if(user.status.equals("Active")){ // If password mismatched + 0 attempts left will redirect to account block screen from here
                        vm.setLoginFailedId(user.id)
                        var timesLeft = user.count
                        timesLeft = timesLeft - 1
                        vm.updateCount(user.id,timesLeft)
                        if(timesLeft > 0 )
                            errorDialog("Invalid login credentials! " + timesLeft + " attempts left! ")
                        if(timesLeft <= 0 ){
                            nav.navigate(R.id.accountBlockFragment)
                        }
                    }
                    else{
                        errorDialog("Invalid login credentials.")
                    }
                }
            }
        }
    }
}