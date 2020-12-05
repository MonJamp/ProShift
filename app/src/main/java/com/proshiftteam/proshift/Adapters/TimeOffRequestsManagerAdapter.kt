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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Activities.ApproveTimeOffRequestActivity
import com.proshiftteam.proshift.DataFiles.ApproveDenyTimeOffRequestsObject
import com.proshiftteam.proshift.DataFiles.GetTimeOffRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

// Adapter to show a list of time off requests
class TimeOffRequestsManagerAdapter(
    val accessCode: Int,
    val tokenCode: String,
    private val timeOffRequestsList: List<GetTimeOffRequestsObject>)
    : RecyclerView.Adapter<TimeOffRequestsManagerAdapter.ViewHolder>()
{
    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val tvEmployee: TextView = cardView.findViewById(R.id.cardTimeOffManagerEmployee)
        val tvDate: TextView = cardView.findViewById(R.id.cardTimeOffManagerDate)
        val tvTime: TextView = cardView.findViewById(R.id.cardTimeOffManagerTime)
        val llControls: LinearLayout = cardView.findViewById(R.id.cardTimeOffManagerControls)
        val ibApprove: ImageButton = cardView.findViewById(R.id.cardTimeOffManagerApprove)
        val ibDeny: ImageButton = cardView.findViewById(R.id.cardTimeOffManagerDeny)
    }

    // Create a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeOffRequestsManagerAdapter.ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_time_off_manager_item, parent, false)
            as CardView

        return ViewHolder(cardView)
    }

    // Gets total number of items in the list
    override fun getItemCount(): Int {
        return timeOffRequestsList.size
    }

    // Performs an action on clicking an item
    private fun onItemClickHandler(holder: ViewHolder, position: Int) {
        holder.llControls.visibility = if(holder.llControls.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Binds data to the views in the card items
    override fun onBindViewHolder(holder: TimeOffRequestsManagerAdapter.ViewHolder, position: Int) {
        val timeOff = timeOffRequestsList.get(position)

        val employeeText = timeOff.employee_name

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val newDateFormat = SimpleDateFormat("MM/dd/yy")
        val startDate = dateFormat.parse(timeOff.start_date)
        val endDate = dateFormat.parse(timeOff.end_date)
        val strStartDate = newDateFormat.format(startDate).toString()
        val strEndDate = newDateFormat.format(endDate).toString()

        val dateText = if(startDate == endDate) {
            strStartDate
        } else {
            "$strStartDate - $strEndDate"
        }

        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val startTime = timeFormat.parse(timeOff.time_start)
        val endTime = timeFormat.parse(timeOff.time_end)
        val strStartTime = newTimeFormat.format(startTime).toString()
        val strEndTime = newTimeFormat.format(endTime).toString()

        val timeText = "$strStartTime - $strEndTime"

        holder.tvEmployee.text = employeeText
        holder.tvDate.text = dateText
        holder.tvTime.text = timeText

        holder.itemView.setOnClickListener {
            onItemClickHandler(holder, position)
        }

        // Button to approve a request
        holder.ibApprove.setOnClickListener {v ->
            val context = v.context
            val requestID = timeOffRequestsList.get(position).id
            val approveTimeOffRequestObject = ApproveDenyTimeOffRequestsObject(requestID)
            val callApiApproveTimeOffRequestObject = connectJsonApiCalls.approveTimeOffRequest("Token $tokenCode", approveTimeOffRequestObject)

            // API call to process approving a request
            callApiApproveTimeOffRequestObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error approving time off request", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Approved Time Off Request", Toast.LENGTH_SHORT).show()

                        val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
                        intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
                        intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
                        context.startActivity(intentToApproveTimeOffRequestActivity)

                    } else {
                        Toast.makeText(context, "Error approving time off request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        // Button to deny a request
        holder.ibDeny.setOnClickListener {v ->
            val context = v.context
            val requestID = timeOffRequestsList.get(position).id
            val denyTimeOffRequestsObject = ApproveDenyTimeOffRequestsObject(requestID)

            // API call to deny a request
            val callApiApproveDenyTimeOffRequestsObject = connectJsonApiCalls.denyTimeOffRequest("Token $tokenCode", denyTimeOffRequestsObject)
            callApiApproveDenyTimeOffRequestsObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error denying time off request", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Denied Time Off Request.", Toast.LENGTH_SHORT).show()

                        val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
                        intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
                        intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
                        context.startActivity(intentToApproveTimeOffRequestActivity)

                    } else {
                        Toast.makeText(context, "Error denying time off request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }
}