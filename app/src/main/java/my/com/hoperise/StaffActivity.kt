package my.com.hoperise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
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
    lateinit var loggedInId: String
    private val vmShared: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(nav)

        // Pass login user's data (Method 2)
        loggedInId = intent.getStringExtra("loggedInId") ?:""
    }

    override fun onSupportNavigateUp(): Boolean {
        galleryPhoto = null
        cameraPhoto  = null
        vmShared.insertLocation("",  LatLng(0.0,0.0))
        return nav.navigateUp() || super.onSupportNavigateUp()
    }
}