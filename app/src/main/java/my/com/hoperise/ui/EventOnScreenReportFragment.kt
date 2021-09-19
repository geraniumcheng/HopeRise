package my.com.hoperise.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.whiteelephant.monthpicker.MonthPickerDialog
import my.com.hoperise.R
import my.com.hoperise.data.EventReportViewModel
import my.com.hoperise.data.currentUser
import my.com.hoperise.databinding.FragmentEventOnScreenReportBinding
import my.com.hoperise.util.EventReportAdapter
import my.com.hoperise.util.infoDialog
import java.util.*

class EventOnScreenReportFragment : Fragment() {

    private lateinit var binding: FragmentEventOnScreenReportBinding
    private val nav by lazy { findNavController() }
    private val vmReport: EventReportViewModel by activityViewModels()

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().title = getString(R.string.eventSummaryReport)

        binding = FragmentEventOnScreenReportBinding.inflate(inflater, container, false)

        vmReport.filterCategory(getString(R.string.all))

        binding.spReportCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vmReport.filterCategory(binding.spReportCategory.selectedItem.toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }

        vmReport.dateSelected.observe(viewLifecycleOwner) { binding.btnMonth.text = it.toString() }

        val adapter = EventReportAdapter()

        binding.rvReport.adapter = adapter
        binding.rvReport.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        vmReport.getEvent().observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            binding.lblRecord.text = getString(R.string.records, events.size)
        }

        binding.btnMonth.setOnClickListener { openDatePicker() }

        return binding.root
    }

    private fun openDatePicker() {
        val cal   = Calendar.getInstance()

        //only show month and year
        val builder = MonthPickerDialog.Builder(requireActivity(), { month, year ->
            vmReport.dateSelected.value = String.format("%02d/%04d", month + 1, year)
            vmReport.updateResult()
        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))

        builder.setActivatedMonth(Calendar.SEPTEMBER)
            .setMinYear(2000)
            .setActivatedYear(cal.get(Calendar.YEAR))
            .setMaxYear(2050)
            .setTitle(getString(R.string.selectDate))
            .build().show()
    }
}