package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.ListOfTimeOffRequestsObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_list_of_time_off_requests.view.*

class ListOfTimeOffRequestsAdapter(val tokenCode: String, val accessCode: Int, private val timeOffRequestsList: List<ListOfTimeOffRequestsObject>) : RecyclerView.Adapter<ListOfTimeOffRequestsAdapter.ShowTimeOffRequestsViewHolder> () {


    class ShowTimeOffRequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startDate: TextView = itemView.findViewById(R.id.list_of_time_off_requests_start_date)
        val endDate: TextView = itemView.findViewById(R.id.list_of_time_off_requests_end_date)
        val startTime: TextView = itemView.findViewById(R.id.list_of_time_off_requests_start_time)
        val endTime: TextView = itemView.findViewById(R.id.list_of_time_off_requests_end_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowTimeOffRequestsViewHolder {
        val shiftView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_list_of_time_off_requests, parent, false)
        return ShowTimeOffRequestsViewHolder(shiftView)
    }

    override fun getItemCount(): Int {
        return timeOffRequestsList.size
    }

    override fun onBindViewHolder(holder: ShowTimeOffRequestsViewHolder, position: Int) {
        holder.startDate.text = timeOffRequestsList.get(position).start_date
        holder.endDate.text = timeOffRequestsList.get(position).end_date
        holder.startTime.text = timeOffRequestsList.get(position).time_start
        holder.endTime.text = timeOffRequestsList.get(position).time_end
    }
}