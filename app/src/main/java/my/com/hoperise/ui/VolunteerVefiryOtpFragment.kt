package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentVolunteerVefiryOtpBinding


class VolunteerVefiryOtpFragment : Fragment() {
    private lateinit var binding: FragmentVolunteerVefiryOtpBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVolunteerVefiryOtpBinding.inflate(inflater, container, false)

        requireActivity().title = "Verify OTP"

        return binding.root
    }


}