package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentUnblockAccountBinding

class UnblockAccountFragment : Fragment() {
    private lateinit var binding: FragmentUnblockAccountBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUnblockAccountBinding.inflate(inflater, container, false)



        return binding.root
    }


}