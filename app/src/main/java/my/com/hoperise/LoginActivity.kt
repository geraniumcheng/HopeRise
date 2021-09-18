package my.com.hoperise

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.data.*
import my.com.hoperise.databinding.ActivityLoginBinding
import java.util.*
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.loginHost)!!.findNavController() }
    private val loginVm: LoginViewModel by viewModels()
    private val vm: UserViewModel by viewModels()
    var progressDialog: ProgressDialog? = null
    var failedLoggedInId =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        checkInternetConnection()

        // Retrieve remember me user
        lifecycleScope.launch { loginVm.loginFromPreferences(this@LoginActivity) }

        loginVm.getUserLiveData().observe(this) { user ->
            if (user == null) { // When there have no user record in shared preferences
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else { // When login credentials match will go here (else will go to Login Fragment to further validate the conditions)
                // Pass login user's data (Method 1)
                currentUser = User(user.id,user.email,user.name,user.password,user.role,user.status,user.count,user.photo,user.otp,user.activateCode,user.registerDate)
                // Show progress dialog when login user from shared preferences
                progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog?.setTitle("Logging In")
                progressDialog?.setMessage("Please wait a moment ...")
                progressDialog?.show()
                val role = user.role
                val status = user.status
                val count = user.count
                if(status.equals("Unactivated")){ // When user status is unactivated, will redirect to register success (send OTP) screen
                    failedLoggedInId = user.id // Get the id of the user that failed to login
                    nav.navigate(R.id.registerSuccessFragment)
                    progressDialog?.dismiss()
                }
                else if(status.equals("Deactivated")){ // When user status is deactivated, will pop up an alert dialog and prevent them for logging in
                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Attention")
                        .setMessage("Please be informed that your account has been deactivated by the manager!")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton("OK", object :
                            DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                                return
                            }
                        }).show()
                    progressDialog?.dismiss()
                }
                else if(count <= 0){ // If there are no login attempts left; redirect to account blocked screen
                    // If password matched + 0 attempts left will redirect to account block screen from here (else will go to Login Fragment)
                    vm.setLoginFailedId(user.id)
                    nav.navigate(R.id.accountBlockFragment)
                    progressDialog?.dismiss()
                }
                else{ // When the user passed all required login conditions
                    vm.updateCount(user.id,3)
                    if (role.equals("Volunteer")) { // Volunteer role will be redirect to Main Activity
                        // Pass login user's data (Method 2)
                        val intent = Intent(this, MainActivity::class.java)
                            .putExtra("loggedInId", user.id)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else { // Manager and employee role will be redirect to Staff Activity
                        // Pass login user's data (Method 2)
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

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    fun checkInternetConnection(): Boolean {
        val conMgr = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo
        if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
            AlertDialog.Builder(this@LoginActivity)
                .setTitle("Opps!")
                .setMessage("Seems like you are offline now.\nPlease turn on your Wi-Fi and come back again. 😥" )
                .setIcon(R.drawable.ic_wifi_disconnect)
                .setPositiveButton("OK", object :
                    DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                        finish()
                    }
                }).show()
            return false
        }
        return true
    }
}