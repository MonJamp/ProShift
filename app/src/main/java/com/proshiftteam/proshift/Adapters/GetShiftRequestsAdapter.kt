package com.proshiftteam.proshift.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.DataFiles.ApproveDenyShiftRequestObject
import com.proshiftteam.proshift.DataFiles.GetShiftRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.card_item_get_shift_requests.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val context = v.context
            val requestID = shiftRequestsList.get(position).id
            val approveShiftRequestObject = ApproveDenyShiftRequestObject(requestID)
            val callApiApproveShiftObject = connectJsonApiCalls.approveShiftRequest("Token $tokenCode", approveShiftRequestObject)
            callApiApproveShiftObject.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error approving shift request.", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Approved Shift Request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error approving shift request. Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
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