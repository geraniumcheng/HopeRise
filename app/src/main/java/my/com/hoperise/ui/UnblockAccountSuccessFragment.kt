package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentUnblockAccountSuccessBinding

class UnblockAccountSuccessFragment : Fragment() {
    private lateinit var binding: FragmentUnblockAccountSuccessBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUnblockAccountSuccessBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener { nav.navigate(R.id.loginFragment) }

        // For prevent back press error happen
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                nav.popBackStack(R.id.loginFragment, false)
            }
        })

        return binding.root
    }

}