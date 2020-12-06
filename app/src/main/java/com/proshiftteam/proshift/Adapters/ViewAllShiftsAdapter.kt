/*
Copyright 2020 ProShift Team

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.proshiftteam.proshift.Adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import java.text.SimpleDateFormat


// Adapter to display all the shifts
class ViewAllShiftsAdapter(val accessCode: Int,val tokenCode: String, private val viewAllShiftsList: List<ViewAllShiftsObject>) : RecyclerView.Adapter<ViewAllShiftsAdapter.ViewAllShiftsViewHolder>() {

    class ViewAllShiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.view_all_shifts_date)
        val time: TextView = itemView.findViewById(R.id.view_all_shifts_time)
        val employeeName: TextView = itemView.findViewById(R.id.view_all_shifts_employee_name)
        val clickOpenControl: LinearLayout = itemView.findViewById(R.id.cardOpenShiftControls2)
        //val llControls2: LinearLayout = itemView.findViewById(R.id.cardOpenShiftControls2)
    }

    // Creates a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllShiftsAdapter.ViewAllShiftsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_view_all_shifts, parent, false)
        return ViewAllShiftsViewHolder(shiftView)
    }

    // Gets total number of items in the list
    override fun getItemCount(): Int {
        return viewAllShiftsList.size
    }

    fun onItemClick(holder: ViewAllShiftsViewHolder) {
        holder.clickOpenControl.visibility = if (holder.clickOpenControl.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Binds data to the views in the card items
    override fun onBindViewHolder(holder: ViewAllShiftsAdapter.ViewAllShiftsViewHolder, position: Int) {
        val context = this

        val shift = viewAllShiftsList.get(position)
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val startTime = timeFormat.parse(shift.time_start)
        val endTime = timeFormat.parse(shift.time_end)
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val strStartTime: String = newTimeFormat.format(startTime)
        val strEndTime: String = newTimeFormat.format(endTime)
        val employeeName: String = viewAllShiftsList.get(position).employee_name
        holder.time.text = strStartTime + " - " + strEndTime

        holder.itemView.setOnClickListener { onItemClick(holder) }
        holder.date.text = viewAllShiftsList.get(position).date
        // holder.startTime.text = viewAllShiftsList.get(position).time_start
        // holder.endTime.text = viewAllShiftsList.get(position).time_end
        holder.employeeName.text = viewAllShiftsList.get(position).employee_name + " - " + viewAllShiftsList.get(position).position
        val dateToPass: String = viewAllShiftsList.get(position).date
        val startTimeToPass: String = viewAllShiftsList.get(position).time_start
        val endTimeToPass: String = viewAllShiftsList.get(position).time_end
        val positionToPass: String = viewAllShiftsList.get(position).position
        val employeeIdToPass: Int = viewAllShiftsList.get(position).employee
        val isOpenToPass: Boolean = viewAllShiftsList.get(position).is_open
        val shiftIdToPass: Int = viewAllShiftsList.get(position).id

        // Allows the user to edit a shift
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
            intentToModifyShiftActivity.putExtra("accessCode", accessCode)
            intentToModifyShiftActivity.putExtra("employee_name", employeeName)
            context.startActivity(intentToModifyShiftActivity)
        }
    }
}

/*
 */