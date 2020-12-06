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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Activities.ApproveShiftRequestActivity
import com.proshiftteam.proshift.DataFiles.ApproveDenyShiftRequestObject
import com.proshiftteam.proshift.DataFiles.GetShiftRequestsObject
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.DataFiles.PickUpShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// Get a list of submitted shift requests
class GetShiftRequestsAdapter (
    val accessCode: Int,
    val tokenCode: String,
    private val shiftRequestsList: List<GetShiftRequestsObject>)
    : RecyclerView.Adapter<GetShiftRequestsAdapter.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmployee: TextView = itemView.findViewById(R.id.cardShiftRequestManagerEmployee)
        val tvDate: TextView = itemView.findViewById(R.id.cardShiftRequestManagerDate)
        val tvTime: TextView = itemView.findViewById(R.id.cardShiftRequestManagerTime)
        val tvAssigngment: TextView = itemView.findViewById(R.id.cardShiftRequestManagerAssignment)
        val llControls: LinearLayout = itemView.findViewById(R.id.cardShiftRequestfManagerControls)
        val ibApprove: ImageButton = itemView.findViewById(R.id.cardShiftRequestManagerApprove)
        val ibDeny: ImageButton = itemView.findViewById(R.id.cardShiftRequestManagerDeny)
    }
    // on create function for the view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetShiftRequestsAdapter.ViewHolder {
        val getShiftRequestsView = LayoutInflater.from(parent.context).inflate(R.layout.card_shift_request_manager_item, parent, false)
        return ViewHolder(getShiftRequestsView)
    }

    // Get total number of items in the list
    override fun getItemCount(): Int {
        return shiftRequestsList.size
    }

    // Performs an action on clicking an item
    private fun onItemClickHandler(holder: ViewHolder, position: Int) {
        holder.llControls.visibility = if(holder.llControls.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Binds data to the views in the card item
    override fun onBindViewHolder(holder: GetShiftRequestsAdapter.ViewHolder, position: Int) {
        val shiftRequest = shiftRequestsList.get(position)
        val shiftId = shiftRequest.shift

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

                holder.tvEmployee.text = shiftRequest.employee_name
                holder.tvDate.text = strDate
                holder.tvTime.text = strShiftTime
                holder.tvAssigngment.text = strAssignment
            }
        })

        holder.itemView.setOnClickListener { onItemClickHandler(holder, position) }

        // Approve button click listener
        holder.ibApprove.setOnClickListener {v ->
            val context = v.context
            val requestID = shiftRequestsList.get(position).id
            val approveShiftRequestObject = ApproveDenyShiftRequestObject(requestID)

            // API call if the manager decides to approve a request
            val callApiApproveShiftObject = connectJsonApiCalls.approveShiftRequest("Token $tokenCode", approveShiftRequestObject)
            callApiApproveShiftObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error approving shift request.", Toast.LENGTH_SHORT).show()
                }

                // Lets the user know the shift request is approved if the response is successful
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Approved Shift Request.", Toast.LENGTH_SHORT).show()

                        val intentToApproveShiftRequestActivity = Intent(context, ApproveShiftRequestActivity::class.java)
                        intentToApproveShiftRequestActivity.putExtra("tokenCode", tokenCode)
                        intentToApproveShiftRequestActivity.putExtra("accessCode", accessCode)
                        context.startActivity(intentToApproveShiftRequestActivity)

                    } else {
                        Toast.makeText(context, "Error approving shift request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }

        // Deny button click listener
        holder.ibDeny.setOnClickListener {v ->
            val context = v.context
            val requestID = shiftRequestsList.get(position).id
            val denyShiftRequestObject = ApproveDenyShiftRequestObject(requestID)

            // API call if the manager decides to denies a request
            val callApiDenyShiftObject = connectJsonApiCalls.denyShiftRequest("Token $tokenCode", denyShiftRequestObject)
            callApiDenyShiftObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error denying shift request.", Toast.LENGTH_SHORT).show()
                }

                // Lets the manager know that the request is denied successfully
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Denied Shift Request.", Toast.LENGTH_SHORT).show()
                        val intentToApproveShiftRequestActivity = Intent(context, ApproveShiftRequestActivity::class.java)
                        intentToApproveShiftRequestActivity.putExtra("tokenCode", tokenCode)
                        intentToApproveShiftRequestActivity.putExtra("accessCode", accessCode)
                        context.startActivity(intentToApproveShiftRequestActivity)
                    } else {
                        Toast.makeText(context, "Error denying shift request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }
}