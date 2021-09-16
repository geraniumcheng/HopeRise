package my.com.hoperise.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
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
import my.com.hoperise.R
import my.com.hoperise.StaffActivity
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
        val currentStatus = getStatus()
        binding.btnUpdateEmployee.setOnClickListener { updateEmployee() }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnPickImage.setOnClickListener{ pickImage() }
        binding.btnDeactivate.setOnClickListener { deactivate(currentStatus) }
        return binding.root
    }

    private fun getStatus(): String {
        val emp = vm.get(id)
        val status = emp!!.status
        return status
    }

    private fun deactivate(currentStatus: String) {
        val userId = (activity as StaffActivity).loggedInId
        val emp = vm.get(id)
        if(userId.equals(emp!!.id)){
            AlertDialog.Builder(requireContext())
                .setTitle("Attention")
                .setMessage("You are not allowed to deactivate your own account!")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("OK", object :
                    DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, whichButton: Int) {
                        return
                    }
                }).show()
        }else{
            if(currentStatus.equals("Active")){
                vm.updateStatus(emp!!.id,"Deactivated")
                nav.navigateUp()
                toast("Employee deactivated successfully!")
            }else if(currentStatus.equals("Deactivated")){
                vm.updateStatus(emp!!.id,"Active")
                nav.navigateUp()
                toast("Employee account back to active status successfully!")
            }else if(currentStatus.equals("Unactivated")){
                toast("Employee haven't verify their account yet!")
            }
        }
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
        binding.imgEmployeePhoto.setImageBitmap(emp.photo?.toBitmap())
        binding.lblEmployeeStatus.setText(emp.status)

        if(emp.status.equals("Unactivated")){
            binding.lblTempPass.visibility = View.VISIBLE
            binding.lblTemporaryPassword.visibility = View.VISIBLE
            binding.lblTemporaryPassword.text = emp.password
        }else{
            binding.lblTempPass.visibility = View.GONE
            binding.lblTemporaryPassword.visibility = View.GONE
        }

        if(emp.status.equals("Deactivated")){
            binding.btnDeactivate.setText("Activate")
        }else if(emp.status.equals("Active")){
            binding.btnDeactivate.setText("Deactivate")
        }else{
            binding.btnDeactivate.setText("Deactivate")
        }

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