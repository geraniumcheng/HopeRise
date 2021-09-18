package my.com.hoperise.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
import my.com.hoperise.data.VOLUNTEERAPPLICATION
import my.com.hoperise.data.VolunteerApplication
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentAccountBinding
import java.text.SimpleDateFormat

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val nav by lazy { findNavController() }
    private var status = ""
    private var date = ""
    private var reason = ""
    private var vaID = ""

    private var application = VolunteerApplication()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            val volApp = VOLUNTEERAPPLICATION.whereEqualTo("userID", currentUser?.id!!).get().await().toObjects<VolunteerApplication>()
            val format = SimpleDateFormat("dd-MM-yyyy")
            if(volApp.isNotEmpty()){
                application = volApp[0]
                vaID = application.id
                status = application.status
                date = format.format(application.date)
                reason = application.reason
            }
        }

        binding.button.setOnClickListener {
            val intent = Intent(activity, StaffActivity::class.java)
            activity?.startActivity(intent)
        }
        binding.btnVolunteerApplication.setOnClickListener {
            val args = bundleOf(
                "vaID" to vaID,
                "status" to status,
                "date" to date,
                "reason" to reason
            )
            nav.navigate(R.id.volunteerApplicationStatusFragment, args)
        }

        return binding.root
    }



}