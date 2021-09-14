package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentEmployeeForgetPasswordBinding

class EmployeeForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentEmployeeForgetPasswordBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEmployeeForgetPasswordBinding.inflate(inflater, container, false)


        return binding.root
    }


}