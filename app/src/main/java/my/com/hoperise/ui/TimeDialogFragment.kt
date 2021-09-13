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
import my.com.hoperise.databinding.FragmentDateDialogBinding
import my.com.hoperise.databinding.FragmentTimeDialogBinding

class TimeDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentTimeDialogBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimeDialogBinding.inflate(inflater, container, false)

        binding.btnConfirm.setOnClickListener { dismiss() }
        binding.timePicker.setOnTimeChangedListener { timePicker, hour, minute ->


            Log.d("time", "$hour:$minute")
            Log.d("time2", timePicker.hour.toString() +":" + timePicker.minute.toString())
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