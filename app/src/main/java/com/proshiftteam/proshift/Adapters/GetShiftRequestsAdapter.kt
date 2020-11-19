package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.GetShiftRequestsObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_get_shift_requests.view.*

class GetShiftRequestsAdapter (val tokenCode: String, private val shiftRequestsList: List<GetShiftRequestsObject>) : RecyclerView.Adapter<GetShiftRequestsAdapter.GetShiftRequestsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetShiftRequestsAdapter.GetShiftRequestsViewHolder {
        val getShiftRequestsView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_get_shift_requests, parent, false)
        return GetShiftRequestsViewHolder(getShiftRequestsView)
    }

    override fun getItemCount(): Int {
        return shiftRequestsList.size
    }

    override fun onBindViewHolder(holder: GetShiftRequestsAdapter.GetShiftRequestsViewHolder, position: Int) {
        holder.company_name.text = shiftRequestsList.get(position).company_name
        holder.employee_name.text = shiftRequestsList.get(position).employee_name
        holder.shift.text = shiftRequestsList.get(position).shift.toString()
        holder.itemView.card_get_shift_requests_approve_button.setOnClickListener {v ->
            // Code if shift request approved
        }
        holder.itemView.card_get_shift_requests_deny_button.setOnClickListener {v ->
            // Code if shift request denied
        }
    }
    class GetShiftRequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shift: TextView = itemView.findViewById(R.id.card_get_shift_requests_shift_id)
        val employee_name: TextView = itemView.findViewById(R.id.card_get_shift_requests_employee_name)
        val company_name: TextView = itemView.findViewById(R.id.card_get_shift_requests_company_name)
    }
}