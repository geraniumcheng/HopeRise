package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.databinding.FragmentManagerManageApplicationRequestBinding

class ManagerManageApplicationRequestFragment : Fragment() {
    private lateinit var binding: FragmentManagerManageApplicationRequestBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentManagerManageApplicationRequestBinding.inflate(inflater, container, false)


        return binding.root
    }

}