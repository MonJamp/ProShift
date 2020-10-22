package com.proshiftteam.proshift.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.R
import com.proshiftteam.proshift.DataFiles.ScheduleData

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle: Bundle? = intent.extras
        val accessCode: Int = bundle!!.getInt("accessCode")
        viewManager = LinearLayoutManager(this)

        val managerControlButton : Button = findViewById(R.id.managerControlsButton)
        if (accessCode == 1) {
            managerControlButton.visibility = View.VISIBLE
        }
        managerControlButton.setOnClickListener {
            startActivity(Intent(this, ManagerControlsActivity::class.java))
        }

        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_home_controls)


        findViewById<ImageView>(R.id.imageMenuButton).setOnClickListener {
            drawerLayoutManagerControls.openDrawer(GravityCompat.START)
        }

        val navigationViewItems : NavigationView = findViewById(R.id.menuNavigationView)

        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {
                R.id.homeButtonMenu -> {
                    drawerLayoutManagerControls.closeDrawer(GravityCompat.START)
                }
                R.id.myScheduleButtonMenu -> {

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
