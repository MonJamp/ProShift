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


        findViewById<ImageView>(R.id.backArrowButton).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }


        addRemoveShiftsButton.setOnClickListener {
            startActivity(Intent(this, CurrentShiftAddRemoveActivity::class.java))
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
