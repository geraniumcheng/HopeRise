package my.com.hoperise.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.RESTORE_DATA
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentStaffMainPageBinding
import my.com.hoperise.util.infoDialog
import my.com.hoperise.util.snackbar
import my.com.hoperise.util.warningDialog

class StaffMainPageFragment : Fragment() {
    private lateinit var binding: FragmentStaffMainPageBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStaffMainPageBinding.inflate(inflater, container, false)

        binding.cardScanAttendance.setOnClickListener { nav.navigate(R.id.scanAttendanceFragment) }
        binding.cardEmployee.setOnClickListener { checkEmployeeAccessibility() }
        binding.cardMaintenance.setOnClickListener { nav.navigate(R.id.eventListingFragment) }
        binding.cardOnScreen.setOnClickListener { accessToReport() }
        binding.cardOrphanage.setOnClickListener { nav.navigate(R.id.orphanageListingFragment) }
        binding.cardStaff.setOnClickListener { nav.navigate(R.id.viewStaffProfileFragment) }
        binding.cardVolunteerA.setOnClickListener { nav.navigate(R.id.managerVolunteerApplicationListingFragment) }
        binding.cardVolunteerM.setOnClickListener { nav.navigate(R.id.volunteerListingFragment) }

        binding.btnRestore.setOnClickListener {
            RESTORE_DATA(requireContext())
            snackbar(getString(R.string.dataRestoreSucess))
        }

        return binding.root
    }

    private fun checkEmployeeAccessibility() {
        val userId = (activity as StaffActivity).loggedInId

        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)
            if (emp!!.role == getString(R.string.employee)) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.noAuthority))
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(getString(R.string.ok), null).show()
            }
            else
                nav.navigate(R.id.employeeListingFragment)
        }
    }

    private fun accessToReport() {
        // if not active manager has not right to view report
        if (currentUser!!.role != getString(R.string.manager) || currentUser!!.status != getString(R.string.active)) {
            warningDialog(getString(R.string.reportAccessWarning)) {
                nav.navigateUp()
            }
        }
        else
            nav.navigate(R.id.eventOnScreenReportFragment)
    }
}