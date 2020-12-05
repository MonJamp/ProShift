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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.Adapters.SearchOpenShiftsAdapter
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_search_open_shifts.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchOpenShiftsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    lateinit var context: Context
    lateinit var tokenCode: String
    var accessCode: Int = 0
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_open_shifts)

        context = this
        val bundle: Bundle? = intent.extras
        tokenCode = bundle!!.getString("tokenCode")!!
        accessCode = bundle!!.getInt("accessCode")

        // API call to get a list of open shifts
        val callApiGetOpenShifts: Call<List<OpenShiftsObject>> = RetrofitBuilderObject.connectJsonApiCalls.getOpenShifts("Token $tokenCode")
        callApiGetOpenShifts.enqueue(object: Callback<List<OpenShiftsObject>> {
            override fun onFailure(call: Call<List<OpenShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying open shifts!", Toast.LENGTH_SHORT).show()
            }

            // Displays a list of open shifts using an adapter
            override fun onResponse(call: Call<List<OpenShiftsObject>>, response: Response<List<OpenShiftsObject>>) {
                if (response.isSuccessful) {
                    // Toast.makeText(context, "Successfully loaded open shifts" + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfOpenShifts = response.body()!!
                    showSearchOpenShiftsRecyclerView.adapter = SearchOpenShiftsAdapter(accessCode!!, tokenCode.toString(), listOfOpenShifts)
                } else {
                    Toast.makeText(context, "Failed loading open shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })

        // Back arrow button to go to home activity
        findViewById<ImageView>(R.id.backArrowButtonSearchOpen).setOnClickListener {
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