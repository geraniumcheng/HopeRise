package my.com.hoperise.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentOrphanageDetailsBinding
import my.com.hoperise.util.toBitmap

class OrphanageDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrphanageDetailsBinding
    private val vmOrphanage: OrphanageViewModel by activityViewModels()
    private val vmEvent: EventViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val isEvent by lazy { requireArguments().getBoolean("isEvent")  }
    private val nav by lazy { findNavController() }
    private var geoLocation = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentOrphanageDetailsBinding.inflate(inflater, container, false)

        if(returnFragment){
            ORPHANAGE.document(id).get().addOnSuccessListener {
                    snap ->
                val orph = snap.toObject<Orphanage>()!!
                Log.d("orph", orph.toString())
                load(orph)
            }
            returnFragment = false
        }
        val user = "staff"
        if(user == "user"){
            binding.btnEdit.text = "Back"
            binding.btnDelete.isVisible = false
        }
        Log.d("id", id)
        Log.d("boolean", isEvent.toString())
        if(isEvent){
            ORPHANAGE.document(id).get().addOnSuccessListener {
                snap ->
                val orph = snap.toObject<Orphanage>()!!
                Log.d("orph", orph.toString())
                load(orph)
            }
        }else{
            vmOrphanage.get(id)?.let { load(it) }
        }

        binding.btnEdit.setOnClickListener {

            if(user == "user"){
                nav.navigateUp()
            }else{
//                val fragmentManager: FragmentManager = parentFragmentManager
//                fragmentManager.beginTransaction().remove(this).commit()
//                fragmentManager.popBackStack()
                val args = bundleOf("id" to id)
                nav.navigate(R.id.editOrphanageFragment, args)
            }
        }
        binding.btnDelete.setOnClickListener { delete() }
        binding.imgLocation.setOnClickListener { openMap(geoLocation) }

        return binding.root
    }

    private fun load(o: Orphanage) {
        binding.orpIDDetails.text = o.id
        binding.orpNameDetails.text = o.name
        binding.orpLocationDetail.text = o.location
        binding.orpPhotoDetails.setImageBitmap(o.photo?.toBitmap())
        binding.lblLat.text = o.latitude.toString()
        binding.lblLong.text = o.longitude.toString()
        geoLocation = "geo:0,0?q=" + o.latitude.toString() + "," + o.longitude.toString()
    }

    private fun delete() {
        //delete event as well
        vmEvent.deleteEvents(id)
        vmOrphanage.delete(id)
        nav.navigateUp()
    }

    private fun openMap(location: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location))
        startActivity(intent)
    }

}