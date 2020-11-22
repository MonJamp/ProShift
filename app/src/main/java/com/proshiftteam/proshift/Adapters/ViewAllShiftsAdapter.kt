package com.proshiftteam.proshift.Adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Activities.CurrentShiftAddRemoveActivity
import com.proshiftteam.proshift.Activities.ModifyShiftActivity
import com.proshiftteam.proshift.DataFiles.ViewAllShiftsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_view_all_shifts.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAllShiftsAdapter(val tokenCode: String, private val viewAllShiftsList: List<ViewAllShiftsObject>) : RecyclerView.Adapter<ViewAllShiftsAdapter.ViewAllShiftsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllShiftsAdapter.ViewAllShiftsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_view_all_shifts, parent, false)
        return ViewAllShiftsViewHolder(shiftView)
    }

    override fun getItemCount(): Int {
        return viewAllShiftsList.size
    }

    override fun onBindViewHolder(holder: ViewAllShiftsAdapter.ViewAllShiftsViewHolder, position: Int) {
        val context = this
        holder.date.text = viewAllShiftsList.get(position).date
        holder.startTime.text = viewAllShiftsList.get(position).time_start
        holder.endTime.text = viewAllShiftsList.get(position).time_end
        holder.jobTitle.text = viewAllShiftsList.get(position).position
        holder.employeeName.text = viewAllShiftsList.get(position).employee_name
        val dateToPass: String = viewAllShiftsList.get(position).date
        val startTimeToPass: String = viewAllShiftsList.get(position).time_start
        val endTimeToPass: String = viewAllShiftsList.get(position).time_end
        val positionToPass: String = viewAllShiftsList.get(position).position
        val employeeIdToPass: Int = viewAllShiftsList.get(position).employee
        val isOpenToPass: Boolean = viewAllShiftsList.get(position).is_open
        val shiftIdToPass: Int = viewAllShiftsList.get(position).id

        holder.itemView.view_all_shifts_edit_button.setOnClickListener { v->
            val context = v.context
            val intentToModifyShiftActivity = Intent(context, ModifyShiftActivity::class.java)
            intentToModifyShiftActivity.putExtra("tokenCode", tokenCode)
            intentToModifyShiftActivity.putExtra("date", dateToPass)
            intentToModifyShiftActivity.putExtra("id", shiftIdToPass)
            intentToModifyShiftActivity.putExtra("start_time", startTimeToPass)
            intentToModifyShiftActivity.putExtra("end_time", endTimeToPass)
            intentToModifyShiftActivity.putExtra("position", positionToPass)
            intentToModifyShiftActivity.putExtra("employeeId", employeeIdToPass)
            intentToModifyShiftActivity.putExtra("is_open", isOpenToPass)
            context.startActivity(intentToModifyShiftActivity)
        }
    }
    class ViewAllShiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.view_all_shifts_date)
        val startTime: TextView = itemView.findViewById(R.id.view_all_shifts_time_start)
        val endTime: TextView = itemView.findViewById(R.id.view_all_shifts_time_end)
        val jobTitle: TextView = itemView.findViewById(R.id.view_all_shifts_job_title)
        val employeeName: TextView = itemView.findViewById(R.id.view_all_shifts_employee_name)
    }
}

/*
 */