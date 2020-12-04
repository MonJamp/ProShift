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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CreateNewShiftActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
    val timeFormat: String = "HH:mm"

    var employeeMap: HashMap<String, Int> = HashMap<String, Int>()
    var employeeNames: ArrayList<String> = ArrayList<String>()
    var selectEmployee: String = "None"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_shift)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

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

        val callApiGetEmployees: Call<List<EmployeeObject>> = connectJsonApiCalls.getEmployees("Token $tokenCode")
        callApiGetEmployees.enqueue(object: Callback<List<EmployeeObject>> {
            override fun onFailure(call: Call<List<EmployeeObject>>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<EmployeeObject>>, response: Response<List<EmployeeObject>>) {
                if(response.code() == 200) {
                    Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()
                    val employeeList = response.body()

                    employeeNames.add("None ")

                    // Get employee names and use a hashmap to keep track of employee ids
                    for(employee in employeeList?.asIterable()!!) {
                        if(employee.employee_name == "None ") {
                            employeeMap.put("None ", employee.id)
                        } else {
                            employeeMap.put(employee.employee_name, employee.id)
                            employeeNames.add(employee.employee_name)
                        }
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

        btnCreateShift.setOnClickListener{
            val employee: Int = employeeMap[selectEmployee]!!
            val is_open: Boolean = selectEmployee == "None " // If none is selected make it an open shift
            val is_dropped: Boolean = false
            val date: String = dateFormat.format(Date(calView.date))
            val time_start: String = shift_begin.text.toString()
            val time_end: String = shift_end.text.toString()

            val shiftObject = ShiftObject(employee, is_open, is_dropped, date, time_start, time_end)

            val callApiCreateShift: Call<ShiftObject> = connectJsonApiCalls.createShift("Token $tokenCode", shiftObject)
            callApiCreateShift.enqueue(object: Callback<ShiftObject> {
                override fun onFailure(call: Call<ShiftObject>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ShiftObject>, response: Response<ShiftObject>) {
                    if(response.code() == 201)
                    {
                        Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()

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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectEmployee = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
