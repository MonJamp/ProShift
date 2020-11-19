package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.Adapters.GetShiftRequestsAdapter
import com.proshiftteam.proshift.DataFiles.GetShiftRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_approve_shift_requests.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApproveShiftRequestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_approve_shift_requests)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        val callApiGetShiftRequests: Call<List<GetShiftRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.getShiftRequests("Token $tokenCode")
        callApiGetShiftRequests.enqueue(object: Callback<List<GetShiftRequestsObject>> {
            override fun onFailure(call: Call<List<GetShiftRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying shift requests!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<GetShiftRequestsObject>>,
                response: Response<List<GetShiftRequestsObject>>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully loaded shift requests" + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfShiftRequests = response.body()!!
                    approve_shift_requests_recycler_view.adapter = GetShiftRequestsAdapter(tokenCode.toString(), listOfShiftRequests)
                } else {
                    Toast.makeText(context, "Failed loading shift requests : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })
        findViewById<ImageView>(R.id.backArrowButtonApproveShiftRequests).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToManagerControlsActivity)
        }

    }
}