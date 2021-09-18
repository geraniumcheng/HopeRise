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
import my.com.hoperise.data.VolunteerViewModel
import my.com.hoperise.databinding.FragmentOrphanageListingBinding
import my.com.hoperise.databinding.FragmentVolunteerListingBinding
import my.com.hoperise.util.VolunteerAdapter

class VolunteerListingFragment : Fragment() {

    private lateinit var binding: FragmentVolunteerListingBinding
    private val nav by lazy { findNavController() }
    private val vm: VolunteerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentVolunteerListingBinding.inflate(inflater, container, false)

        val adapter = VolunteerAdapter(){
            holder, volunteer -> holder.root.setOnClickListener {
            nav.navigate(R.id.volunteerDetailsFragment, bundleOf("id" to volunteer.id,))
        }
        }

        binding.rvVol.adapter = adapter
        binding.rvVol.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getAll().observe(viewLifecycleOwner){
                volunteer -> adapter.submitList(volunteer)
            binding.lblVolRecord.text = volunteer.size.toString() + " record(s)"
        }

        binding.svVol.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(value: String) = true
            override fun onQueryTextChange(value: String): Boolean {
                vm.search(value)
                return true
            }
        })

        binding.spnVolStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = binding.spnVolStatus.selectedItem.toString()
                vm.filterStatus(status)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        binding.btnID.setOnClickListener { sort("id") }
        binding.btnName.setOnClickListener { sort("name") }

        return binding.root
    }

    private fun sort(field: String) {
        val reverse = vm.sort(field)

        binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)

        val res = if (reverse) R.drawable.ic_down else R.drawable.ic_up
        when(field){
            "id" -> binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "name" -> binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
        }

    }

}