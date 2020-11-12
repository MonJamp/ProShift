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

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")


        findViewById<ImageView>(R.id.backArrowButton).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToHomeActivity)
        }

        addRemoveShiftsButton.setOnClickListener {
            val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
            intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToCurrentShiftAddRemoveActivity)
        }
        createNewShiftButton.setOnClickListener {
            val intentToCreateNewShiftActivity = Intent(context, CreateNewShiftActivity::class.java)
            intentToCreateNewShiftActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToCreateNewShiftActivity)
        }
        approveShiftRequestsButton.setOnClickListener {
            val intentToApproveShiftRequestActivity = Intent(context, ApproveShiftRequestActivity::class.java)
            intentToApproveShiftRequestActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToApproveShiftRequestActivity)
        }
        approveTimeOffRequestButton.setOnClickListener {
            val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
            intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToApproveTimeOffRequestActivity)
        }

    }
}
