package my.com.hoperise.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObject
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentEventDetailsBinding
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

        if(returnFragment){
            VOLUNTEERAPPLICATION.document(returnVAID).get().addOnSuccessListener {
                    snap ->
                val va = snap.toObject<VolunteerApplication>()!!
                Log.d("check", va.toString())
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
//                val fragmentManager: FragmentManager = parentFragmentManager
//                fragmentManager.beginTransaction().remove(this).commit()
//                fragmentManager.popBackStack()
                nav.navigate(R.id.volunteerSubmitApplicationFragment, args)
            }
        }
        return binding.root
    }

    private fun printResult(status: String, date: String, reason: String) {
        Log.d("status", status)
        if(status == "Approved"){
            binding.txtApplicationHeader.text = "You application status is Approved!"
            binding.lblVolAppDate.text = date
            binding.lblVolAppStatus.text = "Approved"
            binding.lblVolAppStatus.setTextColor(Color.parseColor("#00FF00"))

            binding.lblReason.isVisible = false
            binding.lblVolAppReason.isVisible = false
            binding.txtPrompt.text = "You can now participate in any events that you like!"
            binding.btnNavigate.text = "Go to Event Page"

        }else if(status == "Pending"){
            binding.txtApplicationHeader.text = "You application status is Pending!"
            binding.lblVolAppDate.text = date
            binding.lblVolAppStatus.text = "Pending"
            binding.lblVolAppStatus.setTextColor(Color.parseColor("#FFA500"))
            binding.lblReason.isVisible = false
            binding.lblVolAppReason.isVisible = false
            binding.txtPrompt.text = "Please wait patiently as the staff will update your status within 2 business days of your submission date! "
            binding.btnNavigate.text = "Back"

        }else if(status == "Rejected"){
            binding.txtApplicationHeader.text = "You application status is Rejected!"
            binding.lblVolAppDate.text = date
            binding.lblVolAppStatus.text = "Rejected"
            binding.lblVolAppStatus.setTextColor(Color.parseColor("#FF0000"))
            binding.lblVolAppReason.text = reason
            binding.txtPrompt.text = "Kindly resubmit your application once again to participate in any events!"
            binding.btnNavigate.text = "Resubmit Application"

        }else{
            binding.txtApplicationHeader.text = "Submit application to be a volunteer now!"
            binding.lblVolAppDate.text = "-"
            binding.lblVolAppStatus.text = "N/A"
            binding.lblReason.isVisible = false
            binding.lblVolAppReason.isVisible = false
            binding.txtPrompt.text = "Submit your application now to participate in any events!"
            binding.btnNavigate.text = "Submit Application"

        }
    }


}