package my.com.hoperise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import my.com.hoperise.databinding.ActivityStaffBinding

class StaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.navHost)!!.findNavController() }
    lateinit var loggedInId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(nav)

        // Pass login user's data (Method 2)
        loggedInId = intent.getStringExtra("loggedInId") ?:""

//        binding.cardEmployee.setOnClickListener {  }
//        binding.cardEventHistory.setOnClickListener {  }
//        binding.cardMaintenance.setOnClickListener { nav.navigate(R.id.eventListingFragment) }
//        binding.cardOnScreen.setOnClickListener {  }
//        binding.cardOrphanage.setOnClickListener { nav.navigate(R.id.orphanageListingFragment) }
//        binding.cardStaff.setOnClickListener {  }
//        binding.cardVolunteerA.setOnClickListener { nav.navigate(R.id.managerVolunteerApplicationListingFragment) }
//        binding.cardVolunteerM.setOnClickListener { nav.navigate(R.id.volunteerListingFragment) }

    }

    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp() || super.onSupportNavigateUp()
    }

}