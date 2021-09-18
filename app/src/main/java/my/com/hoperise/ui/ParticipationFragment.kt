package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import my.com.hoperise.R
import my.com.hoperise.data.ParticipationViewModel
import my.com.hoperise.databinding.FragmentParticipationBinding
import my.com.hoperise.util.EventAdapter
import my.com.hoperise.util.hideKeyboard

class ParticipationFragment : Fragment() {

    private lateinit var binding: FragmentParticipationBinding
    private val nav by lazy { findNavController() }
    private val vmParticipation: ParticipationViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.participation)

        binding = FragmentParticipationBinding.inflate(inflater, container, false)

        vmParticipation.search("")
        vmParticipation.filterParticipationStatus("All")

        val adapter = EventAdapter { holder, event ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.eventDetailsFragment, bundleOf("id" to event.id))
            }
        }

        binding.rvEventList.adapter = adapter
        binding.rvEventList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vmParticipation.getEvent().observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            binding.lblRecord.text = getString(R.string.records, events.size)
        }

        binding.btnID.setOnClickListener   { sort("id")   }
        binding.btnName.setOnClickListener { sort("name") }
        binding.btnDate.setOnClickListener { sort("date") }

        binding.svEventParticipation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(value: String) = true
            override fun onQueryTextChange(value: String): Boolean {
                if (value == "") hideKeyboard()
                vmParticipation.search(value)
                return true
            }
        })

        binding.spEventParticipationCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vmParticipation.filterCategory(binding.spEventParticipationCategory.selectedItem.toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        binding.spEventParticipationStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vmParticipation.filterParticipationStatus(binding.spEventParticipationStatus.selectedItem.toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        return binding.root
    }

    private fun sort(field: String) {
        val reverse = vmParticipation.sort(field)

        // remove icon
        binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)

        val res = if (reverse) R.drawable.ic_down else R.drawable.ic_up

        // set icon
        when (field){
            "id"   -> binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "name" -> binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "date" -> binding.btnDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
        }
    }
}