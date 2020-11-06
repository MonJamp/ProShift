package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_create_new_shift.*

class CreateNewShiftActivity: AppCompatActivity() {

    // Temporary values for the spinner. Will be replaced by values retrieved from the GET request later
    var listOfEmployees = arrayOf("Rupin", "Filjo", "Muhammad", "Richard")
    var employeeSpinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_new_shift)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        findViewById<ImageView>(R.id.backArrowButtonCreateNewShift).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToManagerControlsActivity)
        }

        // Add date functions

        employeeSpinner = this.selectEmployeeSpinnerInNewShift

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfEmployees)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        employeeSpinner!!.adapter = spinnerAdapter
    }
}