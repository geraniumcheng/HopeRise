package my.com.hoperise.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.*
import my.com.hoperise.databinding.FragmentManagerManageApplicationRequestBinding
import my.com.hoperise.util.toBitmap
import java.text.SimpleDateFormat
import java.util.*

class ManagerManageApplicationRequestFragment : Fragment() {
    private lateinit var binding: FragmentManagerManageApplicationRequestBinding
    private val nav by lazy { findNavController() }
    private val vm: VolunteerApplicationViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private var status: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentManagerManageApplicationRequestBinding.inflate(inflater, container, false)

        vm.get(id)?.let { load(it) }
        if(reason != ""){
            binding.lblRejectReason.text = reason
            binding.lblRejectReason.isVisible = true
            binding.btnReject.text = "Confirm Reject"
        }
        if(status != "Pending"){
            binding.btnApprove.isVisible = false
            binding.btnReject.isVisible = false
        }
        binding.btnApprove.setOnClickListener { setStatus("Approved") }
        binding.btnReject.setOnClickListener {
            if(binding.btnReject.text == "Confirm Reject"){
                setStatus("Rejected")
            }else{
                nav.navigate(R.id.rejectReasonFragment)

            }
        }
        return binding.root
    }

    private fun setStatus(item: String) {
        val status = if(item == "Rejected"){
            item.dropLast(2)
        }else{
            item.dropLast(1)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Manage Volunteer Application Request")
            .setMessage("Confirm $status request?" )
            //.setIcon(R.drawable.ic_leave_confirm_dialog)
            .setPositiveButton("Confirm"
            ) { _, _ ->
                if(item == "Rejected"){
                    VOLUNTEERAPPLICATION.document(id).update("status",item, "reason", reason)
                    reason = ""
                    nav.navigateUp()
                }else{
                    VOLUNTEERAPPLICATION.document(id).update("status",item)
                    nav.navigateUp()
                }
            }
            .setNegativeButton("Cancel", null).show()


    }

    private fun load(va: VolunteerApplication) {
        val format = SimpleDateFormat("dd-MM-yyyy")
        binding.lblNameVol.text = va.user.name
        binding.lblDateVolApp.text = format.format(va.date)
        binding.lblVolAppImg.setImageBitmap(va.user.photo?.toBitmap())
        binding.imgFrontIC2.setImageBitmap(va.ICFrontPhoto?.toBitmap())
        binding.imgSelfie2.setImageBitmap(va.ICSelfiePhoto?.toBitmap())
        if(va.status == "Rejected"){
            binding.lblRejectReason.text = va.reason
            binding.lblRejectReason.isVisible = true
        }
        status = va.status

    }
}