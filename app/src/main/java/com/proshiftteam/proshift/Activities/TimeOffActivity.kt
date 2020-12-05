package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.Adapters.TimeOffRequestsAdapter
import com.proshiftteam.proshift.DataFiles.ListOfTimeOffRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_time_off.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListOfTimeOffRequestsActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_time_off)


        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        val callApiShowListOfTimeOffRequests: Call<List<ListOfTimeOffRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.showListOffTimeOffRequests("Token $tokenCode")
        callApiShowListOfTimeOffRequests.enqueue(object: Callback<List<ListOfTimeOffRequestsObject>> {
            override fun onFailure(call: Call<List<ListOfTimeOffRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying time off requests!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<ListOfTimeOffRequestsObject>>, response: Response<List<ListOfTimeOffRequestsObject>>) {
                if (response.isSuccessful) {
                    // Toast.makeText(context, "Successfully loaded time off requests " + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfTimeOffRequests = response.body()!!
                    listOfTimeOffRequestsRecyclerView.adapter = TimeOffRequestsAdapter(tokenCode.toString(), accessCode!!, listOfTimeOffRequests)
                } else {
                    Toast.makeText(context, "Failed loading time off requests : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })

        findViewById<ImageView>(R.id.backArrowButtonListOfTimeOffRequests).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            intentToHomeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToHomeActivity)
        }

        findViewById<Button>(R.id.submitAnotherRequestButton).setOnClickListener {
            val intentToRequestTimeOffActivity = Intent(context, RequestTimeOffActivity::class.java)
            intentToRequestTimeOffActivity.putExtra("tokenCode", tokenCode)
            intentToRequestTimeOffActivity.putExtra("accessCode", accessCode)
            startActivity(intentToRequestTimeOffActivity)
        }

    }
}