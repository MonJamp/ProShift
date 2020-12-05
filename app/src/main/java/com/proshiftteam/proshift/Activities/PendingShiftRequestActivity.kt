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

package com.proshiftteam.proshift.Activities

import android.content.Context
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

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    lateinit var context: Context
    lateinit var tokenCode: String
    var accessCode: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pending_shift_requests)

        context = this
        val bundle: Bundle? = intent.extras
        tokenCode = bundle!!.getString("tokenCode")!!
        accessCode = bundle!!.getInt("accessCode")

        // API request to show a list of pending shift requests
        val callApiShowPendingShifts: Call<List<PendingShiftRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.showPendingShiftRequests("Token $tokenCode")
        callApiShowPendingShifts.enqueue(object: Callback<List<PendingShiftRequestsObject>> {
            override fun onFailure(call: Call<List<PendingShiftRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying pending shifts!", Toast.LENGTH_SHORT).show()
            }

            // Displays the list using an adapter
            override fun onResponse(call: Call<List<PendingShiftRequestsObject>>, response: Response<List<PendingShiftRequestsObject>>) {
                if (response.isSuccessful) {
                    // Toast.makeText(context, "Successfully loaded pending shifts " + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfPendingShifts = response.body()!!
                    pendingShiftRequestsRecyclerView.adapter = PendingShiftsAdapter(tokenCode.toString(), listOfPendingShifts)
                } else {
                    Toast.makeText(context, "Failed loading open shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })

        // Back button to go back to the home activity
        findViewById<ImageView>(R.id.backArrowButtonPendingShiftRequests).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            intentToHomeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToHomeActivity)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentToHomeActivity = Intent(context, HomeActivity::class.java)
        intentToHomeActivity.putExtra("tokenCode", tokenCode)
        intentToHomeActivity.putExtra("accessCode", accessCode)
        startActivity(intentToHomeActivity)
    }
}