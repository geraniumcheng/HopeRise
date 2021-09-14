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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.com.hoperise.data.EmployeeViewModel
import my.com.hoperise.data.User
import my.com.hoperise.databinding.FragmentRegisterEmployeeBinding
import my.com.hoperise.util.cropToBlob
import my.com.hoperise.util.errorDialog


class RegisterEmployeeFragment : Fragment() {
    private lateinit var binding: FragmentRegisterEmployeeBinding
    private val nav by lazy { findNavController() }
    private val vm: EmployeeViewModel by activityViewModels()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imgEmployeePhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterEmployeeBinding.inflate(inflater, container, false)

        binding.btnRegisterEmployee.setOnClickListener{ registerEmployee() }
        binding.btnPickImage.setOnClickListener{ pickImage() }

        return binding.root
    }

//    private fun addEmployee(){
//        val newStaff = User("chengzy","czy2k@gmail.com","Zhi Ying","abcd123!","Employee","Active",3,null,null,123456,null)
//
//        Firebase.firestore
//            .collection("User")
//            .document(newStaff.id)
//            .set(newStaff)
//            .addOnSuccessListener { toast("New Employee Added!") }
//    }

    private fun registerEmployee() {
        // TODO: Insert (set)
        val emp = User(
            id    = binding.edtEmployeeId.text.toString().lowercase().trim(),
            email = binding.edtEmployeeEmail.text.toString().trim(),
            name  = binding.edtEmployeeName.text.toString().trim(),
            password = binding.edtTemporaryPassword.text.toString().trim(),
            role = binding.spnEmployeeRole.selectedItem as String,
            status = "Unactivated",
            count = 3,
            photo = binding.imgEmployeePhoto.cropToBlob(300, 300),
            otp = 0,
            activateCode = 0,
        )

        val err = vm.validate(emp)
        if (err != "") {
            errorDialog(err)
            return
        }

        vm.set(emp)
        nav.navigateUp()
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