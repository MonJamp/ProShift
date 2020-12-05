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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        prefs = this.getSharedPreferences(getString(R.string.PREFERENCES_FILE), Context.MODE_PRIVATE)

        /*  TEMPORARILY REMOVED DUE TO ISSUES
        val bundle: Bundle? = intent.extras
        accessCode= bundle!!.getInt("accessCode"
         */
        viewManager = LinearLayoutManager(this)

        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_home_controls)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        //Intent(context, AssignedShiftsAdapter::class.java).putExtra("tokenCode", tokenCode)

        val callApiGetAssignedShiftsObject: Call<List<AssignedShiftsObject>> = connectJsonApiCalls.getAssignedShifts("Token $tokenCode")

        callApiGetAssignedShiftsObject.enqueue(object : Callback<List<AssignedShiftsObject>> {
            override fun onFailure(call: Call<List<AssignedShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying user shifts!", Toast.LENGTH_LONG).show()
            }
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

        findViewById<ImageView>(R.id.imageMenuButton).setOnClickListener {
            drawerLayoutManagerControls.openDrawer(GravityCompat.START)
        }

        val navigationViewItems : NavigationView = findViewById(R.id.menuNavigationView)

        if (accessCode == 0) {
            navigationViewItems.menu.removeItem(R.id.managerControlsButton)
        }


        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {
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
                R.id.myScheduleButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }
                R.id.requestTimeOffButton -> {
                    val intentToListOfTimeOffRequestsActivity = Intent(context, ListOfTimeOffRequestsActivity::class.java)
                    intentToListOfTimeOffRequestsActivity.putExtra("tokenCode", tokenCode)
                    intentToListOfTimeOffRequestsActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToListOfTimeOffRequestsActivity)
                }
                R.id.searchOpenShiftsButton -> {
                    val intentToOpenShiftsActivity = Intent(context, SearchOpenShiftsActivity::class.java)
                    intentToOpenShiftsActivity.putExtra("tokenCode", tokenCode)
                    intentToOpenShiftsActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToOpenShiftsActivity)
                }
                R.id.pendingShiftsButton -> {
                    val intentToPendingShiftRequestActivity = Intent(context, PendingShiftRequestActivity::class.java)
                    intentToPendingShiftRequestActivity.putExtra("tokenCode", tokenCode)
                    intentToPendingShiftRequestActivity.putExtra("accessCode", accessCode)
                    startActivity(intentToPendingShiftRequestActivity)
                }
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