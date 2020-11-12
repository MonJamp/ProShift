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
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.LogoutObject
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
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
        accessCode= bundle!!.getInt("accessCode")

         */
        viewManager = LinearLayoutManager(this)

        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_home_controls)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")


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

        // Example data
        val dataset: Array<ScheduleData> = arrayOf(
            ScheduleData(
                "September",
                21,
                HMS(9, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "November",
                22,
                HMS(10, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "October",
                25,
                HMS(12, 0),
                HMS(5, 0)
            ),

            // Added more values for testing purposes
            ScheduleData(
                "September",
                21,
                HMS(9, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "November",
                22,
                HMS(10, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "October",
                25,
                HMS(12, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "September",
                21,
                HMS(9, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "November",
                22,
                HMS(10, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "October",
                25,
                HMS(12, 0),
                HMS(5, 0)
            )
        )

        viewAdapter = MyAdapter(dataset)

        recyclerView = findViewById<RecyclerView>(R.id.rvScheduleList).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }
}