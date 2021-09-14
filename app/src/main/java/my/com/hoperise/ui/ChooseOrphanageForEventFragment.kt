package my.com.hoperise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import my.com.hoperise.R
import my.com.hoperise.data.EventViewModel
import my.com.hoperise.data.OrphanageViewModel
import my.com.hoperise.databinding.FragmentChooseOrphanageForEventBinding
import my.com.hoperise.databinding.FragmentDateDialogBinding
import my.com.hoperise.util.OrphanageAdapter
import my.com.hoperise.util.OrphangeEventAdapter


class ChooseOrphanageForEventFragment : Fragment() {

    private lateinit var binding: FragmentChooseOrphanageForEventBinding
    private val nav by lazy { findNavController() }
    private val vmOrphanage: OrphanageViewModel by activityViewModels()
    private val vmEvent: EventViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChooseOrphanageForEventBinding.inflate(inflater, container, false)
        vmEvent.search("")
        val adapter = OrphangeEventAdapter(){
                holder, orphanage -> holder.root.setOnClickListener {
            val args = bundleOf(
                "id" to orphanage.id,
                "name" to orphanage.name
            )
            val fragmentManager: FragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
            nav.navigate(R.id.addEventFragment, args)
        }
        }
        binding.svOrp.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(value: String) = true
            override fun onQueryTextChange(value: String): Boolean {
                vmOrphanage.search(value)
                return true
            }

        })
        binding.rvChooseOrp.adapter = adapter
        binding.rvChooseOrp.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        vmOrphanage.getAll().observe(viewLifecycleOwner){
                orphanage -> adapter.submitList(orphanage)
        }

        return binding.root
    }


}