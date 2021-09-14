package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentEmployeeVerifyOtpBinding

class EmployeeVerifyOtpFragment : Fragment() {
    private lateinit var binding: FragmentEmployeeVerifyOtpBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEmployeeVerifyOtpBinding.inflate(inflater, container, false)


        return binding.root
    }
}