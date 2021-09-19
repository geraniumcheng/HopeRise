package my.com.hoperise.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.ParticipationViewModel
import my.com.hoperise.data.Volunteer
import my.com.hoperise.databinding.FragmentScanAttendanceBinding
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.snackbar
import java.lang.Exception
import my.com.hoperise.util.warningDialog

class ScanAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentScanAttendanceBinding
    private val nav by lazy { findNavController() }
    private val vmParticipation: ParticipationViewModel by activityViewModels()
    private var volunteer : Volunteer? = null

    private val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val content = IntentIntegrator.parseActivityResult(it.resultCode, it.data).contents
        if (content == null) {
            snackbar(getString(R.string.scanFailed))
            nav.navigateUp()
        }
        else {
            try {
                val contentList = content.split("&")
                val userID  = contentList[0]
                val eventID = contentList[1]

                lifecycleScope.launch { volunteer = vmParticipation.get(eventID, userID) }

                when {
                    volunteer == null -> {
                        failedGetRecord()
                    }
                    volunteer!!.attendance -> {
                        binding.btnConfirm.isEnabled     = false
                        load(volunteer!!)
                    }
                    else -> {
                        binding.btnConfirm.isEnabled     = true
                        load(volunteer!!)
                    }
                }
            }
            // in case split failed such as string Hello World provided
            catch (ex: Exception) {
                failedGetRecord()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) scanQRCode()
        else {
            snackbar(getString(R.string.featureCameraUnavailable))
            nav.navigateUp()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.scanAttendance)

        binding = FragmentScanAttendanceBinding.inflate(inflater, container, false)

        reset()

        scanQRCode()

        binding.btnConfirm.setOnClickListener   { takeAttendance() }
        binding.btnScanAgain.setOnClickListener { scanQRCode() }

        return binding.root
    }

    private fun reset() {
        binding.txtUserID.text           = ""
        binding.txtEventID.text          = ""
        binding.txtAttendanceStatus.text = ""
    }

    private fun scanQRCode() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        else {
            val intent = IntentIntegrator.forSupportFragment(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt(getString(R.string.promptQRScan))
                .setBeepEnabled(true)
                .createScanIntent()

            result.launch(intent)
        }
    }

    private fun failedGetRecord() {
        errorDialog(getString(R.string.invalidQR))
        nav.navigateUp()
    }

    private fun load(volunteer: Volunteer) {
        binding.txtUserID.text           = volunteer.userID
        binding.txtEventID.text          = volunteer.eventID
        binding.txtAttendanceStatus.text = volunteer.attendance.toString()
    }

    private fun takeAttendance() {
        val err = vmParticipation.isInEventDuration(volunteer!!)

        if (err != "")
            warningDialog(err, { signedAttendance() }) { snackbar(getString(R.string.cancelAction)) }
        else
            signedAttendance()
    }

    private fun signedAttendance() {
        vmParticipation.update(volunteer!!)
        snackbar(getString(R.string.successTakeAttendance))
        binding.btnConfirm.isEnabled = false
    }
}