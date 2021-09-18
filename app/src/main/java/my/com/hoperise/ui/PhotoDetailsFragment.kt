package my.com.hoperise.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import my.com.hoperise.R
import my.com.hoperise.data.EventGalleyViewModel
import my.com.hoperise.data.Photo
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentPhotoDetailsBinding
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.snackbar
import my.com.hoperise.util.toBitmap
import my.com.hoperise.util.warningDialog
import java.text.SimpleDateFormat

class PhotoDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPhotoDetailsBinding
    private val nav by lazy { findNavController() }
    private val vmGallery: EventGalleyViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.photoDetails)

        binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)

        val p = vmGallery.get(id)!!

        load(p)

        /* Check is volunteer, or staff with status or not */
        binding.btnDelete.isVisible = !(currentUser!!.role == getString(R.string.volunteer) ||
                (currentUser!!.role != getString(R.string.volunteer) && currentUser!!.status != getString(R.string.active)))

        binding.btnDelete.setOnClickListener { delete() }
        binding.btnSave.setOnClickListener   { saveToPhone(p) }

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun load(p: Photo) {
        binding.txtPhotoID.text   = "  %s".format(p.id)
        binding.txtPhotoDate.text = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(p.date)
        binding.imgPhoto.load(p.photo.toBitmap()) { placeholder(R.drawable.loading_ani) }
    }

    private fun delete(){
        warningDialog(getString(R.string.confirmDeletePhoto), {
            vmGallery.delete(id)
            snackbar(getString(R.string.deletedPhoto))
            nav.navigateUp()
        })
    }

    private fun saveToPhone(p: Photo) {
        try {
            val uri = MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                p.photo.toBitmap(),
                p.id,
                getString(R.string.photo_desc, p.id, p.eventID)
            )
            snackbar(getString(R.string.savedImage, uri))
        }
        catch (e: Exception) {
            e.printStackTrace()
            errorDialog(getString(R.string.failSavePhoto))
        }
    }
}