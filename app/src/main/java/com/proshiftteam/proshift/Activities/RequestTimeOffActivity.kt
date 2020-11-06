package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_request_time_off.*

class RequestTimeOffActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_time_off)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        findViewById<ImageView>(R.id.backArrowButtonRequestTimeOff).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToHomeActivity)
        }
        requestTimeOffButtonInTimeOff.setOnClickListener {
            // calender code here
        }
    }
}