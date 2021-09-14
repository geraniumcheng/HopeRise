package my.com.hoperise.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.data.LoginViewModel
import my.com.hoperise.databinding.FragmentLoginBinding
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.hideKeyboard


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val nav by lazy { findNavController() }
    private val loginVm: LoginViewModel by activityViewModels()
    private val vm: EmployeeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        resetErrorMessage()

        binding.btnForgetPassword.setOnClickListener { nav.navigate(R.id.requestResetPasswordFragment) }
        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegister.setOnClickListener {  nav.navigate(R.id.registerFragment) }
        return binding.root
    }

    private fun resetErrorMessage() {
        binding.lblLoginIdErrorMessage.visibility = View.GONE
        binding.lblLoginPasswordErrorMessage.visibility = View.GONE
    }

    private fun login() {
        hideKeyboard()

        val ctx = requireContext()
        val id = binding.edtLoginId.text.toString().trim()
        val password = binding.edtLoginPassword.text.toString().trim()
        val remember = binding.chkRememberMe.isChecked

        if(id.equals("") || password.equals(""))
        {
            errorDialog("Please filled in your login credentials!")
            return
        }

        // TODO(3): Login -> auth.login(...)
        //          Clear navigation backstack
        lifecycleScope.launch {
            val success = loginVm.login(ctx, id, password, remember)
            if (success) {
                //nav.popBackStack(R.id.viewVolunteerProfileFragment, false)
                //nav.navigateUp()
//                loginVm.getUserLiveData().observe(viewLifecycleOwner) { user ->
//                    val role = user.role
//                    if (role.equals("Volunteer")){
//                        val intent = Intent(activity, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        toast(remember.toString())
//                    }
//                    else{
//                        val intent = Intent(activity, StaffActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        toast(remember.toString())
//                    }
//                }

            }
            else {
                // If password mismatched + 0 attempts left will go here
                lifecycleScope.launch {
                    val emp = vm.getLogIn(id)

                    if (emp == null) {
                       errorDialog("Invalid login credentials.")
                    }else{
                        vm.setLoginFailedId(emp.id)
                        //toast("Yes user exists")
                        var timesLeft = emp.count
                        timesLeft = timesLeft - 1
                        //toast(timesLeft.toString())
                        vm.updateCount(emp.id,timesLeft)
                        if(timesLeft > 0 )
                             errorDialog("Invalid login credentials! " + timesLeft + " attempts left! ")
                        if(timesLeft <= 0 ){
                            nav.navigate(R.id.accountBlockFragment)
                        }


                    }

                }
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(activity,text,Toast.LENGTH_SHORT).show()
    }
}