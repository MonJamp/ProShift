package com.proshiftteam.proshift.Adapters

import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_show_shifts.view.*

class showShiftsAdapter (private  val assignedShiftsList: List<AssignedShiftsObject>) : RecyclerView.Adapter<showShiftsAdapter.ShowShiftViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): showShiftsAdapter.ShowShiftViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_show_shifts, parent, false)
        return ShowShiftViewHolder(shiftView)
    }

    override fun onBindViewHolder(holder: showShiftsAdapter.ShowShiftViewHolder, position: Int) {
        holder.date.text = assignedShiftsList.get(position).date
        holder.startTime.text = assignedShiftsList.get(position).time_start
        holder.endTime.text = assignedShiftsList.get(position).time_end
        holder.itemView.setOnClickListener { v ->
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