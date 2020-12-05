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
        val accessCode: Int? = bundle?.getInt("accessCode")

        findViewById<ImageView>(R.id.backArrowButtonRequestTimeOff).setOnClickListener {
            val intentToListOfTimeOffRequestsActivity = Intent(context, ListOfTimeOffRequestsActivity::class.java)
            intentToListOfTimeOffRequestsActivity.putExtra("tokenCode", tokenCode)
            intentToListOfTimeOffRequestsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToListOfTimeOffRequestsActivity)
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

            val timeOffObject = RequestTimeOffObject(start_date, end_date, time_start, time_end)

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
                        // Toast.makeText(context, "Successfully requested time off " + response.code(), Toast.LENGTH_SHORT).show()
                        val intentToListOfTimeOffRequestsActivity = Intent(context, ListOfTimeOffRequestsActivity::class.java)
                        intentToListOfTimeOffRequestsActivity.putExtra("tokenCode", tokenCode)
                        intentToListOfTimeOffRequestsActivity.putExtra("accessCode", accessCode)
                        startActivity(intentToListOfTimeOffRequestsActivity)
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