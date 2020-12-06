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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.R
import java.text.SimpleDateFormat
import java.util.ArrayList
import android.view.View
import android.widget.*
import com.proshiftteam.proshift.DataFiles.EmployeeObject
import com.proshiftteam.proshift.DataFiles.ShiftObject
import com.proshiftteam.proshift.DataFiles.UpdateShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.Utilities.asTimePicker
import kotlinx.android.synthetic.main.activity_modify_shift.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class ModifyShiftActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
    val timeFormat: String = "hh:mm a"

    var employeeMap: HashMap<String, Int> = HashMap<String, Int>()
    var employeeNames: ArrayList<String> = ArrayList<String>()
    var selectEmployee: String = "None"

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_shift)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        // Retreives the values received from the adapter in the previous screen (list of all shifts)
        val date: String? = bundle?.getString("date")
        val startTime: String? = bundle?.getString("start_time")
        val endTime: String? = bundle?.getString("end_time")
        val employeeId: Int? = bundle?.getInt("employeeId")
        val position: String? = bundle?.getString("position")
        val openStatus: Boolean? = bundle?.getBoolean("is_open")
        val employeeNameDefault: String? = bundle?.getString("employee_name")
        val id = bundle?.getInt("id")

        // Formatting the time to 12-hour
        val timeFormatOld = SimpleDateFormat("HH:mm:ss")
        val startTimeOld = timeFormatOld.parse(startTime)
        val endTimeOld = timeFormatOld.parse(endTime)
        val newTimeFormatOld = SimpleDateFormat("hh:mm a")
        val startTimeNew: String = newTimeFormatOld.format(startTimeOld)
        val endTimeNew: String = newTimeFormatOld.format(endTimeOld)

        // Displaying the default time
        shiftBeginTimeInModifyShift.setText(startTimeNew, TextView.BufferType.EDITABLE)
        shiftEndTimeInModifyShift.setText(endTimeNew, TextView.BufferType.EDITABLE)

        val calView: CalendarView = findViewById<CalendarView>(R.id.calrenderViewSelectDateInModifyShift)
        val shift_begin: EditText = findViewById<EditText>(R.id.shiftBeginTimeInModifyShift)
        val shift_end: EditText = findViewById<EditText>(R.id.shiftEndTimeInModifyShift)
        val employeeSpinner: Spinner = findViewById(R.id.selectEmployeeSpinnerInUpdateShift)
        val btnUpdateShift: Button = findViewById<Button>(R.id.updateShiftButtonInModifyShift)

        // Splits the date string to create an array
        val splitBy = "-"
        val listOfDateItems = date!!.split(splitBy)

        // Change the array to int
        val listOfIntItems = listOfDateItems.map { it.toInt()}

        //Toast.makeText(context, listOfIntItems.toString() + " " + employeeNameDefault, Toast.LENGTH_SHORT).show()

        // Sets the value in calender
        var cal1 = Calendar.getInstance()
        cal1.set(Calendar.YEAR, listOfIntItems[0])
        cal1.set(Calendar.MONTH, listOfIntItems[1]-1)
        cal1.set(Calendar.DAY_OF_MONTH, listOfIntItems[2])
        calView.date = cal1.timeInMillis

        // If the user changes calender
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


        // API call to get a list of employees
        val callApiGetEmployees: Call<List<EmployeeObject>> = RetrofitBuilderObject.connectJsonApiCalls.getEmployees("Token $tokenCode")
        callApiGetEmployees.enqueue(object: Callback<List<EmployeeObject>> {
            override fun onFailure(call: Call<List<EmployeeObject>>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            }

            // Gets a list of employees to be added to the spinner
            override fun onResponse(call: Call<List<EmployeeObject>>, response: Response<List<EmployeeObject>>) {
                if(response.code() == 200) {
                    Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()
                    val employeeList = response.body()

                    var count = 0
                    var defaultEmp: Int = 0
                    // Get employee names and use a hashmap to keep track of employee ids
                    for(employee in employeeList?.asIterable()!!) {
                        employeeMap.put(employee.employee_name, employee.id)
                        employeeNames.add(employee.employee_name)
                    }

                    // Sets the default value by counting the index
                    for (employee in employeeNames) {
                        if (employee == employeeNameDefault) {
                            defaultEmp = count
                        }
                        count++
                    }


                    val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, employeeNames)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    employeeSpinner.adapter = spinnerAdapter
                    employeeSpinner.setSelection(defaultEmp)
                    employeeSpinner.onItemSelectedListener = context

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Button to update the shift
        updateShiftButtonInModifyShift.setOnClickListener{
            val employee: Int = employeeMap[selectEmployee]!!
            val is_open: Boolean = false
            val date: String = dateFormat.format(Date(calView.date))

            val time_start_ampm: String = shift_begin.text.toString()
            val time_end_ampm: String = shift_end.text.toString()
            val timeFormat = SimpleDateFormat("hh:mm a")
            val startTimeAgain = timeFormat.parse(time_start_ampm)
            val endTimeAgain = timeFormat.parse(time_end_ampm)
            val newTimeFormat = SimpleDateFormat("HH:mm:ss")
            val time_start: String = newTimeFormat.format(startTimeAgain)
            val time_end: String = newTimeFormat.format(endTimeAgain)

            val updateShiftObject = UpdateShiftObject(id!!, employee, is_open, date, time_start, time_end)

            // Call API request to send the update shift object to the server
            val callApiUpdateShift: Call<UpdateShiftObject> = RetrofitBuilderObject.connectJsonApiCalls.updateShift("Token $tokenCode", updateShiftObject)
            callApiUpdateShift.enqueue(object: Callback<UpdateShiftObject> {
                override fun onFailure(call: Call<UpdateShiftObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Failed to update shift!", Toast.LENGTH_SHORT).show()
                }

                // After modifying the shift, sends the user back to the list of shifts activity
                override fun onResponse(call: Call<UpdateShiftObject>, response: Response<UpdateShiftObject>) {
                    if(response.isSuccessful)
                    {
                        Toast.makeText(context, "Success updating shift. Response code: " + response.code(), Toast.LENGTH_SHORT).show()
                        val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
                        intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
                        intentToCurrentShiftAddRemoveActivity.putExtra("accessCode", accessCode)
                        startActivity(intentToCurrentShiftAddRemoveActivity)
                    }
                    else
                    {
                        Toast.makeText(context, "Error updating shift. Response code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        // Button to go back to the list of shifts activity
        findViewById<ImageView>(R.id.backArrowButtonModifyShifts).setOnClickListener {
            val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
            intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
            intentToCurrentShiftAddRemoveActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCurrentShiftAddRemoveActivity)
        }

    }
    fun append(arr: Array<String>, element: String): Array<String> {
        var list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    // Gets the selected employee item
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectEmployee = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}