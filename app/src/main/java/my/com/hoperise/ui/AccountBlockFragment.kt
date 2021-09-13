package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.databinding.FragmentAccountBlockBinding

class AccountBlockFragment : Fragment() {
    private lateinit var binding: FragmentAccountBlockBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountBlockBinding.inflate(inflater, container, false)

        binding.btnSendOtpUnlock.setOnClickListener {
            //sendOtpUnlockAccount()
            nav.navigate(R.id.unblockAccountFragment)
        }
        return binding.root
    }

}