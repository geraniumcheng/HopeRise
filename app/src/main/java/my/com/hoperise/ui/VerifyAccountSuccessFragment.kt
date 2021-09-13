package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentVerifyAccountSuccessBinding

class VerifyAccountSuccessFragment : Fragment() {
    private lateinit var binding: FragmentVerifyAccountSuccessBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVerifyAccountSuccessBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            nav.navigate(R.id.loginFragment)
        }

        return binding.root
    }

}