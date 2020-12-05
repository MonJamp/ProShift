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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.PendingShiftRequestsObject
import com.proshiftteam.proshift.R

// Adapter to display a list of pending shift requests
class PendingShiftsAdapter(val tokenCode: String, private val pendingShiftsList: List<PendingShiftRequestsObject>) : RecyclerView.Adapter<PendingShiftsAdapter.ShowPendingShiftsViewHolder>() {

    // Creates a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingShiftsAdapter.ShowPendingShiftsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_list_of_shift_requests, parent, false)
        return ShowPendingShiftsViewHolder(shiftView)
    }

    // Get total number of items in the list
    override fun getItemCount(): Int {
        return pendingShiftsList.size
    }

    // Binds the data in the views in the card item
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