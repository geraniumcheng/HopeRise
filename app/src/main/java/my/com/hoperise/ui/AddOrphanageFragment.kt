package my.com.hoperise.ui

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentAddOrphanageBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog

class AddOrphanageFragment : Fragment() {

    private lateinit var binding: FragmentAddOrphanageBinding
    private val nav by lazy { findNavController() }
    private val vmShared: SharedViewModel by activityViewModels()
    private val vmOrphanage: OrphanageViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddOrphanageBinding.inflate(inflater, container, false)

        vmShared.getSetLocation()
        if(vmShared.location.location != ""){
            binding.edtLocation.text = vmShared.location.location
        }
        if(galleryPhoto != null || cameraPhoto != null){
            if(galleryPhoto != null){
                binding.orpPhoto.setImageURI(galleryPhoto)
            }
            else{
                binding.orpPhoto.setImageBitmap(cameraPhoto)
            }
        }
        binding.orpPhoto.setOnClickListener { showSelection() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }
        binding.btnLocation.setOnClickListener {
            val args = bundleOf("location" to "")
            nav.navigate(R.id.mapsFragment, args)
        }
        return binding.root
    }

    private fun showSelection() {
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
                binding.orpPhoto.setImageURI(photoURI)
                galleryPhoto = photoURI
                cameraPhoto = null
            }
        } else if (requestCode == CAMERA) {
            if(resultCode != RESULT_CANCELED){
                val thumbnail = data!!.extras!!["data"] as Bitmap?
                binding.orpPhoto.setImageBitmap(thumbnail)
                cameraPhoto = thumbnail
                galleryPhoto = null
            }
        }
    }

    private fun reset() {
        binding.edtName.text.clear()
        binding.edtName.requestFocus()
        binding.orpPhoto.setImageDrawable(null)
        binding.edtLocation.text  = ""
        galleryPhoto = null
        cameraPhoto = null
        vmShared.insertLocation("",  LatLng(0.0,0.0))
    }

    private fun submit() {
        val o = Orphanage(
            id = vmOrphanage.generateID(),
            name = binding.edtName.text.toString().trim(),
            location = vmShared.location.location,
            latitude = vmShared.location.latitude,
            longitude = vmShared.location.longitude,
            photo = binding.orpPhoto.cropToBlob(300, 300),
        )
        val err = vmOrphanage.validate(o)
        if( err != ""){
            errorDialog(err)
            return
        }
        vmOrphanage.set(o)
        reset()
        nav.navigateUp()
    }

}