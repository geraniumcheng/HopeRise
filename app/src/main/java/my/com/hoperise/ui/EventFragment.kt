package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentEventBinding


class EventFragment : Fragment() {


    private lateinit var binding: FragmentEventBinding
    private val nav by lazy { findNavController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventBinding.inflate(inflater, container, false)


        binding.button2.setOnClickListener { nav.navigate(R.id.mapsFragment) }
        return binding.root
    }


}