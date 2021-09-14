package my.com.hoperise

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.data.LoginViewModel
import my.com.hoperise.data.User
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.ActivityLoginBinding
import my.com.hoperise.ui.RegisterSuccessFragment
import my.com.hoperise.util.errorDialog
import java.util.*
import kotlin.concurrent.schedule

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.loginHost)!!.findNavController() }
    private val loginVm: LoginViewModel by viewModels()
    private val vm: EmployeeViewModel by viewModels()
    var progressDialog: ProgressDialog? = null
    var failedLoggedInId =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        lifecycleScope.launch { loginVm.loginFromPreferences(this@LoginActivity) }

        loginVm.getUserLiveData().observe(this) { user ->
            if (user == null) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                currentUser = User(user.id,user.email,user.name,user.password,user.role,user.status,user.count,user.photo,user.otp,user.activateCode,user.registerDate)
                progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog?.setTitle("Logging In")
                progressDialog?.setMessage("Please wait a moment ...")
                progressDialog?.show()
                val role = user.role
                val status = user.status
                val count = user.count
                if(status.equals("Unactivated")){
                    failedLoggedInId = user.id
                    nav.navigate(R.id.registerSuccessFragment)
                    progressDialog?.dismiss()
                }else if(count <= 0){
                    // If password matched + 0 attempts left will go here
                    vm.setLoginFailedId(user.id)
                    nav.navigate(R.id.accountBlockFragment)
                    progressDialog?.dismiss()
                }
                else{
                    vm.updateCount(user.id,3)
                    if (role.equals("Volunteer")) {
                        val intent = Intent(this, MainActivity::class.java)
                            .putExtra("loggedInId", user.id)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, StaffActivity::class.java)
                            .putExtra("loggedInId", user.id)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }

        }

    }

    fun getFailedLoginId(): String {
        return failedLoggedInId
    }

    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp() || super.onSupportNavigateUp()
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    fun saveData(id: Int, data: Bundle?) {
        // based on the id you'll know which fragment is trying to save data(see below)
        // the Bundle will hold the data
    }

    fun getSavedData() {
        // here you'll save the data previously retrieved from the fragments and
        // return it in a Bundle
    }

}