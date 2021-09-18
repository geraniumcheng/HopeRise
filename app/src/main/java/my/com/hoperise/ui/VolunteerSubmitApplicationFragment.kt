package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.com.hoperise.databinding.FragmentVolunteerSubmitApplicationBinding

class VolunteerSubmitApplicationFragment : Fragment() {
    private lateinit var binding: FragmentVolunteerSubmitApplicationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVolunteerSubmitApplicationBinding.inflate(inflater, container, false)

        return binding.root
    }

}