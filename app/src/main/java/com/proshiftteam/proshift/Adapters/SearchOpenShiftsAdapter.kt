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
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Activities.ApproveShiftRequestActivity
import com.proshiftteam.proshift.Activities.SearchOpenShiftsActivity
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.DataFiles.PickUpShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_search_open_shifts.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class SearchOpenShiftsAdapter(val accessCode: Int, val tokenCode: String, private val openShiftsList: List<OpenShiftsObject>) : RecyclerView.Adapter<SearchOpenShiftsAdapter.ShowOpenShiftsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchOpenShiftsAdapter.ShowOpenShiftsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_search_open_shifts, parent, false)
        return ShowOpenShiftsViewHolder(shiftView)
    }

    override fun getItemCount(): Int {
        return openShiftsList.size
    }

    override fun onBindViewHolder(holder: SearchOpenShiftsAdapter.ShowOpenShiftsViewHolder, position: Int) {
        holder.date.text = openShiftsList.get(position).date

        val shift = openShiftsList.get(position)
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val startTime = timeFormat.parse(shift.time_start)
        val endTime = timeFormat.parse(shift.time_end)
        val newTimeFormat = SimpleDateFormat("hh:mm a")
        val strStartTime: String = newTimeFormat.format(startTime)
        val strEndTime: String = newTimeFormat.format(endTime)
        holder.startTime.text = strStartTime
        holder.endTime.text = strEndTime

        holder.itemView.pickUpImageShowShifts_search_open.setOnClickListener {v ->
            val context = v.context
            val shiftId = openShiftsList.get(position).id
            val shiftInfoSendPickUp = PickUpShiftObject(shiftId)

            val callApiPickUpShiftObject = connectJsonApiCalls.requestShiftPickUp("Token $tokenCode", shiftInfoSendPickUp)

            callApiPickUpShiftObject.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error picking up shift.", Toast.LENGTH_SHORT).show()
                }

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
    class ShowOpenShiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.card_show_shifts_date_search_open)
        val startTime: TextView = itemView.findViewById(R.id.cardShowShiftsStartTime_search_open)
        val endTime: TextView = itemView.findViewById(R.id.cardShowShiftsEndTime_search_open)
    }
}