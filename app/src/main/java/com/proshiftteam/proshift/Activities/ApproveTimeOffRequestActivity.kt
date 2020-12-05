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

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_approve_time_off_requests)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        // API call to get a list of time off requests
        val callApiGetTimeOffRequests: Call<List<GetTimeOffRequestsObject>> = RetrofitBuilderObject.connectJsonApiCalls.getTimeOffRequests("Token $tokenCode")
        callApiGetTimeOffRequests.enqueue(object: Callback<List<GetTimeOffRequestsObject>> {
            override fun onFailure(call: Call<List<GetTimeOffRequestsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying time off requests!", Toast.LENGTH_SHORT).show()
            }

            // Displays a list of time off requests using an adapter
            override fun onResponse(call: Call<List<GetTimeOffRequestsObject>>, response: Response<List<GetTimeOffRequestsObject>>) {
                if (response.isSuccessful) {
                    // Toast.makeText(context, "Successfully loaded time off requests" + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfTimeOffRequests = response.body()!!
                    approve_time_off_requests_recycler_view.adapter = TimeOffRequestsManagerAdapter(accessCode!!,tokenCode.toString(), listOfTimeOffRequests)
                } else {
                    Toast.makeText(context, "Failed loading time off requests : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Back button to go back to the manager controls activity
        findViewById<ImageView>(R.id.backArrowButtonApproveTimeOffRequests).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            intentToManagerControlsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerControlsActivity)
        }
    }
}