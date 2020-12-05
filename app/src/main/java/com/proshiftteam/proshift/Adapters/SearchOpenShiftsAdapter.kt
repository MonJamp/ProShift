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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.proshiftteam.proshift.Activities.SearchOpenShiftsActivity
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

// Adapter to show a list of open shifts
class SearchOpenShiftsAdapter(
    val accessCode: Int,
    val tokenCode: String,
    private val openShiftsList: List<OpenShiftsObject>)
    : RecyclerView.Adapter<SearchOpenShiftsAdapter.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.cardOpenShiftDate)
        val tvTime: TextView = itemView.findViewById(R.id.cardOpenShiftTime)
        val tvStatus: TextView = itemView.findViewById(R.id.cardOpenShiftStatus)
        val llControls: LinearLayout = itemView.findViewById(R.id.cardOpenShiftControls)
        val mbRequest: MaterialButton = itemView.findViewById(R.id.cardOpenShiftRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_open_shift_item, parent, false)
        return ViewHolder(shiftView)
    }

    // Gets total number of items in the list
    override fun getItemCount(): Int {
        return openShiftsList.size
    }

    fun onItemClick(holder: ViewHolder) {
        holder.llControls.visibility = if(holder.llControls.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Binds data to views in the card item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val openShift = openShiftsList.get(position)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date = dateFormat.parse(openShift.date)
        val newDateFormat = SimpleDateFormat("MMM dd (E)")
        val strDate: String = newDateFormat.format(date).toString()

        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val startTime = timeFormat.parse(openShift.time_start)
        val endTime = timeFormat.parse(openShift.time_end)
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val strStartTime: String = newTimeFormat.format(startTime)
        val strEndTime: String = newTimeFormat.format(endTime)
        val strShiftTime: String = strStartTime + " - " + strEndTime

        var strStatus: String = "Unassigned Shift"
        if(openShift.is_dropped && openShift.employee != null) {
            strStatus = "Dropped by ${openShift.employee_name}"
        }

        holder.tvDate.text = strDate
        holder.tvTime.text = strShiftTime
        holder.tvStatus.text = strStatus

        holder.itemView.setOnClickListener { onItemClick(holder) }

        // Button to pick up a shift
        holder.mbRequest.setOnClickListener {v ->
            val context = v.context
            val shiftId = openShiftsList.get(position).id
            val shiftInfoSendPickUp = PickUpShiftObject(shiftId)

            // API call to request to pick up a shift
            val callApiPickUpShiftObject = connectJsonApiCalls.requestShiftPickUp("Token $tokenCode", shiftInfoSendPickUp)

            callApiPickUpShiftObject.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error picking up shift.", Toast.LENGTH_SHORT).show()
                }


                // Lets the user know that the request is submitted succesfully
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        if (response.code() == 208) {
                            Toast.makeText(context, "Shift ID " + shiftId + " has already been picked up!", Toast.LENGTH_SHORT).show()
                        } else {
                        Toast.makeText(context, "Shift ID " + shiftId + " picked up successfully", Toast.LENGTH_SHORT).show()
                            val intentToSearchOpenShiftsActivity = Intent(context, SearchOpenShiftsActivity::class.java)
                            intentToSearchOpenShiftsActivity.putExtra("tokenCode", tokenCode)
                            intentToSearchOpenShiftsActivity.putExtra("accessCode", accessCode)
                            context.startActivity(intentToSearchOpenShiftsActivity)
                        }
                    } else {
                        Toast.makeText(context, "Error picking up shift. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}