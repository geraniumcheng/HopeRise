package my.com.hoperise.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentRegisterEmployeeBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog


class RegisterEmployeeFragment : Fragment() {
    private lateinit var binding: FragmentRegisterEmployeeBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()
    private val GALLERY = 1
    private val CAMERA = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterEmployeeBinding.inflate(inflater, container, false)

        binding.edtEmployeeId.requestFocus()
        binding.btnRegisterEmployee.setOnClickListener{ registerEmployee() }
        binding.btnPickImage.setOnClickListener{ showSelection() }
        binding.btnReset.setOnClickListener { reset() }

        return binding.root
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

    private fun showSelection() {
        var items: Array<CharSequence> = arrayOf<CharSequence>("Take Photo", "Chose from photos")
        AlertDialog.Builder(requireContext())
            .setTitle("Change profile photo")
            .setIcon(R.drawable.ic_select_photo)
            .setSingleChoiceItems(items, 3, object : DialogInterface.OnClickListener {
                override fun onClick(d: DialogInterface?, n: Int) {
                    if(n == 0){
                        pickImage(n)
                        d?.dismiss()
                    }
                    else{
                        pickImage(n)
                        d?.dismiss()
                    }
                }
            })
            .setNegativeButton("Cancel", null).show()
    }

    private fun pickImage(n: Int) {
        if(n == 0){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }else{
            val galleryIntent =  Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GALLERY)
        }
    }

    private fun registerEmployee() {
        // TODO: Insert (set)
        val emp = User(
            id    = binding.edtEmployeeId.text.toString().lowercase().trim(),
            email = binding.edtEmployeeEmail.text.toString().trim(),
            name  = binding.edtEmployeeName.text.toString().trim(),
            password = binding.edtTemporaryPassword.text.toString().trim(),
            role = binding.spnEmployeeRole.selectedItem as String,
            status = "Unactivated",
            count = 3,
            photo = binding.imgEmployeePhoto.cropToBlob(300, 300),
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

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val photoURI: Uri? = data.data
                binding.imgEmployeePhoto.setImageURI(photoURI)
            }
        }
        else if (requestCode == CAMERA) {
            if(resultCode != Activity.RESULT_CANCELED){
                val thumbnail = data!!.extras!!["data"] as Bitmap?
                binding.imgEmployeePhoto.setImageBitmap(thumbnail)
            }
        }
    }

}