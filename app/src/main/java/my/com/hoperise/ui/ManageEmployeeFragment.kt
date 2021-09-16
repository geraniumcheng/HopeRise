package my.com.hoperise.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import my.com.hoperise.data.UserViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentManageEmployeeBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog
import my.com.hoperise.util.toBitmap

class ManageEmployeeFragment : Fragment() {
    private lateinit var binding: FragmentManageEmployeeBinding
    private val nav by lazy { findNavController() }
    private val vm: UserViewModel by activityViewModels()

    private val id by lazy { requireArguments().getString("id") ?: "" }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgEmployeePhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentManageEmployeeBinding.inflate(inflater, container, false)

        reset()
        binding.btnUpdateEmployee.setOnClickListener { updateEmployee() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnPickImage.setOnClickListener{ pickImage() }
        return binding.root
    }

    private fun reset() {
        // TODO: Load data
        val emp = vm.get(id)

        if (emp == null) {
            nav.navigateUp() //if no record then return to previous page
            return
        }

        load(emp)
    }

    private fun load(emp: User){
        binding.lblEmployeeId.text = emp.id
        binding.lblEmployeeEmail.setText(emp.email)
        binding.edtEmployeeName.setText(emp.name)
        binding.spnEmployeeRole.setSelection( if(emp.role == "Manager") 0 else 1)

        // TODO: Load photo and date
        binding.imgEmployeePhoto.setImageBitmap(emp.photo?.toBitmap())

        binding.edtEmployeeName.requestFocus()
    }

    private fun updateEmployee() {
        val empOri = vm.get(id)

        val emp = User(
            id = empOri!!.id,
            email = empOri!!.email,
            name  = binding.edtEmployeeName.text.toString().trim(),
            password = empOri!!.password,
            role = binding.spnEmployeeRole.selectedItem as String,
            status = empOri!!.status,
            count = empOri!!.count,
            photo = binding.imgEmployeePhoto.cropToBlob(300, 300),
            otp = empOri!!.otp,
            activateCode = empOri!!.activateCode,
            registerDate = empOri.registerDate
        )

        // means its not inserting a new record
        val err = vm.validate(emp, false)
        if (err != "") {
            errorDialog(err)
            return
        }

        vm.update(emp)
        nav.navigateUp()
        toast("Employee updated successfully!")
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }
}