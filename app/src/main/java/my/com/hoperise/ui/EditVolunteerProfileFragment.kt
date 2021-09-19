package my.com.hoperise.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentEditVolunteerProfileBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import my.com.hoperise.util.*

class EditVolunteerProfileFragment : Fragment() {
   private lateinit var binding: FragmentEditVolunteerProfileBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()
    var currentEmail = "" // For compare purpose

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != RESULT_CANCELED) {
            val thumbnail = it.data!!.extras!!["data"] as Bitmap?
            binding.imgVolunteerPhoto.setImageBitmap(thumbnail)
        }
    }

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data != null) {
            val photoURI: Uri? = it.data!!.data
            binding.imgVolunteerPhoto.setImageURI(photoURI)
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) addPhoto() else snackbar(getString(R.string.featureCameraUnavailable))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditVolunteerProfileBinding.inflate(inflater, container, false)

        requireActivity().title = getString(R.string.editVolProfile)

        val userId = (activity as MainActivity).loggedInId

        loadProfileData(userId)
        binding.btnReset.setOnClickListener { loadProfileData(userId) }
        binding.btnPickImage.setOnClickListener { addPhoto() }
        binding.btnConfirm.setOnClickListener { updateVolunteer(userId) }

        return binding.root
    }

    private fun addPhoto() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        else {
            showPhotoSelection(getString(R.string.uploadToGallery),
                { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) }, {
                    val photoIntent =  Intent(Intent.ACTION_GET_CONTENT)
                    photoIntent.type = "image/*"
                    photoLauncher.launch(photoIntent)})
        }
    }

    private fun loadProfileData(userId: String) {
        //toast(userId)
        lifecycleScope.launch {
            val vol = vm.getLogIn(userId)

            if (vol == null) {
                nav.navigateUp() //if no record then return to previous page
                return@launch
            }
            loadProfile(vol)
        }
    }

    private fun loadProfile(vol: User){
        binding.imgVolunteerPhoto.setImageBitmap(vol.photo?.toBitmap())
        binding.lblVolunteerId.setText(vol.id)
        binding.edtVolunteerEmail.setText(vol.email)
        binding.edtVolunteerName.setText(vol.name)

        binding.edtVolunteerEmail.requestFocus()
        currentEmail = vol.email
    }

    private fun updateVolunteer(userId: String) {
        lifecycleScope.launch {
            val volunteerOri = vm.getLogIn(userId)

            val emp = User(
                id = volunteerOri!!.id,
                email = volunteerOri!!.email,
                name  = binding.edtVolunteerName.text.toString().trim(),
                password = volunteerOri!!.password,
                role = volunteerOri!!.role,
                status = volunteerOri!!.status,
                count = volunteerOri!!.count,
                photo = binding.imgVolunteerPhoto.cropToBlob(500, 500),
                otp = volunteerOri!!.otp,
                activateCode = volunteerOri!!.activateCode,
                registerDate = volunteerOri.registerDate
            )

            val enteredName = binding.edtVolunteerName.text.toString().trim()
            val enteredEmail = binding.edtVolunteerEmail.text.toString().trim()

            if(enteredName == "" || enteredEmail == ""){
                errorDialog(getString(R.string.fillAllProfileDetail))
            }else{
                if(!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                    errorDialog(getString(R.string.enterValidEmail))
                }else{
                    if(vm.getUserByEmail(enteredEmail) != null && currentEmail != enteredEmail){
                        errorDialog(getString(R.string.emailExist))
                    }else{
                        if(currentEmail == enteredEmail){
                            vm.update(emp)
                            nav.navigateUp()
                            toast(getString(R.string.profileUpdated))
                        }else{
                            vm.update(emp)
                            verifyEmail(enteredEmail,userId)
                            vm.setNewlyVerifiedEmail(enteredEmail)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun verifyEmail(editedEmail: String, userId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.simpleWarning))
            .setIcon(R.drawable.ic_otp_confirm_dialog)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
                val currentDateandTime: String = sdf.format(Date())
                val n = (0..999999).random()
                val fmt = DecimalFormat("000000")
                val verifyEmailOtpCode = fmt.format(n).toString()
                sendEmail(editedEmail, userId, verifyEmailOtpCode, currentDateandTime)
                vm.updateOtp(userId, verifyEmailOtpCode.toInt())
                nav.navigate(R.id.volunteerVerifyEmailFragment)
            }
            .setNegativeButton(android.R.string.no) { _, _ -> loadProfileData(userId) }.show()
    }

    private fun sendEmail(email: String, user: String, verifyEmailOtpCode: String, currentDateandTime: String) {
        val subject = "Verify Your Hope Rise Account's Email @ $currentDateandTime"
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

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}