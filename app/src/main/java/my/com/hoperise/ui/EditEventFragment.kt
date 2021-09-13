package my.com.hoperise.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import my.com.hoperise.R
import my.com.hoperise.data.Event
import my.com.hoperise.data.EventViewModel
import my.com.hoperise.data.Orphanage
import my.com.hoperise.databinding.FragmentEditEventBinding
import my.com.hoperise.databinding.FragmentEventBinding
import my.com.hoperise.databinding.FragmentVolunteerSubmitApplicationBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import java.util.*

class EditEventFragment : Fragment() {

    private lateinit var binding: FragmentEditEventBinding
    private val nav by lazy { findNavController() }
    private val vm: EventViewModel by activityViewModels()
    private val id by lazy { requireArguments().getString("id", "") }
    private var orpID = ""
    private var volCount = 0

    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private var hour = calendar.get(Calendar.HOUR_OF_DAY)
    private var minute = calendar.get(Calendar.MINUTE)
    private var date = ""
    private var time = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditEventBinding.inflate(inflater, container, false)

        reset()
        binding.btnEvDate.setOnClickListener {
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                date = setDate(month, day, year)
                binding.lblEvDate.text = date
            }, year, month, day)
            dpd.show()
            dpd.datePicker.minDate = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3)
        }
        binding.btnEvTime.setOnClickListener {
            val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, min ->
                time = setTime(hour,min)
                binding.lblEvTime.text = time
            },hour, minute, false)
            tpd.show()
        }

        binding.btnEvConfirm.setOnClickListener { submit() }
        return binding.root
    }

    private fun reset(){
        val e = vm.get(id)
        if(e == null){
            nav.navigateUp()
            return
        }
        load(e)

    }

    private fun load(e: Event) {
        binding.editEventName.setText(e.name)
        binding.editEventDesc.setText(e.description)
        val spn = binding.spEventCategory.adapter.count-1
        for (a in 0..spn) {
            if(binding.spEventCategory.getItemAtPosition(a) == e.category){
                binding.spEventCategory.setSelection(a)
            }
        }
        binding.lblEvDate.text = e.date
        binding.lblEvTime.text = e.time
        binding.editVolunteerNo.setText(e.volunteerRequired.toString())
        orpID = e.orphanageID
        volCount = e.volunteerCount
        year = e.date.takeLast(4).toInt()
        month = e.date.substring(3,5).toInt()-1
        day = e.date.take(2).toInt()
        hour = e.time.take(2).toInt()
        minute = e.time.takeLast(2).toInt()
        date = e.date
        time = e.time

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
    private fun submit(){
        if(binding.editVolunteerNo.text.toString() == ""){
            errorDialog("Please fill in every field!")
            return
        }
        val e = Event(
            id = id,
            name = binding.editEventName.text.toString().trim(),
            category = binding.spEventCategory.selectedItem.toString(),
            date = date,
            time = time,
            volunteerRequired = binding.editVolunteerNo.text.toString().toInt(),
            volunteerCount = volCount,
            description = binding.editEventDesc.text.toString(),
            orphanageID = orpID
        )

        val err = vm.validate(e)
        if( err != ""){
            errorDialog(err)
            return
        }
        vm.set(e)
        Toast.makeText(context, "Orphanage updated successfully", Toast.LENGTH_SHORT).show()
        nav.navigateUp()
    }
}