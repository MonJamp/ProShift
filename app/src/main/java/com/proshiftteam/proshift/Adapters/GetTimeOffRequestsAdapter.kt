package com.proshiftteam.proshift.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Activities.ApproveTimeOffRequestActivity
import com.proshiftteam.proshift.DataFiles.ApproveDenyTimeOffRequestsObject
import com.proshiftteam.proshift.DataFiles.GetTimeOffRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_get_time_off_requests.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetTimeOffRequestsAdapter(val accessCode: Int,val tokenCode: String, private val timeOffRequestsList: List<GetTimeOffRequestsObject>) : RecyclerView.Adapter<GetTimeOffRequestsAdapter.ShowTimeOffRequestsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetTimeOffRequestsAdapter.ShowTimeOffRequestsViewHolder {
        val timeOffRequestView = LayoutInflater.from(parent.context).inflate(R.layout.card_item_get_time_off_requests, parent, false)
        return ShowTimeOffRequestsViewHolder(timeOffRequestView)
    }

    override fun getItemCount(): Int {
        return timeOffRequestsList.size
    }

    override fun onBindViewHolder(holder: GetTimeOffRequestsAdapter.ShowTimeOffRequestsViewHolder, position: Int) {
        holder.company.text = timeOffRequestsList.get(position).company.toString()
        holder.employee_name.text = timeOffRequestsList.get(position).employee_name
        holder.start_date.text = timeOffRequestsList.get(position).start_date
        holder.end_date.text = timeOffRequestsList.get(position).end_date
        holder.time_start.text = timeOffRequestsList.get(position).time_start
        holder.time_end.text = timeOffRequestsList.get(position).time_end

        holder.itemView.card_get_time_off_requests_approve_button.setOnClickListener {v ->
            val context = v.context
            val requestID = timeOffRequestsList.get(position).id
            val approveTimeOffRequestObject = ApproveDenyTimeOffRequestsObject(requestID)
            val callApiApproveTimeOffRequestObject = connectJsonApiCalls.approveTimeOffRequest("Token $tokenCode", approveTimeOffRequestObject)

            callApiApproveTimeOffRequestObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error approving time off request", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Approved Time Off Request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()

                        val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
                        intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
                        intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
                        context.startActivity(intentToApproveTimeOffRequestActivity)

                    } else {
                        Toast.makeText(context, "Error approving time off request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        holder.itemView.card_get_time_off_requests_deny_button.setOnClickListener {v ->
            val context = v.context
            val requestID = timeOffRequestsList.get(position).id
            val denyTimeOffRequestsObject = ApproveDenyTimeOffRequestsObject(requestID)
            val callApiApproveDenyTimeOffRequestsObject = connectJsonApiCalls.denyTimeOffRequest("Token $tokenCode", denyTimeOffRequestsObject)
            callApiApproveDenyTimeOffRequestsObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error denying time off request", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Successfully denied Time Off Request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()

                        val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
                        intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
                        intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
                        context.startActivity(intentToApproveTimeOffRequestActivity)

                    } else {
                        Toast.makeText(context, "Error denying time off request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    class ShowTimeOffRequestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val company: TextView = itemView.findViewById(R.id.card_get_time_off_requests_company_id)
        val employee_name: TextView = itemView.findViewById(R.id.card_get_time_off_requests_employee_name)
        val start_date: TextView = itemView.findViewById(R.id.card_get_time_off_requests_start_date)
        val end_date: TextView = itemView.findViewById(R.id.card_get_time_off_requests_end_date)
        val time_start: TextView = itemView.findViewById(R.id.card_get_time_off_requests_start_time)
        val time_end: TextView = itemView.findViewById(R.id.card_get_time_off_requests_end_time)
    }
}