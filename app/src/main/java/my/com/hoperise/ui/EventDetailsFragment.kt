package my.com.hoperise.ui

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
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentEventBinding
import my.com.hoperise.databinding.FragmentEventDetailsBinding
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsFragment : Fragment() {


    private lateinit var binding: FragmentEventDetailsBinding
    private val nav by lazy { findNavController() }
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val vm: EventViewModel by activityViewModels()
    private var orphanageID = ""
    private var status = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        requireActivity().title = "Event Details"

        if(returnFragment){
            EVENT.document(id).get().addOnSuccessListener {
                    snap ->
                val event = snap.toObject<Event>()!!
                load(event)
            }
            returnFragment = false
        }

        vm.get(id)?.let { load(it) }

        val user = "staff"
        if(user == "staff"){
            binding.btnEditOrJoin.text = "Edit"
        }else{
            binding.btnEditOrJoin.text = "Join"
            binding.btnDeleteEvent.isVisible = false
        }

        if(status == "Completed") binding.btnEditOrJoin.isVisible = false

        binding.btnEditOrJoin.setOnClickListener {
            if(user == "staff"){
//                val fragmentManager: FragmentManager = parentFragmentManager
//                fragmentManager.beginTransaction().remove(this).commit()
//                fragmentManager.popBackStack()
                nav.navigate(R.id.editEventFragment, bundleOf("id" to id))
            }else{
                //nav.navigate() join event
            }
        }

        binding.btnOrphanagePage.setOnClickListener {
            val args = bundleOf(
                "id" to orphanageID,
                "isEvent" to true
            )
            nav.navigate(R.id.orphanageDetailsFragment, args)
        }
        binding.btnDeleteEvent.setOnClickListener { delete() }

        return binding.root
    }

    private fun delete() {
        vm.delete(id)
        nav.navigateUp()
    }

    private fun load(e: Event) {
        orphanageID = e.orphanageID
        binding.lblEventName.text = e.name
        binding.lblEventCategory.text = e.category
        binding.lblEventDesc.text = e.description
        binding.lblEventDateTime.text = e.date + " " +e.time
        binding.lblEventVolunteerNeed.text = e.volunteerRequired.toString()
        binding.lblEventVolunteerCount.text = e.volunteerCount.toString()

        val date1 = SimpleDateFormat("dd-MM-yyyy").parse(e.date)
        if(date1.after(Date())){
            binding.lblEventStatus.text =  "Current"
            status = "Current"
        }else {
            binding.lblEventStatus.text =  "Completed"
            status = "Completed"
        }
    }


}