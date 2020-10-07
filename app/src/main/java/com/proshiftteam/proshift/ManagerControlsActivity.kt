package com.proshiftteam.proshift

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_manager_controls.*

class ManagerControlsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manager_controls)

        // Goes back to main activity/home screen when button is clicked
        goBackToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
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
