package my.com.hoperise.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentEditOrphanageBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.toBitmap

class EditOrphanageFragment : Fragment() {

    private lateinit var binding: FragmentEditOrphanageBinding
    private val nav by lazy { findNavController() }
    private val vmOrphanage: OrphanageViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id", "") }
    private val vmShared: SharedViewModel by activityViewModels()

    var latitude = 0.0
    var longitude = 0.0
    var loc = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEditOrphanageBinding.inflate(inflater, container, false)

        reset()
        vmShared.getSetLocation()
        if(vmShared.location.location != ""){
            binding.lblOrpLocation.text = vmShared.location.location
        }
        if(galleryPhoto != null || cameraPhoto != null){
            if(galleryPhoto != null){
                binding.orpPhotoListing.setImageURI(galleryPhoto)
            }
            else{
                binding.orpPhotoListing.setImageBitmap(cameraPhoto)
            }
        }
        binding.orpPhotoListing.setOnClickListener { showSelection() }
        binding.btnMap.setOnClickListener {
            val args = bundleOf(
                "location" to vmOrphanage.get(id)!!.location
            )
            nav.navigate(R.id.mapsFragment, args)
        }
        binding.btnReset.setOnClickListener {
            galleryPhoto = null
            cameraPhoto = null
            vmShared.insertLocation("",  LatLng(0.0,0.0))
            reset()
        }
        binding.btnConfirm.setOnClickListener { submit() }
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
                binding.orpPhotoListing.setImageURI(photoURI)
                galleryPhoto = photoURI
                cameraPhoto = null
            }
        } else if (requestCode == CAMERA) {
            if(resultCode != Activity.RESULT_CANCELED){
                val thumbnail = data!!.extras!!["data"] as Bitmap?
                binding.orpPhotoListing.setImageBitmap(thumbnail)
                cameraPhoto = thumbnail
                galleryPhoto = null
            }
        }
    }

    private fun reset(){
        val o = vmOrphanage.get(id)
        if(o == null){
            nav.navigateUp()
            return
        }
        load(o)
    }

    private fun load(o: Orphanage) {
        binding.orpIDListing.text = o.id
        binding.edtOrpName.setText(o.name)
        binding.lblOrpLocation.text = o.location
        binding.orpPhotoListing.setImageBitmap(o.photo?.toBitmap())
        binding.edtOrpName.requestFocus()
        latitude = o.latitude
        longitude = o.longitude
        loc = o.location
    }

    private fun submit(){
        var lat = 0.0
        var long = 0.0
        var newLoc = ""
        if(vmShared.location.location != ""){
            lat = vmShared.location.latitude
            long = vmShared.location.longitude
            newLoc = vmShared.location.location
        }else{
            lat = latitude
            long = longitude
            newLoc = loc
        }
        val o = Orphanage(
            id = id,
            name = binding.edtOrpName.text.toString().trim(),
            location = newLoc,
            latitude = lat,
            longitude = long,
            photo = binding.orpPhotoListing.cropToBlob(300, 300),
        )
        val err = vmOrphanage.validate(o)
        if( err != ""){
            errorDialog(err)
            return
        }
        vmOrphanage.set(o)
        galleryPhoto = null
        cameraPhoto = null
        vmShared.insertLocation("",  LatLng(0.0,0.0))
        Toast.makeText(context, "Orphanage updated successfully", Toast.LENGTH_SHORT).show()
        returnFragment = true
        nav.navigateUp()
    }

}