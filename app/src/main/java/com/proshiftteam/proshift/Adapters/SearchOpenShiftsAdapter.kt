package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_search_open_shifts.view.*

class SearchOpenShiftsAdapter(val tokenCode: String, private val openShiftsList: List<OpenShiftsObject>) : RecyclerView.Adapter<SearchOpenShiftsAdapter.ShowOpenShiftsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchOpenShiftsAdapter.ShowOpenShiftsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_search_open_shifts, parent, false)
        return ShowOpenShiftsViewHolder(shiftView)
    }

    override fun getItemCount(): Int {
        return openShiftsList.size
    }

    override fun onBindViewHolder(holder: SearchOpenShiftsAdapter.ShowOpenShiftsViewHolder, position: Int) {
        holder.date.text = openShiftsList.get(position).date
        holder.startTime.text = openShiftsList.get(position).time_start
        holder.endTime.text = openShiftsList.get(position).time_end
        holder.itemView.pickUpImageShowShifts_search_open.setOnClickListener {v ->
            // Code for button functions
        }
    }
    class ShowOpenShiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.card_show_shifts_date_search_open)
        val startTime: TextView = itemView.findViewById(R.id.cardShowShiftsStartTime_search_open)
        val endTime: TextView = itemView.findViewById(R.id.cardShowShiftsEndTime_search_open)
    }
}