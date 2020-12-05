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

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_approve_shift_requests)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        // Api request to get a list of shift requests to process
        val callApiGetShiftRequests: Call<List<GetShiftRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.getShiftRequests("Token $tokenCode")
        callApiGetShiftRequests.enqueue(object: Callback<List<GetShiftRequestsObject>> {
            override fun onFailure(call: Call<List<GetShiftRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying shift requests!", Toast.LENGTH_SHORT).show()
            }

            // Displays a list of shifts requests using the adapter
            override fun onResponse(
                call: Call<List<GetShiftRequestsObject>>,
                response: Response<List<GetShiftRequestsObject>>
            ) {
                if (response.isSuccessful) {
                    // Toast.makeText(context, "Successfully loaded shift requests" + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfShiftRequests = response.body()!!
                    approve_shift_requests_recycler_view.adapter = GetShiftRequestsAdapter(accessCode!!,tokenCode.toString(), listOfShiftRequests)
                } else {
                    Toast.makeText(context, "Failed loading shift requests : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })

        // Back arrow button that sends the user to manager control activity
        findViewById<ImageView>(R.id.backArrowButtonApproveShiftRequests).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            intentToManagerControlsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerControlsActivity)
        }

    }
}