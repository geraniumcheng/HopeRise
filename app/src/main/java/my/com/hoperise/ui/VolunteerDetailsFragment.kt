package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.Event
import my.com.hoperise.data.User
import my.com.hoperise.data.VolunteerViewModel
import my.com.hoperise.databinding.FragmentVolunteerDetailsBinding
import my.com.hoperise.databinding.FragmentVolunteerListingBinding
import my.com.hoperise.util.toBitmap
import java.text.SimpleDateFormat
import java.util.*

class VolunteerDetailsFragment : Fragment() {


    private lateinit var binding: FragmentVolunteerDetailsBinding
    private val nav by lazy { findNavController() }
    private val vm: VolunteerViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentVolunteerDetailsBinding.inflate(inflater, container, false)
        load(vm.get(id)!!)

        binding.btnBack.setOnClickListener {
            nav.navigateUp()
        }
        return binding.root
    }

    private fun load(u: User) {

        binding.lblVolID.text = u.id
        binding.lblVolName.text = u.name
        binding.lblVolEmail.text = u.email
        binding.lblVolStatus.text = u.status
        binding.imgVol.setImageBitmap(u.photo?.toBitmap())

    }

}