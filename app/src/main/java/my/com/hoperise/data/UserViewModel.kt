package my.com.hoperise.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserViewModel: ViewModel() {
    // Local copy for filter and sort
    private var employee = listOf<User>()
    // Live data from firestore
    private val employeeResult = MutableLiveData<List<User>>()
    // Live data for login failed user
    val loginFailed = MutableLiveData<String>()
    // Live data for newly register user
    val newlyRegister = MutableLiveData<String>()
    // Live data for verify newly register email
    val verifyingEmail = MutableLiveData<String>()


    private var name = ""       // Search purpose
    private var status = ""     // Filter purpose
    private var role = ""       // Filter purpose
    private var field = ""      // Sort purpose
    private var reverse = false // Sort purpose

    // Employee list will be updated accordingly
    init {
        EMPLOYEE.addSnapshotListener { snap, _ -> employeeResult.value = snap?.toObjects() }

        viewModelScope.launch {

            EMPLOYEE.addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener

                employee = snap.toObjects<User>()

                updateResult()
            }
        }

    }
    // Login failed user id
    fun setLoginFailedId(id: String){
        loginFailed.value = id
    }

    fun getLoginFailedId(): String{
        return loginFailed.value.toString()
    }

    fun setNewlyRegisteredId(id: String){
        newlyRegister.value = id
    }

    fun getNewlyRegisteredId(): String{
        return newlyRegister.value.toString()
    }

    fun getNewlyVerifiedEmail(): String{
        return verifyingEmail.value.toString()
    }

    fun setNewlyVerifiedEmail(id: String){
        verifyingEmail.value = id
    }


    // Update the filtered and sorted Employee List
    private fun updateResult() {
        var list = employee

        // Search Employee Name, Filter Employee Status and Role
        list = list.filter { emp ->
            emp.name.contains(name, true) && (status.equals("All") || status.equals(emp.status)) && (role.equals("All") || role.equals(emp.role))
        }

        // Sort Employee by Id, name and register date
        list = when (field) {
            "id"    -> list.sortedBy { emp -> emp.id }
            "name"  -> list.sortedBy { emp -> emp.name }
            "registerDate"  -> list.sortedBy { emp -> emp.registerDate }
            else    -> list
        }

        if (reverse) list = list.reversed()

        employeeResult.value = list // live data updated accordingly
    }

    // Search employee by name
    fun search(empName: String) {
        this.name = empName
        updateResult()
    }

    // Filter employee by status nad role
    fun filter(empStatus: String,empRole: String) {
        this.status = empStatus
        this.role = empRole
        updateResult()
    }

    // Sort employee by id, name, register date
    fun sort(field: String): Boolean {
        reverse = if (this.field == field) !reverse else false
        this.field = field
        updateResult()

        return reverse
    }

    fun get(id: String): User? {
        return employeeResult.value?.find { emp -> emp.id == id }
    }

    suspend fun getLogIn(id: String): User? {
        val user = USER
            .whereEqualTo(FieldPath.documentId(), id)
            //.whereEqualTo("id", username)
            .get()
            .await()
            .toObjects<User>()
            .firstOrNull()
        return user
    }

    suspend fun getUserByEmail(email:String): User?{
        val user = USER
            .whereEqualTo("email", email)
            .get()
            .await()
            .toObjects<User>()
            .firstOrNull()
        return user
    }

    fun getAll() = employeeResult

    fun set(emp: User) {
        USER.document(emp.id).set(emp)
    }

    fun update(emp: User) {
        USER.document(emp.id).update("name",emp.name,"role",emp.role,"photo",emp.photo)
    }

    fun changePass(id: String, newPass: String){
        USER.document(id).update("password",newPass)
    }

    fun updateOtp(id: String, otpCode: Int){
        USER.document(id).update("otp",otpCode)
    }

    fun updateCount(id: String, count: Int){
        USER.document(id).update("count",count)
    }

    fun updateActivationCode(id: String, activateCode: Int){
        USER.document(id).update("activateCode",activateCode)
    }

    fun updateStatus(id: String, status: String){
        USER.document(id).update("status",status)
    }

    fun updatePassword(id: String, password: String){
        USER.document(id).update("password",password)
    }

    fun updateEmail(id: String, email: String){
        USER.document(id).update("email",email)
    }

    private fun idExists(id: String): Boolean {
        return employeeResult.value?.any { emp -> emp.id == id } ?: false
    }

    private fun emailExists(email: String): Boolean {
        return employeeResult.value?.any { emp -> emp.email == email } ?: false
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validate(emp: User, insert: Boolean = true): String { // since its insert new record so true
        val regexId = Regex("""^[a-zA-Z0-9]{8,20}${'$'}""")
        val regexTemporaryPassword = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

        var e = ""

        if (insert) {
            e += if (emp.id == "") "- Username is required.\n"
            else if (!emp.id.matches(regexId)) "- Username format is invalid.\n" //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
            else if (idExists(emp.id)) "- Username is duplicated.\n"
            else ""
            e += if (emp.email == "") "- Email is required.\n"
            else if (!isValidEmail(emp.email)) "- Email format is invalid.\n"
            else if (emailExists(emp.email)) "- Email is duplicated.\n"
            else ""
            e += if (emp.password == "") "- Temporary password is required.\n"
            else if (!emp.password.matches(regexTemporaryPassword)) "- Temporary password format is invalid.\n"
            else ""
        }

        e += if (emp.name == "") "- Name is required.\n"
        else if (emp.name.length < 4) "- Name is too short.\n"
        else ""

        e += if (emp.photo?.toBytes()?.isEmpty() == true) "- Photo is required.\n"
        else ""

        return e
    }
}