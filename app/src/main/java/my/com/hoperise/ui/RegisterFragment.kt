package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentRegisterBinding
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.toBlob

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        resetErrorMessage()
        binding.btnConfirmRegister.setOnClickListener {
            registerVolunteer()
        }
        binding.btnLogin.setOnClickListener{
            verifyEnteredDetails()
            //nav.navigate(R.id.loginFragment)
        }

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nav.popBackStack(R.id.loginFragment, false)
            }
        })

        return binding.root
    }

    private fun resetErrorMessage(){
        binding.edtEmail.requestFocus()
        binding.edtEmail.setText(" ")
        binding.lblEmailWarning.visibility = View.GONE
        binding.lblUsernameWarning.visibility = View.GONE
        binding.lblPasswordWarning.visibility = View.GONE
        binding.lblConfirmPasswordWarning.visibility = View.GONE
    }

    private fun verifyEnteredDetails(){
        val enteredEmail = binding.edtEmail.text.toString()
        val enteredUsername = binding.edtUsername.text.toString()
        val enteredPassword = binding.edtPassword.text.toString()
        val enteredConfirmPassword = binding.edtConfirmPassword.text.toString()

        if(!enteredEmail.equals("") || !enteredUsername.equals("") || !enteredPassword.equals("") || !enteredConfirmPassword.equals("")){
            AlertDialog.Builder(requireContext())
                .setTitle("Hmmm")
                .setMessage("Just one step away to register with Hope Rise! Are you sure you want to leave now?" )
                .setIcon(R.drawable.ic_leave_confirm_dialog)
                .setPositiveButton("Yes", object :
                    DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                        nav.navigate(R.id.loginFragment)
                    }
                })
                .setNegativeButton("No", null).show()
        }
    }

    private fun registerVolunteer() {
        var defaultPhoto = ContextCompat.getDrawable(requireContext(), R.drawable.ic_default_profile_picture)!!.toBitmap()
        val regexPassword = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[=+@$#!%*?&,._-])[A-Za-z\d=+@$#!%*?&,._-]{8,}$""")
        val regexId = Regex("""^[a-z0-9]{8,20}${'$'}""")
        val enteredEmail = binding.edtEmail.text.toString().trim()
        val enteredUsername = binding.edtUsername.text.toString().trim()
        val enteredPassword = binding.edtPassword.text.toString().trim()
        val enteredConfirmPassword = binding.edtConfirmPassword.text.toString().trim()


        lifecycleScope.launch {

            if(enteredEmail.equals("") || enteredUsername.equals("") || enteredPassword.equals("") || enteredConfirmPassword.equals("")){
                errorDialog("Please filled up all your personal details!")
            }
            else {
                if (vm.getUserByEmail(enteredEmail) == null) {
                    binding.lblEmailWarning.visibility = View.GONE
                  if(Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()){
                      binding.lblEmailWarning.visibility = View.GONE
                      if(enteredUsername.matches(regexId)){
                          binding.lblUsernameWarning.visibility = View.GONE
                          if (vm.getLogIn(enteredUsername) == null) {
                              binding.lblUsernameWarning.visibility = View.GONE
                              if (enteredPassword.matches(regexPassword)) {
                                  binding.lblPasswordWarning.visibility = View.GONE
                                  if (enteredPassword.equals(enteredConfirmPassword)) {
                                      binding.lblConfirmPasswordWarning.visibility = View.GONE
                                      //action
                                      val volunteer = User(
                                          id    = enteredUsername,
                                          email = enteredEmail,
                                          name  = "",
                                          password = enteredConfirmPassword,
                                          role = "Volunteer",
                                          status = "Unactivated",
                                          count = 3,
                                          photo = defaultPhoto.toBlob(),
                                          otp = 0,
                                          activateCode = 0,
                                      )
                                      vm.set(volunteer)
                                      vm.setNewlyRegisteredId(enteredUsername)
                                      nav.navigate(R.id.registerSuccessFragment)
                                  } else {
                                      binding.lblConfirmPasswordWarning.visibility = View.VISIBLE
                                      binding.lblConfirmPasswordWarning.text = "Please ensure your new password is matched!"
                                  }
                              } else {
                                  binding.lblPasswordWarning.visibility = View.VISIBLE
                                  binding.lblPasswordWarning.text =
                                      "Password must be minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character"
                              }
                          } else {
                              binding.lblUsernameWarning.visibility = View.VISIBLE
                              binding.lblUsernameWarning.text =
                                  "Username exists! Please try another ones!\nNote: Once your username is registered, it can't be changed anymore!"
                          }
                      }
                      else{
                          binding.lblUsernameWarning.visibility = View.VISIBLE
                          binding.lblUsernameWarning.text = "Username must be at least eight characters, lowercase letter and no special character are allowed!"
                      }
                  }
                    else{
                      binding.lblEmailWarning.visibility = View.VISIBLE
                      binding.lblEmailWarning.text = "Please entered an valid email."
                    }
                } else {
                    binding.lblEmailWarning.visibility = View.VISIBLE
                    binding.lblEmailWarning.text = "Email exists! Please try another email."
                }
            }

        }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}
