package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.PendingShiftRequestsObject
import com.proshiftteam.proshift.R

class PendingShiftsAdapter(val tokenCode: String, private val pendingShiftsList: List<PendingShiftRequestsObject>) : RecyclerView.Adapter<PendingShiftsAdapter.ShowPendingShiftsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingShiftsAdapter.ShowPendingShiftsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_list_of_shift_requests, parent, false)
        return ShowPendingShiftsViewHolder(shiftView)
    }

    override fun getItemCount(): Int {
        return pendingShiftsList.size
    }

    override fun onBindViewHolder(holder: PendingShiftsAdapter.ShowPendingShiftsViewHolder, position: Int) {
        holder.shiftId.text = pendingShiftsList.get(position).shift.toString()
        holder.employeeId.text = pendingShiftsList.get(position).employee.toString()
        holder.employeeName.text = pendingShiftsList.get(position).employee_name
    }
    class ShowPendingShiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shiftId: TextView = itemView.findViewById(R.id.list_of_shift_requests_shift_id)
        val employeeId: TextView = itemView.findViewById(R.id.list_of_shift_requests_employee_id)
        val employeeName: TextView = itemView.findViewById(R.id.list_of_shift_requests_employee_name)
    }
}