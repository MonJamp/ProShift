package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.TimeOffRequestsManagerAdapter
import com.proshiftteam.proshift.DataFiles.GetTimeOffRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_approve_time_off_requests.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApproveTimeOffRequestActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_approve_time_off_requests)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        val callApiGetTimeOffRequests: Call<List<GetTimeOffRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.getTimeOffRequests("Token $tokenCode")
        callApiGetTimeOffRequests.enqueue(object: Callback<List<GetTimeOffRequestsObject>> {
            override fun onFailure(call: Call<List<GetTimeOffRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying time off requests!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<GetTimeOffRequestsObject>>, response: Response<List<GetTimeOffRequestsObject>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully loaded time off requests" + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfTimeOffRequests = response.body()!!
                    approve_time_off_requests_recycler_view.adapter = TimeOffRequestsManagerAdapter(accessCode!!,tokenCode.toString(), listOfTimeOffRequests)
                } else {
                    Toast.makeText(context, "Failed loading time off requests : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }
        })
        findViewById<ImageView>(R.id.backArrowButtonApproveTimeOffRequests).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            intentToManagerControlsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerControlsActivity)
        }
    }
}