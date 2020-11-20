package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.RequestTimeOffObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_request_time_off.*
import com.proshiftteam.proshift.Utilities.asTimePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RequestTimeOffActivity: AppCompatActivity() {

    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
    val timeFormat: String = "HH:mm"

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

        val calenderViewStart: CalendarView = findViewById<CalendarView>(R.id.date_start_time_off)
        val calenderViewEnd: CalendarView = findViewById<CalendarView>(R.id.date_end_time_off)
        val shiftBegin: EditText = findViewById<EditText>(R.id.time_start_time_off)
        val shiftEnd: EditText = findViewById<EditText>(R.id.time_end_time_off)
        val calenderInstance = Calendar.getInstance()
        val calenderInstance2 = Calendar.getInstance()

        calenderViewStart.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calenderInstance.set(Calendar.YEAR, year)
            calenderInstance.set(Calendar.MONTH, month)
            calenderInstance.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calenderViewStart.date = calenderInstance.timeInMillis
        }
        calenderViewEnd.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calenderInstance2.set(Calendar.YEAR, year)
            calenderInstance2.set(Calendar.MONTH, month)
            calenderInstance2.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calenderViewEnd.date = calenderInstance2.timeInMillis
        }

        shiftBegin.asTimePicker(context, timeFormat)
        shiftEnd.asTimePicker(context, timeFormat)

        requestTimeOffButtonInTimeOff.setOnClickListener {
            val start_date: String = dateFormat.format(Date(calenderViewStart.date))
            val end_date: String = dateFormat.format(Date(calenderViewEnd.date))
            val time_start: String = shiftBegin.text.toString()
            val time_end: String = shiftEnd.text.toString()

            val timeOffObject = RequestTimeOffObject(6, start_date, end_date, time_start, time_end)

            val callApiRequestTimeOff: Call<RequestTimeOffObject> = connectJsonApiCalls.requestTimeOff("Token $tokenCode", timeOffObject)
            callApiRequestTimeOff.enqueue(object: Callback<RequestTimeOffObject> {
                override fun onFailure(call: Call<RequestTimeOffObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error requesting time off!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<RequestTimeOffObject>,
                    response: Response<RequestTimeOffObject>
                ) {
                    if(response.code() == 201)
                    {
                        Toast.makeText(context, "Successfully requested time off " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(context, "Error requesting time off " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }
}