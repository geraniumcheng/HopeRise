package my.com.hoperise.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import my.com.hoperise.LoginActivity
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.UserViewModel
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


        binding.cardEmployee.setOnClickListener { checkEmployeeAccessibility() }
        binding.cardEventHistory.setOnClickListener {  }
        binding.cardMaintenance.setOnClickListener { nav.navigate(R.id.eventListingFragment) }
        binding.cardOnScreen.setOnClickListener {  }
        binding.cardOrphanage.setOnClickListener { nav.navigate(R.id.orphanageListingFragment) }
        binding.cardStaff.setOnClickListener { nav.navigate(R.id.viewStaffProfileFragment) }
        binding.cardVolunteerA.setOnClickListener { nav.navigate(R.id.managerVolunteerApplicationListingFragment) }
        binding.cardVolunteerM.setOnClickListener { nav.navigate(R.id.volunteerListingFragment) }

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