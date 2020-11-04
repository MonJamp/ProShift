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
import com.proshiftteam.proshift.DataFiles.ScheduleData

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


        findViewById<ImageView>(R.id.imageMenuButton).setOnClickListener {
            drawerLayoutManagerControls.openDrawer(GravityCompat.START)
        }

        val navigationViewItems : NavigationView = findViewById(R.id.menuNavigationView)

        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {
                R.id.managerControlsButton -> {
                    if (accessCode == 1) {
                        startActivity(Intent(this, ManagerControlsActivity::class.java))
                    }
                    else {
                        Toast.makeText(this, "Manager controls not available for employees", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.myScheduleButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }
                R.id.setAvailabilityButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }
                R.id.requestTimeOffButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }
                R.id.searchOpenShiftsButton -> {
                    startActivity(Intent(this, SearchOpenShiftsActivity::class.java))
                }
                R.id.viewWorkedHoursButton -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }
                R.id.logOutButtonMenu -> {
                    startActivity(Intent(this, MainActivity::class.java))
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