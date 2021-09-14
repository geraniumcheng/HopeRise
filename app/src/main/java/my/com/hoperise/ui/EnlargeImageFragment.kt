package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.R
import my.com.hoperise.data.EventViewModel
import my.com.hoperise.databinding.FragmentEnlargeImageBinding
import my.com.hoperise.databinding.FragmentEventDetailsBinding


class EnlargeImageFragment : DialogFragment() {

    private lateinit var binding: FragmentEnlargeImageBinding
    private val nav by lazy { findNavController() }
    private val id by lazy { requireArguments().getString("id") ?: "" }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEnlargeImageBinding.inflate(inflater, container, false)

        return binding.root
    }

}