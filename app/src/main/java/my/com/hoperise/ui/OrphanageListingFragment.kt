package my.com.hoperise.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import my.com.hoperise.R
import my.com.hoperise.data.OrphanageViewModel
import my.com.hoperise.databinding.FragmentOrphanageListingBinding
import my.com.hoperise.util.OrphanageAdapter

class OrphanageListingFragment : Fragment() {

    private lateinit var binding: FragmentOrphanageListingBinding
    private val nav by lazy { findNavController() }

    private val vm: OrphanageViewModel by activityViewModels()
    //private lateinit var adapter: OrphanageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentOrphanageListingBinding.inflate(inflater, container, false)
        //sort("id")
        setHasOptionsMenu(true)

        val adapter = OrphanageAdapter(){
            holder, orphanage -> holder.root.setOnClickListener {
            val args = bundleOf(
                "id" to orphanage.id,
                "isEvent" to false
            )
                nav.navigate(R.id.orphanageDetailsFragment, args)
        }
        }
        binding.rvOrphanage.adapter = adapter
        binding.rvOrphanage.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vm.getAll().observe(viewLifecycleOwner){
            orphanage -> adapter.submitList(orphanage)
            binding.edtRecord.text = orphanage.size.toString() + " record(s)"
        }

        binding.btnID.setOnClickListener { sort("id") }
        binding.btnName.setOnClickListener { sort("name") }


        return binding.root
    }

    private fun sort(s: String) {
        val reverse = vm.sort(s)

        binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)

        val res = if (reverse) R.drawable.ic_down else R.drawable.ic_up
        when(s){
            "id" -> binding.btnID.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
            "name" -> binding.btnName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,res,0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //menu.clear()
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add){
            nav.navigate(R.id.addOrphanageFragment)
        }
        else
            return false
        return super.onOptionsItemSelected(item)
    }
}