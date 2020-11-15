package com.proshiftteam.proshift.Adapters

import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.proshiftteam.proshift.DataFiles.DropShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_show_shifts.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class showShiftsAdapter (val tokenCode: String, private  val assignedShiftsList: List<AssignedShiftsObject>) : RecyclerView.Adapter<showShiftsAdapter.ShowShiftViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): showShiftsAdapter.ShowShiftViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_show_shifts, parent, false)
        return ShowShiftViewHolder(shiftView)
    }



    override fun onBindViewHolder(holder: showShiftsAdapter.ShowShiftViewHolder, position: Int) {
        holder.date.text = assignedShiftsList.get(position).date
        holder.startTime.text = assignedShiftsList.get(position).time_start
        holder.endTime.text = assignedShiftsList.get(position).time_end

        holder.itemView.deleteShiftImageShowShifts.setOnClickListener { v ->
            val context = v.context
            val shiftId = assignedShiftsList.get(position).id
            val shiftIdSendDelete = DropShiftObject(shiftId)
            val callApiDropObjectShiftSend = connectJsonApiCalls.dropSelectedShift("Token $tokenCode", shiftIdSendDelete)

            callApiDropObjectShiftSend.enqueue(object : Callback<DropShiftObject> {
                override fun onFailure(call: Call<DropShiftObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error dropping shift.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<DropShiftObject>,
                    response: Response<DropShiftObject>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Shift ID " + shiftId + " dropped successfully, Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    override fun getItemCount(): Int {
        return assignedShiftsList.size
    }
    class ShowShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.card_show_shifts_date)
        val startTime: TextView = itemView.findViewById(R.id.cardShowShiftsStartTime)
        val endTime: TextView = itemView.findViewById(R.id.cardShowShiftsEndTime)
    }
}