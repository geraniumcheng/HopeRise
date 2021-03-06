package my.com.hoperise.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.EventViewModel
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentEventListingBinding
import my.com.hoperise.util.EventAdapter

class EventListingFragment : Fragment() {

    private lateinit var binding: FragmentEventListingBinding
    private val nav by lazy { findNavController() }
    private val vmEvent: EventViewModel by activityViewModels()
    private var role = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEventListingBinding.inflate(inflater, container, false)
        vmEvent.search("")
        vmEvent.filterCategory("")
        requireActivity().title = getString(R.string.eventListing)
        role = currentUser?.role ?: ""

        binding.sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(value: String) = true
            override fun onQueryTextChange(value: String): Boolean {
                vmEvent.search(value)
                return true
            }
        })

        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = binding.spCategory.selectedItem.toString()
                vmEvent.filterCategory(category)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        binding.spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = binding.spStatus.selectedItem.toString()
                vmEvent.filterStatus(status)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        binding.btnID.setOnClickListener { sort("id") }
        binding.btnName.setOnClickListener { sort("name") }
        binding.btnDate.setOnClickListener { sort("date") }
        setHasOptionsMenu(true)

        if(role == getString(R.string.volunteer)){
            binding.spStatus.isVisible = false
            vmEvent.filterStatus(getString(R.string.current))
        }

        val adapter = EventAdapter(){ holder, event ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.eventDetailsFragment, bundleOf("id" to event.id))
            }
        }

        binding.rvEvent.adapter = adapter
        binding.rvEvent.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vmEvent.getAll().observe(viewLifecycleOwner) { event ->
            adapter.submitList(event)
            binding.lblRecord.text = getString(R.string.records, event.size)
        }

        return binding.root
    }

    private fun sort(field: String) {
        val reverse = vmEvent.sort(field)

        binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)

        val res = if (reverse) R.drawable.ic_down else R.drawable.ic_up
        when(field){
            "id" -> binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "name" -> binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "date" -> binding.btnDate.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (role == getString(R.string.volunteer))
            inflater.inflate(R.menu.menu_event_near_me, menu)
        else
            inflater.inflate(R.menu.menu_add, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> nav.navigate(R.id.chooseOrphanageForEventFragment)
            R.id.eventNearMe -> nav.navigate(R.id.eventNearMeFragment)
            else -> return false
        }

        return super.onOptionsItemSelected(item)
    }
}