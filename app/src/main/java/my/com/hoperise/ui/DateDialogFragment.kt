package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import my.com.hoperise.databinding.FragmentDateDialogBinding
import my.com.hoperise.databinding.FragmentEventListingBinding

class DateDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDateDialogBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDateDialogBinding.inflate(inflater, container, false)


        binding.btnConfirm.setOnClickListener { confirm() }

        return binding.root
    }

    private fun confirm() {

        nav.navigateUp()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

}