package my.com.hoperise.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObject
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentVolunteerApplicationStatusBinding
import java.text.SimpleDateFormat

class VolunteerApplicationStatusFragment : Fragment() {

    private lateinit var binding: FragmentVolunteerApplicationStatusBinding
    private val nav by lazy { findNavController() }
    private val id by lazy { requireArguments().getString("vaID") ?: "" }
    private val status by lazy { requireArguments().getString("status") ?: "" }
    private val date by lazy { requireArguments().getString("date") ?: "" }
    private val reason by lazy { requireArguments().getString("reason") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVolunteerApplicationStatusBinding.inflate(inflater, container, false)
        requireActivity().title = getString(R.string.volunteer_status)

        if(returnFragment){
            VOLUNTEERAPPLICATION.document(returnVAID).get().addOnSuccessListener {
                    snap ->
                val va = snap.toObject<VolunteerApplication>()!!
                val format = SimpleDateFormat("dd-MM-yyyy")
                printResult(va.status, format.format(va.date), va.reason)
            }
            returnFragment = false
        }else{
            printResult(status, date, reason)

        }

        binding.btnNavigate.setOnClickListener {
            val text = binding.btnNavigate.text.toString()
            if(text == "Go to Event Page"){
                nav.navigate(R.id.eventListingFragment)
            }else if(text == "Back"){
                nav.navigateUp()
            }
            else {
                val args = bundleOf(
                    "status" to status,
                    "id" to id
                )

                nav.navigate(R.id.volunteerSubmitApplicationFragment, args)
            }
        }
        return binding.root
    }

    private fun printResult(status: String, date: String, reason: String) {
        when (status) {
            resources.getString(R.string.approved) -> {

                binding.txtApplicationHeader.text = resources.getString(R.string.your_application_status_is_approved)
                binding.lblVolAppDate.text = date
                binding.lblVolAppStatus.text = resources.getString(R.string.approved)
                binding.lblVolAppStatus.setTextColor(Color.parseColor("#00FF00"))

                binding.lblReason.isVisible = false
                binding.lblVolAppReason.isVisible = false
                binding.txtPrompt.text = resources.getString(R.string.you_can_now_participate_in_any_events_that_you_like)
                binding.btnNavigate.text = resources.getString(R.string.go_to_event_page)

            }
            resources.getString(R.string.pending) -> {
                binding.txtApplicationHeader.text = resources.getString(R.string.your_application_status_is_pending)
                binding.lblVolAppDate.text = date
                binding.lblVolAppStatus.text = resources.getString(R.string.pending)
                binding.lblVolAppStatus.setTextColor(Color.parseColor("#FFA500"))
                binding.lblReason.isVisible = false
                binding.lblVolAppReason.isVisible = false
                binding.txtPrompt.text = resources.getString(R.string.please_wait_patiently)
                binding.btnNavigate.text = resources.getString(R.string.back)

            }
            resources.getString(R.string.rejected) -> {
                binding.txtApplicationHeader.text = resources.getString(R.string.your_application_status_is_rejected)
                binding.lblVolAppDate.text = date
                binding.lblVolAppStatus.text = resources.getString(R.string.rejected)
                binding.lblVolAppStatus.setTextColor(Color.parseColor("#FF0000"))
                binding.lblVolAppReason.text = reason
                binding.txtPrompt.text = resources.getString(R.string.kindly_submit_your_application_once_again)
                binding.btnNavigate.text = resources.getString(R.string.resubmit_application)

            }
            else -> {
                binding.txtApplicationHeader.text = resources.getString(R.string.submit_application_to_be_a_volunteer_now)
                binding.lblVolAppDate.text = resources.getString(R.string.dash)
                binding.lblVolAppStatus.text = resources.getString(R.string.not_available)
                binding.lblReason.isVisible = false
                binding.lblVolAppReason.isVisible = false
                binding.txtPrompt.text = resources.getString(R.string.submit_your_application_now)
                binding.btnNavigate.text = resources.getString(R.string.submit_application)
            }
        }
    }


}