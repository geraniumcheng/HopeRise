package my.com.hoperise.ui

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentRegisterEmployeeBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.showPhotoSelection
import my.com.hoperise.util.snackbar

class RegisterEmployeeFragment : Fragment() {
    private lateinit var binding: FragmentRegisterEmployeeBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

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
        binding = FragmentRegisterEmployeeBinding.inflate(inflater, container, false)

        binding.edtEmployeeId.requestFocus()
        binding.btnRegisterEmployee.setOnClickListener{ registerEmployee() }
        binding.btnPickImage.setOnClickListener{ addPhoto() }
        binding.btnReset.setOnClickListener { reset() }

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

    private fun reset(){
        binding.edtEmployeeId.setText("")
        binding.edtEmployeeEmail.setText("")
        binding.edtEmployeeName.setText("")
        binding.spnEmployeeRole.setSelection(0)
        binding.edtTemporaryPassword.setText("")
        binding.imgEmployeePhoto.setImageResource(R.drawable.ic_default_profile_picture)

        binding.edtEmployeeId.requestFocus()
    }

    private fun registerEmployee() {
        val emp = User(
            id    = binding.edtEmployeeId.text.toString().lowercase().trim(),
            email = binding.edtEmployeeEmail.text.toString().trim(),
            name  = binding.edtEmployeeName.text.toString().trim(),
            password = binding.edtTemporaryPassword.text.toString().trim(),
            role = binding.spnEmployeeRole.selectedItem as String,
            status = "Unactivated",
            count = 3,
            photo = binding.imgEmployeePhoto.cropToBlob(500, 500),
            otp = 0,
            activateCode = 0,
        )

        val err = vm.validate(emp)
        if (err != "") {
            errorDialog(err)
            return
        }

        vm.set(emp)
        nav.navigateUp()
        toast("Employee register successfully!")
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}