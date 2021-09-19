package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import my.com.hoperise.R
import my.com.hoperise.data.VolunteerApplicationViewModel
import my.com.hoperise.databinding.FragmentManagerVolunteerApplicationListingBinding
import my.com.hoperise.util.VolunteerApplicationAdapter

class ManagerVolunteerApplicationListingFragment : Fragment() {

    private lateinit var binding: FragmentManagerVolunteerApplicationListingBinding
    private val nav by lazy { findNavController() }
    private val vm: VolunteerApplicationViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentManagerVolunteerApplicationListingBinding.inflate(inflater, container, false)

        binding.spnFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = binding.spnFilter.selectedItem.toString()
                vm.filterStatus(status)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
        binding.btnDate.setOnClickListener { sort("date") }
        binding.btnName.setOnClickListener { sort("name") }

        val adapter = VolunteerApplicationAdapter(){
                holder, va -> holder.root.setOnClickListener {
            nav.navigate(R.id.managerManageApplicationRequestFragment, bundleOf("id" to va.id))
        }
        }

        binding.rvVolunteerApplication.adapter = adapter
        binding.rvVolunteerApplication.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getAll().observe(viewLifecycleOwner){
                va -> adapter.submitList(va)
            binding.lblRecords.text = va.size.toString() + " record(s)"
        }

        return binding.root
    }

    private fun sort(field: String) {
        val reverse = vm.sort(field)

        binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)

        val res = if (reverse) R.drawable.ic_down else R.drawable.ic_up
        when(field){
            "name" -> binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "date" -> binding.btnDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
        }
    }


}