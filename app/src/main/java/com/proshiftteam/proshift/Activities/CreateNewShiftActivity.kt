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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.EmployeeObject
import com.proshiftteam.proshift.DataFiles.ShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import com.proshiftteam.proshift.Utilities.asTimePicker
import kotlinx.android.synthetic.main.activity_create_new_shift.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CreateNewShiftActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
    val timeFormat: String = "hh:mm a"

    var employeeMap: HashMap<String, Int?> = HashMap<String, Int?>()
    var employeeNames: ArrayList<String> = ArrayList<String>()
    var selectEmployee: String = "None"

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_shift)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")


        // Back button to go back to the manager controls activity
        findViewById<ImageView>(R.id.backArrowButtonCreateNewShift).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            intentToManagerControlsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerControlsActivity)
        }

        // Widgets
        val calView: CalendarView = findViewById<CalendarView>(R.id.calrenderViewSelectDate)
        val shift_begin: EditText = findViewById<EditText>(R.id.shiftBeginTime)
        val shift_end: EditText = findViewById<EditText>(R.id.shiftEndTime)
        val employeeSpinner: Spinner = findViewById(R.id.selectEmployeeSpinnerInNewShift)
        val btnCreateShift: Button = findViewById<Button>(R.id.createNewShiftButtonInCreateNewShift)

        var cal = Calendar.getInstance()
        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            calView.date = cal.timeInMillis
        }

        // Transform EditText to TimePicker
        shift_begin.asTimePicker(context, timeFormat)
        shift_end.asTimePicker(context, timeFormat)

        // API call to get a list of employees for the spinner
        val callApiGetEmployees: Call<List<EmployeeObject>> = connectJsonApiCalls.getEmployees("Token $tokenCode")
        callApiGetEmployees.enqueue(object: Callback<List<EmployeeObject>> {
            override fun onFailure(call: Call<List<EmployeeObject>>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<EmployeeObject>>, response: Response<List<EmployeeObject>>) {
                if(response.code() == 200) {
                    // Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()
                    val employeeList = response.body()

                    employeeNames.add("None")
                    employeeMap.put("None", null)

                    // Get employee names and use a hashmap to keep track of employee ids
                    for(employee in employeeList?.asIterable()!!) {
                            employeeMap.put(employee.employee_name, employee.id)
                            employeeNames.add(employee.employee_name)
                    }

                    val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, employeeNames)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    employeeSpinner.adapter = spinnerAdapter
                    employeeSpinner.onItemSelectedListener = context

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Creates a shift button
        btnCreateShift.setOnClickListener{
            val employee: Int? = employeeMap[selectEmployee]
            val is_open: Boolean = employee == null // If none is selected make it an open shift
            val is_dropped: Boolean = false
            val date: String = dateFormat.format(Date(calView.date))

            val time_start_ampm: String = shift_begin.text.toString()
            val time_end_ampm: String = shift_end.text.toString()
            val timeFormat = SimpleDateFormat("hh:mm a")
            val startTimeAgain = timeFormat.parse(time_start_ampm)
            val endTimeAgain = timeFormat.parse(time_end_ampm)
            val newTimeFormat = SimpleDateFormat("HH:mm:ss")
            val time_start: String = newTimeFormat.format(startTimeAgain)
            val time_end: String = newTimeFormat.format(endTimeAgain)

            val shiftObject = ShiftObject(employee, is_open, is_dropped, date, time_start, time_end)

            // API request to send a create new shift request
            val callApiCreateShift: Call<ShiftObject> = connectJsonApiCalls.createShift("Token $tokenCode", shiftObject)
            callApiCreateShift.enqueue(object: Callback<ShiftObject> {
                override fun onFailure(call: Call<ShiftObject>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                }

                // Sends the user back to manager controls on successfully creating a shift
                override fun onResponse(call: Call<ShiftObject>, response: Response<ShiftObject>) {
                    if(response.code() == 201)
                    {
                        val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
                        intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
                        intentToManagerControlsActivity.putExtra("accessCode", accessCode)
                        startActivity(intentToManagerControlsActivity)
                    }
                    else
                    {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        var list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }


    // Gets the employee selected
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectEmployee = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
