package my.com.hoperise.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.MainActivity
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentEditVolunteerProfileBinding
import my.com.hoperise.util.SendEmail
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.toBitmap
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class EditVolunteerProfileFragment : Fragment() {
   private lateinit var binding: FragmentEditVolunteerProfileBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()
    var currentEmail = ""

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgVolunteerPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditVolunteerProfileBinding.inflate(inflater, container, false)

        requireActivity().title = "Update Volunteer Profile"

        val userId = (activity as MainActivity).loggedInId

        loadProfileData(userId)
        binding.btnReset.setOnClickListener { loadProfileData(userId) }
        binding.btnPickImage.setOnClickListener { pickImage() }
        binding.btnConfirm.setOnClickListener { updateVolunteer(userId) }
        return binding.root
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

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
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
                photo = binding.imgVolunteerPhoto.cropToBlob(300, 300),
                otp = volunteerOri!!.otp,
                activateCode = volunteerOri!!.activateCode,
                registerDate = volunteerOri.registerDate
            )

            val enteredName = binding.edtVolunteerName.text.toString().trim()
            val enteredEmail = binding.edtVolunteerEmail.text.toString().trim()

            if(enteredName.equals("") || enteredEmail.equals("")){
                errorDialog("Please filled up all your personal details!")
            }else{
                if(!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                    errorDialog("Please enter new valid email!")
                }else{
                    if(vm.getUserByEmail(enteredEmail) != null && !currentEmail.equals(enteredEmail)){
                        errorDialog("Email exist! Try another one!")
                    }else{
                        if(currentEmail.equals(enteredEmail)){
                            vm.update(emp)
                            toast("Profile updated successfully!")
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
                    nav.navigate(R.id.volunteerVerifyEmailFragment)
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

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}