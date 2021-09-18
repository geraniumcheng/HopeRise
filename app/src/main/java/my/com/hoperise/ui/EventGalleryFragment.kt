package my.com.hoperise.ui

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.Blob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.EventGalleyViewModel
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentEventGalleryBinding
import my.com.hoperise.util.*
import java.lang.Exception
import android.provider.MediaStore
import android.view.*

class EventGalleryFragment : Fragment() {

    private lateinit var binding: FragmentEventGalleryBinding
    private val nav by lazy { findNavController() }
    private val vmGallery: EventGalleyViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val clipData = it.data?.clipData
            var imageUri: Uri

            // multiple photos
            if (clipData != null) {
                val count = clipData.itemCount

                lifecycleScope.launch {
                    for (i in 0 until count) {
                        imageUri = clipData.getItemAt(i).uri

                        uploadPhoto(imageUri)
                        delay(1000)
                    }
                }
            }
            // single photo
            else {
                imageUri = it.data?.data!!
                uploadPhoto(imageUri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) addPhoto() else snackbar(getString(R.string.featureFileUnavailable))
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != RESULT_CANCELED) {
            val thumbnail = it.data!!.extras!!["data"] as Bitmap
            uploadPhoto(blob = thumbnail.toBlob())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.eventGallery)

        binding = FragmentEventGalleryBinding.inflate(inflater, container, false)

        vmGallery.setEventID(id)

        if (currentUser!!.role == getString(R.string.volunteer) || currentUser!!.status != getString(R.string.active))
            manageButtonVisibility(false)
        else
            manageButtonVisibility(true)

        val adapter = EventGalleryAdapter { holder, image ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.photoDetailsFragment, bundleOf("id" to image.id))
            }
        }

        binding.rvGallery.adapter = adapter

        vmGallery.getPhotos().observe(viewLifecycleOwner) { photos ->
            adapter.submitList(photos)

            val size = photos.size
            binding.lblRecord.text = getString(R.string.records, size)

            if (currentUser!!.role != getString(R.string.volunteer) && currentUser!!.status == getString(R.string.active))
                binding.btnFloatDelete.isVisible = size != 0
        }

        binding.btnFloatDelete.setOnClickListener { deleteAll() }
        binding.btnFloatAdd.setOnClickListener { addPhoto() }

        return binding.root
    }

    private fun manageButtonVisibility(condition: Boolean) {
        binding.btnFloatAdd.isVisible    = condition
        binding.btnFloatDelete.isVisible = condition
    }

    private fun deleteAll() {
        warningDialog(getString(R.string.promptDeletePhotoConfirm), {
            vmGallery.deleteAll(id)
            snackbar(getString(R.string.deletedPhoto))
            nav.navigateUp()
        }, {
            snackbar(getString(R.string.cancelAction))
        })
    }

    private fun addPhoto() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        else {
            showPhotoSelection(getString(R.string.uploadToGallery),
                { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) }, {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = "image/*"
                    galleryLauncher.launch(intent)
                })
        }
    }

    private fun uploadPhoto(imageUri: Uri = Uri.parse(""), blob: Blob = Blob.fromBytes("".toByteArray())) {
        var fileName     = ""
        var blobToUpload = blob

        if (imageUri.toString() != "") {
            fileName = getFileName(imageUri)
            blobToUpload = uriToBitmapToBlob(imageUri)
        }

        vmGallery.upload(blobToUpload)
        snackbar(getString(R.string.photoUploaded, fileName))
    }

    /**
     * refer to https://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework
     */
    private fun getFileName(imageUri: Uri): String {
        var filePath = ""

        // get the second item (46) of image:46
        val selArg     = imageUri.lastPathSegment!!.split(":").toTypedArray()[1]
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection  = MediaStore.Images.Media._ID + "=?"

        val cursor     = requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, arrayOf(selArg), null
        )!!

        // cursor.moveToFirst() return true if the cursor is not empty then move to the first element
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(projection[0])

            filePath = cursor.getString(columnIndex)
        }

        cursor.close()

        //Example path =  /storage/emulated/0/Pictures/PH0002.jpg
        return filePath.split("/").last()
    }

    private fun uriToBitmapToBlob(uri: Uri): Blob {
        var bitmap: Bitmap? = null

        try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
        catch (e: Exception) {
            e.printStackTrace()
            errorDialog(getString(R.string.convertFailed))
        }

        return bitmap?.crop(350, 350)?.toBlob() ?: Blob.fromBytes(ByteArray(0))
    }
}