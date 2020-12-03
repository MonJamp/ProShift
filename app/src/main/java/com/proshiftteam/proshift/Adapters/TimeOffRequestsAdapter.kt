package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.ListOfTimeOffRequestsObject
import com.proshiftteam.proshift.R
import java.text.SimpleDateFormat
import android.R.color

class TimeOffRequestsAdapter(
    val tokenCode: String,
    val accessCode: Int,
    private val timeOffRequestsList: List<ListOfTimeOffRequestsObject>)
    : RecyclerView.Adapter<TimeOffRequestsAdapter.ViewHolder>()
{
    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val tvDate: TextView = cardView.findViewById(R.id.cardTimeOffDate)
        val tvTime: TextView = cardView.findViewById(R.id.cardTimeOffTime)
        val tvStatusValue: TextView = cardView.findViewById(R.id.cardTimeOffStatusValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shiftView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_time_off_item, parent, false)
            as CardView

        return ViewHolder(shiftView)
    }

    override fun getItemCount(): Int {
        return timeOffRequestsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeOff = timeOffRequestsList.get(position)

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

        val context = holder.itemView.context
        val statusText: String
        if(timeOff.is_approved) {
            statusText = "Approved"
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, android.R.color.holo_green_light)
            )
        }
        else if(timeOff.is_denied) {
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

        holder.tvDate.text = dateText
        holder.tvTime.text = timeText
        holder.tvStatusValue.text = statusText
    }
}