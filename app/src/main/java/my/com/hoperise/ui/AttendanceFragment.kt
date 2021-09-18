package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.ParticipationViewModel
import my.com.hoperise.data.Volunteer
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentAttendanceBinding
import my.com.hoperise.util.infoDialog
import my.com.hoperise.util.snackbar

class AttendanceFragment : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding
    private val nav by lazy { findNavController() }
    private val vmParticipation: ParticipationViewModel by activityViewModels()
    private val id by lazy { arguments?.getString("id") ?: "" }
    private var content = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.attendance)

        binding = FragmentAttendanceBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            if (vmParticipation.checkVolunteerStatus() != getString(R.string.approved)) {
                infoDialog(getString(R.string.verifyFirst)) { nav.navigate(R.id.volunteerSubmitApplicationFragment) }
                return@launch
            }

            if (id == "") { // get the nearest event
                val v = vmParticipation.getNearest()

                if (v == null) {
                    snackbar(getString(R.string.noJoinEvent))
                    with (nav) {
                        popBackStack(R.id.attendanceFragment, true)
                        navigate(R.id.eventListingFragment)
                    }
                }
                else load(v)
            }
            else            // get the event from eventDetails
                load(vmParticipation.get(id, currentUser!!.id)!!)
        }

        binding.imgQRCode.setOnClickListener { nav.navigate(R.id.eventWholeQRCode, bundleOf("content" to content)) }

        return  binding.root
    }

    private fun generateQRCode(content: String) {
        val bitmap = BarcodeEncoder().encodeBitmap(content, BarcodeFormat.QR_CODE, 450, 450)
        binding.imgQRCode.setImageBitmap(bitmap)
    }

    private fun load(v: Volunteer) {
        binding.txtUserID.text        = currentUser!!.id
        binding.txtUserName.text      = currentUser!!.name
        binding.txtEventID.text       = v.eventID
        binding.txtEventName.text     = v.event.name
        binding.txtEventDateTime.text = getString(R.string.eventDateTime, v.event.date, v.event.time)

        content = currentUser!!.id + "&" + v.eventID
        generateQRCode(content)
    }
}