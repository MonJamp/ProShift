package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.DataFiles.PickUpShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_search_open_shifts.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                            Toast.makeText(context, "Shift ID " + shiftId + " has already been picked up!, Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                        } else {
                        Toast.makeText(context, "Shift ID " + shiftId + " picked up successfully, Response Code " + response.code(), Toast.LENGTH_SHORT).show() }
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