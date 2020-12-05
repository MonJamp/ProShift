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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.Adapters.ViewAllShiftsAdapter
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.DataFiles.ViewAllShiftsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_current_shift_add_remove.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CurrentShiftAddRemoveActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_shift_add_remove)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        // API request to view a list of ALL the shifts
        val callApiViewAllShifts: Call<List<ViewAllShiftsObject>> = RetrofitBuilderObject.connectJsonApiCalls.viewAllShifts("Token $tokenCode")
        callApiViewAllShifts.enqueue(object: Callback<List<ViewAllShiftsObject>> {
            override fun onFailure(call: Call<List<ViewAllShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying all the shifts!", Toast.LENGTH_SHORT).show()
            }

            // Displays all the shifts using an adapter
            override fun onResponse(
                call: Call<List<ViewAllShiftsObject>>,
                response: Response<List<ViewAllShiftsObject>>
            ) {
                if (response.isSuccessful) {
                    // Toast.makeText(context, "Successfully displaying all the shifts " + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfEveryShift = response.body()!!
                    recycler_view_View_all_shifts.adapter = ViewAllShiftsAdapter(accessCode!!,tokenCode.toString(), listOfEveryShift)
                } else {
                    Toast.makeText(context, "Failed loading open shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })

        // Back arrow button to go back to manager controls activity
        findViewById<ImageView>(R.id.backArrowButtonAddRemove).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            intentToManagerControlsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerControlsActivity)
        }

    }
}