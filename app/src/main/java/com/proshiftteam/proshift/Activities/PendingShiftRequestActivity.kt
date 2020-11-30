package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.Adapters.PendingShiftsAdapter
import com.proshiftteam.proshift.DataFiles.PendingShiftRequestsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_pending_shift_requests.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendingShiftRequestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pending_shift_requests)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        val callApiShowPendingShifts: Call<List<PendingShiftRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.showPendingShiftRequests("Token $tokenCode")
        callApiShowPendingShifts.enqueue(object: Callback<List<PendingShiftRequestsObject>> {
            override fun onFailure(call: Call<List<PendingShiftRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying pending shifts!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<PendingShiftRequestsObject>>, response: Response<List<PendingShiftRequestsObject>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully loaded pending shifts " + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfPendingShifts = response.body()!!
                    pendingShiftRequestsRecyclerView.adapter = PendingShiftsAdapter(tokenCode.toString(), listOfPendingShifts)
                } else {
                    Toast.makeText(context, "Failed loading open shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })
        findViewById<ImageView>(R.id.backArrowButtonPendingShiftRequests).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            intentToHomeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToHomeActivity)
        }
    }
}