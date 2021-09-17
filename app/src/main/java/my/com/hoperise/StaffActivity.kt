package my.com.hoperise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.model.LatLng
import my.com.hoperise.data.SharedViewModel
import my.com.hoperise.data.cameraPhoto
import my.com.hoperise.data.galleryPhoto
import my.com.hoperise.databinding.ActivityStaffBinding

class StaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.navHost)!!.findNavController() }
    private val vmShared: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(nav)

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
        galleryPhoto = null
        cameraPhoto = null
        vmShared.insertLocation("",  LatLng(0.0,0.0))
        return nav.navigateUp() || super.onSupportNavigateUp()
    }
}