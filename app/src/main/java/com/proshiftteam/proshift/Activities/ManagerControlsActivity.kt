package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_manager_controls.*

class ManagerControlsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manager_controls)

        // Goes back to main activity/home screen when button is clicked
        val drawerLayoutManagerControls: DrawerLayout = findViewById(R.id.drawer_manager_controls)


        findViewById<ImageView>(R.id.imageMenuButton).setOnClickListener {
            drawerLayoutManagerControls.openDrawer(GravityCompat.START)
        }

        val navigationViewItems : NavigationView = findViewById(R.id.menuNavigationView)

        navigationViewItems.setNavigationItemSelectedListener { MenuItem ->
            MenuItem.isChecked = true

            when (MenuItem.itemId) {
                R.id.homeButtonMenu -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.myScheduleButtonMenu -> {
                }
                R.id.logOutButtonMenu -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            true
        }

        addRemoveShiftsButton.setOnClickListener {

        }
        createNewScheduleButton.setOnClickListener {

        }
        approveShiftRequestsButton.setOnClickListener {

        }
        setMaxHoursButton.setOnClickListener {

        }
        estimateCostButton.setOnClickListener {

        }

    }
}
