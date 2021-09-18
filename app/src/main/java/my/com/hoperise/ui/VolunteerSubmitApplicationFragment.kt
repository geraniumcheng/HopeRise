package my.com.hoperise.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentAccountBinding
import my.com.hoperise.databinding.FragmentVolunteerSubmitApplicationBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import java.util.*

class VolunteerSubmitApplicationFragment : Fragment() {
    private lateinit var binding: FragmentVolunteerSubmitApplicationBinding
    private val nav by lazy { findNavController() }
    private val vm: VolunteerApplicationViewModel by activityViewModels()
    private val status by lazy { requireArguments().getString("status") ?: "" }
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private var imageFor : String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVolunteerSubmitApplicationBinding.inflate(inflater, container, false)

        binding.imgSelfie.setOnClickListener { showSelection("Selfie") }
        binding.imgFrontIC.setOnClickListener { showSelection("IC") }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener {
            lifecycleScope.launch{
                submit()
            }
        }
        return binding.root
    }

    private fun showSelection(field: String) {
        imageFor = field
        var items: Array<CharSequence> = arrayOf<CharSequence>("Take Photo", "Chose from photos")
        AlertDialog.Builder(requireContext())
            .setTitle("Change profile photo")
            //.setIcon(R.drawable.ic_select_photo)
            .setSingleChoiceItems(items, 3) { d, n ->
                if (n == 0) {
                    pickImage(n)
                    d?.dismiss()
                } else if(n == 1){
                    pickImage(n)
                    d?.dismiss()
                }else{
                    d?.dismiss()
                }
            }
            .setNegativeButton("Cancel", null).show()    }

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

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val photoURI: Uri? = data.data
                if(imageFor == "IC"){
                   binding.imgFrontIC.setImageURI(photoURI)
               }else{
                    binding.imgSelfie.setImageURI(photoURI)
               }
            }
        } else if (requestCode == CAMERA) {
            if(resultCode != Activity.RESULT_CANCELED){
                val thumbnail = data!!.extras!!["data"] as Bitmap?
                if(imageFor == "IC"){
                    binding.imgFrontIC.setImageBitmap(thumbnail)
                }else{
                    binding.imgSelfie.setImageBitmap(thumbnail)
                }
            }
        }
    }

    private suspend fun submit() {
        val newID = vm.generateID()
        val va = VolunteerApplication(
            id = newID,
            ICFrontPhoto = binding.imgFrontIC.cropToBlob(300, 300),
            ICSelfiePhoto = binding.imgSelfie.cropToBlob(300, 300),
            status = "Pending",
            reason = "",
            date = Date(),
            userID = currentUser?.id!!
        )

        val err = vm.validate(va)
        if( err != ""){
            errorDialog(err)
            return
        }
        vm.set(va)
        reset()
        if(status == "Rejected" && id != ""){
            VOLUNTEERAPPLICATION.document(id).delete()
        }
        Toast.makeText(context, resources.getString(R.string.volunteer_submit_application_success), Toast.LENGTH_SHORT).show()
        returnFragment = true
        returnVAID = newID
        nav.navigateUp()
    }

    private fun reset() {
        binding.imgSelfie.setImageDrawable(null)
        binding.imgFrontIC.setImageDrawable(null)
    }

}