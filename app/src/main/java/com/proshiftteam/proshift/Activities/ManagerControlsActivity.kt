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
        val accessCode: Int? = bundle?.getInt("accessCode")


        findViewById<ImageView>(R.id.backArrowButton).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            intentToHomeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToHomeActivity)
        }

        generateCodeButton.setOnClickListener {
            val intentToGenerateCodeActivity = Intent(context, GenerateCodeActivity::class.java)
            intentToGenerateCodeActivity.putExtra("tokenCode", tokenCode)
            intentToGenerateCodeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToGenerateCodeActivity)
        }

        addRemoveShiftsButton.setOnClickListener {
            val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
            intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
            intentToCurrentShiftAddRemoveActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCurrentShiftAddRemoveActivity)
        }
        createNewShiftButton.setOnClickListener {
            val intentToCreateNewShiftActivity = Intent(context, CreateNewShiftActivity::class.java)
            intentToCreateNewShiftActivity.putExtra("tokenCode", tokenCode)
            intentToCreateNewShiftActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCreateNewShiftActivity)
        }
        approveShiftRequestsButton.setOnClickListener {
            val intentToApproveShiftRequestActivity = Intent(context, ApproveShiftRequestActivity::class.java)
            intentToApproveShiftRequestActivity.putExtra("tokenCode", tokenCode)
            intentToApproveShiftRequestActivity.putExtra("accessCode", accessCode)
            startActivity(intentToApproveShiftRequestActivity)
        }
        approveTimeOffRequestButton.setOnClickListener {
            val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
            intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
            intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
            startActivity(intentToApproveTimeOffRequestActivity)
        }

    }
}
