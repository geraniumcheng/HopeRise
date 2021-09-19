package my.com.hoperise.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentEventDetailsBinding
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.infoDialog
import my.com.hoperise.util.snackbar
import java.util.*
import my.com.hoperise.util.getEventEndTime
import my.com.hoperise.util.parseEventDateTime

class EventDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailsBinding
    private val nav by lazy { findNavController() }
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val vmEvent: EventViewModel by activityViewModels()
    private val vmParticipation: ParticipationViewModel by activityViewModels()
    private var orphanageID = ""
    private var status      = ""
    private var role        = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.eventDetails)

        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)

        role = currentUser?.role ?: ""

        vmEvent.filterStatus("All")

        if(returnFragment){
            EVENT.document(id).get().addOnSuccessListener {
                    snap ->
                val event = snap.toObject<Event>()!!
                load(event)
            }
            returnFragment = false
        }

        vmEvent.get(id)?.let { load(it) }

        eventExtraBtnVisibility(true)

        if (role != getString(R.string.volunteer)) {
            binding.btnEditOrJoin.text = getString(R.string.edit)

            if (currentUser!!.status != getString(R.string.active))
                eventExtraBtnVisibility(false)
        }
        else {
            binding.btnEditOrJoin.text = getString(R.string.join)
            binding.btnDeleteEvent.isVisible = false

            lifecycleScope.launch {
                val isExist = vmParticipation.isExist(id)

                if (isExist) binding.btnEditOrJoin.isVisible = false
                else eventExtraBtnVisibility(false)
            }
        }

        if (status == getString(R.string.completed) || (role != getString(R.string.volunteer) && currentUser!!.status != getString(R.string.active))) {
            binding.btnEditOrJoin.isVisible  = false
            binding.btnDeleteEvent.isVisible = false
        }

        binding.btnEditOrJoin.setOnClickListener {

            lifecycleScope.launch {
                if (role != getString(R.string.volunteer) && currentUser!!.status == getString(R.string.active))
                    nav.navigate(R.id.editEventFragment, bundleOf("id" to id))
                else if (role == getString(R.string.volunteer) && vmParticipation.checkVolunteerStatus() == getString(R.string.approved))
                    joinEvent()
                else
                    infoDialog(getString(R.string.verifyFirst)) { nav.navigate(R.id.viewVolunteerProfileFragment) }
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

        binding.btnEventScan.setOnClickListener {

            nav.popBackStack(R.id.eventDetailsFragment, true)

            if (role == getString(R.string.volunteer))
                nav.navigate(R.id.attendanceFragment, bundleOf("id" to id))
            else
                nav.navigate(R.id.scanAttendanceFragment)
        }

        binding.btnEventVolunteer.setOnClickListener { nav.navigate(R.id.eventVolunteerListingFragment, bundleOf("id" to id)) }
        binding.btnEventGallery.setOnClickListener   { nav.navigate(R.id.eventGalleryFragment, bundleOf("id" to id)) }
        binding.btnEventChatRoom.setOnClickListener  { nav.navigate(R.id.eventChatRoom, bundleOf("id" to id)) }

        return binding.root
    }

    private fun delete() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Confirm delete event?" )
            //.setIcon(R.drawable.ic_leave_confirm_dialog)
            .setPositiveButton("Confirm"
            ) { _, _ -> confirm() }
            .setNegativeButton("Cancel", null).show()
    }

    private fun confirm() {
        vmEvent.delete(id)
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
//        binding.lblEventStatus.text = e.status

        val eventDate    = parseEventDateTime(e)
        val eventEndDate = getEventEndTime(eventDate)
        if(eventEndDate.after(Date()) || eventDate.after(Date())){
            binding.lblEventStatus.text = getString(R.string.current)
            status = getString(R.string.current)
        }else {
            binding.lblEventStatus.text = getString(R.string.completed)
            status = getString(R.string.completed)
        }
    }

    private fun eventExtraBtnVisibility(condition: Boolean) {
        binding.btnEventVolunteer.isVisible = condition
        binding.btnEventGallery.isVisible   = condition
        binding.btnEventChatRoom.isVisible  = condition
        binding.btnEventScan.isVisible      = condition
        binding.lblVolunteerList.isVisible  = condition
        binding.lblGallery.isVisible        = condition
        binding.lblChatRoom.isVisible       = condition
        binding.lblQR.isVisible             = condition
    }

    private fun joinEvent() {
        // 1. if the user had joined the event, the join btn will not appear
        // 2. if the dateTime crashed, the error message return
        // 3. if the event's volunteer full, the error message return
        val err = vmParticipation.validate(vmEvent.get(id)!!)

        if (err != "") {
            errorDialog(err)
            return
        }

        vmParticipation.set(
            Volunteer(
                userID  = currentUser!!.id,
                eventID = id,
        ))

        snackbar(getString(R.string.msgJoinedSuccess, id))

        nav.popBackStack(R.id.eventDetailsFragment, true)
        nav.navigate(R.id.participationFragment)
    }
}