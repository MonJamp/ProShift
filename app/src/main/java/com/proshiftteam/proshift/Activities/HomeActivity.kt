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
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.proshiftteam.proshift.*
import com.proshiftteam.proshift.Adapters.AssignedShiftsAdapter
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import com.proshiftteam.proshift.DataFiles.LogoutObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var prefs: SharedPreferences

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        prefs = this.getSharedPreferences(getString(R.string.PREFERENCES_FILE), Context.MODE_PRIVATE)

        viewManager = LinearLayoutManager(this)

        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_home_controls)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")


        // API call to get a list of assigned shifts
        val callApiGetAssignedShiftsObject: Call<List<AssignedShiftsObject>> = connectJsonApiCalls.getAssignedShifts("Token $tokenCode")

        callApiGetAssignedShiftsObject.enqueue(object : Callback<List<AssignedShiftsObject>> {
            override fun onFailure(call: Call<List<AssignedShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying user shifts!", Toast.LENGTH_LONG).show()
            }

            // Displays a list of shifts using an adapter
            override fun onResponse(call: Call<List<AssignedShiftsObject>>, response: Response<List<AssignedShiftsObject>>) {
                if (response.isSuccessful) {
                    val listOfShiftsScheduled = response.body()!!
                    showShiftsRecyclerView.adapter = AssignedShiftsAdapter(accessCode!!,tokenCode.toString(), listOfShiftsScheduled)
                }
                else {
                    Toast.makeText(context, "Failed loading shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Shows the navigation drawer
        findViewById<ImageView>(R.id.imageMenuButton).setOnClickListener {
            drawerLayoutManagerControls.openDrawer(GravityCompat.START)
        }

        val navigationViewItems : NavigationView = findViewById(R.id.menuNavigationView)


        // Disables the manager controls button if the user is an employee
        if (accessCode == 0) {
            navigationViewItems.menu.removeItem(R.id.managerControlsButton)
        }

        // Items in the navigation view
        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {

                // Sends users to manager controls
                R.id.managerControlsButton -> {
                    if (accessCode == 1) {
                        val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
                        intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
                        intentToManagerControlsActivity.putExtra("accessCode", accessCode)
                        startActivity(intentToManagerControlsActivity)
                    }
                    else {
                        Toast.makeText(context, "Manager controls not available for employees", Toast.LENGTH_SHORT).show()
                    }
                }

                // Displays the current schedule
                R.id.myScheduleButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }

                // Sends user to request time off screen
                R.id.requestTimeOffButton -> {
                    val intentToListOfTimeOffRequestsActivity = Intent(context, ListOfTimeOffRequestsActivity::class.java)
                    intentToListOfTimeOffRequestsActivity.putExtra("tokenCode", tokenCode)
                    intentToListOfTimeOffRequestsActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToListOfTimeOffRequestsActivity)
                }

                // Sends user to search open shift screen
                R.id.searchOpenShiftsButton -> {
                    val intentToOpenShiftsActivity = Intent(context, SearchOpenShiftsActivity::class.java)
                    intentToOpenShiftsActivity.putExtra("tokenCode", tokenCode)
                    intentToOpenShiftsActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToOpenShiftsActivity)
                }

                // Display pending shifts screen
                R.id.pendingShiftsButton -> {
                    val intentToPendingShiftRequestActivity = Intent(context, PendingShiftRequestActivity::class.java)
                    intentToPendingShiftRequestActivity.putExtra("tokenCode", tokenCode)
                    intentToPendingShiftRequestActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToPendingShiftRequestActivity)
                }

                // Button to log out
                R.id.logOutButtonMenu -> {
                    with(prefs.edit()) {
                        putBoolean(getString(R.string.IS_LOGGED_OUT), true)
                        apply()
                    }

                    val intentToHome = Intent(context, MainActivity::class.java)
                    startActivity(intentToHome)
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finishAffinity()
    }
}