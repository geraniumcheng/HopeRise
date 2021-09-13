package my.com.hoperise.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.com.hoperise.R
import my.com.hoperise.data.User
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*




class EmployeeAdapter (
    val fn: (ViewHolder, User) -> Unit = { _, _ -> }
) : ListAdapter<User, EmployeeAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User)    = a.id == b.id
        override fun areContentsTheSame(a: User, b: User) = a == b
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val empPhoto : ImageView = view.findViewById(R.id.imgEmployeePhoto)
        val empId    : TextView = view.findViewById(R.id.lblEmployeeId)
        val empEmail    : TextView = view.findViewById(R.id.lblEmployeeEmail)
        val empName  : TextView = view.findViewById(R.id.lblEmployeeName)
        val empStatus   : TextView = view.findViewById(R.id.lblEmployeeStatus)
        //val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val empRegisterDate   : TextView = view.findViewById(R.id.lblEmployeeRegisterDate)
        val empRegisterRole   : TextView = view.findViewById(R.id.lblEmployeeRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_employee_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = getItem(position)

        var timeStamp = employee.registerDate
        var sfd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var registerDate = sfd.format((timeStamp))

        holder.empId.text   = employee.id
        holder.empName.text = employee.name
        holder.empEmail.text = employee.email
        holder.empStatus.text  = employee.status
        holder.empRegisterDate.text  = registerDate
        holder.empRegisterRole.text  = employee.role

//        // TODO: Photo (blob to bitmap)
        holder.empPhoto.setImageBitmap(employee.photo?.toBitmap())

        fn(holder, employee)
    }

}