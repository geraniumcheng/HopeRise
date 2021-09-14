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
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.databinding.FragmentEmployeeListingBinding
import my.com.hoperise.util.EmployeeAdapter





class EmployeeListingFragment : Fragment() {
    private lateinit var binding: FragmentEmployeeListingBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    private lateinit var adapter: EmployeeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEmployeeListingBinding.inflate(inflater, container, false)

        // Reset search and filter record everytime come in here
        vm.search("")
        vm.filter("","")
        sort("id")

        adapter = EmployeeAdapter() { holder, employee ->
            // Item click
            holder.root.setOnClickListener {
                nav.navigate(R.id.manageEmployeeFragment, bundleOf("id" to employee.id))
            }

        }
        binding.rvEmployeeList.adapter = adapter
        binding.rvEmployeeList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        // Load data from Firestore
        vm.getAll().observe(viewLifecycleOwner) { employeeList ->
            adapter.submitList(employeeList)
            binding.lblEmployeeRecord.text = "${employeeList.size} record(s)"
        }

        binding.btnRegisterEmployee.setOnClickListener {
            nav.navigate(R.id.registerEmployeeFragment)
        }

        binding.svEmployee.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(empName: String) = true
            override fun onQueryTextChange(empName: String): Boolean {
                vm.search(empName)
                return true
            }
        })

        binding.spnEmployeeStatus.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = binding.spnEmployeeStatus.selectedItem as String
                val role = binding.spnEmployeeRole.selectedItem as String
                vm.filter(status,role)
            }
        }

        binding.spnEmployeeRole.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = binding.spnEmployeeStatus.selectedItem as String
                val role = binding.spnEmployeeRole.selectedItem as String
                vm.filter(status,role)
            }
        }

        binding.btnEmployeeId.setOnClickListener { sort("id")}
        binding.btnEmployeeName.setOnClickListener { sort("name") }
        binding.btnRegisterDate.setOnClickListener { sort("registerDate") }

        return binding.root
    }

    private fun sort(field: String){
        val reverse = vm.sort(field)

        // Remove sorting icon for buttons
        binding.btnEmployeeId.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.btnEmployeeName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.btnRegisterDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        // Set sorting icon for buttons
        val res = if (reverse) R.drawable.ic_sort_down else R.drawable.ic_sort_up //down = descending, up = ascending

        when (field) {
            "id"    -> binding.btnEmployeeId.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0) // icon appear at right hand side
            "name"  -> binding.btnEmployeeName.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
            "registerDate" -> binding.btnRegisterDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
        }
    }
}