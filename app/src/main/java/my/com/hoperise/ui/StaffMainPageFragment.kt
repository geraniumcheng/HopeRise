package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.RESTORE_DATA
import my.com.hoperise.databinding.FragmentStaffMainPageBinding


class StaffMainPageFragment : Fragment() {
    private lateinit var binding: FragmentStaffMainPageBinding
    private val nav by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStaffMainPageBinding.inflate(inflater, container, false)

        binding.cardEmployee.setOnClickListener { nav.navigate(R.id.accountFragment) }
        binding.cardScanAttendance.setOnClickListener { nav.navigate(R.id.scanAttendanceFragment) }
        binding.cardMaintenance.setOnClickListener { nav.navigate(R.id.eventListingFragment) }
        binding.cardOnScreen.setOnClickListener { nav.navigate(R.id.eventOnScreenReportFragment) }
        binding.cardOrphanage.setOnClickListener { nav.navigate(R.id.orphanageListingFragment) }
        binding.cardStaff.setOnClickListener {  }
        binding.cardVolunteerA.setOnClickListener { nav.navigate(R.id.managerVolunteerApplicationListingFragment) }
        binding.cardVolunteerM.setOnClickListener { nav.navigate(R.id.volunteerListingFragment) }

        binding.btnRestore.setOnClickListener { RESTORE_DATA(requireContext()) }

        return binding.root
    }


}