package my.com.hoperise.ui

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentViewStaffProfileBinding
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.*
import my.com.hoperise.util.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ViewStaffProfileFragment : Fragment() {
   private lateinit var binding: FragmentViewStaffProfileBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()
    private val loginVm: LoginViewModel by activityViewModels()
    var currentEmail = ""

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != RESULT_CANCELED) {
            val thumbnail = it.data!!.extras!!["data"] as Bitmap?
            binding.imgEmployeePhoto.setImageBitmap(thumbnail)
        }
    }

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data != null) {
            val photoURI: Uri? = it.data!!.data
            binding.imgEmployeePhoto.setImageURI(photoURI)
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) addPhoto() else snackbar(getString(R.string.featureCameraUnavailable))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentViewStaffProfileBinding.inflate(inflater, container, false)

        val userId = (activity as StaffActivity).loggedInId

        loadProfileData(userId)
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnChangePassword.setOnClickListener { nav.navigate(R.id.employeeChangePasswordFragment) }
        binding.btnReset.setOnClickListener { loadProfileData(userId) }
        binding.btnPickImage.setOnClickListener{ addPhoto() }
        binding.btnUpdate.setOnClickListener { updateStaff(userId) }

        return binding.root
    }

    private fun addPhoto() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        else {
            showPhotoSelection(getString(R.string.changeProfilePhoto),
                { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) }, {
                    val photoIntent =  Intent(Intent.ACTION_GET_CONTENT)
                    photoIntent.type = "image/*"
                    photoLauncher.launch(photoIntent)})
        }
    }

    private fun updateStaff(userId: String) {
        lifecycleScope.launch {
            val employeeOri = vm.getLogIn(userId)

                val emp = User(
                    id = employeeOri!!.id,
                    email = employeeOri!!.email,
                    name  = binding.edtEmployeeName.text.toString().trim(),
                    password = employeeOri!!.password,
                    role = employeeOri!!.role,
                    status = employeeOri!!.status,
                    count = employeeOri!!.count,
                    photo = binding.imgEmployeePhoto.cropToBlob(500, 500),
                    otp = employeeOri!!.otp,
                    activateCode = employeeOri!!.activateCode,
                    registerDate = employeeOri.registerDate
                )

        val enteredName = binding.edtEmployeeName.text.toString().trim()
        val enteredEmail = binding.edtEmployeeEmail.text.toString().trim()

        if(enteredName.equals("") || enteredEmail.equals("")){
            errorDialog("Please filled up all your personal details!")
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                errorDialog("Please enter new valid email!")
            }else{
                 if(vm.getUserByEmail(enteredEmail) != null && !currentEmail.equals(enteredEmail)){
                     errorDialog("Email exist! Try another one!")
                  }else{
                      if(currentEmail.equals(enteredEmail)){ // If user not modifying his email
                         vm.update(emp)
                         toast("Profile updated successfully!")
                      }else{
                         vm.update(emp)
                         verifyEmail(enteredEmail,userId) // If user is modifying email, will ask for confirmation and send an OTP through email
                         vm.setNewlyVerifiedEmail(enteredEmail)
                      }
                    }
                 }
            }
        }
    }

    private fun verifyEmail(editedEmail: String, userId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Attention!")
            .setMessage("In order to change your email, you may need to verify your email account again. Proceed to email verification?\nNote: If you rejected, your new email address will not be updated." )
            .setIcon(R.drawable.ic_otp_confirm_dialog)
            .setPositiveButton(android.R.string.yes, object :
                DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                    var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
                    var currentDateandTime: String = sdf.format(Date())
                    val n = (0..999999).random()
                    val fmt = DecimalFormat("000000")
                    val verifyEmailOtpCode = fmt.format(n).toString()
                    sendEmail(editedEmail,userId,verifyEmailOtpCode,currentDateandTime)
                    vm.updateOtp(userId, verifyEmailOtpCode.toInt())
                    nav.navigate(R.id.employeeVerifyEmailFragment)
                }
            })
            .setNegativeButton(android.R.string.no, object :
                DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                    loadProfileData(userId)
                }
            }).show()
    }

    private fun sendEmail(email: String, user: String, verifyEmailOtpCode: String, currentDateandTime: String) {
        val subject = "Verify Your Hope Rise Account's Email @ ${currentDateandTime}"
        val content = """
            <p>Hello <b>$user</b> 🤗 </p>
            <p>Please verify your new email address for your Hope Rise account by entering the following <b>OTP code</b>:</p>
            <h1 style="color: orange">$verifyEmailOtpCode</h1>
            <p>Thank you. Have a nice day! 😬</p>
            <p> </p>
            <p><i>Hope Rise Team</i></p>
            <p> </p>
            <p> </p>
        """.trimIndent()

        SendEmail()
            .to(email)
            .subject(subject)
            .content(content)
            .isHtml()
            .send() {
            }
    }

    private fun loadProfileData(userId: String) {
        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)

        if (emp == null) {
            nav.navigateUp() // If no record then return to previous page
            return@launch
        }
        loadProfile(emp)
    }
    }

    private fun loadProfile(emp: User){
        binding.edtEmployeeEmail.requestFocus()
        binding.imgEmployeePhoto.setImageBitmap(emp.photo?.toBitmap())
        binding.lblEmployeeId.setText(emp.id)
        binding.edtEmployeeEmail.setText(emp.email)
        binding.edtEmployeeName.setText(emp.name)
        binding.lblEmployeeRole.setText(emp.role)

        currentEmail = emp.email
    }

    private fun logout(){
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure want to log out?" )
            .setIcon(R.drawable.ic_leave_confirm_dialog)
            .setPositiveButton("Logout", object :
                DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                    loginVm.logout(requireContext())
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            })
            .setNegativeButton("Stay", null).show()

    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}