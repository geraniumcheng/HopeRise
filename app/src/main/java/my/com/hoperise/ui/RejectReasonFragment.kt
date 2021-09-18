package my.com.hoperise.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.reason
import my.com.hoperise.databinding.FragmentRejectReasonBinding

class RejectReasonFragment : DialogFragment() {

    private lateinit var binding: FragmentRejectReasonBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRejectReasonBinding.inflate(inflater, container, false)

        binding.btnConfirmReject.setOnClickListener {
            reason = binding.spnReject.selectedItem.toString()
            Log.d("reason", reason)
            nav.navigateUp()
        }
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}