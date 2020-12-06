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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.DataFiles.PendingShiftRequestsObject
import com.proshiftteam.proshift.DataFiles.PickUpShiftObject
import com.proshiftteam.proshift.DataFiles.ShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// Adapter to display a list of pending shift requests
class PendingShiftsAdapter(
    val tokenCode: String,
    private val pendingShiftsList: List<PendingShiftRequestsObject>)
    : RecyclerView.Adapter<PendingShiftsAdapter.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.cardPendingShiftDate)
        val tvTime: TextView = itemView.findViewById(R.id.cardPendingShiftTime)
        val tvAssigngment: TextView = itemView.findViewById(R.id.cardPendingShiftAssignment)
        val tvStatus: TextView = itemView.findViewById(R.id.cardPendingShiftStatusValue)
    }
    // Creates a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingShiftsAdapter.ViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_pending_shift_request_item, parent, false)
        return ViewHolder(shiftView)
    }

    // Get total number of items in the list
    override fun getItemCount(): Int {
        return pendingShiftsList.size
    }

    // Binds the data in the views in the card item
    override fun onBindViewHolder(holder: PendingShiftsAdapter.ViewHolder, position: Int) {
        val pendingShift = pendingShiftsList.get(position)
        val shiftId = pendingShift.shift

        val callApiGetShift = connectJsonApiCalls.getShift("Token $tokenCode", PickUpShiftObject(shiftId))
        callApiGetShift.enqueue(object: Callback<OpenShiftsObject> {
            override fun onFailure(call: Call<OpenShiftsObject>, t: Throwable) {

            }

            override fun onResponse(call: Call<OpenShiftsObject>, response: Response<OpenShiftsObject>) {
                val shift = response.body()!!

                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(shift.date)
                val newDateFormat = SimpleDateFormat("MMM dd (E)")
                val strDate: String = newDateFormat.format(date).toString()

                val timeFormat = SimpleDateFormat("HH:mm:ss")
                val startTime = timeFormat.parse(shift.time_start)
                val endTime = timeFormat.parse(shift.time_end)
                val newTimeFormat = SimpleDateFormat("hh:mm a")
                val strStartTime: String = newTimeFormat.format(startTime)
                val strEndTime: String = newTimeFormat.format(endTime)
                val strShiftTime: String = strStartTime + " - " + strEndTime

                var strAssignment: String = "Unassigned"
                if(shift.is_dropped && shift.employee != null) {
                    strAssignment = "Dropped by ${shift.employee_name}"
                }

                val context = holder.itemView.context
                val statusText: String
                if(pendingShift.is_approved) {
                    statusText = "Approved"
                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(context, android.R.color.holo_green_light)
                    )
                }
                else if(pendingShift.is_denied) {
                    statusText = "Denied"
                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(context, android.R.color.holo_red_light)
                    )
                }
                else {
                    statusText = "Pending"
                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(context, android.R.color.background_light)
                    )
                }

                holder.tvDate.text = strDate
                holder.tvTime.text = strShiftTime
                holder.tvAssigngment.text = strAssignment
                holder.tvStatus.text = statusText
            }
        })
    }
}