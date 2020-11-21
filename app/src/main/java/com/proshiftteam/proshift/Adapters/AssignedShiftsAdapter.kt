package com.proshiftteam.proshift.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import com.proshiftteam.proshift.DataFiles.DropShiftObject
import com.proshiftteam.proshift.DataFiles.ShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignedShiftsAdapter(
    private val tokenCode: String,
    private val assignedShiftList: List<AssignedShiftsObject>)
    : RecyclerView.Adapter<AssignedShiftsAdapter.ViewHolder>()
{
    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView)
    {
        val tvDateHours = cardView.findViewById<TextView>(R.id.cardScheduleDateHours)
        val tvShiftTime = cardView.findViewById<TextView>(R.id.cardScheduleShiftTime)
        val llButtons = cardView.findViewById<LinearLayout>(R.id.cardScheduleButtonLayout)
        val mbDropBtn = cardView.findViewById<MaterialButton>(R.id.cardScheduleDrop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_assigned_shift_item, parent, false)
            as CardView

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shift = assignedShiftList.get(position)

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

        val timeDifference = endTime.hours - startTime.hours
        val strHours: String = timeDifference.toString() + " hours"


        holder.tvDateHours.text = strDate + " • " + strHours
        holder.tvShiftTime.text = strShiftTime

        val context = holder.itemView.context
        if(shift.is_dropped) {
            holder.mbDropBtn.text = "Cancel Drop Request"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.design_default_color_secondary))
        } else {
            holder.mbDropBtn.text = "Drop Shift"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.design_default_color_background))
        }

        holder.itemView.setOnClickListener {
            onItemClickHandler(holder, position)
        }

        holder.mbDropBtn.setOnClickListener { v ->
            onDropShiftClicked(v.context, shift, holder, position)
        }
    }

    override fun getItemCount(): Int {
        return assignedShiftList.size
    }

    private fun onItemClickHandler(holder: ViewHolder, position: Int) {
        holder.llButtons.visibility =
            if(holder.llButtons.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    private fun onDropShiftClicked(
        context: Context, shift: AssignedShiftsObject, holder: ViewHolder, position: Int) {
        val dropShiftObject = DropShiftObject(shift.id)
        val callApiDropShift =
            connectJsonApiCalls.dropSelectedShift("Token $tokenCode",dropShiftObject)
        callApiDropShift.enqueue(object : Callback<ShiftObject> {
            override fun onFailure(call: Call<ShiftObject>, t: Throwable) {
                // TODO: If debug mode
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ShiftObject>, response: Response<ShiftObject>) {
                if (response.isSuccessful) {
                    // TODO: If debug mode
                    Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()

                    if(response.body()?.is_dropped == true) {
                        holder.mbDropBtn.text = "Cancel Shift Drop"
                        holder.itemView.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.design_default_color_secondary))
                    } else {
                        holder.mbDropBtn.text = "Drop Shift"
                        holder.itemView.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.design_default_color_background))
                    }
                } else {
                    // TODO: If debug mode
                    Toast.makeText(context, response.code(), Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}