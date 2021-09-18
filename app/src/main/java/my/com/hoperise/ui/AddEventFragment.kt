package my.com.hoperise.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.data.Event
import my.com.hoperise.data.EventViewModel
import my.com.hoperise.data.SharedViewModel
import my.com.hoperise.databinding.FragmentAddEventBinding
import my.com.hoperise.util.errorDialog
import java.text.DateFormat
import java.util.*

class AddEventFragment : Fragment(){

    private lateinit var binding: FragmentAddEventBinding
    private val nav by lazy { findNavController() }
    private val vmEvent: EventViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val name by lazy { requireArguments().getString("name") ?: "" }

    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val day = calendar.get(Calendar.DAY_OF_MONTH)
    private val hour = calendar.get(Calendar.HOUR_OF_DAY)
    private val minute = calendar.get(Calendar.MINUTE)
    private var date = ""
    private var time = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEventBinding.inflate(inflater, container, false)

        binding.lblOrpName.text = "Add Event for $name"
        binding.btnDate.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                date = setDate(month, day, year)
                binding.lblDate.text = date
            }, year, month, day)
            dpd.show()
            dpd.datePicker.minDate = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3)
        }

        binding.btnTime.setOnClickListener {
            val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, min ->
                time = setTime(hour,min)
                binding.lblTime.text = time
            },hour, minute, false)
            tpd.show()
        }

        binding.btnReset.setOnClickListener { reset() }
        binding.btnSubmit.setOnClickListener { submit() }

        return binding.root
    }

    private fun setDate(month: Int, day: Int, year: Int): String {
        var newMonth = month.toString()
        var newDay = day.toString()
        if((month+1) <= 9){
            newMonth = "0${month+1}"
        }else{
            newMonth = "${month+1}"
        }
        if(day <= 9){
            newDay = "0$day"
        }
        return "$newDay-$newMonth-$year"
    }

    private fun setTime(hour: Int, min: Int): String {
        var newHour = hour.toString()
        var newMin = min.toString()

        if(hour <= 9){
            newHour = "0$hour"
        }
        if(min <= 9){
            newMin = "0$min"
        }

        return "$newHour:$newMin"
    }

    private fun submit() {
        if(binding.edtVolunteerNo.text.toString() == ""){
            errorDialog("Please fill in every field!")
            return
        }
        val e = Event(
            id = vmEvent.generateID(),
            name = binding.edtEventName.text.toString().trim(),
            category = binding.spinner.selectedItem.toString(),
            date = date,
            time = time,
            volunteerRequired = binding.edtVolunteerNo.text.toString().toInt(),
            volunteerCount = 0,
            description = binding.edtDesc.text.toString(),
            orphanageID = id
        )


        val err = vmEvent.validate(e)
        if( err != ""){
            errorDialog(err)
            return
        }
        vmEvent.set(e)
        reset()

        Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show()
        nav.navigateUp()
    }

    private fun reset() {
        binding.edtEventName.text.clear()
        binding.edtEventName.requestFocus()
        binding.edtDesc.text.clear()
        binding.edtVolunteerNo.text.clear()
        binding.spinner.setSelection(0)

    }

}