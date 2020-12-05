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
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_manager_controls.*

class ManagerControlsActivity: AppCompatActivity() {
    lateinit var context: Context
    lateinit var tokenCode: String
    var accessCode: Int = 0

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manager_controls)

        context = this
        val bundle: Bundle? = intent.extras
        tokenCode = bundle!!.getString("tokenCode")!!
        accessCode = bundle!!.getInt("accessCode")


        // Button to go back to the home activity
        findViewById<ImageView>(R.id.backArrowButton).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            intentToHomeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToHomeActivity)
        }

        // Button to go to the generate code activity
        generateCodeButton.setOnClickListener {
            val intentToCompanyCodeActivity = Intent(context, CompanyCodeActivity::class.java)
            intentToCompanyCodeActivity.putExtra("tokenCode", tokenCode)
            intentToCompanyCodeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCompanyCodeActivity)
        }

        // Button to go to add/remove shift requests activity
        addRemoveShiftsButton.setOnClickListener {
            val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
            intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
            intentToCurrentShiftAddRemoveActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCurrentShiftAddRemoveActivity)
        }

        // Button to go to the create new shift activity
        createNewShiftButton.setOnClickListener {
            val intentToCreateNewShiftActivity = Intent(context, CreateNewShiftActivity::class.java)
            intentToCreateNewShiftActivity.putExtra("tokenCode", tokenCode)
            intentToCreateNewShiftActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCreateNewShiftActivity)
        }

        // Button to go to the approve/deny shift requests activity
        approveShiftRequestsButton.setOnClickListener {
            val intentToApproveShiftRequestActivity = Intent(context, ApproveShiftRequestActivity::class.java)
            intentToApproveShiftRequestActivity.putExtra("tokenCode", tokenCode)
            intentToApproveShiftRequestActivity.putExtra("accessCode", accessCode)
            startActivity(intentToApproveShiftRequestActivity)
        }

        // Button to go to approve/deny time off request activity
        approveTimeOffRequestButton.setOnClickListener {
            val intentToApproveTimeOffRequestActivity = Intent(context, ApproveTimeOffRequestActivity::class.java)
            intentToApproveTimeOffRequestActivity.putExtra("tokenCode", tokenCode)
            intentToApproveTimeOffRequestActivity.putExtra("accessCode", accessCode)
            startActivity(intentToApproveTimeOffRequestActivity)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentToHomeActivity = Intent(context, HomeActivity::class.java)
        intentToHomeActivity.putExtra("tokenCode", tokenCode)
        intentToHomeActivity.putExtra("accessCode", accessCode)
        startActivity(intentToHomeActivity)
    }
}
