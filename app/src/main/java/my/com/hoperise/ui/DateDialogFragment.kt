package my.com.hoperise.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import my.com.hoperise.databinding.FragmentDateDialogBinding
import my.com.hoperise.databinding.FragmentEventListingBinding
import android.widget.CalendarView.OnDateChangeListener
import androidx.fragment.app.activityViewModels
import my.com.hoperise.data.SharedViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import android.app.Activity

import android.text.format.DateUtils.getMonthString

import android.content.Intent
import android.text.format.DateUtils


class DateDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDateDialogBinding
    private val nav by lazy { findNavController() }
    private var date : Long = 0
    private val vmShared: SharedViewModel by activityViewModels()
    private val calendar: Calendar = Calendar.getInstance()
    //private lateinit var d: DatePickerDialogFragmentEvents
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDateDialogBinding.inflate(inflater, container, false)

        binding.btnConfirm.setOnClickListener { confirm() }
        binding.calendarView.setOnDateChangeListener(OnDateChangeListener { calendarView, year, month, day ->
            calendar.set(year,month,day)
            date = calendar.timeInMillis
            //d.onDateSelected(date)
            //TODO DELETE
            val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            calendarView.date = calendar.timeInMillis
            val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
            //val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            //val dateFormat = DateFormat()

            val selectedDate:Long = calendarView.date
            calendar.timeInMillis = selectedDate
            Log.d("date", year.toString()+month.toString()+day.toString())
            Log.d("date2", calendarView.date.toString().format(format))
            Log.d("date3", dateFormatter.format(calendar.time))
            Log.d("date3", dateFormatter.format(calendar.timeInMillis))
            Log.d("date4", selectedDate.toString())
            Log.d("date5", calendar.timeInMillis.toString())
            Log.d("date6", calendar.time.toString())
            Log.d("date7", date.toString())
            //Log.d("date7", dateFormat.format(date))

        })

        return binding.root
    }

//    interface DatePickerDialogFragmentEvents {
//        fun onDateSelected(date: Long?)
//    }
//    fun setDatePickerDialogFragmentEvents(dpdfe: DatePickerDialogFragmentEvents) {
//        this.d = dpdfe
//    }
    private fun confirm() {
        val i: Intent = Intent()
            .putExtra("date", date)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
        dismiss()
        //vmShared.insertDate(date)
        //dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

}

