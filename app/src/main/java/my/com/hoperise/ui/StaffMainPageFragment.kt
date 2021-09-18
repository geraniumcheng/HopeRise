package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.RESTORE_DATA
import my.com.hoperise.databinding.FragmentStaffMainPageBinding


class StaffMainPageFragment : Fragment() {
    private lateinit var binding: FragmentStaffMainPageBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStaffMainPageBinding.inflate(inflater, container, false)

        binding.cardEmployee.setOnClickListener { nav.navigate(R.id.accountFragment) }
        binding.cardScanAttendance.setOnClickListener { nav.navigate(R.id.scanAttendanceFragment) }

        binding.cardEmployee.setOnClickListener { checkEmployeeAccessibility() }
        binding.cardMaintenance.setOnClickListener { nav.navigate(R.id.eventListingFragment) }
        binding.cardOnScreen.setOnClickListener { nav.navigate(R.id.eventOnScreenReportFragment) }
        binding.cardOrphanage.setOnClickListener { nav.navigate(R.id.orphanageListingFragment) }
        binding.cardStaff.setOnClickListener { nav.navigate(R.id.viewStaffProfileFragment) }
        binding.cardVolunteerA.setOnClickListener { nav.navigate(R.id.managerVolunteerApplicationListingFragment) }
        binding.cardVolunteerM.setOnClickListener { nav.navigate(R.id.volunteerListingFragment) }

        binding.btnRestore.setOnClickListener { RESTORE_DATA(requireContext()) }
        // For alert dialog navigation purpose
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getActivity()?.finish()
            }
        })

        return binding.root
    }

    private fun checkEmployeeAccessibility() {
        val userId = (activity as StaffActivity).loggedInId

        lifecycleScope.launch {
            val emp = vm.getLogIn(userId)
            if (emp!!.role.equals("Employee")) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Warning!")
                    .setMessage("You don't have the authority of manage employees! " )
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton("OK", null).show()
            }
            else{
                nav.navigate(R.id.employeeListingFragment)
            }
        }
    }


}