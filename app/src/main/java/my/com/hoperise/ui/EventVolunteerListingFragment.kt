package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import my.com.hoperise.R
import my.com.hoperise.data.EventVolunteerViewModel
import my.com.hoperise.databinding.FragmentEventVolunteerListingBinding
import my.com.hoperise.util.EventVolunteerAdapter
import my.com.hoperise.util.hideKeyboard

class EventVolunteerListingFragment : Fragment() {

    private lateinit var binding: FragmentEventVolunteerListingBinding
    private val vmEventVolunteer: EventVolunteerViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.eventVolunteerList)
        binding = FragmentEventVolunteerListingBinding.inflate(inflater, container, false)

        vmEventVolunteer.setEventID(id)
        vmEventVolunteer.search("")

        binding.txtEventID.text = id

        val adapter = EventVolunteerAdapter()

        binding.rvEventVolunteerList.adapter = adapter
        binding.rvEventVolunteerList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vmEventVolunteer.getUser().observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
            binding.lblRecord.text = getString(R.string.records, users.size)
        }

        binding.btnID.setOnClickListener   { sort("id")   }
        binding.btnName.setOnClickListener { sort("name") }

        binding.svEventVolunteer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(value: String) = true
            override fun onQueryTextChange(value: String): Boolean {
                if (value == "") hideKeyboard()
                vmEventVolunteer.search(value)
                return true
            }
        })

        return  binding.root
    }

    private fun sort(field: String) {
        val reverse = vmEventVolunteer.sort(field)

        binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)

        val res = if (reverse) R.drawable.ic_down else R.drawable.ic_up

        when (field){
            "id"   -> binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "name" -> binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
        }
    }
}