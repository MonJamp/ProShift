package com.proshiftteam.proshift.Activities

import android.content.Intent
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
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.Adapters.showShiftsAdapter
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.LogoutObject
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val accessCode = 1
        /*  TEMPORARILY REMOVED DUE TO ISSUES
        val bundle: Bundle? = intent.extras
        accessCode= bundle!!.getInt("accessCode"
         */
        viewManager = LinearLayoutManager(this)

        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_home_controls)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        Intent(context, showShiftsAdapter::class.java).putExtra("tokenCode", tokenCode)

        val callApiGetAssignedShiftsObject: Call<List<AssignedShiftsObject>> = connectJsonApiCalls.getAssignedShifts("Token $tokenCode")

        callApiGetAssignedShiftsObject.enqueue(object : Callback<List<AssignedShiftsObject>> {
            override fun onFailure(call: Call<List<AssignedShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying user shifts!", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<List<AssignedShiftsObject>>, response: Response<List<AssignedShiftsObject>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully loaded shifts" + response.code(), Toast.LENGTH_LONG).show()
                    val listOfShiftsScheduled = response.body()!!
                    showShiftsRecyclerView.adapter = showShiftsAdapter(tokenCode.toString(), listOfShiftsScheduled)
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


        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {
                R.id.managerControlsButton -> {
                    if (accessCode == 1) {
                        val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
                        intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
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
                    val intentToRequestTimeOffActivity = Intent(context, RequestTimeOffActivity::class.java)
                    intentToRequestTimeOffActivity.putExtra("tokenCode", tokenCode)
                    startActivity(intentToRequestTimeOffActivity)
                }
                R.id.searchOpenShiftsButton -> {
                    val intentToOpenShiftsActivity = Intent(context, SearchOpenShiftsActivity::class.java)
                    intentToOpenShiftsActivity.putExtra("tokenCode", tokenCode)
                    startActivity(intentToOpenShiftsActivity)
                }
                R.id.logOutButtonMenu -> {
                    val logoutObjectSend = LogoutObject(tokenCode)
                    val callApiPost = connectJsonApiCalls.logoutUser("Token $tokenCode", logoutObjectSend)

                    callApiPost.enqueue(object : Callback<LogoutObject> {
                        override fun onFailure(call: Call<LogoutObject>, t: Throwable) {
                            Toast.makeText(context, "Cannot connect! Error Logging out.", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(
                            call: Call<LogoutObject>,
                            response: Response<LogoutObject>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Successfully logged out user! Response code " + response.code(), Toast.LENGTH_SHORT).show()
                                val intentToHome = Intent(context, MainActivity::class.java)
                                startActivity(intentToHome)
                            }
                            else {
                                Toast.makeText(context, "Failed Logout : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
            true
        }
    }
}