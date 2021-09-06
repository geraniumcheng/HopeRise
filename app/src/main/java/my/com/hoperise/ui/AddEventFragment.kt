package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.hoperise.databinding.FragmentAddEventBinding

class AddEventFragment : Fragment() {

    private lateinit var binding: FragmentAddEventBinding
    private val nav by lazy { findNavController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEventBinding.inflate(inflater, container, false)

       binding.btnDate.setOnClickListener {
           var dialog = DateDialogFragment()
           val fm =requireFragmentManager()
           dialog.show(fm, "Date")
       }
       binding.btnTime.setOnClickListener {
           var dialog = TimeDialogFragment()
           val fm =requireFragmentManager()
           dialog.show(fm, "Time")
       }

        return binding.root
    }

}